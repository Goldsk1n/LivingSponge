package com.goldskinmc.livingsponge.simulation;

import net.minecraft.core.BlockPos;

import java.util.Optional;

public record LivingSpongeTickResult(
        LivingSpongeLifecycleStage stage,
        int absorbedWaterBlocks,
        int fruitDrops,
        Optional<BlockPos> reproductionTarget,
        boolean shouldDie,
        int energyAfterTick
) {
    public LivingSpongeTickResult {
        reproductionTarget = reproductionTarget == null ? Optional.empty() : reproductionTarget;
    }
}
