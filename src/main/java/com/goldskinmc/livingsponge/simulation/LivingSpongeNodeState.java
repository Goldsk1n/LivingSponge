package com.goldskinmc.livingsponge.simulation;

import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import com.goldskinmc.livingsponge.simulation.profile.ResolvedSpongeProfile;
import com.goldskinmc.livingsponge.simulation.profile.SpongeTraits;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class LivingSpongeNodeState {
    private final UUID colonyId;
    private final long rootPos;
    private final int generation;
    private final SpongeTraits traits;
    private final boolean creativeOverrides;

    private int ageTicks;
    private int reproductionCooldownTicks;
    private LivingSpongeNodeState(
            final UUID colonyId,
            final BlockPos rootPos,
            final int generation,
            final SpongeTraits traits,
            final boolean creativeOverrides,
            final int reproductionCooldownTicks
    ) {
        this.colonyId = colonyId;
        this.rootPos = rootPos.asLong();
        this.generation = generation;
        this.traits = traits;
        this.creativeOverrides = creativeOverrides;
        this.reproductionCooldownTicks = Math.max(0, reproductionCooldownTicks);
    }

    public static LivingSpongeNodeState createRoot(
            final BlockPos rootPos,
            final SpongeTraits traits,
            final boolean creativeOverrides,
            final LivingSpongeConfig.BalanceValues values
    ) {
        final ResolvedSpongeProfile profile = new ResolvedSpongeProfile(traits, creativeOverrides);
        return new LivingSpongeNodeState(
                UUID.randomUUID(),
                rootPos,
                0,
                traits,
                creativeOverrides,
                profile.reproductionCooldownTicks(values)
        );
    }

    public static LivingSpongeNodeState createPlacedRoot(
            final BlockPos rootPos,
            final SpongeTraits traits,
            final boolean creativeOverrides,
            final LivingSpongeConfig.BalanceValues values
    ) {
        final LivingSpongeNodeState state = createRoot(rootPos, traits, creativeOverrides, values);
        if (values.lifecycle().placedSpongesStartMature()) {
            state.ageTicks = state.resolveProfile().lifecycle(values).youngDurationTicks();
        }
        return state;
    }

    public static LivingSpongeNodeState createChild(
            final LivingSpongeNodeState parent,
            final LivingSpongeConfig.BalanceValues values
    ) {
        final ResolvedSpongeProfile profile = parent.resolveProfile();
        return new LivingSpongeNodeState(
                parent.colonyId,
                parent.rootPos(),
                parent.generation + 1,
                parent.traits,
                parent.creativeOverrides,
                profile.reproductionCooldownTicks(values)
        );
    }

    public static @Nullable LivingSpongeNodeState load(final CompoundTag tag) {
        if (!tag.contains("Traits") || !tag.contains("CreativeOverrides")) {
            return null;
        }

        final LivingSpongeNodeState state = new LivingSpongeNodeState(
                tag.getUUID("ColonyId"),
                BlockPos.of(tag.getLong("RootPos")),
                tag.getInt("Generation"),
                SpongeTraits.load(tag.getCompound("Traits")),
                tag.getBoolean("CreativeOverrides"),
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
        tag.put("Traits", traits.save());
        tag.putBoolean("CreativeOverrides", creativeOverrides);
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

    public SpongeTraits traits() {
        return traits;
    }

    public boolean creativeOverrides() {
        return creativeOverrides;
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

    public ResolvedSpongeProfile resolveProfile() {
        return new ResolvedSpongeProfile(traits, creativeOverrides);
    }

    public LivingSpongeLifecycleStage stage(final LivingSpongeConfig.BalanceValues values) {
        return LivingSpongeLifecycleStage.fromAgeTicks(ageTicks, resolveProfile().lifecycle(values));
    }
}
