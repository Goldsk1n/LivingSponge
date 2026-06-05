package com.goldskinmc.livingsponge.simulation.profile;

public enum OutputTrait {
    NEUTRAL,
    PODDING,
    WALL_FORMING,
    SOLIDIFYING;

    public boolean isCompatibleWith(final OutputTrait other) {
        return this == other;
    }
}
