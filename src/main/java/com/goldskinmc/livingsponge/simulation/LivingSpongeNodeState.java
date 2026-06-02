package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import net.minecraft.core.BlockPos;

import java.util.UUID;

public final class LivingSpongeNodeState {
    private final UUID colonyId;
    private final long rootPos;
    private final int generation;
    private final boolean creativeVariant;

    private int ageTicks;
    private int energy;
    private int fruitProgress;
    private int reproductionCooldownTicks;
    private int absorbedWaterLifetime;

    private LivingSpongeNodeState(
            final UUID colonyId,
            final BlockPos rootPos,
            final int generation,
            final boolean creativeVariant,
            final int energy
    ) {
        this.colonyId = colonyId;
        this.rootPos = rootPos.asLong();
        this.generation = generation;
        this.creativeVariant = creativeVariant;
        this.energy = Math.max(0, energy);
    }

    public static LivingSpongeNodeState createRoot(
            final BlockPos rootPos,
            final boolean creativeVariant,
            final LivingSpongeConfig.BalanceValues values
    ) {
        return new LivingSpongeNodeState(
                UUID.randomUUID(),
                rootPos,
                0,
                creativeVariant,
                values.energy().baseCapacity()
        );
    }

    public static LivingSpongeNodeState createChild(
            final LivingSpongeNodeState parent,
            final LivingSpongeConfig.BalanceValues values
    ) {
        return new LivingSpongeNodeState(
                parent.colonyId,
                parent.rootPos(),
                parent.generation + 1,
                parent.creativeVariant,
                values.energy().baseCapacity()
        );
    }

    public UUID colonyId() {
        return colonyId;
    }

    public BlockPos rootPos() {
        return BlockPos.of(rootPos);
    }

    public int generation() {
        return generation;
    }

    public boolean creativeVariant() {
        return creativeVariant;
    }

    public int ageTicks() {
        return ageTicks;
    }

    public int energy() {
        return energy;
    }

    public int fruitProgress() {
        return fruitProgress;
    }

    public int reproductionCooldownTicks() {
        return reproductionCooldownTicks;
    }

    public int absorbedWaterLifetime() {
        return absorbedWaterLifetime;
    }

    public void tickAge() {
        ageTicks++;
    }

    public void tickReproductionCooldown() {
        if (reproductionCooldownTicks > 0) {
            reproductionCooldownTicks--;
        }
    }

    public void setReproductionCooldownTicks(final int ticks) {
        reproductionCooldownTicks = Math.max(0, ticks);
    }

    public void addAbsorbedWaterLifetime(final int absorbed) {
        if (absorbed > 0) {
            absorbedWaterLifetime += absorbed;
        }
    }

    public void addFruitProgress(final int progress) {
        if (progress > 0) {
            fruitProgress += progress;
        }
    }

    public int consumeFruitProgress(final int threshold) {
        if (threshold <= 0) {
            return 0;
        }

        int produced = 0;
        while (fruitProgress >= threshold) {
            fruitProgress -= threshold;
            produced++;
        }
        return produced;
    }

    public void addEnergy(final int amount, final int capacity) {
        if (amount <= 0) {
            return;
        }
        energy = Math.min(Math.max(1, capacity), energy + amount);
    }

    public void drainEnergy(final int amount) {
        if (amount <= 0) {
            return;
        }
        energy = Math.max(0, energy - amount);
    }

    public LivingSpongeLifecycleStage stage(final LivingSpongeConfig.Lifecycle lifecycle) {
        return LivingSpongeLifecycleStage.fromAgeTicks(ageTicks, lifecycle);
    }
}
