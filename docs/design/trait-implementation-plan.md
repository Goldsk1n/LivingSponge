# Trait System Implementation Plan

This document turns the sponge trait design into an implementation plan for the current codebase.

## Goal

Replace the current single-variant sponge implementation with a composable trait-driven system built from:

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

- There is one functional sponge family plus a creative variant.
- Phase visuals are implemented as separate blocks:
  - `living_sponge`
  - `mature_living_sponge`
  - `old_living_sponge`
  - creative equivalents
- The runtime model still assumes a simple boolean split:
  - normal vs creative
- The runtime is hardcoded for water colonies:
  - water-only target checks
  - water-only nearby source scanning
  - lava and fire kill behavior
- Colony radius is currently a single config value.
- Death outputs are currently hardcoded in runtime:
  - frontier chance -> `sponge_remains`
  - non-frontier chance -> `hydro_block`
- Block entity state currently stores:
  - colony id
  - root position
  - generation
  - creative flag
  - age
  - reproduction cooldown
- Config still contains legacy scaffolding that no longer matches the intended design cleanly:
  - energy fields
  - old fruit progress fields
  - generic spread parameters that should become profile-driven

## Design Assumptions For Implementation

These assumptions should be treated as the working baseline for implementation:

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
- `Neutral` and `Fruiting` old-age deaths produce `air` unless later overridden by a separate byproduct rule.

## Recommended Architecture

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

## Major Implementation Phases

## Phase 1: Introduce Trait Model Without Changing Gameplay

Objective:

- add the type system and persistence model first
- keep current behavior working during the transition

Work:

- add trait enums and `SpongeTraits`
- add compatibility validation helpers
- add radius trait mapping:
  - `Standard -> 8`
  - `Expanded -> 16`
  - `Vast -> 512`
- add `ResolvedSpongeProfile`
- extend `LivingSpongeNodeState` save/load to store traits
- add migration defaults for old saves:
  - old worlds load as a default baseline profile

Recommended migration default:

- `Water + Volume + Wall-Forming + Standard`

Reason:

- this most closely matches the current frontier/remains-based colony behavior

Success criteria:

- old worlds still load
- newly placed baseline sponges still behave like the current default

## Phase 2: Replace Creative Boolean Logic With Profile + Overrides

Objective:

- decouple sponge identity from creative acceleration

Work:

- replace `creativeVariant` behavior branches with:
  - `SpongeTraits`
  - `creativeOverrides`
- keep creative placement items if desired, but treat them as:
  - baseline traits + creative override flag
- move all timing adjustments into profile resolution or creative override resolution

Success criteria:

- creative sponge still works
- runtime no longer depends on a single boolean for sponge identity

## Phase 3: Move Radius Logic Into Profiles

Objective:

- remove global colony radius as the primary model

Work:

- replace runtime use of `values.spread().maxColonyRadius()` with `profile.radiusCap()`
- keep config only for global lifecycle/update defaults
- stop treating radius as a world-global spread rule
- update frontier and reproduction checks to use radius trait

Success criteria:

- `Standard`, `Expanded`, and `Vast` radius caps work correctly
- existing baseline profile still behaves the same at radius `8`

## Phase 4: Implement Medium Traits

Objective:

- support both water and magma colonies

Work:

- replace water-only checks in runtime with profile-driven medium checks
- abstract nearby-source scanning:
  - `findNearbyValidMediumSources(...)`
- abstract child-target validation:
  - `canHostChild(profile, level, pos)`
- implement cross-medium kill rules:
  - water sponge in lava dies
  - magma sponge in water dies

Success criteria:

- water colonies work unchanged
- magma colonies can live and spread in lava

## Phase 5: Implement Spread Traits

Objective:

- split `Volume` and `Surface` behavior cleanly

Work:

- introduce target-selection strategies:
  - `Volume`: any valid submerged cell
  - `Surface`: valid medium cell with air above
- update frontier detection to respect spread trait
- update reproduction scanning to respect spread trait
- update any death-output logic that depends on frontier shape

Important note:

- `Surface` should not be faked as a cosmetic flag
- it must constrain reproduction target selection directly

Success criteria:

- `Surface` colonies produce raft/ring/platform behavior
- `Volume` colonies continue to fill bodies of fluid

## Phase 6: Implement Output Traits

Objective:

- replace the hardcoded current death-output model with trait-resolved output behavior

