package com.goldskinmc.livingsponge.content;

import com.goldskinmc.livingsponge.LivingSpongeMod;
import com.goldskinmc.livingsponge.simulation.profile.MediumTrait;
import com.goldskinmc.livingsponge.simulation.profile.OutputTrait;
import com.goldskinmc.livingsponge.simulation.profile.RadiusTrait;
import com.goldskinmc.livingsponge.simulation.profile.SpongeTraits;
import com.goldskinmc.livingsponge.simulation.profile.SpreadTrait;
import com.goldskinmc.livingsponge.world.item.LivingSpongePlacementItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public final class LivingSpongeItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LivingSpongeMod.MOD_ID);

    public static final SpongeTraits BASELINE_TRAITS = new SpongeTraits(
            MediumTrait.WATER,
            SpreadTrait.VOLUME,
            OutputTrait.WALL_FORMING,
            RadiusTrait.STANDARD
    );
    public static final SpongeTraits SURFACE_TRAITS = new SpongeTraits(
            MediumTrait.WATER,
            SpreadTrait.SURFACE,
            OutputTrait.WALL_FORMING,
            RadiusTrait.STANDARD
    );
    public static final SpongeTraits SOLIDIFYING_TRAITS = new SpongeTraits(
            MediumTrait.WATER,
            SpreadTrait.VOLUME,
            OutputTrait.SOLIDIFYING,
            RadiusTrait.STANDARD
    );
    public static final SpongeTraits MAGMA_SURFACE_SOLIDIFYING_TRAITS = new SpongeTraits(
            MediumTrait.MAGMA,
            SpreadTrait.SURFACE,
            OutputTrait.SOLIDIFYING,
            RadiusTrait.STANDARD
    );

    public static final RegistryObject<Item> LIVING_SPONGE = ITEMS.register(
            "living_sponge",
            () -> new LivingSpongePlacementItem(
                    LivingSpongeBlocks.LIVING_SPONGE.get(),
                    new Item.Properties(),
                    BASELINE_TRAITS,
                    false
            )
    );
    public static final RegistryObject<Item> SURFACE_LIVING_SPONGE = ITEMS.register(
            "surface_living_sponge",
            () -> new LivingSpongePlacementItem(
                    LivingSpongeBlocks.LIVING_SPONGE.get(),
                    new Item.Properties(),
                    SURFACE_TRAITS,
                    false
            )
    );
    public static final RegistryObject<Item> SOLIDIFYING_LIVING_SPONGE = ITEMS.register(
            "solidifying_living_sponge",
            () -> new LivingSpongePlacementItem(
                    LivingSpongeBlocks.LIVING_SPONGE.get(),
                    new Item.Properties(),
                    SOLIDIFYING_TRAITS,
                    false
            )
    );
    public static final RegistryObject<Item> MAGMA_SURFACE_SOLIDIFYING_LIVING_SPONGE = ITEMS.register(
            "magma_surface_solidifying_living_sponge",
            () -> new LivingSpongePlacementItem(
                    LivingSpongeBlocks.LIVING_SPONGE.get(),
                    new Item.Properties(),
                    MAGMA_SURFACE_SOLIDIFYING_TRAITS,
                    false
            )
    );
    public static final RegistryObject<Item> CREATIVE_LIVING_SPONGE = ITEMS.register(
            "creative_living_sponge",
            () -> new LivingSpongePlacementItem(
                    LivingSpongeBlocks.CREATIVE_LIVING_SPONGE.get(),
                    new Item.Properties(),
                    BASELINE_TRAITS,
                    true
            )
    );
    public static final RegistryObject<Item> HYDRO_FRUIT = ITEMS.register(
            "hydro_fruit",
            () -> new Item(new Item.Properties().stacksTo(16))
    );

    private LivingSpongeItems() {
    }

    public static void register(final IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static Optional<ItemStack> placementStackFor(final SpongeTraits traits, final boolean creativeOverrides) {
        if (creativeOverrides && traits.equals(BASELINE_TRAITS)) {
            return Optional.of(CREATIVE_LIVING_SPONGE.get().getDefaultInstance());
        }
        if (!creativeOverrides && traits.equals(BASELINE_TRAITS)) {
            return Optional.of(LIVING_SPONGE.get().getDefaultInstance());
        }
        if (!creativeOverrides && traits.equals(SURFACE_TRAITS)) {
            return Optional.of(SURFACE_LIVING_SPONGE.get().getDefaultInstance());
        }
        if (!creativeOverrides && traits.equals(SOLIDIFYING_TRAITS)) {
            return Optional.of(SOLIDIFYING_LIVING_SPONGE.get().getDefaultInstance());
        }
        if (!creativeOverrides && traits.equals(MAGMA_SURFACE_SOLIDIFYING_TRAITS)) {
            return Optional.of(MAGMA_SURFACE_SOLIDIFYING_LIVING_SPONGE.get().getDefaultInstance());
        }
        return Optional.empty();
    }
}
