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
import java.util.LinkedHashMap;
import java.util.Map;

public final class LivingSpongeItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LivingSpongeMod.MOD_ID);
    private static final Map<PlacementKey, RegistryObject<Item>> PLACEMENT_ITEMS = new LinkedHashMap<>();

    public static final SpongeTraits BASELINE_TRAITS = new SpongeTraits(
            MediumTrait.WATER,
            SpreadTrait.VOLUME,
            OutputTrait.WALL_FORMING,
            RadiusTrait.STANDARD
    );
    public static final SpongeTraits EXPANDED_BASELINE_TRAITS = baselineTraits(RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_BASELINE_TRAITS = baselineTraits(RadiusTrait.VAST);
    public static final SpongeTraits SURFACE_TRAITS = new SpongeTraits(
            MediumTrait.WATER,
            SpreadTrait.SURFACE,
            OutputTrait.WALL_FORMING,
            RadiusTrait.STANDARD
    );
    public static final SpongeTraits EXPANDED_SURFACE_TRAITS = surfaceTraits(RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_SURFACE_TRAITS = surfaceTraits(RadiusTrait.VAST);
    public static final SpongeTraits SOLIDIFYING_TRAITS = new SpongeTraits(
            MediumTrait.WATER,
            SpreadTrait.VOLUME,
            OutputTrait.SOLIDIFYING,
            RadiusTrait.STANDARD
    );
    public static final SpongeTraits EXPANDED_SOLIDIFYING_TRAITS = solidifyingTraits(RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_SOLIDIFYING_TRAITS = solidifyingTraits(RadiusTrait.VAST);
    public static final SpongeTraits MAGMA_SURFACE_SOLIDIFYING_TRAITS = new SpongeTraits(
            MediumTrait.MAGMA,
            SpreadTrait.SURFACE,
            OutputTrait.SOLIDIFYING,
            RadiusTrait.STANDARD
    );
    public static final SpongeTraits EXPANDED_MAGMA_SURFACE_SOLIDIFYING_TRAITS = magmaSurfaceSolidifyingTraits(RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_MAGMA_SURFACE_SOLIDIFYING_TRAITS = magmaSurfaceSolidifyingTraits(RadiusTrait.VAST);

    public static final RegistryObject<Item> LIVING_SPONGE =
            registerPlacementItem("living_sponge", BASELINE_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_LIVING_SPONGE =
            registerPlacementItem("expanded_living_sponge", EXPANDED_BASELINE_TRAITS, false);
    public static final RegistryObject<Item> VAST_LIVING_SPONGE =
            registerPlacementItem("vast_living_sponge", VAST_BASELINE_TRAITS, false);

    public static final RegistryObject<Item> SURFACE_LIVING_SPONGE =
            registerPlacementItem("surface_living_sponge", SURFACE_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_SURFACE_LIVING_SPONGE =
            registerPlacementItem("expanded_surface_living_sponge", EXPANDED_SURFACE_TRAITS, false);
    public static final RegistryObject<Item> VAST_SURFACE_LIVING_SPONGE =
            registerPlacementItem("vast_surface_living_sponge", VAST_SURFACE_TRAITS, false);

    public static final RegistryObject<Item> SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("solidifying_living_sponge", SOLIDIFYING_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("expanded_solidifying_living_sponge", EXPANDED_SOLIDIFYING_TRAITS, false);
    public static final RegistryObject<Item> VAST_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("vast_solidifying_living_sponge", VAST_SOLIDIFYING_TRAITS, false);

    public static final RegistryObject<Item> MAGMA_SURFACE_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("magma_surface_solidifying_living_sponge", MAGMA_SURFACE_SOLIDIFYING_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_MAGMA_SURFACE_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_surface_solidifying_living_sponge", EXPANDED_MAGMA_SURFACE_SOLIDIFYING_TRAITS, false);
    public static final RegistryObject<Item> VAST_MAGMA_SURFACE_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("vast_magma_surface_solidifying_living_sponge", VAST_MAGMA_SURFACE_SOLIDIFYING_TRAITS, false);

    public static final RegistryObject<Item> CREATIVE_LIVING_SPONGE =
            registerPlacementItem("creative_living_sponge", BASELINE_TRAITS, true);
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
        return Optional.ofNullable(PLACEMENT_ITEMS.get(new PlacementKey(traits, creativeOverrides)))
                .map(RegistryObject::get)
                .map(Item::getDefaultInstance);
    }

    private static RegistryObject<Item> registerPlacementItem(
            final String name,
            final SpongeTraits traits,
            final boolean creativeOverrides
    ) {
        final RegistryObject<Item> item = ITEMS.register(
                name,
                () -> new LivingSpongePlacementItem(
                        creativeOverrides ? LivingSpongeBlocks.CREATIVE_LIVING_SPONGE.get() : LivingSpongeBlocks.LIVING_SPONGE.get(),
                        new Item.Properties(),
                        traits,
                        creativeOverrides
                )
        );
        PLACEMENT_ITEMS.put(new PlacementKey(traits, creativeOverrides), item);
        return item;
    }

    private static SpongeTraits baselineTraits(final RadiusTrait radius) {
        return new SpongeTraits(MediumTrait.WATER, SpreadTrait.VOLUME, OutputTrait.WALL_FORMING, radius);
    }

    private static SpongeTraits surfaceTraits(final RadiusTrait radius) {
        return new SpongeTraits(MediumTrait.WATER, SpreadTrait.SURFACE, OutputTrait.WALL_FORMING, radius);
    }

    private static SpongeTraits solidifyingTraits(final RadiusTrait radius) {
        return new SpongeTraits(MediumTrait.WATER, SpreadTrait.VOLUME, OutputTrait.SOLIDIFYING, radius);
    }

    private static SpongeTraits magmaSurfaceSolidifyingTraits(final RadiusTrait radius) {
        return new SpongeTraits(MediumTrait.MAGMA, SpreadTrait.SURFACE, OutputTrait.SOLIDIFYING, radius);
    }

    private record PlacementKey(SpongeTraits traits, boolean creativeOverrides) {
    }
}
