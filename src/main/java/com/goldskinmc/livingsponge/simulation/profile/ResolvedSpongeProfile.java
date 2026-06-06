package com.goldskinmc.livingsponge.simulation.profile;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;

public record ResolvedSpongeProfile(
        SpongeTraits traits,
        boolean creativeOverrides
) {
    public int updateIntervalTicks(final LivingSpongeConfig.BalanceValues values) {
        return creativeOverrides ? quarterTicks(values.spread().updateIntervalTicks()) : values.spread().updateIntervalTicks();
    }

    public int reproductionCooldownTicks(final LivingSpongeConfig.BalanceValues values) {
        final int baseCooldown = values.spread().reproductionCooldownTicks();
        return creativeOverrides ? quarterTicks(baseCooldown) : baseCooldown;
    }

    public int radiusCap(final LivingSpongeConfig.BalanceValues values) {
        return creativeOverrides ? RadiusTrait.VAST.radiusCap() : traits.radius().radiusCap();
    }

    public LivingSpongeConfig.Lifecycle lifecycle(final LivingSpongeConfig.BalanceValues values) {
        final LivingSpongeConfig.Lifecycle lifecycle = values.lifecycle();
        if (!creativeOverrides) {
            return lifecycle;
        }

        return new LivingSpongeConfig.Lifecycle(
                quarterTicks(lifecycle.youngDurationTicks()),
                quarterTicks(lifecycle.matureDurationTicks()),
                quarterTicks(lifecycle.oldDurationTicks())
        );
    }

    public boolean usesWaterMedium() {
        return traits.medium() == MediumTrait.WATER;
    }

    public boolean supportsWaterMedium() {
        return creativeOverrides || traits.medium() == MediumTrait.WATER;
    }

    public boolean supportsLavaMedium() {
        return creativeOverrides || traits.medium() == MediumTrait.MAGMA;
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
        return creativeOverrides;
    }

    private static int quarterTicks(final int ticks) {
        return Math.max(1, ticks / 4);
    }
}
