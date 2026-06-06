# Sponge Trait UX Spec

This document defines the player-facing UX for trait-driven Living Sponge variants.

The current behavior system is workable, but the item and recipe presentation is too opaque. This spec fixes that by making trait identity explicit in item names, tooltips, and creative inventory organization.

## Goals

- Let a player identify a sponge's behavior before placing it.
- Make recipe outcomes predictable without memorizing every item id.
- Keep the current supported trait matrix understandable as it grows.
- Avoid committing to a custom workstation until the simpler UX path is proven insufficient.

## Core Principle

The important information is the sponge's traits, not its raw registry name.

Every player-facing sponge item should communicate:

- `Growth Medium`
- `Spread`
- `Output`
- `Radius`

This information should be visible in the tooltip every time. The item display name can be shorter and cleaner.

## Naming Scheme

All non-creative sponge variants should use the same display name:

- `Living Sponge`

Trait identity should not be encoded in the display name. It should be encoded entirely in tooltip data, similar to how fireworks carry behavior information in their tooltip instead of their item name.

This keeps the item list clean and prevents long generated names from becoming the primary UX.

Recommended naming rules:

- All survival variants use `Living Sponge`.
- The creative-only override item can keep `Creative Living Sponge`.
- Trait and radius differences are shown only in tooltips.

This means the player reads the tooltip to understand the sponge, not the registry-facing variant name.

## Tooltip Format

Every sponge item should have a structured tooltip in a fixed order.

The tooltip should behave like a compact trait card, comparable to firework metadata presentation: the item name stays simple, while the tooltip carries the meaningful variant data.

Recommended tooltip layout:

```text
Growth Medium: Water
Spread: Flat
Output: Wall-Forming
Radius: Expanded (16)

Only border-shell old-age deaths leave remains.
```

Formatting rules:

- First four lines are always trait lines.
- Fifth line onward is a concise behavior summary.
- Keep behavior summary to one or two short lines.
- Use the same field order on every sponge item.

Recommended behavior summary text by trait:

- `Water`
  - `Grows in water. Dies on lava contact.`
- `Magma`
  - `Grows in lava. Dies on water contact.`
- `Volume`
  - `Spreads through the body of the fluid.`
- `Flat`
  - `Spreads only across exposed flat fluid layers.`
- `Neutral`
  - `Leaves no special byproduct on old age death.`
- `Wall-Forming`
  - `Only frontier old-age deaths leave remains.`
- `Solidifying`
  - `All old-age deaths leave remains.`

Recommended radius text:

- `Standard (8)`
- `Expanded (16)`
- `Vast (256)`

## Tooltip Priorities

The tooltip should solve these questions immediately:

1. Where does this sponge live.
2. How does it spread.
3. What does it leave behind or produce.
4. How large can the colony become.

If the tooltip does not answer those four questions, it is incomplete.

## Creative Tab Organization

The creative tab should be grouped by behavior, not by raw registration order.

Recommended order:

1. Neutral water variants
   - `Living Sponge`
   - `Living Sponge`
   - `Living Sponge`
2. Neutral flat variants
   - `Living Sponge`
   - `Living Sponge`
   - `Living Sponge`
3. Wall-forming variants
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
4. Solidifying variants
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
    - `Living Sponge`
    - `Living Sponge`
    - `Living Sponge`
5. Magma neutral variants
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
6. Magma wall-forming variants
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
7. Magma solidifying variants
  - `Living Sponge`
  - `Living Sponge`
  - `Living Sponge`
    - `Living Sponge`
    - `Living Sponge`
    - `Living Sponge`
8. Support items and blocks
   - `Sponge Remains`
9. Creative-only utility
   - `Creative Living Sponge`

This order matches how a player is likely to evaluate the items.

Because the names are intentionally unified, ordering and tooltip clarity become more important. The player should discover differences by hovering, not by parsing long names.

## Recipe UX Direction

The current shapeless recipes are acceptable as a first usable step, but they are not the long-term ideal.

### Short-Term Recommendation

Keep direct crafting recipes, but make the trait meaning legible through tooltips and JEI.

Ingredient mapping should remain simple and mnemonic:

- `Lily Pad` -> `Flat`
- `Cobblestone Wall` -> `Wall-Forming`
- `Calcite` -> `Solidifying`
- `Magma Cream` -> `Magma`
- `Prismarine Crystals` -> `Expanded`
- `Heart of the Sea` -> `Vast`

This is already a reasonable visual language.

### Mid-Term Recommendation

Move toward a trait-component model:

- base sponge item
- trait modifiers or cores
- radius upgrade items

Example conceptual items:

- `Flat Membrane`
- `Wall-Forming Core`
- `Solidifying Core`
- `Magma Core`
- `Expanded Radius Core`
- `Vast Radius Core`

Benefits:

- the player thinks in traits directly
- recipes become more self-explanatory
- future combinator growth is easier to manage

### Long-Term Recommendation

Only add a custom workstation if one of these becomes true:

- recipe count becomes hard to navigate in JEI
- incompatibility handling becomes confusing
- players need to inspect or modify an existing sponge before placement

Until then, a workstation is extra complexity without enough payoff.

## Recommended Immediate UX Changes

Implemented baseline UX changes:

1. Structured tooltips on all sponge items.
2. Unified `Living Sponge` display name on survival variants.
3. Recipe outputs and JEI-facing items inherit the same tooltip identity.

Still recommended:

1. keep refining creative-tab grouping as the exposed matrix grows
2. add stronger in-world visual differentiation beyond tooltips

## Examples

### Water Flat Wall-Forming Expanded

Display name:

- `Living Sponge`

Tooltip:

```text
Growth Medium: Water
Spread: Flat
Output: Wall-Forming
Radius: Expanded (16)

Only frontier old-age deaths leave remains.
```

### Water Volume Neutral Standard

Display name:

- `Living Sponge`

Tooltip:

```text
Growth Medium: Water
Spread: Volume
Output: Neutral
Radius: Standard (8)

Spreads through the body of the fluid.
Leaves no special byproduct on old age death.
```

### Magma Flat Solidifying Vast

Display name:

- `Living Sponge`

Tooltip:

```text
Growth Medium: Lava
Spread: Flat
Output: Solidifying
Radius: Vast (256)

Grows in lava. Dies on water contact.
All old-age deaths leave remains.
```

## Decision

Use this approach for the next implementation pass:

- unified item names
- explicit trait tooltips
- grouped creative tab
- keep current direct recipes for now
- defer trait-component items and workstation UX until the current system is fully proven
