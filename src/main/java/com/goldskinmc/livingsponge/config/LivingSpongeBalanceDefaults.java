package com.goldskinmc.livingsponge.config;

public final class LivingSpongeBalanceDefaults {
    private LivingSpongeBalanceDefaults() {
    }

    public static final class Spread {
        public static final int UPDATE_INTERVAL_TICKS = 20;
        public static final int ABSORB_RADIUS = 2;
        public static final int MAX_ABSORBS_PER_UPDATE = 3;
        public static final int MAX_COLONY_RADIUS = 8;
        public static final int REPRODUCTION_COOLDOWN_TICKS = 100;
        public static final double REPRODUCTION_BASE_CHANCE = 0.12D;
        public static final double REPRODUCTION_ENERGY_BONUS_PER_POINT = 0.0015D;
        public static final double REPRODUCTION_ENERGY_BONUS_CAP = 0.18D;
        public static final double REPRODUCTION_CHANCE_CAP = 0.30D;

        private Spread() {
        }
    }

    public static final class Energy {
        public static final int BASE_CAPACITY = 120;
        public static final int GAIN_PER_WATER_ABSORBED = 8;
        public static final int IDLE_DECAY_PER_UPDATE = 1;
        public static final int REPRODUCTION_COST = 45;
        public static final int MIN_TO_REPRODUCE = 70;

        private Energy() {
        }
    }

    public static final class Lifecycle {
        public static final int YOUNG_DURATION_TICKS = 100;
        public static final int MATURE_DURATION_TICKS = 400;
        public static final int OLD_DURATION_TICKS = 100;
        public static final double FRONTIER_REMAINS_CHANCE = 1.0D;
        public static final double NON_FRONTIER_HYDRO_BLOCK_CHANCE = 0.05D;

        private Lifecycle() {
        }
    }

    public static final class Fruit {
        public static final int PROGRESS_PER_WATER_ABSORBED = 1;
        public static final int PROGRESS_NEEDED = 28;
        public static final double BONUS_DROP_CHANCE = 0.04D;
        public static final int SMALL_STACK_SIZE = 16;
        public static final int THROW_COOLDOWN_TICKS = 12;

        public static final int SMALL_IMPACT_DAMAGE = 2;
        public static final int MEDIUM_IMPACT_DAMAGE = 3;
        public static final int LARGE_IMPACT_DAMAGE = 4;

        public static final int SMALL_BURST_RADIUS = 1;
        public static final int MEDIUM_BURST_RADIUS = 2;
        public static final int LARGE_BURST_RADIUS = 3;

        public static final int SMALL_WATER_DURATION_TICKS = 80;
        public static final int MEDIUM_WATER_DURATION_TICKS = 120;
        public static final int LARGE_WATER_DURATION_TICKS = 160;

        public static final boolean ALLOW_PERMANENT_SOURCES = false;

        private Fruit() {
        }
    }

    public static final class Containment {
        public static final boolean LAVA_INSTANT_KILL = true;
        public static final int FIRE_ENERGY_DRAIN_PER_UPDATE = 0;
        public static final boolean SALTED_BLOCK_PREVENTS_REPRODUCTION = true;

        private Containment() {
        }
    }

    public static final class Creative {
        public static final int UPDATE_INTERVAL_TICKS = 10;
        public static final boolean IGNORES_AGE = true;
        public static final boolean IGNORES_ENERGY = true;
        public static final boolean IGNORES_SPREAD_LIMITS = true;
        public static final boolean DROPS_ENABLED = false;

        private Creative() {
        }
    }
}
