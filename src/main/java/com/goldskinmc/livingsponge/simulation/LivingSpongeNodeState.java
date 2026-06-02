package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public final class LivingSpongeNodeState {
    private final UUID colonyId;
    private final long rootPos;
    private final int generation;
    private final boolean creativeVariant;

    private int ageTicks;
    private int reproductionCooldownTicks;

    private LivingSpongeNodeState(
            final UUID colonyId,
            final BlockPos rootPos,
            final int generation,
            final boolean creativeVariant,
            final int reproductionCooldownTicks
    ) {
        this.colonyId = colonyId;
        this.rootPos = rootPos.asLong();
        this.generation = generation;
        this.creativeVariant = creativeVariant;
        this.reproductionCooldownTicks = Math.max(0, reproductionCooldownTicks);
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
                initialReproductionCooldown(creativeVariant, values.spread().reproductionCooldownTicks())
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
                initialReproductionCooldown(parent.creativeVariant, values.spread().reproductionCooldownTicks())
        );
    }

    public static LivingSpongeNodeState load(final CompoundTag tag) {
        final LivingSpongeNodeState state = new LivingSpongeNodeState(
                tag.getUUID("ColonyId"),
                BlockPos.of(tag.getLong("RootPos")),
                tag.getInt("Generation"),
                tag.getBoolean("CreativeVariant"),
                tag.getInt("ReproductionCooldownTicks")
        );
        state.ageTicks = tag.getInt("AgeTicks");
        return state;
    }

    public CompoundTag save() {
        final CompoundTag tag = new CompoundTag();
        tag.putUUID("ColonyId", colonyId);
        tag.putLong("RootPos", rootPos);
        tag.putInt("Generation", generation);
        tag.putBoolean("CreativeVariant", creativeVariant);
        tag.putInt("AgeTicks", ageTicks);
        tag.putInt("ReproductionCooldownTicks", reproductionCooldownTicks);
        return tag;
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

    public int reproductionCooldownTicks() {
        return reproductionCooldownTicks;
    }

    public void tickAge(final int elapsedTicks) {
        ageTicks += Math.max(0, elapsedTicks);
    }

    public void tickReproductionCooldown(final int elapsedTicks) {
        reproductionCooldownTicks = Math.max(0, reproductionCooldownTicks - Math.max(0, elapsedTicks));
    }

    public void setReproductionCooldownTicks(final int ticks) {
        reproductionCooldownTicks = Math.max(0, ticks);
    }

    public LivingSpongeLifecycleStage stage(final LivingSpongeConfig.Lifecycle lifecycle, final boolean creativeVariant) {
        return LivingSpongeLifecycleStage.fromAgeTicks(ageTicks, adjustedLifecycle(lifecycle, creativeVariant));
    }

    private static LivingSpongeConfig.Lifecycle adjustedLifecycle(
            final LivingSpongeConfig.Lifecycle lifecycle,
            final boolean creativeVariant
    ) {
        if (!creativeVariant) {
            return lifecycle;
        }

        return new LivingSpongeConfig.Lifecycle(
                halveTicks(lifecycle.youngDurationTicks()),
                halveTicks(lifecycle.matureDurationTicks()),
                halveTicks(lifecycle.oldDurationTicks()),
                lifecycle.frontierRemainsChance(),
                lifecycle.nonFrontierHydroBlockChance()
        );
    }

    private static int initialReproductionCooldown(final boolean creativeVariant, final int baseCooldown) {
        return creativeVariant ? halveTicks(baseCooldown) : baseCooldown;
    }

    private static int halveTicks(final int ticks) {
        return Math.max(1, ticks / 2);
    }
}
