package de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.model;

import com.viaversion.viaversion.util.Pair;

public enum ViewDistance {

    FAR(16), NORMAL(8), SHORT(4), TINY(2);

    private final int distance;

    ViewDistance(int distance) {
        this.distance = distance;
    }

    public static ViewDistance approximateDistance(final byte viewDistance) {
        Pair<ViewDistance, Integer> bestDistance = null;

        for (ViewDistance value : ViewDistance.values()) {
            final int delta = viewDistance - value.getDistance();
            if (bestDistance == null || bestDistance.value() >= delta) {
                bestDistance = new Pair<>(value, delta);
            }
        }

        return bestDistance.key();
    }

    public int getDistance() {
        return distance;
    }
}
