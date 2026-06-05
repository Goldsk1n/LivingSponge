package com.goldskinmc.livingsponge.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;

public final class FluidPodBlock extends Block {
    private static final Map<ResourceKey<Level>, Set<BlockPos>> SUPPRESSED_RELEASES = new HashMap<>();
    private final Supplier<Item> pickupItem;
    private final Supplier<BlockState> releasedState;

    public FluidPodBlock(
            final BlockBehaviour.Properties properties,
            final Supplier<Item> pickupItem,
            final Supplier<BlockState> releasedState
    ) {
        super(properties);
        this.pickupItem = pickupItem;
        this.releasedState = releasedState;
    }

    @Override
    public InteractionResult use(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Player player,
            final net.minecraft.world.InteractionHand hand,
            final BlockHitResult hitResult
    ) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        suppressRelease(level, pos);
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        if (!player.getAbilities().instabuild) {
            final ItemStack stack = new ItemStack(pickupItem.get());
            if (!player.addItem(stack)) {
                popResource(level, pos, stack);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final BlockState newState,
            final boolean isMoving
    ) {
        if (!state.is(newState.getBlock()) && !level.isClientSide()) {
            if (consumeSuppressedRelease(level, pos)) {
                super.onRemove(state, level, pos, newState, isMoving);
                return;
            }

            releaseConnected((ServerLevel) level, pos, state.getBlock(), releasedState.get());
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public ItemStack getCloneItemStack(final net.minecraft.world.level.BlockGetter level, final BlockPos pos, final BlockState state) {
        return new ItemStack(pickupItem.get());
    }

    private static void releaseConnected(
            final ServerLevel level,
            final BlockPos origin,
            final Block block,
            final BlockState releasedState
    ) {
        final Set<BlockPos> toRelease = new HashSet<>();
        final Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(origin.immutable());
        toRelease.add(origin.immutable());

        while (!queue.isEmpty()) {
            final BlockPos current = queue.remove();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }

                        final BlockPos neighbor = current.offset(dx, dy, dz);
                        if (!toRelease.contains(neighbor) && level.getBlockState(neighbor).is(block)) {
                            final BlockPos immutableNeighbor = neighbor.immutable();
                            toRelease.add(immutableNeighbor);
                            queue.add(immutableNeighbor);
                        }
                    }
                }
            }
        }

        for (BlockPos pos : toRelease) {
            suppressRelease(level, pos);
        }

        for (BlockPos pos : toRelease) {
            level.setBlock(pos, releasedState, Block.UPDATE_ALL);
        }
    }

    private static void suppressRelease(final Level level, final BlockPos pos) {
        SUPPRESSED_RELEASES.computeIfAbsent(level.dimension(), ignored -> new HashSet<>()).add(pos.immutable());
    }

    private static boolean consumeSuppressedRelease(final Level level, final BlockPos pos) {
        final Set<BlockPos> suppressed = SUPPRESSED_RELEASES.get(level.dimension());
        if (suppressed == null) {
            return false;
        }

        final boolean removed = suppressed.remove(pos);
        if (suppressed.isEmpty()) {
            SUPPRESSED_RELEASES.remove(level.dimension());
        }
        return removed;
    }
}
