package de.florianmichael.clampclient.injection.instrumentation_1_8;

import net.minecraft.world.border.WorldBorder;

public class WorldBorderWrapper {

    public static double minX(final WorldBorder worldBorder) {
        double d0 = worldBorder.getCenterX() - worldBorder.getSize() / 2.0D;
        if (d0 < (double)(-worldBorder.maxRadius)) {
            d0 = (double)(-worldBorder.maxRadius);
        }
        return d0;
    }

    public static double minZ(final WorldBorder worldBorder) {
        double d0 = worldBorder.getCenterZ() - worldBorder.getSize() / 2.0D;
        if (d0 < (double)(-worldBorder.maxRadius)) {
            d0 = (double)(-worldBorder.maxRadius);
        }
        return d0;
    }

    public static double maxX(final WorldBorder worldBorder) {
        double d0 = worldBorder.getCenterX() + worldBorder.getSize() / 2.0D;
        if (d0 > (double)worldBorder.maxRadius) {
            d0 = (double)worldBorder.maxRadius;
        }
        return d0;
    }

    public static double maxZ(final WorldBorder worldBorder) {
        double d0 = worldBorder.getCenterZ() + worldBorder.getSize() / 2.0D;
        if (d0 > (double)worldBorder.maxRadius) {
            d0 = (double)worldBorder.maxRadius;
        }
        return d0;
    }
}
