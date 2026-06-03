package com.goldskinmc.livingsponge.world.level.block;

import com.goldskinmc.livingsponge.content.LivingSpongeBlocks;
import com.goldskinmc.livingsponge.simulation.profile.SpongeTraits;
import com.goldskinmc.livingsponge.world.level.block.entity.LivingSpongeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class LivingSpongeBlock extends BaseEntityBlock implements EntityBlock {
    private final boolean creativeOverrides;

    public LivingSpongeBlock(final boolean creativeOverrides, final Properties properties) {
        super(properties);
        this.creativeOverrides = creativeOverrides;
    }

    public boolean creativeOverrides() {
        return creativeOverrides;
    }

    @Override
    public RenderShape getRenderShape(final BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(
            final Level level,
            final BlockPos pos,
            final BlockState state,
            @Nullable final net.minecraft.world.entity.LivingEntity placer,
            final net.minecraft.world.item.ItemStack stack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        final BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
        if (blockEntity instanceof LivingSpongeBlockEntity livingSpongeBlockEntity && !livingSpongeBlockEntity.hasNodeState()) {
            livingSpongeBlockEntity.initializeRoot(SpongeTraits.DEFAULT, creativeOverrides);
        }
    }

    @Override
    public void onRemove(final BlockState state, final Level level, final BlockPos pos, final BlockState newState, final boolean isMoving) {
        if (!state.is(newState.getBlock()) && !LivingSpongeBlocks.isLivingSponge(newState.getBlock())) {
            final BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LivingSpongeBlockEntity livingSpongeBlockEntity) {
                livingSpongeBlockEntity.beforeBlockRemoved();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new LivingSpongeBlockEntity(pos, state);
    }

    @Override
    public boolean propagatesSkylightDown(final BlockState state, final BlockGetter reader, final BlockPos pos) {
        return true;
    }
}
