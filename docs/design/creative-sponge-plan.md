# Creative Sponge Implementation Plan

This document defines the next creative-sponge redesign before code changes begin.

## Goal

Keep creative support, but narrow it to a small, builder-focused ruleset instead of treating it like a full parallel trait matrix.

Target behavior:

- creative sponges use the same `young -> mature -> old -> dead` lifecycle as survival sponges
- creative timing is `4x` faster than survival
- creative sponges do not die from:
  - lava contact
  - water contact
  - fire contact
- creative sponges can operate in both water and lava
- creative sponges always use `Huge` radius behavior

This intentionally drops the earlier immortal-creative idea. The point is fast, predictable creative tooling, not permanent autonomous colonies.

## Variants To Keep

Keep only the creative variants that map to clear builder use cases.

Retain:

- `Creative Living Sponge` with `Neutral + Volume`
- `Creative Living Sponge` with `Neutral + Flat`
- `Creative Living Sponge` with `Solidifying + Volume`
- `Creative Living Sponge` with `Solidifying + Flat`

Do not keep:

- creative wall-forming variants
- creative water-only variants
- creative magma-only variants
- creative standard/expanded radius variants

Reasoning:

- `Neutral` covers fast clearing in both media
- `Solidifying` covers platform and filled-structure use cases
- `Wall-Forming` is less important in creative and adds maintenance without a strong build workflow payoff
- medium and radius are already overridden by the creative rules, so creative-specific medium/radius families would be redundant

## Behavior Rules

Creative behavior should remain an override layer on top of normal trait handling.

### Lifecycle

- keep the standard stage order:
  - `young`
  - `mature`
  - `old`
  - `dead`
- creative sponges still die of age
- creative sponges do not get immortality or lifecycle looping

This keeps them useful as fast creative tools while preserving the same broad colony rhythm as survival.

### Speed

Creative timing should be exactly `4x` faster than survival.

Apply the multiplier to:

- update interval
- reproduction cooldown
- young duration
- mature duration
- old duration

Implementation rule:

- derive creative timing from the normal values by quartering them
- clamp each computed value to at least `1` tick

### Medium Handling

Creative sponges should treat both valid fluids as usable growth media:

- water
- lava

Implementation meaning:

- do not reject a creative colony for being in the "wrong" fluid
- let creative reproduction targets include either water or lava cells

### Environmental Immunity

Creative sponges should ignore environment death from:

- opposing fluid contact
- fire contact

They should still respect:

- age death
- invalid-state death if the colony is no longer in any valid medium context at all

### Radius

Creative sponges should always behave as `Huge`.

Implementation meaning:

- ignore the stored radius trait when `creativeOverrides == true`
- return the `Huge` cap (`256`) from the resolved profile

## Item And Registry Plan

Keep the current creative override model instead of inventing a second creative species system.

Recommended exposure:

- keep one base creative neutral item family
- add only the needed creative conversions:
  - flat
  - solidifying

Practical creative item set:

- `creative_living_sponge`
- `creative_flat_living_sponge`
- `creative_solidifying_living_sponge`
- `creative_flat_solidifying_living_sponge`

All four should still display as:

- `Creative Living Sponge`

Trait differences should continue to live in tooltips.

Do not add:

- creative wall-forming item ids
- creative magma item ids
- creative radius-tier item ids

## Runtime Changes

### 1. Resolved Profile

Update `ResolvedSpongeProfile` so creative overrides:

- force `radiusCap()` to `256`
- quarter timing values instead of using the current `2x` behavior
- expose a `usesBothMedia()` or equivalent helper

### 2. Medium Validation

Update the runtime/context medium checks so creative colonies:

- can stay active in water
- can stay active in lava
- can reproduce into either medium

This should be implemented in the same medium-check path used by survival, not by ad hoc special cases scattered across the runtime.

### 3. Environment Death

Update simulation/runtime checks so creative colonies ignore:

- `hasOpposingFluidContact()`
- `hasFireContact()`

They should still die when the lifecycle reaches `DEAD`.

### 4. Item Exposure

Update creative item registration and creative-tab population to match the reduced set:

- neutral volume
- neutral flat
- solidifying volume
- solidifying flat

Do not expose creative wall-forming entries.

### 5. Tooltips

Keep the current tooltip model and add or retain one short creative summary line that makes the override behavior explicit.

Recommended wording:

- `Creative: 4x faster, works in water and lava.`

Do not claim immortality in the tooltip, because age death remains active.

## Config Strategy

Keep the creative override surface small.

Recommended first pass:

- do not add many new booleans
- hardcode the creative rules in profile/runtime while the design settles

If config is needed later, add only:

- `creative_speed_multiplier`

Everything else is part of the creative identity and does not need early configuration.

## Testing Checklist

After implementation, test at minimum:

- creative neutral in water
- creative neutral in lava
- creative flat neutral in water
- creative flat neutral in lava
- creative solidifying in water
- creative solidifying in lava
- creative flat solidifying in lava as a platform tool
- age progression still ends in death
- fire contact no longer kills creative colonies
- no creative wall-forming items appear in the creative tab

## Success Criteria

The redesign is correct when:

- creative sponges are noticeably faster than survival by a clean `4x`
- the same creative item families work in both water and lava
- creative colonies still age and die normally
- creative no longer exposes unnecessary wall-forming or radius/medium duplication
- the creative tab is smaller and easier to understand
