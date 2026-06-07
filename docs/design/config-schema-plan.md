# Config Schema Plan

This document defines the next intended shape of `livingsponge-common.toml`.

Goal:

- keep the mod widely configurable
- expose real gameplay and performance policy knobs
- avoid leaking low-level implementation detail into config
- keep recipes mostly as datapack/JSON content instead of forcing ingredient lists into TOML

## Principles

1. Config should control gameplay policy, not only numeric timing.
2. Categories should stay stable even if internal code changes.
3. Recipes should remain primarily data-driven through recipe JSONs.
4. Only expose runtime/performance toggles that materially affect behavior or server load.

## Current Config Surface

Current live config sections:

- `spread`
- `lifecycle`

Current live keys:

- `spread.update_interval_ticks`
- `spread.medium_scan_radius`
- `spread.max_medium_samples_per_update`
- `spread.reproduction_cooldown_ticks`
- `spread.death_target_cooldown_ticks`
- `lifecycle.young_duration_ticks`
- `lifecycle.mature_duration_ticks`
- `lifecycle.old_duration_ticks`

This is useful, but too narrow for the actual trait/runtime system now in code.

## Target Schema

### `[general]`

Top-level toggles only.

```toml
[general]
enable_mod = true
debug_logging = false
```

Future candidates:

- `enable_creative_variants = true`

### `[lifecycle]`

Phase timing and placement lifecycle policy.

```toml
[lifecycle]
young_duration_ticks = 100
mature_duration_ticks = 400
old_duration_ticks = 100
placed_sponges_start_mature = true
```

Why:

- timings already exist
- `placed_sponges_start_mature` is now a real gameplay policy

### `[spread]`

Main colony pacing and local spread rules.

```toml
[spread]
update_interval_ticks = 20
reproduction_cooldown_ticks = 100
medium_scan_radius = 2
max_medium_samples_per_update = 3
death_target_cooldown_ticks = 800
flat_uses_diagonals = false
flat_requires_air_above = true
```

Why:

- these directly control colony feel and server load
- diagonal flat spread has already changed repeatedly and should be a config key

### `[radius]`

Explicit tier values.

```toml
[radius]
standard = 8
expanded = 16
vast = 256
```

Why:

- radius tiers are headline balance values
- these are currently hardcoded in `RadiusTrait`

### `[output]`

Output-branch policy.

```toml
[output]
wall_forming_uses_shell_border = true
wall_forming_shell_thickness = 1
solidifying_leaves_remains_everywhere = true
neutral_leaves_remains = false
wall_forming_uses_special_remains_style = true
```

Why:

- these are trait rules the user may want to tune
- shell-thickness is a safer knob than exposing raw runtime heuristics

### `[creative]`

Creative override policy.

```toml
[creative]
enabled = true
speed_multiplier = 4
force_vast_radius = true
ignore_environment_death = true
supports_water = true
supports_lava = true
```

Why:

- creative behavior is now meaningfully different from survival
- these rules are currently hardcoded in `ResolvedSpongeProfile`

### `[performance]`

Only high-value server/runtime knobs.

```toml
[performance]
enable_medium_sample_cache = true
flat_sampling_mode = "layer_only"
```

Future candidate:

- `frontier_only_processing = false`

Why:

- keep this section small
- do not expose every cache/internal branch

### `[recipes]`

Keep minimal. Recipes themselves should stay in JSON/datapacks.

```toml
[recipes]
enable_recipe_overrides = true
```

Possible later:

- `use_easy_progression = false`

Important:

- do **not** try to encode normal ingredient lists directly in TOML unless there is a very strong need

## Current Hardcoded Policy Map

This maps today’s behavior to the future config keys that should own it.

### Lifecycle

- `placed_sponges_start_mature`
  - current source:
    - `LivingSpongeNodeState.createPlacedRoot(...)`
    - `LivingSpongeBlock.setPlacedBy(...)`

### Spread

- `flat_uses_diagonals = false`
  - current source:
    - `LivingSpongeRuntime.FLAT_OFFSETS`
- `flat_requires_air_above = true`
  - current source:
    - `LivingSpongeRuntime.canHostChild(...)`

### Radius

- `standard = 8`
- `expanded = 16`
- `vast = 256`
  - current source:
    - `RadiusTrait`

### Output

- `wall_forming_uses_shell_border = true`
- `wall_forming_shell_thickness = 1`
  - current source:
    - `LivingSpongeRuntime.isBorderShellDeath(...)`
    - currently exact shell check: `distance == radiusCap`
- `solidifying_leaves_remains_everywhere = true`
  - current source:
    - `LivingSpongeRuntime.applyDeathOutcome(...)`
- `neutral_leaves_remains = false`
  - current source:
    - `LivingSpongeRuntime.applyDeathOutcome(...)`
- `wall_forming_uses_special_remains_style = true`
  - current source:
    - `SpongeRemainsStyle`
    - `LivingSpongeBlocks.spongeRemainsState(...)`

### Creative

- `speed_multiplier = 4`
  - current source:
    - `ResolvedSpongeProfile.quarterTicks(...)`
- `force_vast_radius = true`
  - current source:
    - `ResolvedSpongeProfile.radiusCap(...)`
- `ignore_environment_death = true`
  - current source:
    - `ResolvedSpongeProfile.ignoresEnvironmentDeath(...)`
- `supports_water = true`
- `supports_lava = true`
  - current source:
    - `ResolvedSpongeProfile.supportsWaterMedium()`
    - `ResolvedSpongeProfile.supportsLavaMedium()`

### Performance

- `enable_medium_sample_cache = true`
  - current source:
    - `LivingSpongeRuntime.matchesMediumSourceCached(...)`
- `flat_sampling_mode = "layer_only"`
  - current source:
    - `LivingSpongeRuntime.countNearbyMediumSources(...)`

## Recommended First Implementation Slice

The highest-value first pass is:

```toml
[lifecycle]
placed_sponges_start_mature = true

[spread]
flat_uses_diagonals = false

[radius]
standard = 8
expanded = 16
vast = 256

[creative]
speed_multiplier = 4
force_vast_radius = true
ignore_environment_death = true
supports_water = true
supports_lava = true
```

Why:

- all of these are already established gameplay decisions
- all are currently hardcoded
- all are meaningful to pack makers/server owners

## Recommended Order

1. Extend `LivingSpongeConfig` with:
   - `radius`
   - `creative`
   - lifecycle/spread policy toggles
2. Move hardcoded branches in:
   - `ResolvedSpongeProfile`
   - `LivingSpongeRuntime`
   - placement lifecycle
   into config-backed accessors
3. Keep recipes in JSON
4. Only after that consider exposing any output-policy keys

## What Not To Externalize Yet

Do not config these yet:

- texture profile names
- model/blockstate names
- low-level cache key structure
- item ids
- recipe ingredient lists in TOML

Those are either datapack concerns or internal implementation detail.