Work:

- move death replacement logic out of current special-case assumptions
- implement:
  - `Neutral`
  - `Fruiting`
  - `Wall-Forming`
  - `Solidifying`
- for `Wall-Forming`:
  - old-age frontier death -> `sponge_remains`
  - old-age non-frontier death -> `air`
- for `Solidifying`:
  - old-age death anywhere -> `sponge_remains`
- for `Neutral`:
  - old-age death -> `air`
- for `Fruiting`:
  - fruit production logic
  - old-age death -> `air`

Also decide the fate of `hydro_block`:

- either remove it from sponge death flow entirely
- or repurpose it as a separate fruit-resource artifact if explicitly desired

Recommended direction:

- remove `hydro_block` from default death handling
- keep it only if a future trait or recipe needs it

Success criteria:

- each output trait has a single coherent behavior
- no leftover hybrid logic from the old hardening/hydro-block model

## Phase 7: Implement Fruiting Properly

Objective:

- make `Fruiting` a real output trait instead of a placeholder concept

Work:

- define fruit spawn or attachment rules
- implement fruit placement or drop representation
- implement `Surface + Fruiting` special rule:
  - fruit attaches to top/side
  - never hangs below
- decide whether fruit is:
  - a separate placed block
  - a block entity state
  - a direct item drop event

Recommended direction:

- use a separate fruit node block if physical harvesting matters
- use direct item spawn only if visual farming is not required

Success criteria:

- fruiting colonies visibly differ from neutral colonies
- surface fruiting rule is enforced

## Phase 8: Placement Items, Crafting, and UX

Objective:

- let players intentionally build trait combinations

Work:

- decide whether combinations are created by:
  - direct crafted sponge items
  - upgrade items applied to a base sponge
  - a workstation
- add item naming based on trait combinations
- add lang entries
- add creative tab organization
- add JEI-visible recipes

Recommended direction:

- use crafted combination items first
- move to a combinator workstation later if the recipe count becomes too high

Success criteria:

- a player can intentionally obtain at least the baseline supported combinations

## Phase 9: Assets and Rendering

Objective:

- make the trait system readable in-game

Work:

- keep lifecycle phase visuals
- decide how traits affect visuals:
  - item name only
  - tinting
  - distinct overlay textures
  - dedicated models later
- ensure block/item models and lang keys match supported combinations

Recommended direction:

- start with item-name differentiation
- keep phase textures as the primary visual signal
- add trait overlays only after gameplay is stable

Success criteria:

- players can distinguish functional variants without asset explosion

## Phase 10: Cleanup and Config Simplification

Objective:

- remove the now-misleading legacy config surface

Work:

- remove unused energy settings
- remove legacy spread chance settings if no longer used
- convert lifecycle and update settings to trait-aware defaults where appropriate
- keep only config that still controls real gameplay

Success criteria:

- config matches actual runtime behavior
- dead legacy knobs are gone

## Migration Strategy

Old worlds need a deterministic migration path.

### Save Migration

- If a saved node has no traits, assign:
  - `Water + Volume + Wall-Forming + Standard`
- If a saved node only has the old creative flag:
  - preserve it as `creativeOverrides = true`

### Runtime Migration

- do not rely on users replacing old blocks manually
- block entities should self-heal missing trait data on load

### Registry Safety

- avoid renaming existing sponge phase block ids during the trait migration
- trait identity should live in data first, not in registry ids

## Testing Plan

Each implementation phase should end with a focused verification pass.

### Automated Minimum

- `./gradlew compileJava`
- `./gradlew build`

### Manual Gameplay Matrix

At minimum test:

- baseline migrated sponge from old world
- new `Water + Volume + Neutral + Standard`
- `Water + Surface + Wall-Forming + Standard`
- `Water + Surface + Fruiting + Standard`
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

### Legacy Config Drift

Risk:

- code and config describe different systems

Mitigation:

- add cleanup as an explicit final phase, not a vague follow-up

## Recommended Delivery Order

Use this exact sequence:

1. Trait model and migration defaults
2. Creative override separation
3. Radius trait
4. Medium trait
5. Spread trait
6. Output trait
7. Fruiting implementation
8. Player-facing items and recipes
9. Visual differentiation
10. Config cleanup

This order minimizes breakage because it upgrades persistence and runtime identity before changing gameplay semantics.
