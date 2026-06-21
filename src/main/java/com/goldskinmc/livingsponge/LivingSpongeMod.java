package com.goldskinmc.livingsponge;

import com.goldskinmc.livingsponge.content.LivingSpongeBlockEntities;
import com.goldskinmc.livingsponge.content.LivingSpongeBlocks;
import com.goldskinmc.livingsponge.content.LivingSpongeCreativeTabs;
import com.goldskinmc.livingsponge.content.LivingSpongeItems;
import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import com.goldskinmc.livingsponge.recipe.conditions.ConfigFlagCondition;
import com.goldskinmc.livingsponge.simulation.LivingSpongeServerEvents;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(LivingSpongeMod.MOD_ID)
public final class LivingSpongeMod {
    public static final String MOD_ID = "livingsponge";
    private static final Logger LOGGER = LogUtils.getLogger();

    public LivingSpongeMod(final IEventBus modEventBus, final ModContainer modContainer) {
        LivingSpongeBlocks.register(modEventBus);
        LivingSpongeItems.register(modEventBus);
        LivingSpongeBlockEntities.register(modEventBus);
        LivingSpongeCreativeTabs.register(modEventBus);
        ConfigFlagCondition.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, LivingSpongeConfig.SPEC);
        LivingSpongeConfig.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(LivingSpongeServerEvents::onServerTick);
        LOGGER.info("Living Sponge initialized.");
    }
}
