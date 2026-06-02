package com.goldskinmc.livingsponge.simulation;

import net.minecraft.core.BlockPos;

import java.util.List;

public record LivingSpongeTickContext(
        boolean canStayActive,
        int absorbedWaterBlocks,
        boolean hasLavaContact,
        boolean hasFireContact,
        List<BlockPos> reproductionTargets,
        int colonyChildren,
        int distanceFromRoot
) {
    public LivingSpongeTickContext {
        reproductionTargets = List.copyOf(reproductionTargets);
    }
}
