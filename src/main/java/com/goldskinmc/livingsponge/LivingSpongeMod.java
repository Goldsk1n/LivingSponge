package com.goldskinmc.livingsponge;

import com.goldskinmc.livingsponge.content.LivingSpongeBlockEntities;
import com.goldskinmc.livingsponge.content.LivingSpongeBlocks;
import com.goldskinmc.livingsponge.content.LivingSpongeCreativeTabs;
import com.goldskinmc.livingsponge.content.LivingSpongeItems;
import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import com.goldskinmc.livingsponge.recipe.conditions.ConfigFlagCondition;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
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
        LivingSpongeCreativeTabs.register(modEventBus);
        context.registerConfig(ModConfig.Type.COMMON, LivingSpongeConfig.SPEC);
        CraftingHelper.register(ConfigFlagCondition.Serializer.INSTANCE);
        LOGGER.info("Living Sponge initialized.");
    }
}
