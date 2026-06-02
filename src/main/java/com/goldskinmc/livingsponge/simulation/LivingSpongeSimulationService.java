package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.Optional;

public final class LivingSpongeSimulationService {
    public LivingSpongeTickResult tickNode(
            final LivingSpongeNodeState state,
            final LivingSpongeTickContext context,
            final RandomSource random
    ) {
        final LivingSpongeConfig.BalanceValues values = LivingSpongeConfig.values();
        final LivingSpongeConfig.Energy energyConfig = values.energy();
        final LivingSpongeConfig.Spread spreadConfig = values.spread();
        final LivingSpongeConfig.Lifecycle lifecycleConfig = values.lifecycle();

        final boolean ignoreAge = state.creativeVariant() && values.creative().ignoresAge();
        final boolean ignoreEnergy = state.creativeVariant() && values.creative().ignoresEnergy();
        final boolean ignoreSpreadLimits = state.creativeVariant() && values.creative().ignoresSpreadLimits();
        final boolean creativeDropsEnabled = !state.creativeVariant() || values.creative().dropsEnabled();

        state.tickAge();
        state.tickReproductionCooldown();

        LivingSpongeLifecycleStage stage = state.stage(lifecycleConfig);

        if (!context.canStayActive()) {
            return new LivingSpongeTickResult(stage, 0, 0, Optional.empty(), true, state.energy());
        }

        if (values.containment().lavaInstantKill() && context.hasLavaContact()) {
            return new LivingSpongeTickResult(stage, 0, 0, Optional.empty(), true, state.energy());
        }

        final int cappedAbsorbed = Math.min(
                Math.max(0, context.absorbedWaterBlocks()),
                spreadConfig.maxAbsorbsPerUpdate()
        );
        final double absorbMultiplier = stage == LivingSpongeLifecycleStage.SENESCENT
                ? lifecycleConfig.senescentAbsorbMultiplier()
                : 1.0D;
        final int effectiveAbsorbed = (int) Math.floor(cappedAbsorbed * absorbMultiplier);

        if (!ignoreEnergy) {
            state.addEnergy(effectiveAbsorbed * energyConfig.gainPerWaterAbsorbed(), energyConfig.baseCapacity());
        }

        state.addAbsorbedWaterLifetime(effectiveAbsorbed);
        state.addFruitProgress(effectiveAbsorbed * values.fruit().progressPerWaterAbsorbed());

        if (!ignoreEnergy) {
            state.drainEnergy(energyConfig.idleDecayPerUpdate());
            if (context.hasFireContact()) {
                state.drainEnergy(values.containment().fireEnergyDrainPerUpdate());
            }
        }

        int fruitDrops = state.consumeFruitProgress(values.fruit().progressNeeded());
        if (fruitDrops > 0 && values.fruit().bonusDropChance() > 0.0D) {
            for (int i = 0; i < fruitDrops; i++) {
                if (random.nextDouble() < values.fruit().bonusDropChance()) {
                    fruitDrops++;
                }
            }
        }
        if (!creativeDropsEnabled) {
            fruitDrops = 0;
        }

        Optional<BlockPos> reproductionTarget = Optional.empty();
        if (canAttemptReproduction(state, context, values, stage, ignoreEnergy, ignoreSpreadLimits)) {
            final double chance = reproductionChance(state, values, stage);
            if (chance > 0.0D && random.nextDouble() < chance && !context.reproductionTargets().isEmpty()) {
                final int targetIndex = random.nextInt(context.reproductionTargets().size());
                reproductionTarget = Optional.of(context.reproductionTargets().get(targetIndex));
                state.setReproductionCooldownTicks(spreadConfig.reproductionCooldownTicks());
                if (!ignoreEnergy) {
                    state.drainEnergy(energyConfig.reproductionCost());
                }
            }
        }

        stage = state.stage(lifecycleConfig);
        final boolean deadFromAge = !ignoreAge && stage == LivingSpongeLifecycleStage.DEAD;
        final boolean deadFromEnergy = !ignoreEnergy && state.energy() <= 0;
        final boolean shouldDie = deadFromAge || deadFromEnergy;

        return new LivingSpongeTickResult(
                stage,
                effectiveAbsorbed,
                fruitDrops,
                reproductionTarget,
                shouldDie,
                state.energy()
        );
    }

    private static boolean canAttemptReproduction(
            final LivingSpongeNodeState state,
            final LivingSpongeTickContext context,
            final LivingSpongeConfig.BalanceValues values,
            final LivingSpongeLifecycleStage stage,
            final boolean ignoreEnergy,
            final boolean ignoreSpreadLimits
    ) {
        if (stage == LivingSpongeLifecycleStage.DEAD) {
            return false;
        }
        if (state.reproductionCooldownTicks() > 0) {
            return false;
        }
        if (context.reproductionTargets().isEmpty()) {
            return false;
        }

        if (!ignoreSpreadLimits) {
            if (context.colonyChildren() >= values.spread().maxChildrenPerColony()) {
                return false;
            }
            if (context.distanceFromRoot() > values.spread().maxColonyRadius()) {
                return false;
            }
        }

        return ignoreEnergy || state.energy() >= values.energy().minToReproduce();
    }

    private static double reproductionChance(
            final LivingSpongeNodeState state,
            final LivingSpongeConfig.BalanceValues values,
            final LivingSpongeLifecycleStage stage
    ) {
        final LivingSpongeConfig.Spread spread = values.spread();
        final LivingSpongeConfig.Energy energy = values.energy();
        final int surplus = Math.max(0, state.energy() - energy.minToReproduce());
        final double bonus = Math.min(
                spread.reproductionEnergyBonusCap(),
                surplus * spread.reproductionEnergyBonusPerPoint()
        );

        final double stageMultiplier = switch (stage) {
            case YOUNG -> values.lifecycle().youngReproductionMultiplier();
            case MATURE -> values.lifecycle().matureReproductionMultiplier();
            case SENESCENT -> values.lifecycle().senescentReproductionMultiplier();
            case DEAD -> 0.0D;
        };

        final double base = (spread.reproductionBaseChance() + bonus) * stageMultiplier;
        return Math.min(spread.reproductionChanceCap(), Math.max(0.0D, base));
    }
}
