package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.content.LivingSpongeBlocks;
import com.goldskinmc.livingsponge.content.LivingSpongeItems;
import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import com.goldskinmc.livingsponge.world.level.block.entity.LivingSpongeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

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
        final Map<UUID, Integer> colonySizes = buildColonySizes(levelNodes);
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

            final int colonyChildren = Math.max(0, colonySizes.getOrDefault(state.colonyId(), 1) - 1);
            final SampledContext sampledContext = sampleContext(level, pos, state, colonyChildren);
            final LivingSpongeTickResult result = simulationService.tickNode(state, sampledContext.tickContext(), level.getRandom());

            if (result.shouldDie()) {
                iterator.remove();
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                continue;
            }

            consumeWaterSources(level, sampledContext.waterSourceTargets(), result.absorbedWaterBlocks());
            dropHydroFruit(level, pos, result.fruitDrops());

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
            final LivingSpongeNodeState state,
            final int colonyChildren
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
                        colonyChildren,
                        distanceFromRoot
                ),
                waterSources
        );
    }

    private static Map<UUID, Integer> buildColonySizes(final Map<BlockPos, LivingSpongeNodeState> nodes) {
        final Map<UUID, Integer> sizes = new HashMap<>();
        for (LivingSpongeNodeState state : nodes.values()) {
            sizes.merge(state.colonyId(), 1, Integer::sum);
        }
        return sizes;
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
            if (level.getBlockState(target).canBeReplaced() && level.getFluidState(target).isEmpty()) {
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

    private static void consumeWaterSources(final ServerLevel level, final List<BlockPos> waterSources, final int amount) {
        for (int i = 0; i < amount && i < waterSources.size(); i++) {
            final BlockPos sourcePos = waterSources.get(i);
            if (level.getBlockState(sourcePos).is(Blocks.WATER) && level.getFluidState(sourcePos).isSource()) {
                level.setBlock(sourcePos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    private static void dropHydroFruit(final ServerLevel level, final BlockPos pos, final int amount) {
        if (amount <= 0) {
            return;
        }

        int remaining = amount;
        final int stackLimit = LivingSpongeItems.HYDRO_FRUIT.get().getMaxStackSize();
        while (remaining > 0) {
            final int stackSize = Math.min(remaining, stackLimit);
            Block.popResource(level, pos.below(), new ItemStack(LivingSpongeItems.HYDRO_FRUIT.get(), stackSize));
            remaining -= stackSize;
        }
    }

    private void placeChild(final ServerLevel level, final BlockPos pos, final LivingSpongeNodeState state) {
        if (!level.getBlockState(pos).canBeReplaced() || !level.getFluidState(pos).isEmpty()) {
            return;
        }

        final Block block = state.creativeVariant()
                ? LivingSpongeBlocks.CREATIVE_LIVING_SPONGE.get()
                : LivingSpongeBlocks.LIVING_SPONGE.get();
        if (!level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL)) {
            return;
        }

        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LivingSpongeBlockEntity livingSpongeBlockEntity) {
            livingSpongeBlockEntity.initializeFromState(state);
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
