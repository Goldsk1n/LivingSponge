package com.goldskinmc.livingsponge.simulation.profile;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;

public record ResolvedSpongeProfile(
        SpongeTraits traits,
        boolean creativeOverrides
) {
    public int updateIntervalTicks(final LivingSpongeConfig.BalanceValues values) {
        return creativeOverrides ? values.creative().updateIntervalTicks() : values.spread().updateIntervalTicks();
    }

    public int reproductionCooldownTicks(final LivingSpongeConfig.BalanceValues values) {
        final int baseCooldown = values.spread().reproductionCooldownTicks();
        return creativeOverrides ? halveTicks(baseCooldown) : baseCooldown;
    }

    public int radiusCap(final LivingSpongeConfig.BalanceValues values) {
        return traits.radius().radiusCap();
    }

    public LivingSpongeConfig.Lifecycle lifecycle(final LivingSpongeConfig.BalanceValues values) {
        final LivingSpongeConfig.Lifecycle lifecycle = values.lifecycle();
        if (!creativeOverrides) {
            return lifecycle;
        }

        return new LivingSpongeConfig.Lifecycle(
                halveTicks(lifecycle.youngDurationTicks()),
                halveTicks(lifecycle.matureDurationTicks()),
                halveTicks(lifecycle.oldDurationTicks())
        );
    }

    public boolean usesWaterMedium() {
        return traits.medium() == MediumTrait.WATER;
    }

    public boolean isFlatSpread() {
        return traits.spread() == SpreadTrait.FLAT;
    }

    public boolean isNeutralOutput() {
        return traits.output() == OutputTrait.NEUTRAL;
    }

    public boolean isFruitingOutput() {
        return traits.output() == OutputTrait.FRUITING;
    }

    public boolean isWallFormingOutput() {
        return traits.output() == OutputTrait.WALL_FORMING;
    }

    public boolean isSolidifyingOutput() {
        return traits.output() == OutputTrait.SOLIDIFYING;
    }

    private static int halveTicks(final int ticks) {
        return Math.max(1, ticks / 2);
    }
}
