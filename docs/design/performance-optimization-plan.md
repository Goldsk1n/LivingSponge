# Performance Optimization Plan

This document defines a behavior-preserving optimization plan for Living Sponge runtime performance.

The goal is to reduce server load without changing the intended gameplay rules for:

- lifecycle progression
- reproduction eligibility
- medium handling
- death output behavior
- radius caps
- creative overrides

## Optimization Principles

All performance changes must follow these rules:

1. Do not change visible sponge behavior unless the change is explicitly approved as gameplay tuning.
2. Prefer reducing repeated work over changing colony rules.
3. Prefer optimizations that preserve deterministic outcomes for the same world state.
4. Add verification steps after each optimization phase.

## Behavior Invariants

The following behavior must stay the same after optimization:

- `young -> mature -> old -> dead` lifecycle ordering
- old sponges do not reproduce
- reproduction still requires:
  - valid medium
  - valid target
  - cooldown complete
  - nearby medium present
  - target within radius cap
- `Neutral` old-age death leaves `air`
- `Wall-Forming` old-age death leaves `sponge_remains` only on the root-distance border shell
- `Solidifying` old-age death always leaves `sponge_remains`
- `Flat` spread still requires exposed top-layer fluid cells
- `Volume` spread still requires fluid-filled cells
- creative rules remain:
  - `4x` faster
  - radius forced to `Vast`
  - valid in both water and lava
  - ignores fire and opposing-fluid death
  - still dies from aging

If an optimization changes any of those outcomes, it is not a pure optimization and must be treated as a design change.

## Current Hot Paths

The main runtime costs are:

1. Repeated full-node iteration
- large colonies keep many active runtime nodes
- every eligible node is reconsidered each tick window

2. Repeated medium sampling
- nearby-medium scans walk a local cube per processed node
- neighboring nodes often scan highly overlapping space

3. Repeated reproduction-target discovery
- neighbor checks are re-run even for nodes that cannot reproduce anymore

4. High instantaneous load from creative colonies
- creative runs `4x` faster
- creative supports both water and lava
- creative `Volume` colonies can activate large 3D regions quickly

## Optimization Phases

Implement in this order, from lowest risk to highest payoff.

### Phase 1: Low-Risk Short-Circuiting

These changes should be done first because they reduce work without changing runtime structure.

#### 1. Skip reproduction-target scanning for `OLD` nodes

Reason:

- `OLD` nodes cannot reproduce already
- they do not need expensive target discovery

Implementation idea:

- stage-check before building reproduction target lists
- still age, phase-sync, and die normally

Expected impact:

- immediate reduction in wasted neighbor scanning

Risk:

- very low

#### 2. Skip reproduction-target scanning while cooldown is active

Reason:

- nodes with a positive reproduction cooldown cannot reproduce this update anyway

Implementation idea:

- compute only the minimum context needed first
- defer target collection until the node is actually eligible

Expected impact:

- reduces neighbor scanning on most mature colonies

Risk:

- low

#### 3. Separate cheap validity checks from expensive sampling

Reason:

- chunk validity, block-entity validity, and stage checks are cheaper than medium scans

Implementation idea:

- keep a two-step evaluation order:
  1. cheap early exits
  2. expensive medium and target sampling

Expected impact:

- reduces wasted scans on invalid or aging-out nodes

Risk:

- low

### Phase 2: Cheaper Sampling

These changes keep behavior the same but reduce repeated world reads.

#### 4. Give `Flat` its own medium-sampling path

Reason:

- `Flat` colonies do not need a full 3D medium search equivalent to `Volume`
- their meaningful medium neighborhood is much closer to 2D

Implementation idea:

- use a flat-specific sampling pattern around the active layer
- keep the same semantic meaning of "nearby medium present"

Important constraint:

- do not change which flat colonies are considered viable
- preserve the same practical spread eligibility wherever possible

Expected impact:

- significant reduction for large flat colonies

Risk:

- medium, because flat viability semantics must remain stable

#### 5. Add short-lived medium-scan caching

Reason:

- neighboring nodes repeatedly inspect overlapping fluid cells

Implementation idea:

- cache nearby-medium counts by position and game time bucket
- keep cache lifetime short, for example one update window

Important constraint:

