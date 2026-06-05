# Full Matrix Test Plan

This document defines the manual testing, verification, and balancing pass for the current Living Sponge trait matrix.

It assumes the current exposed system includes:

- `Water` and `Magma`
- `Volume` and `Flat`
- `Neutral`, `Podding`, `Wall-Forming`, and `Solidifying`
- `Standard`, `Expanded`, and `Vast`

## Test Setup

Use a clean dev world.

Recommended environment:

- one controlled water basin with flat surface
- one controlled lava basin with flat surface
- enough open space to observe `Expanded` and `Vast` behavior
- easy access to both `creative` and `survival`

Recommended workflow:

- use `creative` for setup and rapid placement
- switch to `survival` for drop and crafting verification

## Verification Order

Run the checks in this order to catch failures early.

### 1. Item Identity

Open the creative inventory and hover representative variants from each family.

Verify that every survival sponge item:

- is named `Living Sponge`
- shows a tooltip with:
  - `Growth Medium`
  - `Spread`
  - `Output`
  - `Radius`

Verify that:

- `Creative Living Sponge` is the only differently named sponge item
- tooltip wording matches the actual behavior

### 2. Crafting Graph

Start from crafted base `living_sponge`.

Verify conversions:

- `+ lily_pad` -> flat
- `+ cobblestone_wall` -> wall-forming
- `+ hydro_pod` -> podding
- `+ calcite` -> solidifying
- `+ magma_cream` -> magma

Verify upgrades:

- `+ prismarine_crystals` -> `Expanded`
- `+ heart_of_the_sea` -> `Vast`

Check that:

- resulting tooltips match the expected trait combination
- no recipe produces invalid mixed output families

### 3. Neutral Behavior

Test these profiles:

- `Water + Volume + Neutral + Standard`
- `Water + Flat + Neutral + Standard`
- `Magma + Volume + Neutral + Standard`
- `Magma + Flat + Neutral + Standard`

Verify:

- colony spreads
- colony dies by age
- no pods are produced
- no remains are left on old-age death

### 4. Wall-Forming Behavior

Test:

- `Water + Volume + Wall-Forming + Standard`
- `Water + Flat + Wall-Forming + Standard`
- `Magma + Volume + Wall-Forming + Standard`
- `Magma + Flat + Wall-Forming + Standard`

Verify:

- frontier old-age deaths leave `sponge_remains`
- interior old-age deaths leave air
- flat variants produce perimeter/ring-like outcomes rather than full fill

### 5. Solidifying Behavior

Test:

- `Water + Volume + Solidifying + Standard`
- `Water + Flat + Solidifying + Standard`
- `Magma + Volume + Solidifying + Standard`
- `Magma + Flat + Solidifying + Standard`

Verify:

- all old-age deaths leave `sponge_remains`
- `Flat + Solidifying` produces filled crust/platform outcomes
- magma flat solidifying remains usable as a lava platform tool

### 6. Podding Behavior

Test:

- `Water + Volume + Podding + Standard`
- `Water + Flat + Podding + Standard`
- `Magma + Volume + Podding + Standard`
- `Magma + Flat + Podding + Standard`

Verify:

- no live pods appear while the colony is alive
- old-age deaths can leave pods
- `Flat + Podding` leaves pods in place on death
- `Volume + Podding` creates falling pods on death
- right-click picks pods up safely
- breaking pods releases their stored fluid
- connected pods burst in a domino effect, including diagonals
- magma podding creates lava pods in lava environments

### 7. Medium Rules

Verify cross-medium kill behavior:

- water sponge in lava dies
- magma sponge in water dies
- magma sponge in lava survives
- water sponge in water survives

### 8. Radius Caps

Test `Standard`, `Expanded`, and `Vast` for both `Volume` and `Flat`.

Verify:

- no off-by-one growth beyond the intended cap
- flat variants reach their corners
- `Expanded` flat platforms stabilize at the expected footprint
- `Vast` remains bounded by the configured practical cap

### 9. Persistence

For several trait combinations:

- place colony
- let it begin spreading
- save and quit
- reload the world

Verify:

- colony resumes correctly
- traits are preserved
- drops preserve traits when broken
- no current-format colonies become inert after reload

### 10. Lifecycle Progression

Watch at least one colony from:

- `young`
- `mature`
- `old`
- `death`

Verify:

- lifecycle visuals change as expected
- old sponges do not reproduce
- death output matches output trait

### 11. Creative Variant

Test `Creative Living Sponge`.

Verify:

- it uses the same trait tooltip model
- it runs faster than survival timing
- it behaves consistently through phase progression and spread

## Recommended Gameplay Matrix

At minimum, explicitly test these profiles:

- `Water + Volume + Neutral + Standard`
- `Water + Flat + Wall-Forming + Standard`
- `Water + Flat + Podding + Standard`
- `Magma + Volume + Podding + Standard`
- `Magma + Flat + Podding + Standard`
- `Magma + Flat + Solidifying + Standard`
- `Magma + Flat + Solidifying + Expanded`
- one `Vast` profile for stress/performance

## Balancing Pass

Do balancing in layers.

### 1. Start with Standard

Ignore `Expanded` and `Vast` initially.

Judge:

- spread feel
- pod frequency
- remains frequency
- colony lifetime usefulness

### 2. Tune Lifecycle

If pacing feels wrong, adjust:

- `young_duration_ticks`
- `mature_duration_ticks`
- `old_duration_ticks`

Questions:

- does young stage matter
- does mature stage last long enough to be useful
- does old stage overstay or end too abruptly

### 3. Tune Spread Speed

If colonies are too explosive or too inert, adjust:

- `spread.update_interval_ticks`
- `spread.reproduction_cooldown_ticks`

### 4. Tune Podding Rate

If pods feel too rare or too noisy, adjust:

- `pod.death_spawn_chance`

### 5. Tune Scan Pressure

If behavior feels weak or expensive, adjust:

- `spread.medium_scan_radius`
- `spread.max_medium_samples_per_update`

### 6. Move to Expanded

Once `Standard` feels right, test `Expanded`.

Prefer tuning timing and scan pressure before adding special-case behavior.

### 7. Test Vast Last

Use one colony first.

Watch for:

- heavy spreading cost
- chunk boundary oddities
- visual clutter
- pod clutter

If `Vast` is too heavy, first reduce:

- scan radius
- sample count
- update frequency

before changing core rules.

## Regression Checklist

Use this as a quick pass after any major behavior change.

- tooltips correct
- recipes correct
- neutral leaves no byproduct
- wall-forming leaves frontier remains only
- solidifying leaves remains everywhere on old-age death
- podding creates pods only on old-age death
- volume podding creates falling pods
- flat podding leaves pods in place
- magma survives lava
- water survives water
- wrong-medium death still works
- radius caps respected
- flat corners fill
- traits persist across reloads and drops

## Notes

- Current-format worlds are supported.
- Legacy traitless or pre-trait worlds are not part of the supported test scope.
- If a failure appears, record:
  - exact sponge traits
  - world medium
  - radius tier
  - whether the issue happens only after reload
