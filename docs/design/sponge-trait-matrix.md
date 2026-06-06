# Sponge Trait Matrix

This document defines the composable trait system used by Living Sponge variants.

## Trait Slots

A sponge instance is composed from four trait slots:

- `Medium`
  - `Water`
  - `Magma`
- `Spread`
  - `Volume`
  - `Flat`
- `Output`
  - `Neutral`
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
- `Water + Flat + Wall-Forming + Expanded`
- `Magma + Flat + Solidifying + Vast`

## Radius Tiers

- `Standard` = `8`
- `Expanded` = `16`
- `Vast` = `256`

`Vast` is treated as practically unbounded, but it is still capped in code.

## Hard Rules

- All radius tiers share the same lifecycle rules unless explicitly changed later.

## Medium Rules

### `Water`

- Grows in water.
- Dies on lava contact.

### `Magma`

- Grows in lava.
- Dies on water contact.

## Spread Rules

### `Volume`

- Spreads through the body of the valid fluid.
- Valid target: a fluid-filled cell in the correct medium.

### `Flat`

- Spreads only on the exposed top layer of the valid fluid.
- Valid target: a fluid-filled cell in the correct medium with air above it.

## Output Rules

### `Neutral`

- Old-age death output: `air`

### `Wall-Forming`

- Frontier old-age death output: `sponge_remains`
- Non-frontier old-age death output: `air`
- Intended result with `Flat`: rings, rims, and perimeter structures

### `Solidifying`

- Any old-age death output: `sponge_remains`
- Intended result with `Flat`: filled crusts and platforms

## Frontier Rule

For wall-forming behavior, a sponge counts as a border-shell sponge when:

- its Chebyshev distance from the colony root is equal to the active radius cap

This is an explicit root-distance shell rule, not a live frontier-geometry heuristic.

## Combination Meaning

### Water Medium

- `Water + Volume + Neutral`
  - classic water-clearing colony
- `Water + Volume + Wall-Forming`
  - underwater shell or ring maker
- `Water + Volume + Solidifying`
  - submerged fossilizing mass
- `Water + Flat + Neutral`
  - floating cleaner
- `Water + Flat + Wall-Forming`
  - floating ring maker
- `Water + Flat + Solidifying`
  - floating platform maker

### Magma Medium

- `Magma + Volume + Neutral`
  - lava-clearing colony
- `Magma + Volume + Wall-Forming`
  - lava shell maker
- `Magma + Volume + Solidifying`
  - lava crust mass
- `Magma + Flat + Neutral`
  - lava skimmer
- `Magma + Flat + Wall-Forming`
  - lava perimeter ring
- `Magma + Flat + Solidifying`
  - Nether platform builder

Each valid combination can use any radius tier:

- `Standard`
- `Expanded`
- `Vast`

## Player-Facing Identity

All survival variants use the display name:

- `Living Sponge`

Trait identity is communicated through the tooltip, not the display name.

The tooltip shows:

- `Growth Medium`
- `Spread`
- `Output`
- `Radius`

`Creative Living Sponge` remains the only named exception.

## Internal Model

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
- border-shell behavior

## Current Exposure

The full supported matrix is currently exposed for gameplay:

- `Water + Volume + Neutral`
- `Water + Flat + Neutral`
- `Water + Volume + Wall-Forming`
- `Water + Flat + Wall-Forming`
- `Water + Volume + Solidifying`
- `Water + Flat + Solidifying`
- `Magma + Volume + Neutral`
- `Magma + Flat + Neutral`
- `Magma + Volume + Wall-Forming`
- `Magma + Flat + Wall-Forming`
- `Magma + Volume + Solidifying`
- `Magma + Flat + Solidifying`

Each of those combinations supports:

- `Standard`
- `Expanded`
- `Vast`

The base `Living Sponge` item family is neutral by default.
