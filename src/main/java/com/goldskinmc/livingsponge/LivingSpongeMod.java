package com.goldskinmc.livingsponge;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(LivingSpongeMod.MOD_ID)
public final class LivingSpongeMod {
    public static final String MOD_ID = "livingsponge";
    private static final Logger LOGGER = LogUtils.getLogger();

    public LivingSpongeMod(final FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, LivingSpongeConfig.SPEC);
        LOGGER.info("Living Sponge initialized.");
    }
}
