package de.florianmichael.clampclient.injection.instrumentation_1_8.definition;

import de.florianmichael.clampclient.injection.instrumentation_1_8.fastmath.FastMath;

/**
 * This class represents the MathHelper Changes (Table Isn) in 1.8
 */
public class MathHelper_1_8 {
    public static FastMath fastMath = FastMath.NONE;

    public static float sin(float p_76126_0_) {
        return fastMath.getMath().sin(p_76126_0_);
    }

    public static float cos(float value) {
        return fastMath.getMath().cos(value);
    }

    public static float sqrt_float(float value) {
        return (float)Math.sqrt((double)value);
    }

    public static float sqrt_double(double value) {
        return (float)Math.sqrt(value);
    }
}
