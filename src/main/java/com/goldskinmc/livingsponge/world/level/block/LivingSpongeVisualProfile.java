package com.goldskinmc.livingsponge.world.level.block;

import com.goldskinmc.livingsponge.simulation.profile.OutputTrait;
import com.goldskinmc.livingsponge.simulation.profile.SpongeTraits;
import net.minecraft.util.StringRepresentable;

public enum LivingSpongeVisualProfile implements StringRepresentable {
    WATER_NEUTRAL("water_neutral"),
    WATER_WALL_FORMING("water_wall_forming"),
    WATER_SOLIDIFYING("water_solidifying"),
    MAGMA_NEUTRAL("magma_neutral"),
    MAGMA_WALL_FORMING("magma_wall_forming"),
    MAGMA_SOLIDIFYING("magma_solidifying"),
    CREATIVE_NEUTRAL("creative_neutral"),
    CREATIVE_SOLIDIFYING("creative_solidifying");

    private final String serializedName;

    LivingSpongeVisualProfile(final String serializedName) {
        this.serializedName = serializedName;
    }

    public static LivingSpongeVisualProfile from(final SpongeTraits traits, final boolean creativeOverrides) {
        if (creativeOverrides) {
            return traits.output() == OutputTrait.SOLIDIFYING
                    ? CREATIVE_SOLIDIFYING
                    : CREATIVE_NEUTRAL;
        }

        return switch (traits.medium()) {
            case WATER -> switch (traits.output()) {
                case NEUTRAL -> WATER_NEUTRAL;
                case WALL_FORMING -> WATER_WALL_FORMING;
                case SOLIDIFYING -> WATER_SOLIDIFYING;
            };
            case MAGMA -> switch (traits.output()) {
                case NEUTRAL -> MAGMA_NEUTRAL;
                case WALL_FORMING -> MAGMA_WALL_FORMING;
                case SOLIDIFYING -> MAGMA_SOLIDIFYING;
            };
        };
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
