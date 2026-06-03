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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class LivingSpongeItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LivingSpongeMod.MOD_ID);
    private static final Map<PlacementKey, RegistryObject<Item>> PLACEMENT_ITEMS = new LinkedHashMap<>();

    public static final SpongeTraits BASELINE_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_BASELINE_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_BASELINE_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.VAST);
    public static final SpongeTraits SURFACE_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.SURFACE, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_SURFACE_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.SURFACE, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_SURFACE_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.SURFACE, RadiusTrait.VAST);

    public static final SpongeTraits WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.VAST);
    public static final SpongeTraits SURFACE_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.WATER, SpreadTrait.SURFACE, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_SURFACE_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.WATER, SpreadTrait.SURFACE, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_SURFACE_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.WATER, SpreadTrait.SURFACE, RadiusTrait.VAST);

    public static final SpongeTraits SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.VAST);
    public static final SpongeTraits SURFACE_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.SURFACE, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_SURFACE_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.SURFACE, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_SURFACE_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.SURFACE, RadiusTrait.VAST);

    public static final SpongeTraits FRUITING_TRAITS = fruitingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_FRUITING_TRAITS = fruitingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_FRUITING_TRAITS = fruitingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.VAST);
    public static final SpongeTraits SURFACE_FRUITING_TRAITS = fruitingTraits(MediumTrait.WATER, SpreadTrait.SURFACE, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_SURFACE_FRUITING_TRAITS = fruitingTraits(MediumTrait.WATER, SpreadTrait.SURFACE, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_SURFACE_FRUITING_TRAITS = fruitingTraits(MediumTrait.WATER, SpreadTrait.SURFACE, RadiusTrait.VAST);
    public static final SpongeTraits MAGMA_FRUITING_TRAITS = fruitingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_FRUITING_TRAITS = fruitingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_MAGMA_FRUITING_TRAITS = fruitingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.VAST);
    public static final SpongeTraits MAGMA_SURFACE_FRUITING_TRAITS = fruitingTraits(MediumTrait.MAGMA, SpreadTrait.SURFACE, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_SURFACE_FRUITING_TRAITS = fruitingTraits(MediumTrait.MAGMA, SpreadTrait.SURFACE, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_MAGMA_SURFACE_FRUITING_TRAITS = fruitingTraits(MediumTrait.MAGMA, SpreadTrait.SURFACE, RadiusTrait.VAST);

    public static final SpongeTraits MAGMA_TRAITS = neutralTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_TRAITS = neutralTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_MAGMA_TRAITS = neutralTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.VAST);
    public static final SpongeTraits MAGMA_SURFACE_TRAITS = neutralTraits(MediumTrait.MAGMA, SpreadTrait.SURFACE, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_SURFACE_TRAITS = neutralTraits(MediumTrait.MAGMA, SpreadTrait.SURFACE, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_MAGMA_SURFACE_TRAITS = neutralTraits(MediumTrait.MAGMA, SpreadTrait.SURFACE, RadiusTrait.VAST);

    public static final SpongeTraits MAGMA_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_MAGMA_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.VAST);
    public static final SpongeTraits MAGMA_SURFACE_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.MAGMA, SpreadTrait.SURFACE, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_SURFACE_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.MAGMA, SpreadTrait.SURFACE, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_MAGMA_SURFACE_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.MAGMA, SpreadTrait.SURFACE, RadiusTrait.VAST);

    public static final SpongeTraits MAGMA_VOLUME_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_VOLUME_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_MAGMA_VOLUME_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.VAST);
    public static final SpongeTraits MAGMA_SURFACE_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.MAGMA, SpreadTrait.SURFACE, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_SURFACE_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.MAGMA, SpreadTrait.SURFACE, RadiusTrait.EXPANDED);
    public static final SpongeTraits VAST_MAGMA_SURFACE_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.MAGMA, SpreadTrait.SURFACE, RadiusTrait.VAST);

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

    public static final RegistryObject<Item> WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("wall_forming_living_sponge", WALL_FORMING_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("expanded_wall_forming_living_sponge", EXPANDED_WALL_FORMING_TRAITS, false);
    public static final RegistryObject<Item> VAST_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("vast_wall_forming_living_sponge", VAST_WALL_FORMING_TRAITS, false);
    public static final RegistryObject<Item> SURFACE_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("surface_wall_forming_living_sponge", SURFACE_WALL_FORMING_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_SURFACE_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("expanded_surface_wall_forming_living_sponge", EXPANDED_SURFACE_WALL_FORMING_TRAITS, false);
    public static final RegistryObject<Item> VAST_SURFACE_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("vast_surface_wall_forming_living_sponge", VAST_SURFACE_WALL_FORMING_TRAITS, false);

    public static final RegistryObject<Item> SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("solidifying_living_sponge", SOLIDIFYING_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("expanded_solidifying_living_sponge", EXPANDED_SOLIDIFYING_TRAITS, false);
    public static final RegistryObject<Item> VAST_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("vast_solidifying_living_sponge", VAST_SOLIDIFYING_TRAITS, false);
    public static final RegistryObject<Item> SURFACE_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("surface_solidifying_living_sponge", SURFACE_SOLIDIFYING_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_SURFACE_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("expanded_surface_solidifying_living_sponge", EXPANDED_SURFACE_SOLIDIFYING_TRAITS, false);
    public static final RegistryObject<Item> VAST_SURFACE_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("vast_surface_solidifying_living_sponge", VAST_SURFACE_SOLIDIFYING_TRAITS, false);

    public static final RegistryObject<Item> FRUITING_LIVING_SPONGE =
            registerPlacementItem("fruiting_living_sponge", FRUITING_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_FRUITING_LIVING_SPONGE =
            registerPlacementItem("expanded_fruiting_living_sponge", EXPANDED_FRUITING_TRAITS, false);
    public static final RegistryObject<Item> VAST_FRUITING_LIVING_SPONGE =
            registerPlacementItem("vast_fruiting_living_sponge", VAST_FRUITING_TRAITS, false);
    public static final RegistryObject<Item> SURFACE_FRUITING_LIVING_SPONGE =
            registerPlacementItem("surface_fruiting_living_sponge", SURFACE_FRUITING_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_SURFACE_FRUITING_LIVING_SPONGE =
            registerPlacementItem("expanded_surface_fruiting_living_sponge", EXPANDED_SURFACE_FRUITING_TRAITS, false);
    public static final RegistryObject<Item> VAST_SURFACE_FRUITING_LIVING_SPONGE =
            registerPlacementItem("vast_surface_fruiting_living_sponge", VAST_SURFACE_FRUITING_TRAITS, false);
    public static final RegistryObject<Item> MAGMA_FRUITING_LIVING_SPONGE =
            registerPlacementItem("magma_fruiting_living_sponge", MAGMA_FRUITING_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_MAGMA_FRUITING_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_fruiting_living_sponge", EXPANDED_MAGMA_FRUITING_TRAITS, false);
    public static final RegistryObject<Item> VAST_MAGMA_FRUITING_LIVING_SPONGE =
            registerPlacementItem("vast_magma_fruiting_living_sponge", VAST_MAGMA_FRUITING_TRAITS, false);
    public static final RegistryObject<Item> MAGMA_SURFACE_FRUITING_LIVING_SPONGE =
            registerPlacementItem("magma_surface_fruiting_living_sponge", MAGMA_SURFACE_FRUITING_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_MAGMA_SURFACE_FRUITING_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_surface_fruiting_living_sponge", EXPANDED_MAGMA_SURFACE_FRUITING_TRAITS, false);
    public static final RegistryObject<Item> VAST_MAGMA_SURFACE_FRUITING_LIVING_SPONGE =
            registerPlacementItem("vast_magma_surface_fruiting_living_sponge", VAST_MAGMA_SURFACE_FRUITING_TRAITS, false);

    public static final RegistryObject<Item> MAGMA_LIVING_SPONGE =
            registerPlacementItem("magma_living_sponge", MAGMA_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_MAGMA_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_living_sponge", EXPANDED_MAGMA_TRAITS, false);
    public static final RegistryObject<Item> VAST_MAGMA_LIVING_SPONGE =
            registerPlacementItem("vast_magma_living_sponge", VAST_MAGMA_TRAITS, false);
    public static final RegistryObject<Item> MAGMA_SURFACE_LIVING_SPONGE =
            registerPlacementItem("magma_surface_living_sponge", MAGMA_SURFACE_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_MAGMA_SURFACE_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_surface_living_sponge", EXPANDED_MAGMA_SURFACE_TRAITS, false);
    public static final RegistryObject<Item> VAST_MAGMA_SURFACE_LIVING_SPONGE =
            registerPlacementItem("vast_magma_surface_living_sponge", VAST_MAGMA_SURFACE_TRAITS, false);

    public static final RegistryObject<Item> MAGMA_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("magma_wall_forming_living_sponge", MAGMA_WALL_FORMING_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_MAGMA_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_wall_forming_living_sponge", EXPANDED_MAGMA_WALL_FORMING_TRAITS, false);
    public static final RegistryObject<Item> VAST_MAGMA_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("vast_magma_wall_forming_living_sponge", VAST_MAGMA_WALL_FORMING_TRAITS, false);
    public static final RegistryObject<Item> MAGMA_SURFACE_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("magma_surface_wall_forming_living_sponge", MAGMA_SURFACE_WALL_FORMING_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_MAGMA_SURFACE_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_surface_wall_forming_living_sponge", EXPANDED_MAGMA_SURFACE_WALL_FORMING_TRAITS, false);
    public static final RegistryObject<Item> VAST_MAGMA_SURFACE_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("vast_magma_surface_wall_forming_living_sponge", VAST_MAGMA_SURFACE_WALL_FORMING_TRAITS, false);

    public static final RegistryObject<Item> MAGMA_VOLUME_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("magma_volume_solidifying_living_sponge", MAGMA_VOLUME_SOLIDIFYING_TRAITS, false);
    public static final RegistryObject<Item> EXPANDED_MAGMA_VOLUME_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_volume_solidifying_living_sponge", EXPANDED_MAGMA_VOLUME_SOLIDIFYING_TRAITS, false);
    public static final RegistryObject<Item> VAST_MAGMA_VOLUME_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("vast_magma_volume_solidifying_living_sponge", VAST_MAGMA_VOLUME_SOLIDIFYING_TRAITS, false);
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

    private static SpongeTraits neutralTraits(final MediumTrait medium, final SpreadTrait spread, final RadiusTrait radius) {
        return new SpongeTraits(medium, spread, OutputTrait.NEUTRAL, radius);
    }

    private static SpongeTraits wallFormingTraits(final MediumTrait medium, final SpreadTrait spread, final RadiusTrait radius) {
        return new SpongeTraits(medium, spread, OutputTrait.WALL_FORMING, radius);
    }

    private static SpongeTraits solidifyingTraits(final MediumTrait medium, final SpreadTrait spread, final RadiusTrait radius) {
        return new SpongeTraits(medium, spread, OutputTrait.SOLIDIFYING, radius);
    }

    private static SpongeTraits fruitingTraits(final MediumTrait medium, final SpreadTrait spread, final RadiusTrait radius) {
        return new SpongeTraits(medium, spread, OutputTrait.FRUITING, radius);
    }

    private record PlacementKey(SpongeTraits traits, boolean creativeOverrides) {
    }
}
