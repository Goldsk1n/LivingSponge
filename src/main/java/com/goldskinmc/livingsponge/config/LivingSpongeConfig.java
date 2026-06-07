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
    private static final ForgeConfigSpec.BooleanValue SPREAD_FLAT_USES_DIAGONALS;

    private static final ForgeConfigSpec.IntValue LIFECYCLE_YOUNG_DURATION_TICKS;
    private static final ForgeConfigSpec.IntValue LIFECYCLE_MATURE_DURATION_TICKS;
    private static final ForgeConfigSpec.IntValue LIFECYCLE_OLD_DURATION_TICKS;
    private static final ForgeConfigSpec.BooleanValue LIFECYCLE_PLACED_SPONGES_START_MATURE;

    private static final ForgeConfigSpec.IntValue RADIUS_STANDARD;
    private static final ForgeConfigSpec.IntValue RADIUS_EXPANDED;
    private static final ForgeConfigSpec.IntValue RADIUS_VAST;

    private static final ForgeConfigSpec.IntValue CREATIVE_SPEED_MULTIPLIER;
    private static final ForgeConfigSpec.BooleanValue CREATIVE_FORCE_VAST_RADIUS;
    private static final ForgeConfigSpec.BooleanValue CREATIVE_IGNORE_ENVIRONMENT_DEATH;
    private static final ForgeConfigSpec.BooleanValue CREATIVE_SUPPORTS_WATER;
    private static final ForgeConfigSpec.BooleanValue CREATIVE_SUPPORTS_LAVA;

    private static final ForgeConfigSpec.IntValue OUTPUT_WALL_FORMING_SHELL_THICKNESS;

    private static final ForgeConfigSpec.BooleanValue RECIPES_ENABLE_VANILLA_SPONGE_RECIPE;

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
        SPREAD_FLAT_USES_DIAGONALS = BUILDER.comment("Whether flat sponges can reproduce diagonally.")
                .define("flat_uses_diagonals", LivingSpongeBalanceDefaults.Spread.FLAT_USES_DIAGONALS);
        BUILDER.pop();

        BUILDER.push("lifecycle");
        LIFECYCLE_YOUNG_DURATION_TICKS = BUILDER.comment("Young stage duration in ticks.")
                .defineInRange("young_duration_ticks", LivingSpongeBalanceDefaults.Lifecycle.YOUNG_DURATION_TICKS, 1, Integer.MAX_VALUE);
        LIFECYCLE_MATURE_DURATION_TICKS = BUILDER.comment("Mature stage duration in ticks.")
                .defineInRange("mature_duration_ticks", LivingSpongeBalanceDefaults.Lifecycle.MATURE_DURATION_TICKS, 1, Integer.MAX_VALUE);
        LIFECYCLE_OLD_DURATION_TICKS = BUILDER.comment("Old stage duration in ticks before death.")
                .defineInRange("old_duration_ticks", LivingSpongeBalanceDefaults.Lifecycle.OLD_DURATION_TICKS, 1, Integer.MAX_VALUE);
        LIFECYCLE_PLACED_SPONGES_START_MATURE = BUILDER.comment("Whether player-placed sponge roots start in the mature phase.")
                .define("placed_sponges_start_mature", LivingSpongeBalanceDefaults.Lifecycle.PLACED_SPONGES_START_MATURE);
        BUILDER.pop();

        BUILDER.push("radius");
        RADIUS_STANDARD = BUILDER.comment("Maximum root distance for standard radius sponges.")
                .defineInRange("standard", LivingSpongeBalanceDefaults.Radius.STANDARD, 1, Integer.MAX_VALUE);
        RADIUS_EXPANDED = BUILDER.comment("Maximum root distance for expanded radius sponges.")
                .defineInRange("expanded", LivingSpongeBalanceDefaults.Radius.EXPANDED, 1, Integer.MAX_VALUE);
        RADIUS_VAST = BUILDER.comment("Maximum root distance for vast radius sponges.")
                .defineInRange("vast", LivingSpongeBalanceDefaults.Radius.VAST, 1, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("creative");
        CREATIVE_SPEED_MULTIPLIER = BUILDER.comment("How much faster creative sponges run than normal sponges.")
                .defineInRange("speed_multiplier", LivingSpongeBalanceDefaults.Creative.SPEED_MULTIPLIER, 1, 64);
        CREATIVE_FORCE_VAST_RADIUS = BUILDER.comment("Whether creative sponges always use the vast radius cap.")
                .define("force_vast_radius", LivingSpongeBalanceDefaults.Creative.FORCE_VAST_RADIUS);
        CREATIVE_IGNORE_ENVIRONMENT_DEATH = BUILDER.comment("Whether creative sponges ignore opposing fluid and fire death.")
                .define("ignore_environment_death", LivingSpongeBalanceDefaults.Creative.IGNORE_ENVIRONMENT_DEATH);
        CREATIVE_SUPPORTS_WATER = BUILDER.comment("Whether creative sponges can live and spread in water.")
                .define("supports_water", LivingSpongeBalanceDefaults.Creative.SUPPORTS_WATER);
        CREATIVE_SUPPORTS_LAVA = BUILDER.comment("Whether creative sponges can live and spread in lava.")
                .define("supports_lava", LivingSpongeBalanceDefaults.Creative.SUPPORTS_LAVA);
        BUILDER.pop();

        BUILDER.push("output");
        OUTPUT_WALL_FORMING_SHELL_THICKNESS = BUILDER.comment("How many outer radius layers wall-forming sponges preserve as remains on old-age death.")
                .defineInRange("wall_forming_shell_thickness", LivingSpongeBalanceDefaults.Output.WALL_FORMING_SHELL_THICKNESS, 1, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("recipes");
        RECIPES_ENABLE_VANILLA_SPONGE_RECIPE = BUILDER.comment("Whether the custom wool-based vanilla sponge recipe is enabled.")
                .define("enable_vanilla_sponge_recipe", LivingSpongeBalanceDefaults.Recipes.ENABLE_VANILLA_SPONGE_RECIPE);
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
                        SPREAD_DEATH_TARGET_COOLDOWN_TICKS.get(),
                        SPREAD_FLAT_USES_DIAGONALS.get()
                ),
                new Lifecycle(
                        LIFECYCLE_YOUNG_DURATION_TICKS.get(),
                        LIFECYCLE_MATURE_DURATION_TICKS.get(),
                        LIFECYCLE_OLD_DURATION_TICKS.get(),
                        LIFECYCLE_PLACED_SPONGES_START_MATURE.get()
                ),
                new Radius(
                        RADIUS_STANDARD.get(),
                        RADIUS_EXPANDED.get(),
                        RADIUS_VAST.get()
                ),
                new Creative(
                        CREATIVE_SPEED_MULTIPLIER.get(),
                        CREATIVE_FORCE_VAST_RADIUS.get(),
                        CREATIVE_IGNORE_ENVIRONMENT_DEATH.get(),
                        CREATIVE_SUPPORTS_WATER.get(),
                        CREATIVE_SUPPORTS_LAVA.get()
                ),
                new Output(
                        OUTPUT_WALL_FORMING_SHELL_THICKNESS.get()
                ),
                new Recipes(
                        RECIPES_ENABLE_VANILLA_SPONGE_RECIPE.get()
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
                        LivingSpongeBalanceDefaults.Spread.DEATH_TARGET_COOLDOWN_TICKS,
                        LivingSpongeBalanceDefaults.Spread.FLAT_USES_DIAGONALS
                ),
                new Lifecycle(
                        LivingSpongeBalanceDefaults.Lifecycle.YOUNG_DURATION_TICKS,
                        LivingSpongeBalanceDefaults.Lifecycle.MATURE_DURATION_TICKS,
                        LivingSpongeBalanceDefaults.Lifecycle.OLD_DURATION_TICKS,
                        LivingSpongeBalanceDefaults.Lifecycle.PLACED_SPONGES_START_MATURE
                ),
                new Radius(
                        LivingSpongeBalanceDefaults.Radius.STANDARD,
                        LivingSpongeBalanceDefaults.Radius.EXPANDED,
                        LivingSpongeBalanceDefaults.Radius.VAST
                ),
                new Creative(
                        LivingSpongeBalanceDefaults.Creative.SPEED_MULTIPLIER,
                        LivingSpongeBalanceDefaults.Creative.FORCE_VAST_RADIUS,
                        LivingSpongeBalanceDefaults.Creative.IGNORE_ENVIRONMENT_DEATH,
                        LivingSpongeBalanceDefaults.Creative.SUPPORTS_WATER,
                        LivingSpongeBalanceDefaults.Creative.SUPPORTS_LAVA
                ),
                new Output(
                        LivingSpongeBalanceDefaults.Output.WALL_FORMING_SHELL_THICKNESS
                ),
                new Recipes(
                        LivingSpongeBalanceDefaults.Recipes.ENABLE_VANILLA_SPONGE_RECIPE
                )
        );
    }

    public record BalanceValues(
            Spread spread,
            Lifecycle lifecycle,
            Radius radius,
            Creative creative,
            Output output,
            Recipes recipes
    ) {
    }

    public record Spread(
            int updateIntervalTicks,
            int mediumScanRadius,
            int maxMediumSamplesPerUpdate,
            int reproductionCooldownTicks,
            int deathTargetCooldownTicks,
            boolean flatUsesDiagonals
    ) {
    }

    public record Lifecycle(
            int youngDurationTicks,
            int matureDurationTicks,
            int oldDurationTicks,
            boolean placedSpongesStartMature
    ) {
    }

    public record Radius(
            int standard,
            int expanded,
            int vast
    ) {
    }

    public record Creative(
            int speedMultiplier,
            boolean forceVastRadius,
            boolean ignoreEnvironmentDeath,
            boolean supportsWater,
            boolean supportsLava
    ) {
    }

    public record Output(
            int wallFormingShellThickness
    ) {
    }

    public record Recipes(
            boolean enableVanillaSpongeRecipe
    ) {
    }

    public static boolean configFlag(final String flag) {
        return switch (flag) {
            case "enable_vanilla_sponge_recipe" -> values.recipes().enableVanillaSpongeRecipe();
            default -> false;
        };
    }

}
