package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import net.minecraft.util.StringRepresentable;

public enum LivingSpongeLifecycleStage implements StringRepresentable {
    YOUNG,
    MATURE,
    OLD,
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

        final int oldEnd = matureEnd + lifecycle.oldDurationTicks();
        if (ageTicks < oldEnd) {
            return OLD;
        }

        return DEAD;
    }

    public boolean isAlive() {
        return this != DEAD;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
