package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.LivingSpongeMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

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
