package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.content.LivingSpongeBlocks;
import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import com.goldskinmc.livingsponge.simulation.profile.ResolvedSpongeProfile;
import com.goldskinmc.livingsponge.world.level.block.entity.LivingSpongeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class LivingSpongeRuntime {
    private static final LivingSpongeRuntime INSTANCE = new LivingSpongeRuntime();
    private static final int[][] FLAT_OFFSETS = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1},
            {1, 1},
            {1, -1},
            {-1, 1},
            {-1, -1}
    };

    private final LivingSpongeSimulationService simulationService = new LivingSpongeSimulationService();
    private final Map<ResourceKey<Level>, Map<BlockPos, LivingSpongeNodeState>> nodesByLevel = new HashMap<>();
    private final Map<ResourceKey<Level>, Map<BlockPos, Long>> blockedTargetsByLevel = new HashMap<>();

    private LivingSpongeRuntime() {
    }

    public static LivingSpongeRuntime instance() {
        return INSTANCE;
    }

    public void registerNode(final ServerLevel level, final BlockPos pos, final LivingSpongeNodeState state) {
        nodes(level).put(pos.immutable(), state);
    }

    public void unregisterNode(final ServerLevel level, final BlockPos pos) {
        final Map<BlockPos, LivingSpongeNodeState> levelNodes = nodesByLevel.get(level.dimension());
        if (levelNodes == null) {
            return;
        }
        levelNodes.remove(pos);
        if (levelNodes.isEmpty()) {
            nodesByLevel.remove(level.dimension());
        }
    }

    public Optional<LivingSpongeNodeState> getNode(final ServerLevel level, final BlockPos pos) {
        final Map<BlockPos, LivingSpongeNodeState> levelNodes = nodesByLevel.get(level.dimension());
        if (levelNodes == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(levelNodes.get(pos));
    }

    public void tickServer(final MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            tickLevel(level);
        }
    }

    private void tickLevel(final ServerLevel level) {
        pruneExpiredBlockedTargets(level, level.getGameTime());

        final Map<BlockPos, LivingSpongeNodeState> levelNodes = nodesByLevel.get(level.dimension());
        if (levelNodes == null || levelNodes.isEmpty()) {
            return;
        }

        final LivingSpongeConfig.BalanceValues values = LivingSpongeConfig.values();
        final List<PendingChild> pendingChildren = new ArrayList<>();
        final long gameTime = level.getGameTime();

        final Iterator<Map.Entry<BlockPos, LivingSpongeNodeState>> iterator = levelNodes.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<BlockPos, LivingSpongeNodeState> entry = iterator.next();
            final BlockPos pos = entry.getKey();
            final LivingSpongeNodeState state = entry.getValue();
            if (!isManagedLivingSponge(level, pos)) {
                iterator.remove();
                continue;
            }

            final ResolvedSpongeProfile profile = state.resolveProfile();
            final int updateInterval = profile.updateIntervalTicks(values);

            if (!shouldProcessAtTick(gameTime, pos, state, updateInterval)) {
                continue;
            }

            final SampledContext sampledContext = sampleContext(level, pos, state, profile, gameTime);
            final LivingSpongeTickResult result = simulationService.tickNode(
                    state,
                    profile,
                    sampledContext.tickContext(),
                    level.getRandom(),
                    values,
                    updateInterval
            );

            if (result.shouldDie()) {
                iterator.remove();
                if ((profile.isNeutralOutput() || profile.isFruitingOutput() || profile.isWallFormingOutput())
                        && result.deathReason() == LivingSpongeDeathReason.AGING) {
                    blockTargetUntil(level, pos, gameTime + values.spread().deathTargetCooldownTicks());
                }
                applyDeathOutcome(level, pos, state, profile, result);
                continue;
            }

            syncPhase(level, pos, state, profile, result.stage());

            result.reproductionTarget().ifPresent(target -> {
                final LivingSpongeNodeState child = LivingSpongeNodeState.createChild(state, LivingSpongeConfig.values());
                pendingChildren.add(new PendingChild(target.immutable(), child));
            });
        }

        for (PendingChild childEntry : pendingChildren) {
            placeChild(level, childEntry.pos(), childEntry.state(), gameTime);
        }

        if (levelNodes.isEmpty()) {
            nodesByLevel.remove(level.dimension());
        }
    }

    private static boolean shouldProcessAtTick(
            final long gameTime,
            final BlockPos pos,
            final LivingSpongeNodeState state,
            final int interval
    ) {
        if (interval <= 1) {
            return true;
        }
        final long mixed = pos.asLong() ^ state.colonyId().getLeastSignificantBits();
        final int slot = Math.floorMod((int) (mixed ^ (mixed >>> 32)), interval);
        return Math.floorMod(gameTime, interval) == slot;
    }

    private SampledContext sampleContext(
            final ServerLevel level,
            final BlockPos pos,
            final LivingSpongeNodeState state,
            final ResolvedSpongeProfile profile,
            final long gameTime
    ) {
        final LivingSpongeConfig.BalanceValues values = LivingSpongeConfig.values();
        final List<BlockPos> mediumSources = findNearbyMediumSources(
                level,
                pos,
                profile,
                values.spread().mediumScanRadius(),
                values.spread().maxMediumSamplesPerUpdate()
        );
        final boolean hasOpposingFluidContact = hasOpposingFluidContact(level, pos, profile);
        final boolean hasFireContact = hasFireContact(level, pos);
        final int radiusCap = profile.radiusCap(values);
        final List<BlockPos> reproductionTargets = findReproductionTargets(level, pos, state.rootPos(), profile, radiusCap, gameTime);
        final int distanceFromRoot = chebyshevDistance(pos, state.rootPos());
        final boolean canStayActive = level.hasChunkAt(pos) && isManagedLivingSponge(level, pos);

        return new SampledContext(
                new LivingSpongeTickContext(
                        canStayActive,
                        mediumSources.size(),
                        hasOpposingFluidContact,
                        hasFireContact,
                        reproductionTargets,
                        0,
                        distanceFromRoot
                ),
                mediumSources
        );
    }

    private static List<BlockPos> findNearbyMediumSources(
            final ServerLevel level,
            final BlockPos center,
            final ResolvedSpongeProfile profile,
            final int radius,
            final int cap
    ) {
        final List<BlockPos> targets = new ArrayList<>(cap);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    final BlockPos samplePos = center.offset(x, y, z);
                    if (matchesMediumSource(level, samplePos, profile)) {
                        targets.add(samplePos.immutable());
                        if (targets.size() >= cap) {
                            return targets;
                        }
                    }
                }
            }
        }
        return targets;
    }

    private static boolean hasOpposingFluidContact(
            final ServerLevel level,
            final BlockPos pos,
            final ResolvedSpongeProfile profile
    ) {
        if (isOpposingFluid(level, pos, profile)) {
            return true;
        }
        for (Direction direction : Direction.values()) {
            if (isOpposingFluid(level, pos.relative(direction), profile)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasFireContact(final ServerLevel level, final BlockPos pos) {
        if (isFire(level, pos)) {
            return true;
        }
        for (Direction direction : Direction.values()) {
            if (isFire(level, pos.relative(direction))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFire(final ServerLevel level, final BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.FIRE) || level.getBlockState(pos).is(Blocks.SOUL_FIRE);
    }

    private List<BlockPos> findReproductionTargets(
            final ServerLevel level,
            final BlockPos pos,
            final BlockPos rootPos,
            final ResolvedSpongeProfile profile,
            final int radiusCap,
            final long gameTime
    ) {
        final List<BlockPos> targets = new ArrayList<>(profile.isFlatSpread() ? FLAT_OFFSETS.length : 6);

        if (profile.isFlatSpread()) {
            for (int[] offset : FLAT_OFFSETS) {
                final BlockPos target = pos.offset(offset[0], 0, offset[1]);
                if (isWithinRadius(rootPos, target, radiusCap)
                        && !isTargetBlocked(level, target, gameTime)
                        && canHostChild(level, target, profile)) {
                    targets.add(target.immutable());
                }
            }
            return targets;
        }

        for (Direction direction : Direction.values()) {
            final BlockPos target = pos.relative(direction);
            if (isWithinRadius(rootPos, target, radiusCap)
                    && !isTargetBlocked(level, target, gameTime)
                    && canHostChild(level, target, profile)) {
                targets.add(target.immutable());
            }
        }
        return targets;
    }

    private static boolean isManagedLivingSponge(final ServerLevel level, final BlockPos pos) {
        if (!LivingSpongeBlocks.isLivingSponge(level.getBlockState(pos).getBlock())) {
            return false;
        }
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof LivingSpongeBlockEntity;
    }

    private void placeChild(final ServerLevel level, final BlockPos pos, final LivingSpongeNodeState state, final long gameTime) {
        final ResolvedSpongeProfile profile = state.resolveProfile();
        if (isTargetBlocked(level, pos, gameTime) || !canHostChild(level, pos, profile)) {
            return;
        }

        final Block block = LivingSpongeBlocks.spongeBlockFor(
                state.stage(LivingSpongeConfig.values()),
                state.creativeOverrides()
        );
        if (!level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL)) {
            return;
        }

        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LivingSpongeBlockEntity livingSpongeBlockEntity) {
            livingSpongeBlockEntity.initializeFromState(state);
        }
    }

    private static boolean canHostChild(
            final ServerLevel level,
            final BlockPos pos,
            final ResolvedSpongeProfile profile
    ) {
        final BlockState state = level.getBlockState(pos);
        final boolean mediumFilled = matchesMedium(level, pos, profile);
        if (!mediumFilled) {
            return false;
        }

        if (profile.isFlatSpread() && !level.getBlockState(pos.above()).isAir()) {
            return false;
        }

        final Block mediumBlock = profile.usesWaterMedium() ? Blocks.WATER : Blocks.LAVA;
        return state.canBeReplaced() || state.is(mediumBlock);
    }

    private static void applyDeathOutcome(
            final ServerLevel level,
            final BlockPos pos,
            final LivingSpongeNodeState state,
            final ResolvedSpongeProfile profile,
            final LivingSpongeTickResult result
    ) {
        if (result.deathReason() != LivingSpongeDeathReason.AGING) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            return;
        }

        if (profile.isSolidifyingOutput()) {
            level.setBlock(pos, LivingSpongeBlocks.SPONGE_REMAINS.get().defaultBlockState(), Block.UPDATE_ALL);
            return;
        }

        if (profile.isWallFormingOutput()) {
            level.setBlock(
                    pos,
                    isBorderShellDeath(pos, state, profile)
                            ? LivingSpongeBlocks.SPONGE_REMAINS.get().defaultBlockState()
                            : Blocks.AIR.defaultBlockState(),
                    Block.UPDATE_ALL
            );
            return;
        }

        if (profile.isFruitingOutput() && level.getRandom().nextDouble() < LivingSpongeConfig.values().fruit().deathSpawnChance()) {
            final BlockState fruitState = profile.usesWaterMedium()
                    ? LivingSpongeBlocks.HYDRO_FRUIT_CLUSTER.get().defaultBlockState()
                    : LivingSpongeBlocks.LAVA_FRUIT_CLUSTER.get().defaultBlockState();
            if (profile.isFlatSpread()) {
                level.setBlock(pos, fruitState, Block.UPDATE_ALL);
            } else {
                FallingBlockEntity.fall(level, pos, fruitState);
            }
            return;
        }

        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    }

    private static boolean isBorderShellDeath(
            final BlockPos pos,
            final LivingSpongeNodeState state,
            final ResolvedSpongeProfile profile
    ) {
        final int currentDistance = chebyshevDistance(pos, state.rootPos());
        final int radiusCap = profile.radiusCap(LivingSpongeConfig.values());
        return currentDistance == radiusCap;
    }

    private static void syncPhase(
            final ServerLevel level,
            final BlockPos pos,
            final LivingSpongeNodeState state,
            final ResolvedSpongeProfile profile,
            final LivingSpongeLifecycleStage stage
    ) {
        if (stage == LivingSpongeLifecycleStage.DEAD) {
            return;
        }

        final BlockState currentState = level.getBlockState(pos);
        final Block targetBlock = LivingSpongeBlocks.spongeBlockFor(stage, profile.creativeOverrides());
        if (currentState.is(targetBlock)) {
            return;
        }

        if (level.setBlock(pos, targetBlock.defaultBlockState(), Block.UPDATE_ALL)) {
            final BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LivingSpongeBlockEntity livingSpongeBlockEntity) {
                livingSpongeBlockEntity.initializeFromState(state);
            }
        }
    }

    private static int chebyshevDistance(final BlockPos a, final BlockPos b) {
        final int dx = Math.abs(a.getX() - b.getX());
        final int dy = Math.abs(a.getY() - b.getY());
        final int dz = Math.abs(a.getZ() - b.getZ());
        return Math.max(dx, Math.max(dy, dz));
    }

    private static boolean isWithinRadius(final BlockPos rootPos, final BlockPos targetPos, final int radiusCap) {
        return chebyshevDistance(targetPos, rootPos) <= radiusCap;
    }

    private Map<BlockPos, LivingSpongeNodeState> nodes(final ServerLevel level) {
        return nodesByLevel.computeIfAbsent(level.dimension(), ignored -> new HashMap<>());
    }

    private void blockTargetUntil(final ServerLevel level, final BlockPos pos, final long blockedUntilGameTime) {
        if (blockedUntilGameTime <= level.getGameTime()) {
            return;
        }
        blockedTargets(level).put(pos.immutable(), blockedUntilGameTime);
    }

    private boolean isTargetBlocked(final ServerLevel level, final BlockPos pos, final long gameTime) {
        final Map<BlockPos, Long> blockedTargets = blockedTargetsByLevel.get(level.dimension());
        if (blockedTargets == null) {
            return false;
        }

        final Long blockedUntil = blockedTargets.get(pos);
        if (blockedUntil == null) {
            return false;
        }
        if (blockedUntil <= gameTime) {
            blockedTargets.remove(pos);
            if (blockedTargets.isEmpty()) {
                blockedTargetsByLevel.remove(level.dimension());
            }
            return false;
        }
        return true;
    }

    private void pruneExpiredBlockedTargets(final ServerLevel level, final long gameTime) {
        final Map<BlockPos, Long> blockedTargets = blockedTargetsByLevel.get(level.dimension());
        if (blockedTargets == null || blockedTargets.isEmpty()) {
            return;
        }

        final Iterator<Map.Entry<BlockPos, Long>> iterator = blockedTargets.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue() <= gameTime) {
                iterator.remove();
            }
        }

        if (blockedTargets.isEmpty()) {
            blockedTargetsByLevel.remove(level.dimension());
        }
    }

    private Map<BlockPos, Long> blockedTargets(final ServerLevel level) {
        return blockedTargetsByLevel.computeIfAbsent(level.dimension(), ignored -> new HashMap<>());
    }

    private static boolean matchesMediumSource(
            final ServerLevel level,
            final BlockPos pos,
            final ResolvedSpongeProfile profile
    ) {
        if (!matchesMedium(level, pos, profile) || !level.getFluidState(pos).isSource()) {
            return false;
        }

        return profile.usesWaterMedium()
                ? level.getBlockState(pos).is(Blocks.WATER)
                : level.getBlockState(pos).is(Blocks.LAVA);
    }

    private static boolean matchesMedium(
            final ServerLevel level,
            final BlockPos pos,
            final ResolvedSpongeProfile profile
    ) {
        return profile.usesWaterMedium()
                ? level.getFluidState(pos).is(FluidTags.WATER)
                : level.getFluidState(pos).is(FluidTags.LAVA);
    }

    private static boolean isOpposingFluid(
            final ServerLevel level,
            final BlockPos pos,
            final ResolvedSpongeProfile profile
    ) {
        return profile.usesWaterMedium()
                ? level.getFluidState(pos).is(FluidTags.LAVA)
                : level.getFluidState(pos).is(FluidTags.WATER);
    }

    private record PendingChild(BlockPos pos, LivingSpongeNodeState state) {
    }

    private record SampledContext(LivingSpongeTickContext tickContext, List<BlockPos> mediumSourceTargets) {
    }
}
