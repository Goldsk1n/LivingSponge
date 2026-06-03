package com.goldskinmc.livingsponge.simulation.profile;

public enum RadiusTrait {
    STANDARD(8),
    EXPANDED(16),
    VAST(512);

    private final int radiusCap;

    RadiusTrait(final int radiusCap) {
        this.radiusCap = radiusCap;
    }

    public int radiusCap() {
        return radiusCap;
    }
}
