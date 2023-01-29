package de.florianmichael.clampclient.injection.instrumentation_1_8.fastmath;

import de.florianmichael.clampclient.injection.instrumentation_1_8.fastmath.impl.*;

public enum MathTable {

    NONE("None", new NoneMath()),
    D5("<= D5", new D5Math()),
    K4(">= K4", new K4Math()),
    JAVA("Java", new JavaMath()),
    LIB_GDX("LibGDX", new LibGDXMath()),
    RANDOM("Random", new RandomMath()),
    RIVENS_FULL("Rivens Full", new RivensFullMath()),
    RIVENS_HALF("Rivens Half", new RivensHalfMath()),
    RIVENS("Rivens", new RivensMath()),
    TAYLOR("Taylor", new TaylorMath());

    private final String name;
    private final Math math;

    MathTable(String name, Math math) {
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
