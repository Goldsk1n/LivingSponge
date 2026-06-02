package com.goldskinmc.livingsponge.content;

import com.goldskinmc.livingsponge.LivingSpongeMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class LivingSpongeItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LivingSpongeMod.MOD_ID);

    public static final RegistryObject<Item> HYDRO_FRUIT = ITEMS.register(
            "hydro_fruit",
            () -> new Item(new Item.Properties().stacksTo(16))
    );

    private LivingSpongeItems() {
    }

    public static void register(final IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
