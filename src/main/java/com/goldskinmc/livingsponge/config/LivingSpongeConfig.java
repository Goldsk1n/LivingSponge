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
    private static final ForgeConfigSpec.IntValue SPREAD_MEDIUM_SCAN_RADIUS;
    private static final ForgeConfigSpec.IntValue SPREAD_MAX_MEDIUM_SAMPLES_PER_UPDATE;
    private static final ForgeConfigSpec.IntValue SPREAD_REPRODUCTION_COOLDOWN_TICKS;
    private static final ForgeConfigSpec.IntValue SPREAD_DEATH_TARGET_COOLDOWN_TICKS;

    private static final ForgeConfigSpec.IntValue LIFECYCLE_YOUNG_DURATION_TICKS;
    private static final ForgeConfigSpec.IntValue LIFECYCLE_MATURE_DURATION_TICKS;
    private static final ForgeConfigSpec.IntValue LIFECYCLE_OLD_DURATION_TICKS;

    private static final ForgeConfigSpec.IntValue CREATIVE_UPDATE_INTERVAL_TICKS;

    public static final ForgeConfigSpec SPEC;

    private static volatile BalanceValues values = defaults();

    static {
        BUILDER.comment("Living Sponge balancing and behavior settings.");

        BUILDER.push("spread");
        SPREAD_UPDATE_INTERVAL_TICKS = BUILDER.comment("Main update interval for natural living sponge behavior.")
                .defineInRange("update_interval_ticks", LivingSpongeBalanceDefaults.Spread.UPDATE_INTERVAL_TICKS, 1, 1200);
        SPREAD_MEDIUM_SCAN_RADIUS = BUILDER.comment("Radius used to sample nearby valid growth medium around each sponge.")
                .defineInRange("medium_scan_radius", LivingSpongeBalanceDefaults.Spread.MEDIUM_SCAN_RADIUS, 0, 8);
        SPREAD_MAX_MEDIUM_SAMPLES_PER_UPDATE = BUILDER.comment("Maximum nearby medium cells counted per update.")
                .defineInRange("max_medium_samples_per_update", LivingSpongeBalanceDefaults.Spread.MAX_MEDIUM_SAMPLES_PER_UPDATE, 0, 64);
        SPREAD_REPRODUCTION_COOLDOWN_TICKS = BUILDER.comment("Delay between reproduction attempts.")
                .defineInRange("reproduction_cooldown_ticks", LivingSpongeBalanceDefaults.Spread.REPRODUCTION_COOLDOWN_TICKS, 0, 24000);
        SPREAD_DEATH_TARGET_COOLDOWN_TICKS = BUILDER.comment("How long a sponge death cell stays blocked for reproduction.")
                .defineInRange("death_target_cooldown_ticks", LivingSpongeBalanceDefaults.Spread.DEATH_TARGET_COOLDOWN_TICKS, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("lifecycle");
        LIFECYCLE_YOUNG_DURATION_TICKS = BUILDER.comment("Young stage duration in ticks.")
                .defineInRange("young_duration_ticks", LivingSpongeBalanceDefaults.Lifecycle.YOUNG_DURATION_TICKS, 1, Integer.MAX_VALUE);
        LIFECYCLE_MATURE_DURATION_TICKS = BUILDER.comment("Mature stage duration in ticks.")
                .defineInRange("mature_duration_ticks", LivingSpongeBalanceDefaults.Lifecycle.MATURE_DURATION_TICKS, 1, Integer.MAX_VALUE);
        LIFECYCLE_OLD_DURATION_TICKS = BUILDER.comment("Old stage duration in ticks before death.")
                .defineInRange("old_duration_ticks", LivingSpongeBalanceDefaults.Lifecycle.OLD_DURATION_TICKS, 1, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("creative");
        CREATIVE_UPDATE_INTERVAL_TICKS = BUILDER.comment("Update interval for creative living sponge variants.")
                .defineInRange("update_interval_ticks", LivingSpongeBalanceDefaults.Creative.UPDATE_INTERVAL_TICKS, 1, 1200);
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
                        SPREAD_MEDIUM_SCAN_RADIUS.get(),
                        SPREAD_MAX_MEDIUM_SAMPLES_PER_UPDATE.get(),
                        SPREAD_REPRODUCTION_COOLDOWN_TICKS.get(),
                        SPREAD_DEATH_TARGET_COOLDOWN_TICKS.get()
                ),
                new Lifecycle(
                        LIFECYCLE_YOUNG_DURATION_TICKS.get(),
                        LIFECYCLE_MATURE_DURATION_TICKS.get(),
                        LIFECYCLE_OLD_DURATION_TICKS.get()
                ),
                new Creative(
                        CREATIVE_UPDATE_INTERVAL_TICKS.get()
                )
        );
    }

    private static BalanceValues defaults() {
        return new BalanceValues(
                new Spread(
                        LivingSpongeBalanceDefaults.Spread.UPDATE_INTERVAL_TICKS,
                        LivingSpongeBalanceDefaults.Spread.MEDIUM_SCAN_RADIUS,
                        LivingSpongeBalanceDefaults.Spread.MAX_MEDIUM_SAMPLES_PER_UPDATE,
                        LivingSpongeBalanceDefaults.Spread.REPRODUCTION_COOLDOWN_TICKS,
                        LivingSpongeBalanceDefaults.Spread.DEATH_TARGET_COOLDOWN_TICKS
                ),
                new Lifecycle(
                        LivingSpongeBalanceDefaults.Lifecycle.YOUNG_DURATION_TICKS,
                        LivingSpongeBalanceDefaults.Lifecycle.MATURE_DURATION_TICKS,
                        LivingSpongeBalanceDefaults.Lifecycle.OLD_DURATION_TICKS
                ),
                new Creative(
                        LivingSpongeBalanceDefaults.Creative.UPDATE_INTERVAL_TICKS
                )
        );
    }

    public record BalanceValues(
            Spread spread,
            Lifecycle lifecycle,
            Creative creative
    ) {
    }

    public record Spread(
            int updateIntervalTicks,
            int mediumScanRadius,
            int maxMediumSamplesPerUpdate,
            int reproductionCooldownTicks,
            int deathTargetCooldownTicks
    ) {
    }

    public record Lifecycle(
            int youngDurationTicks,
            int matureDurationTicks,
            int oldDurationTicks
    ) {
    }

    public record Creative(
            int updateIntervalTicks
    ) {
    }
}
