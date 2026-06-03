package com.goldskinmc.livingsponge.world.item;

import com.goldskinmc.livingsponge.simulation.profile.SpongeTraits;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public final class LivingSpongePlacementItem extends BlockItem {
    private final SpongeTraits traits;
    private final boolean creativeOverrides;

    public LivingSpongePlacementItem(
            final Block block,
            final Properties properties,
            final SpongeTraits traits,
            final boolean creativeOverrides
    ) {
        super(block, properties);
        this.traits = traits;
        this.creativeOverrides = creativeOverrides;
    }

    public SpongeTraits traits() {
        return traits;
    }

    public boolean creativeOverrides() {
        return creativeOverrides;
    }
}
