package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import com.goldskinmc.livingsponge.simulation.profile.ResolvedSpongeProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.Optional;

public final class LivingSpongeSimulationService {
    public LivingSpongeTickResult tickNode(
            final LivingSpongeNodeState state,
            final ResolvedSpongeProfile profile,
            final LivingSpongeTickContext context,
            final RandomSource random,
            final LivingSpongeConfig.BalanceValues values,
            final int elapsedTicks
    ) {
        final int reproductionCooldownTicks = profile.reproductionCooldownTicks(values);

        state.tickAge(elapsedTicks);
        state.tickReproductionCooldown(elapsedTicks);

        LivingSpongeLifecycleStage stage = state.stage(values);

        if (!context.canStayActive()) {
            return new LivingSpongeTickResult(stage, Optional.empty(), Optional.empty(), true, LivingSpongeDeathReason.INVALID_STATE);
        }

        if (context.hasOpposingFluidContact() || context.hasFireContact()) {
            return new LivingSpongeTickResult(stage, Optional.empty(), Optional.empty(), true, LivingSpongeDeathReason.ENVIRONMENT);
        }

        Optional<BlockPos> reproductionTarget = Optional.empty();
        if (canAttemptReproduction(state, profile, context, values, stage)) {
            final int targetIndex = random.nextInt(context.reproductionTargets().size());
            reproductionTarget = Optional.of(context.reproductionTargets().get(targetIndex));
            state.setReproductionCooldownTicks(reproductionCooldownTicks);
        }

        Optional<BlockPos> fruitTarget = Optional.empty();
        if (canProduceFruit(profile, stage, context)) {
            state.addFruitProgress(context.nearbyMediumBlocks() * values.fruit().progressPerNearbyMediumBlock());
            if (state.fruitProgress() >= values.fruit().progressNeeded()) {
                final int targetIndex = random.nextInt(context.fruitTargets().size());
                fruitTarget = Optional.of(context.fruitTargets().get(targetIndex));
                state.consumeFruitProgress(values.fruit().progressNeeded());
            }
        }

        stage = state.stage(values);
        final boolean shouldDie = stage == LivingSpongeLifecycleStage.DEAD;

        return new LivingSpongeTickResult(
                stage,
                reproductionTarget,
                fruitTarget,
                shouldDie,
                shouldDie ? LivingSpongeDeathReason.AGING : LivingSpongeDeathReason.NONE
        );
    }

    private static boolean canAttemptReproduction(
            final LivingSpongeNodeState state,
            final ResolvedSpongeProfile profile,
            final LivingSpongeTickContext context,
            final LivingSpongeConfig.BalanceValues values,
            final LivingSpongeLifecycleStage stage
    ) {
        if (stage == LivingSpongeLifecycleStage.DEAD || stage == LivingSpongeLifecycleStage.OLD) {
            return false;
        }
        if (state.reproductionCooldownTicks() > 0) {
            return false;
        }
        if (context.reproductionTargets().isEmpty()) {
            return false;
        }
        if (context.nearbyMediumBlocks() <= 0) {
            return false;
        }
        if (context.distanceFromRoot() > profile.radiusCap(values)) {
            return false;
        }
        return true;
    }

    private static boolean canProduceFruit(
            final ResolvedSpongeProfile profile,
            final LivingSpongeLifecycleStage stage,
            final LivingSpongeTickContext context
    ) {
        if (!profile.isFruitingOutput()) {
            return false;
        }
        if (stage != LivingSpongeLifecycleStage.MATURE && stage != LivingSpongeLifecycleStage.OLD) {
            return false;
        }
        if (context.nearbyMediumBlocks() <= 0) {
            return false;
        }
        return !context.fruitTargets().isEmpty();
    }
}
