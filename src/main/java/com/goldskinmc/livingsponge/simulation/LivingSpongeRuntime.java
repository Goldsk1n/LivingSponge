package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.content.LivingSpongeBlocks;
import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class LivingSpongeRuntime {
    private static final LivingSpongeRuntime INSTANCE = new LivingSpongeRuntime();

    private final LivingSpongeSimulationService simulationService = new LivingSpongeSimulationService();
    private final Map<ResourceKey<Level>, Map<BlockPos, LivingSpongeNodeState>> nodesByLevel = new HashMap<>();

    private LivingSpongeRuntime() {
    }

    public static LivingSpongeRuntime instance() {
        return INSTANCE;
    }

    public void registerNode(final ServerLevel level, final BlockPos pos, final LivingSpongeNodeState state) {
        nodes(level).put(pos.immutable(), state);
    }

    public void registerRoot(final ServerLevel level, final BlockPos pos, final boolean creativeVariant) {
        registerNode(level, pos, LivingSpongeNodeState.createRoot(pos, creativeVariant, LivingSpongeConfig.values()));
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

            final int updateInterval = state.creativeVariant()
                    ? values.creative().updateIntervalTicks()
                    : values.spread().updateIntervalTicks();

            if (!shouldProcessAtTick(gameTime, pos, state, updateInterval)) {
                continue;
            }

            final SampledContext sampledContext = sampleContext(level, pos, state);
            final LivingSpongeTickResult result = simulationService.tickNode(
                    state,
                    sampledContext.tickContext(),
                    level.getRandom(),
                    updateInterval
            );

            if (result.shouldDie()) {
                iterator.remove();
                final BlockState replacementState = deathReplacementState(level, pos, state, sampledContext.tickContext(), result);
                level.setBlock(pos, replacementState, Block.UPDATE_ALL);
                continue;
            }

            syncPhase(level, pos, state, result.stage());

            result.reproductionTarget().ifPresent(target -> {
                final LivingSpongeNodeState child = LivingSpongeNodeState.createChild(state, LivingSpongeConfig.values());
                pendingChildren.add(new PendingChild(target.immutable(), child));
            });
        }

        for (PendingChild childEntry : pendingChildren) {
            placeChild(level, childEntry.pos(), childEntry.state());
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
            final LivingSpongeNodeState state
    ) {
        final LivingSpongeConfig.BalanceValues values = LivingSpongeConfig.values();
        final List<BlockPos> waterSources = findNearbyWaterSources(
                level,
                pos,
                values.spread().absorbRadius(),
                values.spread().maxAbsorbsPerUpdate()
        );
        final boolean hasLavaContact = hasLavaContact(level, pos);
        final boolean hasFireContact = hasFireContact(level, pos);
        final List<BlockPos> reproductionTargets = findReproductionTargets(level, pos);
        final int distanceFromRoot = chebyshevDistance(pos, state.rootPos());
        final boolean canStayActive = level.hasChunkAt(pos) && isManagedLivingSponge(level, pos);

        return new SampledContext(
                new LivingSpongeTickContext(
                        canStayActive,
                        waterSources.size(),
                        hasLavaContact,
                        hasFireContact,
                        reproductionTargets,
                        0,
                        distanceFromRoot
                ),
                waterSources
        );
    }

    private static List<BlockPos> findNearbyWaterSources(
            final ServerLevel level,
            final BlockPos center,
            final int radius,
            final int cap
    ) {
        final List<BlockPos> targets = new ArrayList<>(cap);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    final BlockPos samplePos = center.offset(x, y, z);
                    if (level.getFluidState(samplePos).is(FluidTags.WATER)
                            && level.getFluidState(samplePos).isSource()
                            && level.getBlockState(samplePos).is(Blocks.WATER)) {
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

    private static boolean hasLavaContact(final ServerLevel level, final BlockPos pos) {
        if (level.getFluidState(pos).is(FluidTags.LAVA)) {
            return true;
        }
        for (Direction direction : Direction.values()) {
            if (level.getFluidState(pos.relative(direction)).is(FluidTags.LAVA)) {
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

    private static List<BlockPos> findReproductionTargets(final ServerLevel level, final BlockPos pos) {
        final List<BlockPos> targets = new ArrayList<>(6);
        for (Direction direction : Direction.values()) {
            final BlockPos target = pos.relative(direction);
            if (canHostChild(level, target)) {
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

    private void placeChild(final ServerLevel level, final BlockPos pos, final LivingSpongeNodeState state) {
        if (!canHostChild(level, pos)) {
            return;
        }

        final Block block = LivingSpongeBlocks.spongeBlockFor(
                state.stage(LivingSpongeConfig.values().lifecycle(), state.creativeVariant()),
                state.creativeVariant()
        );
        if (!level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL)) {
            return;
        }

        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LivingSpongeBlockEntity livingSpongeBlockEntity) {
            livingSpongeBlockEntity.initializeFromState(state);
        }
    }

    private static boolean canHostChild(final ServerLevel level, final BlockPos pos) {
        final BlockState state = level.getBlockState(pos);
        final boolean waterFilled = level.getFluidState(pos).is(FluidTags.WATER);
        return waterFilled && (state.canBeReplaced() || state.is(Blocks.WATER));
    }

    private static BlockState deathReplacementState(
            final ServerLevel level,
            final BlockPos pos,
            final LivingSpongeNodeState state,
            final LivingSpongeTickContext context,
            final LivingSpongeTickResult result
    ) {
        if (result.stage() != LivingSpongeLifecycleStage.DEAD) {
            return Blocks.AIR.defaultBlockState();
        }

        final LivingSpongeConfig.Lifecycle lifecycle = LivingSpongeConfig.values().lifecycle();
        if (isFrontierDeath(pos, state, context)) {
            return level.getRandom().nextDouble() < lifecycle.frontierRemainsChance()
                    ? LivingSpongeBlocks.SPONGE_REMAINS.get().defaultBlockState()
                    : Blocks.AIR.defaultBlockState();
        }

        if (level.getRandom().nextDouble() < lifecycle.nonFrontierHydroBlockChance()) {
            return LivingSpongeBlocks.HYDRO_BLOCK.get().defaultBlockState();
        }

        return Blocks.AIR.defaultBlockState();
    }

    private static boolean isFrontierDeath(
            final BlockPos pos,
            final LivingSpongeNodeState state,
            final LivingSpongeTickContext context
    ) {
        final int currentDistance = chebyshevDistance(pos, state.rootPos());
        for (BlockPos target : context.reproductionTargets()) {
            if (chebyshevDistance(target, state.rootPos()) >= currentDistance) {
                return true;
            }
        }
        return false;
    }

    private static void syncPhase(
            final ServerLevel level,
            final BlockPos pos,
            final LivingSpongeNodeState state,
            final LivingSpongeLifecycleStage stage
    ) {
        if (stage == LivingSpongeLifecycleStage.DEAD) {
            return;
        }

        final BlockState currentState = level.getBlockState(pos);
        final Block targetBlock = LivingSpongeBlocks.spongeBlockFor(stage, state.creativeVariant());
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

    private Map<BlockPos, LivingSpongeNodeState> nodes(final ServerLevel level) {
        return nodesByLevel.computeIfAbsent(level.dimension(), ignored -> new HashMap<>());
    }

    private record PendingChild(BlockPos pos, LivingSpongeNodeState state) {
    }

    private record SampledContext(LivingSpongeTickContext tickContext, List<BlockPos> waterSourceTargets) {
    }
}
