package com.goldskinmc.livingsponge.simulation.profile;

public enum OutputTrait {
    NEUTRAL,
    FRUITING,
    WALL_FORMING,
    SOLIDIFYING;

    public boolean isCompatibleWith(final OutputTrait other) {
        return this == other;
    }
}
