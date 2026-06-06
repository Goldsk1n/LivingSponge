package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import com.goldskinmc.livingsponge.simulation.profile.ResolvedSpongeProfile;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Optional;

public final class LivingSpongeSimulationService {
    public LivingSpongeTickResult tickLifecycle(
            final LivingSpongeNodeState state,
            final boolean canStayActive,
            final boolean hasOpposingFluidContact,
            final boolean hasFireContact,
            final LivingSpongeConfig.BalanceValues values,
            final int elapsedTicks
    ) {
        state.tickAge(elapsedTicks);
        state.tickReproductionCooldown(elapsedTicks);

        LivingSpongeLifecycleStage stage = state.stage(values);

        if (!canStayActive) {
            return new LivingSpongeTickResult(stage, Optional.empty(), true, LivingSpongeDeathReason.INVALID_STATE);
        }

        if (hasOpposingFluidContact || hasFireContact) {
            return new LivingSpongeTickResult(stage, Optional.empty(), true, LivingSpongeDeathReason.ENVIRONMENT);
        }

        stage = state.stage(values);
        final boolean shouldDie = stage == LivingSpongeLifecycleStage.DEAD;

        return new LivingSpongeTickResult(
                stage,
                Optional.empty(),
                shouldDie,
                shouldDie ? LivingSpongeDeathReason.AGING : LivingSpongeDeathReason.NONE
        );
    }

    public boolean canAttemptReproduction(
            final LivingSpongeNodeState state,
            final ResolvedSpongeProfile profile,
            final LivingSpongeConfig.BalanceValues values,
            final LivingSpongeLifecycleStage stage,
            final int nearbyMediumBlocks,
            final List<net.minecraft.core.BlockPos> reproductionTargets,
            final int distanceFromRoot
    ) {
        if (stage == LivingSpongeLifecycleStage.DEAD || stage == LivingSpongeLifecycleStage.OLD) {
            return false;
        }
        if (state.reproductionCooldownTicks() > 0) {
            return false;
        }
        if (reproductionTargets.isEmpty()) {
            return false;
        }
        if (nearbyMediumBlocks <= 0) {
            return false;
        }
        if (distanceFromRoot > profile.radiusCap(values)) {
            return false;
        }
        return true;
    }

    public Optional<net.minecraft.core.BlockPos> chooseReproductionTarget(
            final LivingSpongeNodeState state,
            final ResolvedSpongeProfile profile,
            final RandomSource random,
            final LivingSpongeConfig.BalanceValues values,
            final List<net.minecraft.core.BlockPos> reproductionTargets
    ) {
        if (reproductionTargets.isEmpty()) {
            return Optional.empty();
        }

        final int targetIndex = random.nextInt(reproductionTargets.size());
        state.setReproductionCooldownTicks(profile.reproductionCooldownTicks(values));
        return Optional.of(reproductionTargets.get(targetIndex));
    }

}
