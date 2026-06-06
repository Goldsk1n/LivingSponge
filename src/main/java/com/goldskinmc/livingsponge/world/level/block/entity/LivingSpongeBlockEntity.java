package com.goldskinmc.livingsponge.world.level.block.entity;

import com.goldskinmc.livingsponge.content.LivingSpongeBlocks;
import com.goldskinmc.livingsponge.content.LivingSpongeBlockEntities;
import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import com.goldskinmc.livingsponge.simulation.LivingSpongeNodeState;
import com.goldskinmc.livingsponge.simulation.LivingSpongeRuntime;
import com.goldskinmc.livingsponge.simulation.profile.SpongeTraits;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class LivingSpongeBlockEntity extends BlockEntity {
    private LivingSpongeNodeState nodeState;

    public LivingSpongeBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(LivingSpongeBlockEntities.LIVING_SPONGE.get(), pos, blockState);
    }

    public boolean hasNodeState() {
        return nodeState != null;
    }

    public LivingSpongeNodeState getNodeState() {
        return nodeState;
    }

    public void initializeRoot(final SpongeTraits traits, final boolean creativeOverrides) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        nodeState = LivingSpongeNodeState.createPlacedRoot(worldPosition, traits, creativeOverrides, LivingSpongeConfig.values());
        LivingSpongeRuntime.instance().registerNode(serverLevel, worldPosition, nodeState);
        setChanged();
    }

    public void initializeFromState(final LivingSpongeNodeState state) {
        nodeState = state;
        if (level instanceof ServerLevel serverLevel) {
            LivingSpongeRuntime.instance().registerNode(serverLevel, worldPosition, nodeState);
        }
        setChanged();
    }

    public void beforeBlockRemoved() {
        if (level instanceof ServerLevel serverLevel) {
            LivingSpongeRuntime.instance().unregisterNode(serverLevel, worldPosition);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (nodeState != null) {
            LivingSpongeRuntime.instance().registerNode(serverLevel, worldPosition, nodeState);
        }
    }

    @Override
    public void setRemoved() {
        if (nodeState != null
                && level instanceof ServerLevel serverLevel
                && !LivingSpongeBlocks.isLivingSponge(serverLevel.getBlockState(worldPosition).getBlock())) {
            LivingSpongeRuntime.instance().unregisterNode(serverLevel, worldPosition);
        }
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);
        if (nodeState != null) {
            tag.put("LivingSpongeState", nodeState.save());
        }
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        if (tag.contains("LivingSpongeState")) {
            nodeState = LivingSpongeNodeState.load(tag.getCompound("LivingSpongeState"));
        } else {
            nodeState = null;
        }
    }
}
