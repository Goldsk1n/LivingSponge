# Living Sponge

Living Sponge is a Minecraft Forge mod that adds self-spreading sponge colonies for water and lava.

Each sponge is defined by four trait slots:

- `Growth Medium`: `Water` or `Lava`
- `Spread`: `Volume` or `Flat`
- `Output`: `Neutral`, `Wall-Forming`, or `Solidifying`
- `Radius`: `Standard`, `Expanded`, or `Huge`

The mod includes a dedicated creative tab and configurable balance settings.

## What The Mod Does

Living sponges grow through valid fluid and age through three phases:

- `Young`
- `Living`
- `Old`

When they reach old age, the output trait controls the result:

- `Neutral`: leaves nothing behind
- `Wall-Forming`: leaves `Sponge Remains` on the colony border shell
- `Solidifying`: leaves `Sponge Remains` everywhere on old-age death

Spread types:

- `Volume`: spreads through the body of the fluid
- `Flat`: spreads only across exposed flat fluid surfaces

Medium types:

- `Water`: grows in water
- `Lava`: grows in lava

Radius tiers:

- `Standard`: `8`
- `Expanded`: `16`
- `Huge`: `256`

## Crafting Progression

### 1. Vanilla Sponge

The mod can optionally enable a custom vanilla sponge recipe:

- `Any Wool` + `Kelp` + `Seagrass` -> `Sponge`

This recipe is enabled by default and can be disabled in config.

### 2. Base Living Sponges

- `Sponge` + `Slime Ball` -> `Living Sponge`
- `Sponge` + `Magma Cream` -> `Magma Living Sponge`

### 3. Output / Spread Modifiers

- `Lily Pad` -> `Flat`
- `Cobblestone Wall` -> `Wall-Forming`
- `Clay Block` -> `Solidifying`

### 4. Radius Upgrades

- `Ender Pearl` -> `Expanded`
- `Ender Eye` -> `Huge`

## Important Gameplay Notes

- Living sponges do **not** drop themselves when mined.
- `Sponge Remains` can be mined and recovered.
- Living sponge blocks cannot be moved by pistons.
- Player-placed sponge roots start at the mature phase by default.
- Creative variants are faster, use huge radius by default, and can be configured to work in water and lava.

## Tooltip System

All survival sponge items use the shared display name `Living Sponge`.

Trait identity is shown in the tooltip:

- `Growth Medium`
- `Spread`
- `Output`
- `Radius`

This keeps item names clean while still exposing full behavior.

## Configuration

Main config file:

- `config/livingsponge-common.toml`

Current configurable areas include:

- lifecycle timings
- spread timings and cooldowns
- flat diagonal spread
- radius values
- creative sponge behavior
- wall-forming shell thickness
- custom vanilla sponge recipe toggle

## Creative Tab

The mod adds its own creative tab:

- `Living Sponge`

It contains:

- all survival sponge variants
- all magma variants
- creative variants
- `Sponge Remains`

## License

`All Rights Reserved`
