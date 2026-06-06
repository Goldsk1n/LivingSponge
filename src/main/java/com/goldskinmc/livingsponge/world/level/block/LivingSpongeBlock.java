package com.goldskinmc.livingsponge.world.level.block;

import com.goldskinmc.livingsponge.content.LivingSpongeBlocks;
import com.goldskinmc.livingsponge.content.LivingSpongeItems;
import com.goldskinmc.livingsponge.simulation.LivingSpongeLifecycleStage;
import com.goldskinmc.livingsponge.simulation.profile.SpongeTraits;
import com.goldskinmc.livingsponge.world.level.block.entity.LivingSpongeBlockEntity;
import com.goldskinmc.livingsponge.world.item.LivingSpongePlacementItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class LivingSpongeBlock extends BaseEntityBlock implements EntityBlock {
    public static final EnumProperty<LivingSpongeVisualProfile> VISUAL_PROFILE =
            EnumProperty.create("visual_profile", LivingSpongeVisualProfile.class);

    private final boolean creativeOverrides;

    public LivingSpongeBlock(final boolean creativeOverrides, final Properties properties) {
        super(properties);
        this.creativeOverrides = creativeOverrides;
        final LivingSpongeVisualProfile defaultProfile = creativeOverrides
                ? LivingSpongeVisualProfile.CREATIVE_NEUTRAL
                : LivingSpongeVisualProfile.WATER_NEUTRAL;
        registerDefaultState(defaultBlockState().setValue(VISUAL_PROFILE, defaultProfile));
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

        final SpongeTraits traits = stack.getItem() instanceof LivingSpongePlacementItem placementItem
                ? placementItem.traits()
                : SpongeTraits.DEFAULT;
        final boolean overrides = stack.getItem() instanceof LivingSpongePlacementItem placementItem
                ? placementItem.creativeOverrides()
                : creativeOverrides;
        final BlockState placedState = LivingSpongeBlocks.spongeStateFor(
                LivingSpongeLifecycleStage.MATURE,
                traits,
                overrides
        );
        if (!state.equals(placedState)) {
            level.setBlock(pos, placedState, Block.UPDATE_ALL);
        }

        final BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
        if (blockEntity instanceof LivingSpongeBlockEntity livingSpongeBlockEntity) {
            livingSpongeBlockEntity.initializeRoot(traits, overrides);
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
    public List<ItemStack> getDrops(final BlockState state, final LootParams.Builder params) {
        final BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof LivingSpongeBlockEntity livingSpongeBlockEntity && livingSpongeBlockEntity.hasNodeState()) {
            return LivingSpongeItems.placementStackFor(
                    livingSpongeBlockEntity.getNodeState().traits(),
                    livingSpongeBlockEntity.getNodeState().creativeOverrides()
            ).map(List::of).orElseGet(() -> List.of(defaultPlacementStack()));
        }

        return List.of(defaultPlacementStack());
    }

    @Override
    public boolean propagatesSkylightDown(final BlockState state, final BlockGetter reader, final BlockPos pos) {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction(final BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VISUAL_PROFILE);
    }

    private ItemStack defaultPlacementStack() {
        final SpongeTraits fallbackTraits = creativeOverrides
                ? LivingSpongeItems.CREATIVE_BASELINE_TRAITS
                : SpongeTraits.DEFAULT;
        return LivingSpongeItems.placementStackFor(fallbackTraits, creativeOverrides)
                .orElseGet(() -> ItemStack.EMPTY)
                .copy();
    }
}
