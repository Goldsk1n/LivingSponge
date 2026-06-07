package com.goldskinmc.livingsponge.simulation.profile;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;

public enum RadiusTrait {
    STANDARD,
    EXPANDED,
    HUGE;

    public int radiusCap() {
        return radiusCap(LivingSpongeConfig.values());
    }

    public int radiusCap(final LivingSpongeConfig.BalanceValues values) {
        return switch (this) {
            case STANDARD -> values.radius().standard();
            case EXPANDED -> values.radius().expanded();
            case HUGE -> values.radius().huge();
        };
    }
}
