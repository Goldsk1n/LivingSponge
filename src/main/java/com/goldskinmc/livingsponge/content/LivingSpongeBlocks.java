package com.goldskinmc.livingsponge.content;

import com.goldskinmc.livingsponge.LivingSpongeMod;
import com.goldskinmc.livingsponge.simulation.LivingSpongeLifecycleStage;
import com.goldskinmc.livingsponge.simulation.profile.SpongeTraits;
import com.goldskinmc.livingsponge.world.level.block.LivingSpongeBlock;
import com.goldskinmc.livingsponge.world.level.block.LivingSpongeVisualProfile;
import com.goldskinmc.livingsponge.world.level.block.SpongeRemainsBlock;
import com.goldskinmc.livingsponge.world.level.block.SpongeRemainsStyle;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class LivingSpongeBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LivingSpongeMod.MOD_ID);

    public static final Supplier<Block> LIVING_SPONGE = register(
            "living_sponge",
            properties -> new LivingSpongeBlock(false, properties),
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()
    );

    public static final Supplier<Block> MATURE_LIVING_SPONGE = register(
            "mature_living_sponge",
            properties -> new LivingSpongeBlock(false, properties),
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()
    );

    public static final Supplier<Block> OLD_LIVING_SPONGE = register(
            "old_living_sponge",
            properties -> new LivingSpongeBlock(false, properties),
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()
    );

    public static final Supplier<Block> CREATIVE_LIVING_SPONGE = register(
            "creative_living_sponge",
            properties -> new LivingSpongeBlock(true, properties),
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()
    );

    public static final Supplier<Block> CREATIVE_MATURE_LIVING_SPONGE = register(
            "creative_mature_living_sponge",
            properties -> new LivingSpongeBlock(true, properties),
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()
    );

    public static final Supplier<Block> CREATIVE_OLD_LIVING_SPONGE = register(
            "creative_old_living_sponge",
            properties -> new LivingSpongeBlock(true, properties),
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()
    );

    public static final Supplier<Block> SPONGE_REMAINS = registerWithItem(
            "sponge_remains",
            SpongeRemainsBlock::new,
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(0.4F)
                    .sound(SoundType.DEEPSLATE)
    );

    private LivingSpongeBlocks() {
    }

    public static void register(final IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    public static boolean isLivingSponge(final Block block) {
        return block == LIVING_SPONGE.get()
                || block == MATURE_LIVING_SPONGE.get()
                || block == OLD_LIVING_SPONGE.get()
                || block == CREATIVE_LIVING_SPONGE.get()
                || block == CREATIVE_MATURE_LIVING_SPONGE.get()
                || block == CREATIVE_OLD_LIVING_SPONGE.get();
    }

    public static Block spongeBlockFor(final LivingSpongeLifecycleStage stage, final boolean creativeOverrides) {
        return switch (stage) {
            case YOUNG -> creativeOverrides ? CREATIVE_LIVING_SPONGE.get() : LIVING_SPONGE.get();
            case MATURE -> creativeOverrides ? CREATIVE_MATURE_LIVING_SPONGE.get() : MATURE_LIVING_SPONGE.get();
            case OLD -> creativeOverrides ? CREATIVE_OLD_LIVING_SPONGE.get() : OLD_LIVING_SPONGE.get();
            case DEAD -> throw new IllegalArgumentException("Dead sponges do not have a block state");
        };
    }

    public static BlockState spongeStateFor(
            final LivingSpongeLifecycleStage stage,
            final SpongeTraits traits,
            final boolean creativeOverrides
    ) {
        return spongeBlockFor(stage, creativeOverrides)
                .defaultBlockState()
                .setValue(LivingSpongeBlock.VISUAL_PROFILE, LivingSpongeVisualProfile.from(traits, creativeOverrides));
    }

    public static BlockState spongeRemainsState(final boolean wallFormingStyle) {
        return SPONGE_REMAINS.get()
                .defaultBlockState()
                .setValue(SpongeRemainsBlock.STYLE, wallFormingStyle ? SpongeRemainsStyle.WALL_FORMING : SpongeRemainsStyle.DEFAULT);
    }

    private static DeferredBlock<Block> register(
            final String name,
            final java.util.function.Function<BlockBehaviour.Properties, ? extends Block> factory,
            final Supplier<BlockBehaviour.Properties> properties
    ) {
        return BLOCKS.registerBlock(name, factory, properties);
    }

    private static DeferredBlock<Block> registerWithItem(
            final String name,
            final java.util.function.Function<BlockBehaviour.Properties, ? extends Block> factory,
            final Supplier<BlockBehaviour.Properties> properties
    ) {
        final DeferredBlock<Block> registered = register(name, factory, properties);
        LivingSpongeItems.ITEMS.registerSimpleBlockItem(name, registered);
        return registered;
    }
}
