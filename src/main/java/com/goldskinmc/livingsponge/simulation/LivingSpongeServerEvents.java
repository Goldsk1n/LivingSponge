package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.LivingSpongeMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = LivingSpongeMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class LivingSpongeServerEvents {
    private LivingSpongeServerEvents() {
    }

    @SubscribeEvent
    public static void onServerTick(final ServerTickEvent.Post event) {
        if (event.getServer() != null) {
            LivingSpongeRuntime.instance().tickServer(event.getServer());
        }
    }
}
