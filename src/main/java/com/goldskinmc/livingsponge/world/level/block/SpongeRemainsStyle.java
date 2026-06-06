package com.goldskinmc.livingsponge.world.level.block;

import net.minecraft.util.StringRepresentable;

public enum SpongeRemainsStyle implements StringRepresentable {
    DEFAULT("default"),
    WALL_FORMING("wall_forming");

    private final String serializedName;

    SpongeRemainsStyle(final String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
