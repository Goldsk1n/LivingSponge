package com.goldskinmc.livingsponge.simulation.profile;

import net.minecraft.nbt.CompoundTag;

import java.util.Locale;
import java.util.Objects;

public record SpongeTraits(
        MediumTrait medium,
        SpreadTrait spread,
        OutputTrait output,
        RadiusTrait radius
) {
    private static final String MEDIUM_KEY = "Medium";
    private static final String SPREAD_KEY = "Spread";
    private static final String OUTPUT_KEY = "Output";
    private static final String RADIUS_KEY = "Radius";

    public static final SpongeTraits DEFAULT = new SpongeTraits(
            MediumTrait.WATER,
            SpreadTrait.VOLUME,
            OutputTrait.WALL_FORMING,
            RadiusTrait.STANDARD
    );

    public SpongeTraits {
        medium = Objects.requireNonNull(medium, "medium");
        spread = Objects.requireNonNull(spread, "spread");
        output = Objects.requireNonNull(output, "output");
        radius = Objects.requireNonNull(radius, "radius");
    }

    public CompoundTag save() {
        final CompoundTag tag = new CompoundTag();
        tag.putString(MEDIUM_KEY, medium.name());
        tag.putString(SPREAD_KEY, spread.name());
        tag.putString(OUTPUT_KEY, output.name());
        tag.putString(RADIUS_KEY, radius.name());
        return tag;
    }

    public static SpongeTraits load(final CompoundTag tag) {
        return new SpongeTraits(
                parseEnum(tag.getString(MEDIUM_KEY), MediumTrait.WATER, MediumTrait.class),
                parseEnum(tag.getString(SPREAD_KEY), SpreadTrait.VOLUME, SpreadTrait.class),
                parseEnum(tag.getString(OUTPUT_KEY), OutputTrait.WALL_FORMING, OutputTrait.class),
                parseEnum(tag.getString(RADIUS_KEY), RadiusTrait.STANDARD, RadiusTrait.class)
        );
    }

    private static <T extends Enum<T>> T parseEnum(final String rawValue, final T fallback, final Class<T> type) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }

        try {
            return Enum.valueOf(type, rawValue.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }
}
