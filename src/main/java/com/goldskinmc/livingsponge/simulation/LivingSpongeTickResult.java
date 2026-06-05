package com.goldskinmc.livingsponge.simulation;

import net.minecraft.core.BlockPos;

import java.util.Optional;

public record LivingSpongeTickResult(
        LivingSpongeLifecycleStage stage,
        Optional<BlockPos> reproductionTarget,
        boolean shouldDie,
        LivingSpongeDeathReason deathReason
) {
    public LivingSpongeTickResult {
        reproductionTarget = reproductionTarget == null ? Optional.empty() : reproductionTarget;
        deathReason = deathReason == null ? LivingSpongeDeathReason.NONE : deathReason;
    }
}
