package com.goldskinmc.livingsponge.config;

import com.goldskinmc.livingsponge.LivingSpongeMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = LivingSpongeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class LivingSpongeConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue SPREAD_UPDATE_INTERVAL_TICKS;
    private static final ForgeConfigSpec.IntValue SPREAD_ABSORB_RADIUS;
    private static final ForgeConfigSpec.IntValue SPREAD_MAX_ABSORBS_PER_UPDATE;
    private static final ForgeConfigSpec.IntValue SPREAD_MAX_COLONY_RADIUS;
    private static final ForgeConfigSpec.IntValue SPREAD_REPRODUCTION_COOLDOWN_TICKS;
    private static final ForgeConfigSpec.DoubleValue SPREAD_REPRODUCTION_BASE_CHANCE;
    private static final ForgeConfigSpec.DoubleValue SPREAD_REPRODUCTION_ENERGY_BONUS_PER_POINT;
    private static final ForgeConfigSpec.DoubleValue SPREAD_REPRODUCTION_ENERGY_BONUS_CAP;
    private static final ForgeConfigSpec.DoubleValue SPREAD_REPRODUCTION_CHANCE_CAP;

    private static final ForgeConfigSpec.IntValue ENERGY_BASE_CAPACITY;
    private static final ForgeConfigSpec.IntValue ENERGY_GAIN_PER_WATER_ABSORBED;
    private static final ForgeConfigSpec.IntValue ENERGY_IDLE_DECAY_PER_UPDATE;
    private static final ForgeConfigSpec.IntValue ENERGY_REPRODUCTION_COST;
    private static final ForgeConfigSpec.IntValue ENERGY_MIN_TO_REPRODUCE;

    private static final ForgeConfigSpec.IntValue LIFECYCLE_YOUNG_DURATION_TICKS;
    private static final ForgeConfigSpec.IntValue LIFECYCLE_MATURE_DURATION_TICKS;
    private static final ForgeConfigSpec.IntValue LIFECYCLE_OLD_DURATION_TICKS;

    private static final ForgeConfigSpec.IntValue FRUIT_PROGRESS_PER_WATER_ABSORBED;
    private static final ForgeConfigSpec.IntValue FRUIT_PROGRESS_NEEDED;
    private static final ForgeConfigSpec.DoubleValue FRUIT_BONUS_DROP_CHANCE;
    private static final ForgeConfigSpec.IntValue FRUIT_SMALL_STACK_SIZE;
    private static final ForgeConfigSpec.IntValue FRUIT_THROW_COOLDOWN_TICKS;
    private static final ForgeConfigSpec.IntValue FRUIT_SMALL_IMPACT_DAMAGE;
    private static final ForgeConfigSpec.IntValue FRUIT_MEDIUM_IMPACT_DAMAGE;
    private static final ForgeConfigSpec.IntValue FRUIT_LARGE_IMPACT_DAMAGE;
    private static final ForgeConfigSpec.IntValue FRUIT_SMALL_BURST_RADIUS;
    private static final ForgeConfigSpec.IntValue FRUIT_MEDIUM_BURST_RADIUS;
    private static final ForgeConfigSpec.IntValue FRUIT_LARGE_BURST_RADIUS;
    private static final ForgeConfigSpec.IntValue FRUIT_SMALL_WATER_DURATION_TICKS;
    private static final ForgeConfigSpec.IntValue FRUIT_MEDIUM_WATER_DURATION_TICKS;
    private static final ForgeConfigSpec.IntValue FRUIT_LARGE_WATER_DURATION_TICKS;
    private static final ForgeConfigSpec.BooleanValue FRUIT_ALLOW_PERMANENT_SOURCES;

    private static final ForgeConfigSpec.BooleanValue CONTAINMENT_LAVA_INSTANT_KILL;
    private static final ForgeConfigSpec.IntValue CONTAINMENT_FIRE_ENERGY_DRAIN_PER_UPDATE;
    private static final ForgeConfigSpec.BooleanValue CONTAINMENT_SALTED_BLOCK_PREVENTS_REPRODUCTION;

    private static final ForgeConfigSpec.IntValue CREATIVE_UPDATE_INTERVAL_TICKS;
    private static final ForgeConfigSpec.BooleanValue CREATIVE_IGNORES_AGE;
    private static final ForgeConfigSpec.BooleanValue CREATIVE_IGNORES_ENERGY;
    private static final ForgeConfigSpec.BooleanValue CREATIVE_IGNORES_SPREAD_LIMITS;
    private static final ForgeConfigSpec.BooleanValue CREATIVE_DROPS_ENABLED;

    public static final ForgeConfigSpec SPEC;

    private static volatile BalanceValues values = defaults();

    static {
        BUILDER.comment("Living Sponge balancing and behavior settings.");

        BUILDER.push("spread");
        SPREAD_UPDATE_INTERVAL_TICKS = BUILDER.comment("Main update interval for natural living sponge behavior.")
                .defineInRange("update_interval_ticks", LivingSpongeBalanceDefaults.Spread.UPDATE_INTERVAL_TICKS, 1, 1200);
        SPREAD_ABSORB_RADIUS = BUILDER.comment("Water scan radius for absorption.")
                .defineInRange("absorb_radius", LivingSpongeBalanceDefaults.Spread.ABSORB_RADIUS, 0, 8);
        SPREAD_MAX_ABSORBS_PER_UPDATE = BUILDER.comment("Maximum water blocks absorbed per update.")
                .defineInRange("max_absorbs_per_update", LivingSpongeBalanceDefaults.Spread.MAX_ABSORBS_PER_UPDATE, 0, 64);
        SPREAD_MAX_COLONY_RADIUS = BUILDER.comment("Maximum distance from colony root for growth.")
                .defineInRange("max_colony_radius", LivingSpongeBalanceDefaults.Spread.MAX_COLONY_RADIUS, 1, 256);
        SPREAD_REPRODUCTION_COOLDOWN_TICKS = BUILDER.comment("Delay between reproduction attempts.")
                .defineInRange("reproduction_cooldown_ticks", LivingSpongeBalanceDefaults.Spread.REPRODUCTION_COOLDOWN_TICKS, 0, 24000);
        SPREAD_REPRODUCTION_BASE_CHANCE = BUILDER.comment("Base chance to reproduce during an eligible update.")
                .defineInRange("reproduction_base_chance", LivingSpongeBalanceDefaults.Spread.REPRODUCTION_BASE_CHANCE, 0.0D, 1.0D);
        SPREAD_REPRODUCTION_ENERGY_BONUS_PER_POINT = BUILDER.comment("Added reproduction chance per energy above min threshold.")
                .defineInRange("reproduction_energy_bonus_per_point", LivingSpongeBalanceDefaults.Spread.REPRODUCTION_ENERGY_BONUS_PER_POINT, 0.0D, 1.0D);
        SPREAD_REPRODUCTION_ENERGY_BONUS_CAP = BUILDER.comment("Maximum added chance contributed by energy surplus.")
                .defineInRange("reproduction_energy_bonus_cap", LivingSpongeBalanceDefaults.Spread.REPRODUCTION_ENERGY_BONUS_CAP, 0.0D, 1.0D);
        SPREAD_REPRODUCTION_CHANCE_CAP = BUILDER.comment("Hard cap for final reproduction chance.")
                .defineInRange("reproduction_chance_cap", LivingSpongeBalanceDefaults.Spread.REPRODUCTION_CHANCE_CAP, 0.0D, 1.0D);
        BUILDER.pop();

        BUILDER.push("energy");
        ENERGY_BASE_CAPACITY = BUILDER.comment("Maximum stored moisture energy.")
                .defineInRange("base_capacity", LivingSpongeBalanceDefaults.Energy.BASE_CAPACITY, 1, 100000);
        ENERGY_GAIN_PER_WATER_ABSORBED = BUILDER.comment("Energy gained per absorbed water block.")
                .defineInRange("gain_per_water_absorbed", LivingSpongeBalanceDefaults.Energy.GAIN_PER_WATER_ABSORBED, 0, 10000);
        ENERGY_IDLE_DECAY_PER_UPDATE = BUILDER.comment("Energy lost every update while alive.")
                .defineInRange("idle_decay_per_update", LivingSpongeBalanceDefaults.Energy.IDLE_DECAY_PER_UPDATE, 0, 10000);
        ENERGY_REPRODUCTION_COST = BUILDER.comment("Energy spent per reproduction.")
                .defineInRange("reproduction_cost", LivingSpongeBalanceDefaults.Energy.REPRODUCTION_COST, 0, 100000);
        ENERGY_MIN_TO_REPRODUCE = BUILDER.comment("Minimum energy required before reproduction can happen.")
                .defineInRange("min_to_reproduce", LivingSpongeBalanceDefaults.Energy.MIN_TO_REPRODUCE, 0, 100000);
        BUILDER.pop();

        BUILDER.push("lifecycle");
        LIFECYCLE_YOUNG_DURATION_TICKS = BUILDER.comment("Young stage duration in ticks.")
                .defineInRange("young_duration_ticks", LivingSpongeBalanceDefaults.Lifecycle.YOUNG_DURATION_TICKS, 1, Integer.MAX_VALUE);
        LIFECYCLE_MATURE_DURATION_TICKS = BUILDER.comment("Mature stage duration in ticks.")
                .defineInRange("mature_duration_ticks", LivingSpongeBalanceDefaults.Lifecycle.MATURE_DURATION_TICKS, 1, Integer.MAX_VALUE);
        LIFECYCLE_OLD_DURATION_TICKS = BUILDER.comment("Old stage duration in ticks before death.")
                .defineInRange("old_duration_ticks", LivingSpongeBalanceDefaults.Lifecycle.OLD_DURATION_TICKS, 1, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("fruit");
        FRUIT_PROGRESS_PER_WATER_ABSORBED = BUILDER.comment("Fruit progress granted by each absorbed water block.")
                .defineInRange("progress_per_water_absorbed", LivingSpongeBalanceDefaults.Fruit.PROGRESS_PER_WATER_ABSORBED, 0, 1000);
        FRUIT_PROGRESS_NEEDED = BUILDER.comment("Progress needed to create one small hydro-fruit.")
                .defineInRange("progress_needed", LivingSpongeBalanceDefaults.Fruit.PROGRESS_NEEDED, 1, Integer.MAX_VALUE);
        FRUIT_BONUS_DROP_CHANCE = BUILDER.comment("Bonus chance for an extra fruit when threshold is met.")
                .defineInRange("bonus_drop_chance", LivingSpongeBalanceDefaults.Fruit.BONUS_DROP_CHANCE, 0.0D, 1.0D);
        FRUIT_SMALL_STACK_SIZE = BUILDER.comment("Maximum stack size for small hydro-fruit.")
                .defineInRange("small_stack_size", LivingSpongeBalanceDefaults.Fruit.SMALL_STACK_SIZE, 1, 64);
        FRUIT_THROW_COOLDOWN_TICKS = BUILDER.comment("Throw cooldown for hydro-fruits.")
                .defineInRange("throw_cooldown_ticks", LivingSpongeBalanceDefaults.Fruit.THROW_COOLDOWN_TICKS, 0, 24000);
        FRUIT_SMALL_IMPACT_DAMAGE = BUILDER.comment("Impact damage for small hydro-fruit.")
                .defineInRange("small_impact_damage", LivingSpongeBalanceDefaults.Fruit.SMALL_IMPACT_DAMAGE, 0, 100);
        FRUIT_MEDIUM_IMPACT_DAMAGE = BUILDER.comment("Impact damage for medium hydro-fruit.")
                .defineInRange("medium_impact_damage", LivingSpongeBalanceDefaults.Fruit.MEDIUM_IMPACT_DAMAGE, 0, 100);
        FRUIT_LARGE_IMPACT_DAMAGE = BUILDER.comment("Impact damage for large hydro-fruit.")
                .defineInRange("large_impact_damage", LivingSpongeBalanceDefaults.Fruit.LARGE_IMPACT_DAMAGE, 0, 100);
        FRUIT_SMALL_BURST_RADIUS = BUILDER.comment("Water burst radius for small hydro-fruit.")
                .defineInRange("small_burst_radius", LivingSpongeBalanceDefaults.Fruit.SMALL_BURST_RADIUS, 0, 32);
        FRUIT_MEDIUM_BURST_RADIUS = BUILDER.comment("Water burst radius for medium hydro-fruit.")
                .defineInRange("medium_burst_radius", LivingSpongeBalanceDefaults.Fruit.MEDIUM_BURST_RADIUS, 0, 32);
        FRUIT_LARGE_BURST_RADIUS = BUILDER.comment("Water burst radius for large hydro-fruit.")
                .defineInRange("large_burst_radius", LivingSpongeBalanceDefaults.Fruit.LARGE_BURST_RADIUS, 0, 32);
        FRUIT_SMALL_WATER_DURATION_TICKS = BUILDER.comment("Temporary water duration for small hydro-fruit.")
                .defineInRange("small_water_duration_ticks", LivingSpongeBalanceDefaults.Fruit.SMALL_WATER_DURATION_TICKS, 0, Integer.MAX_VALUE);
        FRUIT_MEDIUM_WATER_DURATION_TICKS = BUILDER.comment("Temporary water duration for medium hydro-fruit.")
                .defineInRange("medium_water_duration_ticks", LivingSpongeBalanceDefaults.Fruit.MEDIUM_WATER_DURATION_TICKS, 0, Integer.MAX_VALUE);
        FRUIT_LARGE_WATER_DURATION_TICKS = BUILDER.comment("Temporary water duration for large hydro-fruit.")
                .defineInRange("large_water_duration_ticks", LivingSpongeBalanceDefaults.Fruit.LARGE_WATER_DURATION_TICKS, 0, Integer.MAX_VALUE);
        FRUIT_ALLOW_PERMANENT_SOURCES = BUILDER.comment("If true, hydro-fruits may create permanent water sources in survival.")
                .define("allow_permanent_sources", LivingSpongeBalanceDefaults.Fruit.ALLOW_PERMANENT_SOURCES);
        BUILDER.pop();

        BUILDER.push("containment");
        CONTAINMENT_LAVA_INSTANT_KILL = BUILDER.comment("If true, lava contact kills living sponge instantly.")
                .define("lava_instant_kill", LivingSpongeBalanceDefaults.Containment.LAVA_INSTANT_KILL);
        CONTAINMENT_FIRE_ENERGY_DRAIN_PER_UPDATE = BUILDER.comment("Energy drained per update while exposed to fire.")
                .defineInRange("fire_energy_drain_per_update", LivingSpongeBalanceDefaults.Containment.FIRE_ENERGY_DRAIN_PER_UPDATE, 0, 10000);
        CONTAINMENT_SALTED_BLOCK_PREVENTS_REPRODUCTION = BUILDER.comment("If true, salted blocks block neighboring reproduction attempts.")
                .define("salted_block_prevents_reproduction", LivingSpongeBalanceDefaults.Containment.SALTED_BLOCK_PREVENTS_REPRODUCTION);
        BUILDER.pop();

        BUILDER.push("creative");
        CREATIVE_UPDATE_INTERVAL_TICKS = BUILDER.comment("Update interval for creative living sponge variant.")
                .defineInRange("update_interval_ticks", LivingSpongeBalanceDefaults.Creative.UPDATE_INTERVAL_TICKS, 1, 1200);
        CREATIVE_IGNORES_AGE = BUILDER.comment("If true, creative variant ignores lifecycle aging/death.")
                .define("ignores_age", LivingSpongeBalanceDefaults.Creative.IGNORES_AGE);
        CREATIVE_IGNORES_ENERGY = BUILDER.comment("If true, creative variant ignores energy constraints.")
                .define("ignores_energy", LivingSpongeBalanceDefaults.Creative.IGNORES_ENERGY);
        CREATIVE_IGNORES_SPREAD_LIMITS = BUILDER.comment("If true, creative variant ignores spread caps.")
                .define("ignores_spread_limits", LivingSpongeBalanceDefaults.Creative.IGNORES_SPREAD_LIMITS);
        CREATIVE_DROPS_ENABLED = BUILDER.comment("If true, creative variant can still produce hydro-fruits.")
                .define("drops_enabled", LivingSpongeBalanceDefaults.Creative.DROPS_ENABLED);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private LivingSpongeConfig() {
    }

    public static BalanceValues values() {
        return values;
    }

    @SubscribeEvent
    public static void onConfigLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            bake();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            bake();
        }
    }

    private static void bake() {
        values = new BalanceValues(
                new Spread(
                        SPREAD_UPDATE_INTERVAL_TICKS.get(),
                        SPREAD_ABSORB_RADIUS.get(),
                        SPREAD_MAX_ABSORBS_PER_UPDATE.get(),
                        SPREAD_MAX_COLONY_RADIUS.get(),
                        SPREAD_REPRODUCTION_COOLDOWN_TICKS.get(),
                        SPREAD_REPRODUCTION_BASE_CHANCE.get(),
                        SPREAD_REPRODUCTION_ENERGY_BONUS_PER_POINT.get(),
                        SPREAD_REPRODUCTION_ENERGY_BONUS_CAP.get(),
                        SPREAD_REPRODUCTION_CHANCE_CAP.get()
                ),
                new Energy(
                        ENERGY_BASE_CAPACITY.get(),
                        ENERGY_GAIN_PER_WATER_ABSORBED.get(),
                        ENERGY_IDLE_DECAY_PER_UPDATE.get(),
                        ENERGY_REPRODUCTION_COST.get(),
                        ENERGY_MIN_TO_REPRODUCE.get()
                ),
                new Lifecycle(
                        LIFECYCLE_YOUNG_DURATION_TICKS.get(),
                        LIFECYCLE_MATURE_DURATION_TICKS.get(),
                        LIFECYCLE_OLD_DURATION_TICKS.get()
                ),
                new Fruit(
                        FRUIT_PROGRESS_PER_WATER_ABSORBED.get(),
                        FRUIT_PROGRESS_NEEDED.get(),
                        FRUIT_BONUS_DROP_CHANCE.get(),
                        FRUIT_SMALL_STACK_SIZE.get(),
                        FRUIT_THROW_COOLDOWN_TICKS.get(),
                        FRUIT_SMALL_IMPACT_DAMAGE.get(),
                        FRUIT_MEDIUM_IMPACT_DAMAGE.get(),
                        FRUIT_LARGE_IMPACT_DAMAGE.get(),
                        FRUIT_SMALL_BURST_RADIUS.get(),
                        FRUIT_MEDIUM_BURST_RADIUS.get(),
                        FRUIT_LARGE_BURST_RADIUS.get(),
                        FRUIT_SMALL_WATER_DURATION_TICKS.get(),
                        FRUIT_MEDIUM_WATER_DURATION_TICKS.get(),
                        FRUIT_LARGE_WATER_DURATION_TICKS.get(),
                        FRUIT_ALLOW_PERMANENT_SOURCES.get()
                ),
                new Containment(
                        CONTAINMENT_LAVA_INSTANT_KILL.get(),
                        CONTAINMENT_FIRE_ENERGY_DRAIN_PER_UPDATE.get(),
                        CONTAINMENT_SALTED_BLOCK_PREVENTS_REPRODUCTION.get()
                ),
                new Creative(
                        CREATIVE_UPDATE_INTERVAL_TICKS.get(),
                        CREATIVE_IGNORES_AGE.get(),
                        CREATIVE_IGNORES_ENERGY.get(),
                        CREATIVE_IGNORES_SPREAD_LIMITS.get(),
                        CREATIVE_DROPS_ENABLED.get()
                )
        );
    }

    private static BalanceValues defaults() {
        return new BalanceValues(
                new Spread(
                        LivingSpongeBalanceDefaults.Spread.UPDATE_INTERVAL_TICKS,
                        LivingSpongeBalanceDefaults.Spread.ABSORB_RADIUS,
                        LivingSpongeBalanceDefaults.Spread.MAX_ABSORBS_PER_UPDATE,
                        LivingSpongeBalanceDefaults.Spread.MAX_COLONY_RADIUS,
                        LivingSpongeBalanceDefaults.Spread.REPRODUCTION_COOLDOWN_TICKS,
                        LivingSpongeBalanceDefaults.Spread.REPRODUCTION_BASE_CHANCE,
                        LivingSpongeBalanceDefaults.Spread.REPRODUCTION_ENERGY_BONUS_PER_POINT,
                        LivingSpongeBalanceDefaults.Spread.REPRODUCTION_ENERGY_BONUS_CAP,
                        LivingSpongeBalanceDefaults.Spread.REPRODUCTION_CHANCE_CAP
                ),
                new Energy(
                        LivingSpongeBalanceDefaults.Energy.BASE_CAPACITY,
                        LivingSpongeBalanceDefaults.Energy.GAIN_PER_WATER_ABSORBED,
                        LivingSpongeBalanceDefaults.Energy.IDLE_DECAY_PER_UPDATE,
                        LivingSpongeBalanceDefaults.Energy.REPRODUCTION_COST,
                        LivingSpongeBalanceDefaults.Energy.MIN_TO_REPRODUCE
                ),
                new Lifecycle(
                        LivingSpongeBalanceDefaults.Lifecycle.YOUNG_DURATION_TICKS,
                        LivingSpongeBalanceDefaults.Lifecycle.MATURE_DURATION_TICKS,
                        LivingSpongeBalanceDefaults.Lifecycle.OLD_DURATION_TICKS
                ),
                new Fruit(
                        LivingSpongeBalanceDefaults.Fruit.PROGRESS_PER_WATER_ABSORBED,
                        LivingSpongeBalanceDefaults.Fruit.PROGRESS_NEEDED,
                        LivingSpongeBalanceDefaults.Fruit.BONUS_DROP_CHANCE,
                        LivingSpongeBalanceDefaults.Fruit.SMALL_STACK_SIZE,
                        LivingSpongeBalanceDefaults.Fruit.THROW_COOLDOWN_TICKS,
                        LivingSpongeBalanceDefaults.Fruit.SMALL_IMPACT_DAMAGE,
                        LivingSpongeBalanceDefaults.Fruit.MEDIUM_IMPACT_DAMAGE,
                        LivingSpongeBalanceDefaults.Fruit.LARGE_IMPACT_DAMAGE,
                        LivingSpongeBalanceDefaults.Fruit.SMALL_BURST_RADIUS,
                        LivingSpongeBalanceDefaults.Fruit.MEDIUM_BURST_RADIUS,
                        LivingSpongeBalanceDefaults.Fruit.LARGE_BURST_RADIUS,
                        LivingSpongeBalanceDefaults.Fruit.SMALL_WATER_DURATION_TICKS,
                        LivingSpongeBalanceDefaults.Fruit.MEDIUM_WATER_DURATION_TICKS,
                        LivingSpongeBalanceDefaults.Fruit.LARGE_WATER_DURATION_TICKS,
                        LivingSpongeBalanceDefaults.Fruit.ALLOW_PERMANENT_SOURCES
                ),
                new Containment(
                        LivingSpongeBalanceDefaults.Containment.LAVA_INSTANT_KILL,
                        LivingSpongeBalanceDefaults.Containment.FIRE_ENERGY_DRAIN_PER_UPDATE,
                        LivingSpongeBalanceDefaults.Containment.SALTED_BLOCK_PREVENTS_REPRODUCTION
                ),
                new Creative(
                        LivingSpongeBalanceDefaults.Creative.UPDATE_INTERVAL_TICKS,
                        LivingSpongeBalanceDefaults.Creative.IGNORES_AGE,
                        LivingSpongeBalanceDefaults.Creative.IGNORES_ENERGY,
                        LivingSpongeBalanceDefaults.Creative.IGNORES_SPREAD_LIMITS,
                        LivingSpongeBalanceDefaults.Creative.DROPS_ENABLED
                )
        );
    }

    public record BalanceValues(
            Spread spread,
            Energy energy,
            Lifecycle lifecycle,
            Fruit fruit,
            Containment containment,
            Creative creative
    ) {
    }

    public record Spread(
            int updateIntervalTicks,
            int absorbRadius,
            int maxAbsorbsPerUpdate,
            int maxColonyRadius,
            int reproductionCooldownTicks,
            double reproductionBaseChance,
            double reproductionEnergyBonusPerPoint,
            double reproductionEnergyBonusCap,
            double reproductionChanceCap
    ) {
    }

    public record Energy(
            int baseCapacity,
            int gainPerWaterAbsorbed,
            int idleDecayPerUpdate,
            int reproductionCost,
            int minToReproduce
    ) {
    }

    public record Lifecycle(
            int youngDurationTicks,
            int matureDurationTicks,
            int oldDurationTicks
    ) {
    }

    public record Fruit(
            int progressPerWaterAbsorbed,
            int progressNeeded,
            double bonusDropChance,
            int smallStackSize,
            int throwCooldownTicks,
            int smallImpactDamage,
            int mediumImpactDamage,
            int largeImpactDamage,
            int smallBurstRadius,
            int mediumBurstRadius,
            int largeBurstRadius,
            int smallWaterDurationTicks,
            int mediumWaterDurationTicks,
            int largeWaterDurationTicks,
            boolean allowPermanentSources
    ) {
    }

    public record Containment(
            boolean lavaInstantKill,
            int fireEnergyDrainPerUpdate,
            boolean saltedBlockPreventsReproduction
    ) {
    }

    public record Creative(
            int updateIntervalTicks,
            boolean ignoresAge,
            boolean ignoresEnergy,
            boolean ignoresSpreadLimits,
            boolean dropsEnabled
    ) {
    }
}
