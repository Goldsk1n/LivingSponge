package com.goldskinmc.livingsponge.simulation.profile;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;

public record ResolvedSpongeProfile(
        SpongeTraits traits,
        boolean creativeOverrides
) {
    public int updateIntervalTicks(final LivingSpongeConfig.BalanceValues values) {
        return creativeOverrides ? scaledTicks(values.spread().updateIntervalTicks(), values.creative().speedMultiplier()) : values.spread().updateIntervalTicks();
    }

    public int reproductionCooldownTicks(final LivingSpongeConfig.BalanceValues values) {
        final int baseCooldown = values.spread().reproductionCooldownTicks();
        return creativeOverrides ? scaledTicks(baseCooldown, values.creative().speedMultiplier()) : baseCooldown;
    }

    public int radiusCap(final LivingSpongeConfig.BalanceValues values) {
        if (creativeOverrides && values.creative().forceHugeRadius()) {
            return RadiusTrait.HUGE.radiusCap(values);
        }
        return traits.radius().radiusCap(values);
    }

    public LivingSpongeConfig.Lifecycle lifecycle(final LivingSpongeConfig.BalanceValues values) {
        final LivingSpongeConfig.Lifecycle lifecycle = values.lifecycle();
        if (!creativeOverrides) {
            return lifecycle;
        }

        return new LivingSpongeConfig.Lifecycle(
                scaledTicks(lifecycle.youngDurationTicks(), values.creative().speedMultiplier()),
                scaledTicks(lifecycle.matureDurationTicks(), values.creative().speedMultiplier()),
                scaledTicks(lifecycle.oldDurationTicks(), values.creative().speedMultiplier()),
                lifecycle.placedSpongesStartMature()
        );
    }

    public boolean usesWaterMedium() {
        if (creativeOverrides) {
            return supportsWaterMedium() && !supportsLavaMedium();
        }
        return traits.medium() == MediumTrait.WATER;
    }

    public boolean supportsWaterMedium() {
        if (creativeOverrides) {
            return LivingSpongeConfig.values().creative().supportsWater();
        }
        return traits.medium() == MediumTrait.WATER;
    }

    public boolean supportsLavaMedium() {
        if (creativeOverrides) {
            return LivingSpongeConfig.values().creative().supportsLava();
        }
        return traits.medium() == MediumTrait.MAGMA;
    }

    public boolean isFlatSpread() {
        return traits.spread() == SpreadTrait.FLAT;
    }

    public boolean isNeutralOutput() {
        return traits.output() == OutputTrait.NEUTRAL;
    }

    public boolean isWallFormingOutput() {
        return traits.output() == OutputTrait.WALL_FORMING;
    }

    public boolean isSolidifyingOutput() {
        return traits.output() == OutputTrait.SOLIDIFYING;
    }

    public boolean ignoresEnvironmentDeath() {
        return creativeOverrides && LivingSpongeConfig.values().creative().ignoreEnvironmentDeath();
    }

    private static int scaledTicks(final int ticks, final int speedMultiplier) {
        return Math.max(1, ticks / Math.max(1, speedMultiplier));
    }
}
