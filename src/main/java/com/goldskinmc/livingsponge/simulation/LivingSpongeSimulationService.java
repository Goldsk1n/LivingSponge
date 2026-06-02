package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.Optional;

public final class LivingSpongeSimulationService {
    public LivingSpongeTickResult tickNode(
            final LivingSpongeNodeState state,
            final LivingSpongeTickContext context,
            final RandomSource random,
            final int elapsedTicks
    ) {
        final LivingSpongeConfig.BalanceValues values = LivingSpongeConfig.values();
        final LivingSpongeConfig.Spread spreadConfig = values.spread();
        final LivingSpongeConfig.Lifecycle lifecycleConfig = values.lifecycle();
        final int reproductionCooldownTicks = state.creativeVariant()
                ? halveTicks(spreadConfig.reproductionCooldownTicks())
                : spreadConfig.reproductionCooldownTicks();

        state.tickAge(elapsedTicks);
        state.tickReproductionCooldown(elapsedTicks);

        LivingSpongeLifecycleStage stage = state.stage(lifecycleConfig, state.creativeVariant());

        if (!context.canStayActive()) {
            return new LivingSpongeTickResult(stage, Optional.empty(), true);
        }

        if ((values.containment().lavaInstantKill() && context.hasLavaContact()) || context.hasFireContact()) {
            return new LivingSpongeTickResult(stage, Optional.empty(), true);
        }

        Optional<BlockPos> reproductionTarget = Optional.empty();
        if (canAttemptReproduction(state, context, values, stage)) {
            final int targetIndex = random.nextInt(context.reproductionTargets().size());
            reproductionTarget = Optional.of(context.reproductionTargets().get(targetIndex));
            state.setReproductionCooldownTicks(reproductionCooldownTicks);
        }

        stage = state.stage(lifecycleConfig, state.creativeVariant());
        final boolean shouldDie = stage == LivingSpongeLifecycleStage.DEAD;

        return new LivingSpongeTickResult(
                stage,
                reproductionTarget,
                shouldDie
        );
    }

    private static boolean canAttemptReproduction(
            final LivingSpongeNodeState state,
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
        if (context.absorbedWaterBlocks() <= 0) {
            return false;
        }
        if (context.distanceFromRoot() > values.spread().maxColonyRadius()) {
            return false;
        }
        return true;
    }

    private static int halveTicks(final int ticks) {
        return Math.max(1, ticks / 2);
    }
}
