package com.goldskinmc.livingsponge;

import com.goldskinmc.livingsponge.content.LivingSpongeBlockEntities;
import com.goldskinmc.livingsponge.content.LivingSpongeBlocks;
import com.goldskinmc.livingsponge.content.LivingSpongeItems;
import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(LivingSpongeMod.MOD_ID)
public final class LivingSpongeMod {
    public static final String MOD_ID = "livingsponge";
    private static final Logger LOGGER = LogUtils.getLogger();

    public LivingSpongeMod(final FMLJavaModLoadingContext context) {
        final IEventBus modEventBus = context.getModEventBus();
        LivingSpongeBlocks.register(modEventBus);
        LivingSpongeItems.register(modEventBus);
        LivingSpongeBlockEntities.register(modEventBus);
        modEventBus.addListener(this::addCreativeTabContents);
        context.registerConfig(ModConfig.Type.COMMON, LivingSpongeConfig.SPEC);
        LOGGER.info("Living Sponge initialized.");
    }

    private void addCreativeTabContents(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(LivingSpongeItems.LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.FLAT_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_FLAT_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_FLAT_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.WALL_FORMING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_WALL_FORMING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_WALL_FORMING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.FLAT_WALL_FORMING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_FLAT_WALL_FORMING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_FLAT_WALL_FORMING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.MAGMA_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_MAGMA_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_MAGMA_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.MAGMA_FLAT_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_MAGMA_FLAT_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_MAGMA_FLAT_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.MAGMA_WALL_FORMING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_MAGMA_WALL_FORMING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_MAGMA_WALL_FORMING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.MAGMA_FLAT_WALL_FORMING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_MAGMA_FLAT_WALL_FORMING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_MAGMA_FLAT_WALL_FORMING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.FRUITING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_FRUITING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_FRUITING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.FLAT_FRUITING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_FLAT_FRUITING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_FLAT_FRUITING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.MAGMA_FRUITING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_MAGMA_FRUITING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_MAGMA_FRUITING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.MAGMA_FLAT_FRUITING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_MAGMA_FLAT_FRUITING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_MAGMA_FLAT_FRUITING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.SOLIDIFYING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_SOLIDIFYING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_SOLIDIFYING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.FLAT_SOLIDIFYING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_FLAT_SOLIDIFYING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_FLAT_SOLIDIFYING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.MAGMA_VOLUME_SOLIDIFYING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_MAGMA_VOLUME_SOLIDIFYING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_MAGMA_VOLUME_SOLIDIFYING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.MAGMA_FLAT_SOLIDIFYING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.EXPANDED_MAGMA_FLAT_SOLIDIFYING_LIVING_SPONGE.get());
            event.accept(LivingSpongeItems.VAST_MAGMA_FLAT_SOLIDIFYING_LIVING_SPONGE.get());
            event.accept(LivingSpongeBlocks.HYDRO_BLOCK.get());
            event.accept(LivingSpongeBlocks.SPONGE_REMAINS.get());
            event.accept(LivingSpongeItems.CREATIVE_LIVING_SPONGE.get());
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(LivingSpongeItems.HYDRO_FRUIT.get());
        }
    }
}
