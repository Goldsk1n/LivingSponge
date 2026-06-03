# Trait System Implementation Plan

This document records how the sponge trait system is implemented in the current codebase and what remains to polish.

## Goal

Use a composable trait-driven system built from:

- `Medium`
- `Spread`
- `Output`
- `Radius`

while preserving the existing lifecycle foundation:

- `young -> mature -> old -> dead`
- separate phase blocks for visuals
- persistent block entity state
- server-tick-driven colony simulation

## Current Codebase Snapshot

The current implementation has these important properties:

- The full exposed trait matrix is available for gameplay except for intentionally unsupported mixed outputs.
- Phase visuals are implemented as separate blocks:
  - `living_sponge`
  - `mature_living_sponge`
  - `old_living_sponge`
  - creative equivalents
- The runtime uses persisted trait data plus a creative override flag.
- Water and magma colonies are both supported.
- Radius is trait-driven, not globally configured per world.
- Death output is trait-driven:
  - `Neutral` -> `air`
  - `Fruiting` -> `air`
  - `Wall-Forming` -> frontier old-age deaths leave `sponge_remains`
  - `Solidifying` -> all old-age deaths leave `sponge_remains`
- Block entity state currently stores:
  - colony id
  - root position
  - generation
  - traits
  - creative override flag
  - age
  - reproduction cooldown
  - fruit progress
- Config has already been reduced to the currently used lifecycle, spread sampling, fruit progress, and creative timing settings.

## Design Assumptions For Implementation

These assumptions describe the current supported system:

- Trait slots are fixed:
  - `Medium = Water | Magma`
  - `Spread = Volume | Surface`
  - `Output = Neutral | Fruiting | Wall-Forming | Solidifying`
  - `Radius = Standard | Expanded | Vast`
- Radius caps are fixed:
  - `Standard = 8`
  - `Expanded = 16`
  - `Vast = 512`
- `Wall-Forming` and `Fruiting` are incompatible.
- `Solidifying` and `Fruiting` are incompatible.
- `Surface + Fruiting` is valid, but fruit cannot hang downward.
- `Water` dies on lava contact.
- `Magma` dies on water contact.
- `Wall-Forming` frontier deaths produce `sponge_remains`.
- `Solidifying` all old-age deaths produce `sponge_remains`.
- `Neutral` and `Fruiting` old-age deaths produce `air`.
- The base `Living Sponge` item families are neutral by default.
- All survival variants share the same display name and expose traits through tooltips.

## Architecture

Implement the new system around a resolved profile model, not around branching on booleans or block ids.

### New Core Types

Add these types under a new package such as `simulation.profile` or `content.traits`:

- `MediumTrait`
- `SpreadTrait`
- `OutputTrait`
- `RadiusTrait`
- `SpongeTraits`
- `ResolvedSpongeProfile`

Recommended responsibilities:

- `SpongeTraits`
  - stores the chosen trait combination for one sponge
- `ResolvedSpongeProfile`
  - computes the actual behavior used by runtime
  - valid fluid
  - reproduction target rule
  - radius cap
  - death output mode
  - fruit attachment rule
  - whether water should be removed or preserved

### Node State Changes

`LivingSpongeNodeState` should stop storing `creativeVariant` as the primary identity.

It should instead store:

- `SpongeTraits traits`
- `boolean creativeOverrides`

This keeps creative behavior as an override layer instead of mixing it into species identity.

### Registry Strategy

Keep separate phase blocks for visuals.

Do not encode all trait combinations as separate block ids.

Recommended block strategy:

- one block family per visible lifecycle phase:
  - `living_sponge`
  - `mature_living_sponge`
  - `old_living_sponge`
- block entity stores traits
- block rendering uses block entity data or blockstate/model indirection only if needed later

However, if custom per-trait visuals are required immediately, use a staged approach:

- phase block family first
- trait-specific visuals second

This avoids exploding the registry before behavior is stable.

## Current Status

Implemented:

- trait persistence and resolved profiles
- creative override separation
- trait-driven radius, medium, spread, and output behavior
- water and magma fruiting
- full exposed trait matrix across `Standard`, `Expanded`, and `Vast`
- tooltip-based variant identity
- direct recipe-based trait conversions and radius upgrades
- reduced config surface aligned to the live runtime

Still left to polish:

- in-world visual differentiation beyond lifecycle phases
- creative-tab and JEI readability tuning as recipe count grows
- focused stress/performance testing for `Vast` colonies
- gameplay balancing and wording refinement after wider in-game testing

## Save Compatibility

The mod is still in active development, so legacy sponge save compatibility is not guaranteed.

Current policy:

- current-format traitful sponge saves are supported
- legacy traitless or pre-trait sponge states are not migrated
- broken sponge block entities are not auto-healed on load

Registry safety still matters:

- avoid renaming existing sponge phase block ids casually
- keep trait identity in block entity data rather than exploding block registries

## Testing Plan

Each implementation phase should end with a focused verification pass.

### Automated Minimum

- `./gradlew compileJava`
- `./gradlew build`

### Manual Gameplay Matrix

At minimum test:

- freshly placed `Water + Volume + Neutral + Standard`
- freshly placed `Water + Volume + Wall-Forming + Standard`
- `Water + Surface + Wall-Forming + Standard`
- `Water + Surface + Fruiting + Standard`
- `Magma + Volume + Fruiting + Standard`
- `Magma + Surface + Fruiting + Standard`
- `Magma + Surface + Solidifying + Standard`
- `Magma + Surface + Solidifying + Expanded`
- one `Vast` profile to confirm cap and performance behavior

### Specific Regressions To Watch

- block entity state loss on phase swap
- trait loss on chunk unload/reload
- incorrect frontier detection under `Surface`
- incorrect medium kill behavior
- invalid reproduction into air for volume colonies
- fruit placement falling when `Surface + Fruiting`
- runaway spread in `Vast` colonies causing heavy tick cost

## Risks

### Trait Explosion

Risk:

- too many combinations become hard to maintain

Mitigation:

- implement only the matrix already approved
- enforce incompatibility rules centrally

### Asset Explosion

Risk:

- one registry/model per combination becomes unmanageable

Mitigation:

- keep trait identity in block entity data
- use shared phase blocks initially

### Performance

Risk:

- `Vast` colonies and surface scans become expensive

Mitigation:

- profile-aware radius caps
- chunk-loaded-only processing
- keep deterministic update intervals
- optimize scan logic before widening adoption

### Config Drift

Risk:

- code and config describe different systems

Mitigation:

- keep docs and config aligned with the actual runtime after each behavior change
