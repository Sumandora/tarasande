package de.florianmichael.clampclient.injection.instrumentation_1_8.fastmath;

import de.florianmichael.clampclient.injection.instrumentation_1_8.fastmath.impl.D5Math;
import de.florianmichael.clampclient.injection.instrumentation_1_8.fastmath.impl.K4Math;
import de.florianmichael.clampclient.injection.instrumentation_1_8.fastmath.impl.NoneMath;

public enum FastMath {

    NONE("None", new NoneMath()),
    D5("<= D5", new D5Math()),
    K4(">= K4", new K4Math());

    private final String name;
    private final Math math;

    FastMath(String name, Math math) {
        this.name = name;
        this.math = math;
    }

    public Math getMath() {
        return math;
    }

    @Override
    public String toString() {
        return name;
    }
}
