package com.goldskinmc.livingsponge.config;

public final class LivingSpongeBalanceDefaults {
    private LivingSpongeBalanceDefaults() {
    }

    public static final class Spread {
        public static final int UPDATE_INTERVAL_TICKS = 20;
        public static final int MEDIUM_SCAN_RADIUS = 2;
        public static final int MAX_MEDIUM_SAMPLES_PER_UPDATE = 3;
        public static final int REPRODUCTION_COOLDOWN_TICKS = 100;
        public static final int DEATH_TARGET_COOLDOWN_TICKS = 800;

        private Spread() {
        }
    }

    public static final class Lifecycle {
        public static final int YOUNG_DURATION_TICKS = 100;
        public static final int MATURE_DURATION_TICKS = 400;
        public static final int OLD_DURATION_TICKS = 100;

        private Lifecycle() {
        }
    }

    public static final class Pod {
        public static final double DEATH_SPAWN_CHANCE = 0.05D;

        private Pod() {
        }
    }

    public static final class Creative {
        public static final int UPDATE_INTERVAL_TICKS = 10;

        private Creative() {
        }
    }
}
