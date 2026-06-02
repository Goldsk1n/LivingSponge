package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;

public enum LivingSpongeLifecycleStage {
    YOUNG,
    MATURE,
    SENESCENT,
    DEAD;

    public static LivingSpongeLifecycleStage fromAgeTicks(final int ageTicks, final LivingSpongeConfig.Lifecycle lifecycle) {
        final int youngEnd = lifecycle.youngDurationTicks();
        if (ageTicks < youngEnd) {
            return YOUNG;
        }

        final int matureEnd = youngEnd + lifecycle.matureDurationTicks();
        if (ageTicks < matureEnd) {
            return MATURE;
        }

        final int senescentEnd = matureEnd + lifecycle.senescentDurationTicks();
        if (ageTicks < senescentEnd) {
            return SENESCENT;
        }

        return DEAD;
    }

    public boolean isAlive() {
        return this != DEAD;
    }
}
