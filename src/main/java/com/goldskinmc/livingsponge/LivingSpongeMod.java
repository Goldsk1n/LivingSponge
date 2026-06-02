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
            event.accept(LivingSpongeBlocks.LIVING_SPONGE.get());
            event.accept(LivingSpongeBlocks.CREATIVE_LIVING_SPONGE.get());
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(LivingSpongeItems.HYDRO_FRUIT.get());
        }
    }
}
