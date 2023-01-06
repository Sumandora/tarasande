package de.florianmichael.clampclient.injection.instrumentation_1_8;

import net.minecraft.util.math.Box;

public class BoxWrapper {

    public static double calculateXOffset(final Box origin, final Box other, double offsetX) {
        if (other.maxY > origin.minY && other.minY < origin.maxY && other.maxZ > origin.minZ && other.minZ < origin.maxZ) {
            if (offsetX > 0.0D && other.maxX <= origin.minX) {
                final double d1 = origin.minX - other.maxX;
                if (d1 < offsetX) {
                    offsetX = d1;
                }
            } else if (offsetX < 0.0D && other.minX >= origin.maxX) {
                final double d0 = origin.maxX - other.minX;
                if (d0 > offsetX) {
                    offsetX = d0;
                }
            }

        }
        return offsetX;
    }

    public static double calculateYOffset(final Box origin, final Box other, double offsetY) {
        if (other.maxX > origin.minX && other.minX < origin.maxX && other.maxZ > origin.minZ && other.minZ < origin.maxZ) {
            if (offsetY > 0.0D && other.maxY <= origin.minY) {
                final double d1 = origin.minY - other.maxY;
                if (d1 < offsetY) {
                    offsetY = d1;
                }
            } else if (offsetY < 0.0D && other.minY >= origin.maxY) {
                final double d0 = origin.maxY - other.minY;
                if (d0 > offsetY) {
                    offsetY = d0;
                }
            }

        }
        return offsetY;
    }

    public static double calculateZOffset(final Box origin, final Box other, double offsetZ) {
        if (other.maxX > origin.minX && other.minX < origin.maxX && other.maxY > origin.minY && other.minY < origin.maxY) {
            if (offsetZ > 0.0D && other.maxZ <= origin.minZ) {
                final double d1 = origin.minZ - other.maxZ;
                if (d1 < offsetZ) {
                    offsetZ = d1;
                }
            } else if (offsetZ < 0.0D && other.minZ >= origin.maxZ) {
                final double d0 = origin.maxZ - other.minZ;
                if (d0 > offsetZ) {
                    offsetZ = d0;
                }
            }
        }
        return offsetZ;
    }
}
