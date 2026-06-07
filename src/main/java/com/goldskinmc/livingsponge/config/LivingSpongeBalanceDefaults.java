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
        public static final boolean FLAT_USES_DIAGONALS = false;

        private Spread() {
        }
    }

    public static final class Lifecycle {
        public static final int YOUNG_DURATION_TICKS = 100;
        public static final int MATURE_DURATION_TICKS = 400;
        public static final int OLD_DURATION_TICKS = 100;
        public static final boolean PLACED_SPONGES_START_MATURE = true;

        private Lifecycle() {
        }
    }

    public static final class Radius {
        public static final int STANDARD = 8;
        public static final int EXPANDED = 16;
        public static final int VAST = 256;

        private Radius() {
        }
    }

    public static final class Creative {
        public static final int SPEED_MULTIPLIER = 4;
        public static final boolean FORCE_VAST_RADIUS = true;
        public static final boolean IGNORE_ENVIRONMENT_DEATH = true;
        public static final boolean SUPPORTS_WATER = true;
        public static final boolean SUPPORTS_LAVA = true;

        private Creative() {
        }
    }
}
