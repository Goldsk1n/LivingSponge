package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.LivingSpongeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LivingSpongeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LivingSpongeServerEvents {
    private LivingSpongeServerEvents() {
    }

    @SubscribeEvent
    public static void onServerTick(final TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.getServer() != null) {
            LivingSpongeRuntime.instance().tickServer(event.getServer());
        }
    }
}
