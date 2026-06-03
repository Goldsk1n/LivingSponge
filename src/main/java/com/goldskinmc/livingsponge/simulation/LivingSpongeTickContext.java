package com.goldskinmc.livingsponge.simulation;

import net.minecraft.core.BlockPos;

import java.util.List;

public record LivingSpongeTickContext(
        boolean canStayActive,
        int nearbyMediumBlocks,
        boolean hasOpposingFluidContact,
        boolean hasFireContact,
        List<BlockPos> reproductionTargets,
        List<BlockPos> fruitTargets,
        int colonyChildren,
        int distanceFromRoot
) {
    public LivingSpongeTickContext {
        reproductionTargets = List.copyOf(reproductionTargets);
        fruitTargets = List.copyOf(fruitTargets);
    }
}
