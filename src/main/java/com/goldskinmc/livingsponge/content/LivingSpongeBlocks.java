package com.goldskinmc.livingsponge.content;

import com.goldskinmc.livingsponge.LivingSpongeMod;
import com.goldskinmc.livingsponge.simulation.LivingSpongeLifecycleStage;
import com.goldskinmc.livingsponge.simulation.profile.SpongeTraits;
import com.goldskinmc.livingsponge.world.level.block.LivingSpongeBlock;
import com.goldskinmc.livingsponge.world.level.block.LivingSpongeVisualProfile;
import com.goldskinmc.livingsponge.world.level.block.SpongeRemainsBlock;
import com.goldskinmc.livingsponge.world.level.block.SpongeRemainsStyle;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class LivingSpongeBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LivingSpongeMod.MOD_ID);

    public static final RegistryObject<Block> LIVING_SPONGE = register(
            "living_sponge",
            () -> new LivingSpongeBlock(false, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion())
    );

    public static final RegistryObject<Block> MATURE_LIVING_SPONGE = register(
            "mature_living_sponge",
            () -> new LivingSpongeBlock(false, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion())
    );

    public static final RegistryObject<Block> OLD_LIVING_SPONGE = register(
            "old_living_sponge",
            () -> new LivingSpongeBlock(false, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion())
    );

    public static final RegistryObject<Block> CREATIVE_LIVING_SPONGE = register(
            "creative_living_sponge",
            () -> new LivingSpongeBlock(true, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion())
    );

    public static final RegistryObject<Block> CREATIVE_MATURE_LIVING_SPONGE = register(
            "creative_mature_living_sponge",
            () -> new LivingSpongeBlock(true, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion())
    );

    public static final RegistryObject<Block> CREATIVE_OLD_LIVING_SPONGE = register(
            "creative_old_living_sponge",
            () -> new LivingSpongeBlock(true, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion())
    );

    public static final RegistryObject<Block> SPONGE_REMAINS = registerWithItem(
            "sponge_remains",
            () -> new SpongeRemainsBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(0.4F)
                    .sound(SoundType.DEEPSLATE))
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

    private static RegistryObject<Block> register(final String name, final Supplier<? extends Block> blockSupplier) {
        return BLOCKS.register(name, blockSupplier);
    }

    private static RegistryObject<Block> registerWithItem(final String name, final Supplier<? extends Block> blockSupplier) {
        final RegistryObject<Block> registered = register(name, blockSupplier);
        LivingSpongeItems.ITEMS.register(name, () -> new BlockItem(registered.get(), new Item.Properties()));
        return registered;
    }
}
