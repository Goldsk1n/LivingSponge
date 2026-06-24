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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class LivingSpongeRuntime {
    private static final LivingSpongeRuntime INSTANCE = new LivingSpongeRuntime();
    private static final int[][] FLAT_CARDINAL_OFFSETS = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
    };
    private static final int[][] FLAT_DIAGONAL_OFFSETS = {
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
    private final Map<ResourceKey<Level>, Map<MediumSampleCacheKey, Boolean>> mediumSourceMatchesByLevel = new HashMap<>();

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
        clearMediumSampleCache(level);

        final Map<BlockPos, LivingSpongeNodeState> levelNodes = nodesByLevel.get(level.dimension());
        if (levelNodes == null || levelNodes.isEmpty()) {
            return;
        }

        final LivingSpongeConfig.BalanceValues values = LivingSpongeConfig.values();
        final List<PendingChild> pendingChildren = new ArrayList<>();
        final long gameTime = level.getGameTime();

        final List<Map.Entry<BlockPos, LivingSpongeNodeState>> nodeEntries = new ArrayList<>(levelNodes.entrySet());
        for (Map.Entry<BlockPos, LivingSpongeNodeState> entry : nodeEntries) {
            final BlockPos pos = entry.getKey();
            final LivingSpongeNodeState state = entry.getValue();
            if (levelNodes.get(pos) != state) {
                continue;
            }
            if (!isManagedLivingSponge(level, pos)) {
                levelNodes.remove(pos, state);
                continue;
            }

            final ResolvedSpongeProfile profile = state.resolveProfile();
            final int updateInterval = profile.updateIntervalTicks(values);

            if (!shouldProcessAtTick(gameTime, pos, state, updateInterval)) {
                continue;
            }

            final int distanceFromRoot = chebyshevDistance(pos, state.rootPos());
            final boolean canStayActive = level.hasChunkAt(pos) && isManagedLivingSponge(level, pos);
            final boolean hasOpposingFluidContact = hasOpposingFluidContact(level, pos, profile);
            final boolean hasFireContact = hasFireContact(level, pos, profile);

            final LivingSpongeTickResult result = simulationService.tickLifecycle(
                    state,
                    canStayActive,
                    hasOpposingFluidContact,
                    hasFireContact,
                    values,
                    updateInterval
            );

            if (result.shouldDie()) {
                levelNodes.remove(pos, state);
                if ((profile.isNeutralOutput() || profile.isWallFormingOutput())
                        && result.deathReason() == LivingSpongeDeathReason.AGING) {
                    blockTargetUntil(level, pos, gameTime + values.spread().deathTargetCooldownTicks());
                }
                applyDeathOutcome(level, pos, state, profile, result);
                continue;
            }

            syncPhase(level, pos, state, profile, result.stage());

            final int radiusCap = profile.radiusCap(values);
            if (!requiresReproductionSampling(
                    state,
                    result.stage(),
                    distanceFromRoot,
                    radiusCap
            )) {
                continue;
            }

            final int nearbyMediumSources = countNearbyMediumSources(
                    level,
                    pos,
                    profile,
                    values.spread().mediumScanRadius(),
                    values.spread().maxMediumSamplesPerUpdate()
            );
            if (nearbyMediumSources <= 0) {
                continue;
            }

            final List<BlockPos> reproductionTargets = findReproductionTargets(level, pos, state.rootPos(), profile, radiusCap, gameTime);
            if (!simulationService.canAttemptReproduction(
                    state,
                    profile,
                    values,
                    result.stage(),
                    nearbyMediumSources,
                    reproductionTargets,
                    distanceFromRoot
            )) {
                continue;
            }

            simulationService.chooseReproductionTarget(
                    state,
                    profile,
                    level.getRandom(),
                    values,
                    reproductionTargets
            ).ifPresent(target -> {
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

    private static boolean requiresReproductionSampling(
            final LivingSpongeNodeState state,
            final LivingSpongeLifecycleStage stage,
            final int distanceFromRoot,
            final int radiusCap
    ) {
        if (stage == LivingSpongeLifecycleStage.OLD || stage == LivingSpongeLifecycleStage.DEAD) {
            return false;
        }
        if (state.reproductionCooldownTicks() > 0) {
            return false;
        }
        return distanceFromRoot <= radiusCap;
    }

    private int countNearbyMediumSources(
            final ServerLevel level,
            final BlockPos center,
            final ResolvedSpongeProfile profile,
            final int radius,
            final int cap
    ) {
        int count = 0;
        if (profile.isFlatSpread()) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    final BlockPos samplePos = center.offset(x, 0, z);
                    if (matchesFlatMediumSource(level, samplePos, profile)) {
                        count++;
                        if (count >= cap) {
                            return count;
                        }
                    }
                }
            }
            return count;
        }

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    final BlockPos samplePos = center.offset(x, y, z);
                    if (matchesMediumSourceCached(level, samplePos, profile, false)) {
                        count++;
                        if (count >= cap) {
                            return count;
                        }
                    }
                }
            }
        }
        return count;
    }

    private boolean matchesFlatMediumSource(
            final ServerLevel level,
            final BlockPos pos,
            final ResolvedSpongeProfile profile
    ) {
        return matchesMediumSourceCached(level, pos, profile, true);
    }

    private static boolean hasOpposingFluidContact(
            final ServerLevel level,
            final BlockPos pos,
            final ResolvedSpongeProfile profile
    ) {
        if (profile.ignoresEnvironmentDeath()) {
            return false;
        }
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

    private static boolean hasFireContact(
            final ServerLevel level,
            final BlockPos pos,
            final ResolvedSpongeProfile profile
    ) {
        if (profile.ignoresEnvironmentDeath()) {
            return false;
        }
        return hasFireContact(level, pos);
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
        final int[][] flatOffsets = LivingSpongeConfig.values().spread().flatUsesDiagonals()
                ? FLAT_DIAGONAL_OFFSETS
                : FLAT_CARDINAL_OFFSETS;
        final List<BlockPos> targets = new ArrayList<>(profile.isFlatSpread() ? flatOffsets.length : 6);

        if (profile.isFlatSpread()) {
            for (int[] offset : flatOffsets) {
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

    private boolean placeChild(final ServerLevel level, final BlockPos pos, final LivingSpongeNodeState state, final long gameTime) {
        final ResolvedSpongeProfile profile = state.resolveProfile();
        if (isTargetBlocked(level, pos, gameTime) || !canHostChild(level, pos, profile)) {
            return false;
        }

        if (!level.setBlock(
                pos,
                LivingSpongeBlocks.spongeStateFor(
                        state.stage(LivingSpongeConfig.values()),
                        state.traits(),
                        state.creativeOverrides()
                ),
                Block.UPDATE_ALL
        )) {
            return false;
        }

        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LivingSpongeBlockEntity livingSpongeBlockEntity) {
            livingSpongeBlockEntity.initializeFromState(state);
            return true;
        }
        return false;
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

        return state.canBeReplaced() || isMediumBlock(state, profile);
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
            level.setBlock(pos, LivingSpongeBlocks.spongeRemainsState(false), Block.UPDATE_ALL);
            return;
        }

        if (profile.isWallFormingOutput()) {
            level.setBlock(
                    pos,
                    isBorderShellDeath(pos, state, profile)
                            ? LivingSpongeBlocks.spongeRemainsState(true)
                            : Blocks.AIR.defaultBlockState(),
                    Block.UPDATE_ALL
            );
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
        final LivingSpongeConfig.BalanceValues values = LivingSpongeConfig.values();
        final int radiusCap = profile.radiusCap(values);
        final int shellThickness = Math.max(1, values.output().wallFormingShellThickness());
        final int shellStart = Math.max(0, radiusCap - shellThickness + 1);
        return currentDistance >= shellStart && currentDistance <= radiusCap;
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
        final BlockState targetState = LivingSpongeBlocks.spongeStateFor(stage, state.traits(), profile.creativeOverrides());
        if (currentState.equals(targetState)) {
            return;
        }

        if (level.setBlock(pos, targetState, Block.UPDATE_ALL)) {
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

        final List<BlockPos> expiredTargets = new ArrayList<>();
        for (Map.Entry<BlockPos, Long> entry : blockedTargets.entrySet()) {
            if (entry.getValue() <= gameTime) {
                expiredTargets.add(entry.getKey());
            }
        }

        for (BlockPos expiredTarget : expiredTargets) {
            blockedTargets.remove(expiredTarget);
        }

        if (blockedTargets.isEmpty()) {
            blockedTargetsByLevel.remove(level.dimension());
        }
    }

    private Map<BlockPos, Long> blockedTargets(final ServerLevel level) {
        return blockedTargetsByLevel.computeIfAbsent(level.dimension(), ignored -> new HashMap<>());
    }

    private boolean matchesMediumSource(
            final ServerLevel level,
            final BlockPos pos,
            final ResolvedSpongeProfile profile
    ) {
        if (!matchesMedium(level, pos, profile) || !level.getFluidState(pos).isSource()) {
            return false;
        }

        return isMediumBlock(level.getBlockState(pos), profile);
    }

    private boolean matchesMediumSourceCached(
            final ServerLevel level,
            final BlockPos pos,
            final ResolvedSpongeProfile profile,
            final boolean requireAirAbove
    ) {
        final MediumSampleCacheKey key = new MediumSampleCacheKey(
                pos.immutable(),
                profile.supportsWaterMedium(),
                profile.supportsLavaMedium(),
                requireAirAbove
        );
        final Map<MediumSampleCacheKey, Boolean> cache = mediumSourceMatches(level);
        final Boolean cached = cache.get(key);
        if (cached != null) {
            return cached;
        }

        final boolean matches = requireAirAbove
                ? matchesMediumSource(level, pos, profile) && level.getBlockState(pos.above()).isAir()
                : matchesMediumSource(level, pos, profile);
        cache.put(key, matches);
        return matches;
    }

    private static boolean matchesMedium(
            final ServerLevel level,
            final BlockPos pos,
            final ResolvedSpongeProfile profile
    ) {
        return (profile.supportsWaterMedium() && level.getFluidState(pos).is(FluidTags.WATER))
                || (profile.supportsLavaMedium() && level.getFluidState(pos).is(FluidTags.LAVA));
    }

    private static boolean isOpposingFluid(
            final ServerLevel level,
            final BlockPos pos,
            final ResolvedSpongeProfile profile
    ) {
        if (profile.ignoresEnvironmentDeath()) {
            return false;
        }
        if (profile.supportsWaterMedium() && profile.supportsLavaMedium()) {
            return false;
        }

        return profile.usesWaterMedium()
                ? level.getFluidState(pos).is(FluidTags.LAVA)
                : level.getFluidState(pos).is(FluidTags.WATER);
    }

    private static boolean isMediumBlock(final BlockState state, final ResolvedSpongeProfile profile) {
        return (profile.supportsWaterMedium() && state.is(Blocks.WATER))
                || (profile.supportsLavaMedium() && state.is(Blocks.LAVA));
    }

    private void clearMediumSampleCache(final ServerLevel level) {
        final Map<MediumSampleCacheKey, Boolean> cache = mediumSourceMatchesByLevel.get(level.dimension());
        if (cache != null) {
            cache.clear();
        }
    }

    private Map<MediumSampleCacheKey, Boolean> mediumSourceMatches(final ServerLevel level) {
        return mediumSourceMatchesByLevel.computeIfAbsent(level.dimension(), ignored -> new HashMap<>());
    }

    private record PendingChild(BlockPos pos, LivingSpongeNodeState state) {
    }

    private record MediumSampleCacheKey(
            BlockPos pos,
            boolean supportsWater,
            boolean supportsLava,
            boolean requireAirAbove
    ) {
    }
}
