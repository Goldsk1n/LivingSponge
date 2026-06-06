package com.goldskinmc.livingsponge.world.level.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class SpongeRemainsBlock extends Block {
    public static final EnumProperty<SpongeRemainsStyle> STYLE =
            EnumProperty.create("style", SpongeRemainsStyle.class);

    public SpongeRemainsBlock(final Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(STYLE, SpongeRemainsStyle.DEFAULT));
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STYLE);
    }
}