- cached results must expire quickly enough that fluid changes still affect behavior naturally

Expected impact:

- moderate to high reduction in repeated world reads

Risk:

- medium

### Phase 3: Reduce Work Per Colony

These changes optimize the colony update model while preserving outcomes.

#### 6. Split lifecycle processing from spread processing

Reason:

- all nodes must age
- not all nodes need full reproduction work every update

Implementation idea:

- keep age and phase progression cheap and universal
- run expensive spread logic only for nodes that are actually eligible

Expected impact:

- high

Risk:

- medium, because update ordering must remain correct

#### 7. Add per-colony spread budgeting per server tick

Reason:

- large colonies can generate burst placement spikes even when the average load is acceptable

Implementation idea:

- limit how many child placements one colony can realize in one server tick
- carry the rest into later ticks

Important constraint:

- do not change whether a valid colony can eventually fill its allowed space
- only smooth timing spikes

Expected impact:

- high spike reduction

Risk:

- medium, because timing changes can become visible if tuned too aggressively

### Phase 4: Frontier-Focused Processing

This is the strongest structural optimization and should be done only after the lower-risk phases.

#### 8. Maintain a frontier-candidate set per colony

Reason:

- only a subset of nodes can meaningfully create new growth
- interior nodes dominate large colony counts

Implementation idea:

- track frontier candidates as a runtime-side colony structure
- update the frontier set only when:
  - a child is placed
  - a node dies
  - a local block changes invalidate growth

Important constraint:

- aging and death must still apply to all nodes
- only expensive spread eligibility should be frontier-focused

Expected impact:

- very high, especially for `Volume`

Risk:

- high, because this is a runtime model refactor

#### 9. Incrementally update local frontier neighborhoods

Reason:

- full colony-wide frontier rebuilds can erase the gains from frontier tracking

Implementation idea:

- after a local colony change, update only nearby candidates

Expected impact:

- high, complements frontier tracking

Risk:

- high

## Creative-Specific Guidance

Creative is the main stress branch, especially:

- `Creative + Volume + Neutral`
- `Creative + Volume + Solidifying`

Behavior-preserving optimization priorities for creative:

1. benefit from all general runtime optimizations above
2. prioritize frontier-focused processing for `Volume`
3. prioritize cheaper medium sampling for `Flat`

Do not optimize creative by silently changing:

- speed multiplier
- radius cap
- lifecycle durations
- death rules

Those are design levers, not performance optimizations.

## Explicitly Avoid

These ideas are likely to alter behavior too much or create fragile runtime logic:

- random reproduction skipping as a load reducer
- colony-wide hard reproduction caps as a hidden perf fix
- broad changes to radius logic to compensate for poor runtime scaling
- caching that survives long enough to ignore real fluid changes
- replacing old-age behavior with instant cleanup just to reduce node count

## Verification Plan

After each optimization phase:

1. Build verification
- `./gradlew build`

2. Manual behavior verification
- `Water + Volume + Neutral + Standard`
- `Water + Flat + Wall-Forming + Standard`
- `Water + Flat + Solidifying + Standard`
- `Magma + Volume + Neutral + Standard`
- `Magma + Flat + Solidifying + Standard`
- one creative flat profile
- one creative volume profile

3. Specific regression checks
- no missing phase swaps
- no invalid reproduction into air for `Volume`
- no missing exposed-layer requirement for `Flat`
- no incorrect border-shell remains for `Wall-Forming`
- no changed death behavior for `Neutral` or `Solidifying`
- no creative loss of water/lava dual-medium support
- no creative loss of age death

## Recommended Implementation Order

Use this order:

1. skip target scanning for `OLD`
2. skip target scanning while cooldown is active
3. keep cheap early exits before expensive scans
4. add flat-specific medium sampling
5. add short-lived medium-scan caching
6. split lifecycle work from spread work
7. add per-colony spread budgeting
8. move to frontier-focused processing

This order gives the best chance of measurable wins without destabilizing colony behavior.

## Success Criteria

The optimization work is successful when:

- large colonies process fewer expensive scans per server tick
- creative flat colonies are clearly cheaper than they are now
- creative volume colonies stop causing sharp tick spikes as early
- the same world state still produces the same visible colony behavior
- no trait rules need to be weakened just to keep runtime acceptable
