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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class LivingSpongeItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LivingSpongeMod.MOD_ID);
    private static final Map<PlacementKey, Supplier<Item>> PLACEMENT_ITEMS = new LinkedHashMap<>();

    public static final SpongeTraits BASELINE_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_BASELINE_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits HUGE_BASELINE_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.HUGE);
    public static final SpongeTraits FLAT_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.FLAT, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_FLAT_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.FLAT, RadiusTrait.EXPANDED);
    public static final SpongeTraits HUGE_FLAT_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.FLAT, RadiusTrait.HUGE);

    public static final SpongeTraits WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits HUGE_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.HUGE);
    public static final SpongeTraits FLAT_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.WATER, SpreadTrait.FLAT, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_FLAT_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.WATER, SpreadTrait.FLAT, RadiusTrait.EXPANDED);
    public static final SpongeTraits HUGE_FLAT_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.WATER, SpreadTrait.FLAT, RadiusTrait.HUGE);

    public static final SpongeTraits SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits HUGE_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.HUGE);
    public static final SpongeTraits FLAT_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.FLAT, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_FLAT_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.FLAT, RadiusTrait.EXPANDED);
    public static final SpongeTraits HUGE_FLAT_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.FLAT, RadiusTrait.HUGE);

    public static final SpongeTraits MAGMA_TRAITS = neutralTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_TRAITS = neutralTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits HUGE_MAGMA_TRAITS = neutralTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.HUGE);
    public static final SpongeTraits MAGMA_FLAT_TRAITS = neutralTraits(MediumTrait.MAGMA, SpreadTrait.FLAT, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_FLAT_TRAITS = neutralTraits(MediumTrait.MAGMA, SpreadTrait.FLAT, RadiusTrait.EXPANDED);
    public static final SpongeTraits HUGE_MAGMA_FLAT_TRAITS = neutralTraits(MediumTrait.MAGMA, SpreadTrait.FLAT, RadiusTrait.HUGE);

    public static final SpongeTraits MAGMA_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits HUGE_MAGMA_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.HUGE);
    public static final SpongeTraits MAGMA_FLAT_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.MAGMA, SpreadTrait.FLAT, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_FLAT_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.MAGMA, SpreadTrait.FLAT, RadiusTrait.EXPANDED);
    public static final SpongeTraits HUGE_MAGMA_FLAT_WALL_FORMING_TRAITS = wallFormingTraits(MediumTrait.MAGMA, SpreadTrait.FLAT, RadiusTrait.HUGE);

    public static final SpongeTraits MAGMA_VOLUME_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_VOLUME_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.EXPANDED);
    public static final SpongeTraits HUGE_MAGMA_VOLUME_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.MAGMA, SpreadTrait.VOLUME, RadiusTrait.HUGE);
    public static final SpongeTraits MAGMA_FLAT_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.MAGMA, SpreadTrait.FLAT, RadiusTrait.STANDARD);
    public static final SpongeTraits EXPANDED_MAGMA_FLAT_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.MAGMA, SpreadTrait.FLAT, RadiusTrait.EXPANDED);
    public static final SpongeTraits HUGE_MAGMA_FLAT_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.MAGMA, SpreadTrait.FLAT, RadiusTrait.HUGE);

    public static final SpongeTraits CREATIVE_BASELINE_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.HUGE);
    public static final SpongeTraits CREATIVE_FLAT_TRAITS = neutralTraits(MediumTrait.WATER, SpreadTrait.FLAT, RadiusTrait.HUGE);
    public static final SpongeTraits CREATIVE_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.VOLUME, RadiusTrait.HUGE);
    public static final SpongeTraits CREATIVE_FLAT_SOLIDIFYING_TRAITS = solidifyingTraits(MediumTrait.WATER, SpreadTrait.FLAT, RadiusTrait.HUGE);

    public static final Supplier<Item> LIVING_SPONGE =
            registerPlacementItem("living_sponge", BASELINE_TRAITS, false);
    public static final Supplier<Item> EXPANDED_LIVING_SPONGE =
            registerPlacementItem("expanded_living_sponge", EXPANDED_BASELINE_TRAITS, false);
    public static final Supplier<Item> HUGE_LIVING_SPONGE =
            registerPlacementItem("huge_living_sponge", HUGE_BASELINE_TRAITS, false);

    public static final Supplier<Item> FLAT_LIVING_SPONGE =
            registerPlacementItem("flat_living_sponge", FLAT_TRAITS, false);
    public static final Supplier<Item> EXPANDED_FLAT_LIVING_SPONGE =
            registerPlacementItem("expanded_flat_living_sponge", EXPANDED_FLAT_TRAITS, false);
    public static final Supplier<Item> HUGE_FLAT_LIVING_SPONGE =
            registerPlacementItem("huge_flat_living_sponge", HUGE_FLAT_TRAITS, false);

    public static final Supplier<Item> WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("wall_forming_living_sponge", WALL_FORMING_TRAITS, false);
    public static final Supplier<Item> EXPANDED_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("expanded_wall_forming_living_sponge", EXPANDED_WALL_FORMING_TRAITS, false);
    public static final Supplier<Item> HUGE_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("huge_wall_forming_living_sponge", HUGE_WALL_FORMING_TRAITS, false);
    public static final Supplier<Item> FLAT_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("flat_wall_forming_living_sponge", FLAT_WALL_FORMING_TRAITS, false);
    public static final Supplier<Item> EXPANDED_FLAT_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("expanded_flat_wall_forming_living_sponge", EXPANDED_FLAT_WALL_FORMING_TRAITS, false);
    public static final Supplier<Item> HUGE_FLAT_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("huge_flat_wall_forming_living_sponge", HUGE_FLAT_WALL_FORMING_TRAITS, false);

    public static final Supplier<Item> SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("solidifying_living_sponge", SOLIDIFYING_TRAITS, false);
    public static final Supplier<Item> EXPANDED_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("expanded_solidifying_living_sponge", EXPANDED_SOLIDIFYING_TRAITS, false);
    public static final Supplier<Item> HUGE_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("huge_solidifying_living_sponge", HUGE_SOLIDIFYING_TRAITS, false);
    public static final Supplier<Item> FLAT_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("flat_solidifying_living_sponge", FLAT_SOLIDIFYING_TRAITS, false);
    public static final Supplier<Item> EXPANDED_FLAT_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("expanded_flat_solidifying_living_sponge", EXPANDED_FLAT_SOLIDIFYING_TRAITS, false);
    public static final Supplier<Item> HUGE_FLAT_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("huge_flat_solidifying_living_sponge", HUGE_FLAT_SOLIDIFYING_TRAITS, false);

    public static final Supplier<Item> MAGMA_LIVING_SPONGE =
            registerPlacementItem("magma_living_sponge", MAGMA_TRAITS, false);
    public static final Supplier<Item> EXPANDED_MAGMA_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_living_sponge", EXPANDED_MAGMA_TRAITS, false);
    public static final Supplier<Item> HUGE_MAGMA_LIVING_SPONGE =
            registerPlacementItem("huge_magma_living_sponge", HUGE_MAGMA_TRAITS, false);
    public static final Supplier<Item> MAGMA_FLAT_LIVING_SPONGE =
            registerPlacementItem("magma_flat_living_sponge", MAGMA_FLAT_TRAITS, false);
    public static final Supplier<Item> EXPANDED_MAGMA_FLAT_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_flat_living_sponge", EXPANDED_MAGMA_FLAT_TRAITS, false);
    public static final Supplier<Item> HUGE_MAGMA_FLAT_LIVING_SPONGE =
            registerPlacementItem("huge_magma_flat_living_sponge", HUGE_MAGMA_FLAT_TRAITS, false);

    public static final Supplier<Item> MAGMA_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("magma_wall_forming_living_sponge", MAGMA_WALL_FORMING_TRAITS, false);
    public static final Supplier<Item> EXPANDED_MAGMA_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_wall_forming_living_sponge", EXPANDED_MAGMA_WALL_FORMING_TRAITS, false);
    public static final Supplier<Item> HUGE_MAGMA_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("huge_magma_wall_forming_living_sponge", HUGE_MAGMA_WALL_FORMING_TRAITS, false);
    public static final Supplier<Item> MAGMA_FLAT_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("magma_flat_wall_forming_living_sponge", MAGMA_FLAT_WALL_FORMING_TRAITS, false);
    public static final Supplier<Item> EXPANDED_MAGMA_FLAT_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_flat_wall_forming_living_sponge", EXPANDED_MAGMA_FLAT_WALL_FORMING_TRAITS, false);
    public static final Supplier<Item> HUGE_MAGMA_FLAT_WALL_FORMING_LIVING_SPONGE =
            registerPlacementItem("huge_magma_flat_wall_forming_living_sponge", HUGE_MAGMA_FLAT_WALL_FORMING_TRAITS, false);

    public static final Supplier<Item> MAGMA_VOLUME_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("magma_volume_solidifying_living_sponge", MAGMA_VOLUME_SOLIDIFYING_TRAITS, false);
    public static final Supplier<Item> EXPANDED_MAGMA_VOLUME_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_volume_solidifying_living_sponge", EXPANDED_MAGMA_VOLUME_SOLIDIFYING_TRAITS, false);
    public static final Supplier<Item> HUGE_MAGMA_VOLUME_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("huge_magma_volume_solidifying_living_sponge", HUGE_MAGMA_VOLUME_SOLIDIFYING_TRAITS, false);
    public static final Supplier<Item> MAGMA_FLAT_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("magma_flat_solidifying_living_sponge", MAGMA_FLAT_SOLIDIFYING_TRAITS, false);
    public static final Supplier<Item> EXPANDED_MAGMA_FLAT_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("expanded_magma_flat_solidifying_living_sponge", EXPANDED_MAGMA_FLAT_SOLIDIFYING_TRAITS, false);
    public static final Supplier<Item> HUGE_MAGMA_FLAT_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("huge_magma_flat_solidifying_living_sponge", HUGE_MAGMA_FLAT_SOLIDIFYING_TRAITS, false);

    public static final Supplier<Item> CREATIVE_LIVING_SPONGE =
            registerPlacementItem("creative_living_sponge", CREATIVE_BASELINE_TRAITS, true);
    public static final Supplier<Item> CREATIVE_FLAT_LIVING_SPONGE =
            registerPlacementItem("creative_flat_living_sponge", CREATIVE_FLAT_TRAITS, true);
    public static final Supplier<Item> CREATIVE_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("creative_solidifying_living_sponge", CREATIVE_SOLIDIFYING_TRAITS, true);
    public static final Supplier<Item> CREATIVE_FLAT_SOLIDIFYING_LIVING_SPONGE =
            registerPlacementItem("creative_flat_solidifying_living_sponge", CREATIVE_FLAT_SOLIDIFYING_TRAITS, true);

    private LivingSpongeItems() {
    }

    public static void register(final IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static Optional<ItemStack> placementStackFor(final SpongeTraits traits, final boolean creativeOverrides) {
        return Optional.ofNullable(PLACEMENT_ITEMS.get(new PlacementKey(traits, creativeOverrides)))
                .map(Supplier::get)
                .map(Item::getDefaultInstance);
    }

    private static Supplier<Item> registerPlacementItem(
            final String name,
            final SpongeTraits traits,
            final boolean creativeOverrides
    ) {
        final Supplier<Item> item = ITEMS.register(
                name,
                () -> new LivingSpongePlacementItem(
                        creativeOverrides ? LivingSpongeBlocks.CREATIVE_MATURE_LIVING_SPONGE.get() : LivingSpongeBlocks.MATURE_LIVING_SPONGE.get(),
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

    private record PlacementKey(SpongeTraits traits, boolean creativeOverrides) {
    }
}


