package com.goldskinmc.livingsponge.simulation;

import net.minecraft.core.BlockPos;

import java.util.Optional;

public record LivingSpongeTickResult(
        LivingSpongeLifecycleStage stage,
        Optional<BlockPos> reproductionTarget,
        Optional<BlockPos> fruitTarget,
        boolean shouldDie,
        LivingSpongeDeathReason deathReason
) {
    public LivingSpongeTickResult {
        reproductionTarget = reproductionTarget == null ? Optional.empty() : reproductionTarget;
        fruitTarget = fruitTarget == null ? Optional.empty() : fruitTarget;
        deathReason = deathReason == null ? LivingSpongeDeathReason.NONE : deathReason;
    }
}
