package de.florianmichael.clampclient.injection.instrumentation_1_8.definition;

import de.florianmichael.rmath.mathtable.MathTableRegistry;
import net.minecraft.util.math.Vec3d;

/**
 * This class represents the MathHelper Changes (Table Isn) in 1.8
 */
public class MathHelper_1_8 {
    public static MathTableRegistry mathTable = MathTableRegistry.MINECRAFT;

    public static Vec3d getIntermediateWithXValue(Vec3d t, Vec3d vec, double x) {
        final double xOffset = vec.x - t.x;
        final double yOffset = vec.y - t.y;
        final double zOffset = vec.z - t.z;

        if (xOffset * xOffset < 1.0000000116860974E-7D) {
            return null;
        } else {
            final double tileOffset = (x - t.x) / xOffset;

            return tileOffset >= 0.0D && tileOffset <= 1.0D ? new Vec3d(t.x + xOffset * tileOffset, t.y + yOffset * tileOffset, t.z + zOffset * tileOffset) : null;
        }
    }

    public static Vec3d getIntermediateWithYValue(Vec3d t, Vec3d vec, double y) {
        final double xOffset = vec.x - t.x;
        final double yOffset = vec.y - t.y;
        final double zOffset = vec.z - t.z;

        if (yOffset * yOffset < 1.0000000116860974E-7D) {
            return null;
        } else {
            final double tileOffset = (y - t.y) / yOffset;

            return tileOffset >= 0.0D && tileOffset <= 1.0D ? new Vec3d(t.x + xOffset * tileOffset, t.y + yOffset * tileOffset, t.z + zOffset * tileOffset) : null;
        }
    }

    public static Vec3d getIntermediateWithZValue(Vec3d t, Vec3d vec, double z) {
        final double xOffset = vec.x - t.x;
        final double yOffset = vec.y - t.y;
        final double zOffset = vec.z - t.z;

        if (zOffset * zOffset < 1.0000000116860974E-7D) {
            return null;
        } else {
            final double tileOffset = (z - t.z) / zOffset;

            return tileOffset >= 0.0D && tileOffset <= 1.0D ? new Vec3d(t.x + xOffset * tileOffset, t.y + yOffset * tileOffset, t.z + zOffset * tileOffset) : null;
        }
    }


    public static float sin(float value) {
        return mathTable.getMath().sin(value);
    }

    public static float cos(float value) {
        return mathTable.getMath().cos(value);
    }

    public static float sqrt_float(float value) {
        return (float)Math.sqrt((double)value);
    }

    public static float sqrt_double(double value) {
        return (float)Math.sqrt(value);
    }
}
