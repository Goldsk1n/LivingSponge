# Texture and Visual Identity Plan

This document defines a simple, readable, and player-friendly visual plan for Living Sponge blocks.

## Goal

A player should be able to understand the most important sponge traits after placement without opening a tooltip.

The block textures should communicate:

- lifecycle phase
- medium type
- output type

The textures should **not** try to communicate:

- spread type
- radius tier

Those traits are better left to behavior and tooltips. Putting too much information into the block surface will make the blocks noisy and hard to read.

## Visual Priority

Use this hierarchy:

1. `Lifecycle` = strongest visual cue
2. `Medium` = second strongest cue
3. `Output` = third strongest cue

This keeps the visuals understandable at a glance.

## Player-Facing Rules

At a glance, players should be able to answer:

1. Is this young, mature, or old?
2. Is this a water sponge, magma sponge, or creative sponge?
3. Is this neutral, wall-forming, or solidifying?

If the block does not answer those three questions visually, the texture system is too weak.

## Lifecycle Language

Lifecycle should drive the base body color and overall energy level.

### Young

- lightest value
- softest contrast
- reads as fresh and active

Current placeholder direction:

- light green / lime family

### Mature

- richest saturation
- strongest definition
- reads as healthy and fully active

Current placeholder direction:

- green family

### Old

- desaturated
- drier / ashier
- reads as weakened and close to death

Current placeholder direction:

- light gray family

## Medium Language

Medium should be shown through accent hue and internal detail, not by replacing the whole lifecycle palette.

### Water

Visual cues:

- cool cyan / teal pores, veins, or damp streaks
- moist, organic look

Player read:

- belongs in water

### Magma

Visual cues:

- ember orange / red fissures
- heat-darkened shell
- stronger contrast than water

Player read:

- belongs in lava

### Creative

Visual cues:

- dual-medium, tool-like look
- turquoise-white or cyan-gold luminous filaments
- cleaner and more deliberate than survival variants

Player read:

- special creative-only tool
- not just another survival sponge

Important:

- creative should be immediately distinguishable from both water and magma
- do not simply recolor it slightly

## Output Language

Output should be shown by surface pattern and shell structure.

### Neutral

Visual cues:

- clean porous body
- least ornamented
- no hard shell emphasis

Player read:

- pure growth / clearing type

### Wall-Forming

Visual cues:

- clear edge banding
- shell-like perimeter striations
- slight rim or ring implication

Player read:

- forms boundaries, rims, or outlines

### Solidifying

Visual cues:

- denser crust patches
- calcified plates or nodules
- visually heavier than neutral

Player read:

- hardens into filled structures

Important:

- wall-forming and solidifying should not be distinguished only by color
- they need different pattern logic

## Combined Visual Matrix

Use:

- `3` lifecycle phases
- `3` medium families
- `3` output families

Conceptually that is `27` combinations, but the currently exposed gameplay set is smaller.

Actual currently useful combinations:

- `Water + Neutral`
- `Water + Wall-Forming`
- `Water + Solidifying`
- `Magma + Neutral`
- `Magma + Wall-Forming`
- `Magma + Solidifying`
- `Creative + Neutral`
- `Creative + Solidifying`

Creative wall-forming is intentionally not needed.

That means the first practical pass needs:

- `8` visual profiles per lifecycle phase
- total `24` sponge textures

This is large but manageable, and far simpler than trying to encode everything dynamically through procedural rendering.

## Recommended Texture Style

Keep the overall form readable and Minecraft-like:

- simple large shapes first
- medium-detail pores and cracks second
- avoid tiny noisy pixel detail

Use this structure on each texture:

1. Base body color from lifecycle
2. Accent hue from medium
3. Pattern overlay from output

That means:

- lifecycle changes the body
- medium changes the accent
- output changes the pattern

This is the cleanest system for artists and for players.

## Asset Strategy

Keep implementation simple.

### Phase 1: Static all-face textures

Use one `cube_all` texture per visual profile.

Reason:

- current models already use `cube_all`
- this avoids top/side/bottom complexity in the first pass
- easiest way to ship readable visuals quickly

So the first pass remains:

- one model per lifecycle phase
- one texture per visual profile per phase

### Phase 2: Optional face specialization

Only if needed later:

- give top faces slightly stronger output cues
- give side faces slightly stronger medium cues

Do not start here.

The first pass should stay simple.

## Implementation Direction

The current block structure already separates lifecycle by block id:

- `living_sponge`
- `mature_living_sponge`
- `old_living_sponge`
- creative equivalents

That is good. Keep it.

To add medium/output visuals cleanly:

### Recommended runtime/blockstate approach

- keep the current phase blocks
- add one visual-profile blockstate property shared by sponge phase blocks
- derive that profile from:
  - medium trait
  - output trait
  - creative override

That lets lifecycle remain a block id while medium/output become a small, explicit state enum.

Example visual profile enum values:

- `water_neutral`
- `water_wall_forming`
- `water_solidifying`
- `magma_neutral`
- `magma_wall_forming`
- `magma_solidifying`
- `creative_neutral`
- `creative_solidifying`

This is much simpler than exploding the block registry for every trait combination.

## What Not To Visualize

Do not texture-code:

- `Flat` vs `Volume` in a strong way
- `Standard` vs `Expanded` vs `Vast`

Reason:

- spread is better learned through placement behavior
- radius is not a block identity

If desired later, spread can get a subtle cue:

- flat = slightly layered horizontal grain
- volume = fuller porous body

But do not prioritize that over lifecycle, medium, and output.

## Example Readability Goals

From a short distance:

- young water neutral = pale green sponge with cool damp accents and plain pores
- mature magma wall-forming = rich green/dark body with ember seams and ring-shell banding
- old water solidifying = faded gray sponge with cool moisture traces and heavy calcified crust
- creative solidifying = special bright creative sponge with dual-medium accents and obvious hardened crust

If those reads are possible at a glance, the system is doing its job.

## Success Criteria

The texture redesign is successful when:

- players can identify lifecycle, medium, and output without opening a tooltip
- creative looks clearly special
- wall-forming and solidifying are easy to tell apart
- the textures still feel like Minecraft blocks, not noisy UI panels
- implementation stays manageable without blowing up the block registry
