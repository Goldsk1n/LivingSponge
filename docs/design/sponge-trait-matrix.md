# Sponge Trait Matrix

This document defines the planned composable trait system for Living Sponge variants.

## Trait Slots

A sponge instance is composed from four trait slots:

- `Medium`
  - `Water`
  - `Magma`
- `Spread`
  - `Volume`
  - `Surface`
- `Output`
  - `Neutral`
  - `Fruiting`
  - `Wall-Forming`
  - `Solidifying`
- `Radius`
  - `Standard`
  - `Expanded`
  - `Vast`

Canonical profile form:

- `Medium + Spread + Output + Radius`

Examples:

- `Water + Volume + Neutral + Standard`
- `Water + Surface + Fruiting + Expanded`
- `Magma + Surface + Solidifying + Vast`

## Radius Tiers

- `Standard` = `8`
- `Expanded` = `16`
- `Vast` = `512`

`Vast` is treated as practically unbounded, but it is still capped in code.

## Hard Rules

- `Wall-Forming` and `Fruiting` are incompatible.
- `Solidifying` and `Fruiting` are incompatible.
- `Surface + Fruiting` is allowed.
- `Surface + Fruiting` fruit does not hang downward.
- All radius tiers share the same lifecycle rules unless explicitly changed later.

## Medium Rules

### `Water`

- Lives in water.
- Dies on lava contact.

### `Magma`

- Lives in lava.
- Dies on water contact.

## Spread Rules

### `Volume`

- Spreads through the body of the valid fluid.
- Valid target: a fluid-filled cell in the correct medium.

### `Surface`

- Spreads only on the exposed top layer of the valid fluid.
- Valid target: a fluid-filled cell in the correct medium with air above it.

## Output Rules

### `Neutral`

- Old-age death output: `air`

### `Fruiting`

- Produces fruit during lifecycle progression.
- Old-age death output: `air`
- When combined with `Surface`, fruit attaches to the side or top and does not hang below.

### `Wall-Forming`

- Frontier old-age death output: `sponge_remains`
- Non-frontier old-age death output: `air`
- Intended result with `Surface`: rings, rims, and perimeter structures

### `Solidifying`

- Any old-age death output: `sponge_remains`
- Intended result with `Surface`: filled crusts and platforms

## Frontier Rule

A sponge counts as a frontier sponge when:

- it has at least one adjacent valid growth target
- that target is not closer to the colony root than the sponge itself

This is intentionally a local frontier rule based on live colony geometry, not a perfect radial shell test.

## Combination Meaning

### Water Medium

- `Water + Volume + Neutral`
  - classic water-clearing colony
- `Water + Volume + Fruiting`
  - submerged fruit colony
- `Water + Volume + Wall-Forming`
  - underwater shell or ring maker
- `Water + Volume + Solidifying`
  - submerged fossilizing mass
- `Water + Surface + Neutral`
  - floating cleaner
- `Water + Surface + Fruiting`
  - floating orchard
- `Water + Surface + Wall-Forming`
  - floating ring maker
- `Water + Surface + Solidifying`
  - floating platform maker

### Magma Medium

- `Magma + Volume + Neutral`
  - lava-clearing colony
- `Magma + Volume + Fruiting`
  - lava fruit colony
- `Magma + Volume + Wall-Forming`
  - lava shell maker
- `Magma + Volume + Solidifying`
  - lava crust mass
- `Magma + Surface + Neutral`
  - lava skimmer
- `Magma + Surface + Fruiting`
  - lava surface orchard
- `Magma + Surface + Wall-Forming`
  - lava perimeter ring
- `Magma + Surface + Solidifying`
  - Nether platform builder

Each valid combination can use any radius tier:

- `Standard`
- `Expanded`
- `Vast`

## Naming Rule

Player-facing naming should follow this order:

- `Radius + Medium + Spread + Output + Living Sponge`

Examples:

- `Standard Water Volume Living Sponge`
- `Expanded Water Surface Fruiting Living Sponge`
- `Expanded Magma Surface Solidifying Living Sponge`

UI-friendly short forms are allowed as long as the underlying trait identity remains the same.

## Recommended Internal Model

Use explicit enums plus a resolved profile:

- `enum MediumTrait`
- `enum SpreadTrait`
- `enum OutputTrait`
- `enum RadiusTrait`
- `record SpongeProfile(...)`

Resolved profile fields should cover:

- valid fluid
- target predicate
- radius cap
- death output mode
- fruit mode
- frontier behavior

## Recommended Implementation Order

1. Add `RadiusTrait`.
2. Move current radius logic into resolved profile handling.
3. Add `OutputTrait.Solidifying`.
4. Add `Medium` and `Spread` specialization.
