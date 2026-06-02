package com.goldskinmc.livingsponge.content;

import com.goldskinmc.livingsponge.LivingSpongeMod;
import com.goldskinmc.livingsponge.world.level.block.LivingSpongeBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class LivingSpongeBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LivingSpongeMod.MOD_ID);

    public static final RegistryObject<Block> LIVING_SPONGE = register(
            "living_sponge",
            new LivingSpongeBlock(false, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion())
    );

    public static final RegistryObject<Block> CREATIVE_LIVING_SPONGE = register(
            "creative_living_sponge",
            new LivingSpongeBlock(true, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(0.6F)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion())
    );

    private LivingSpongeBlocks() {
    }

    public static void register(final IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    public static boolean isLivingSponge(final Block block) {
        return block == LIVING_SPONGE.get() || block == CREATIVE_LIVING_SPONGE.get();
    }

    private static RegistryObject<Block> register(final String name, final Block block) {
        final RegistryObject<Block> registered = BLOCKS.register(name, () -> block);
        LivingSpongeItems.ITEMS.register(name, () -> new BlockItem(registered.get(), new Item.Properties()));
        return registered;
    }
}
