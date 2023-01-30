package de.florianmichael.rmath.mathtable;

import de.florianmichael.rmath.mathtable.impl.*;

public enum MathTableRegistry {

    NONE("None", new NoneMathTable()),
    OPTIFINE_D5("Optifine <= D5", new OptifineD5MathTable()),
    OPTIFINE_K4("Optifine >= K4", new OptifineK4MathTable()),
    JAVA("Java", new JavaMathTable()),
    LIB_GDX("LibGDX", new LibGDXMathTable()),
    RANDOM("Random", new RandomMathTable()),
    RIVENS_FULL("Rivens Full", new RivensFullMathTable()),
    RIVENS_HALF("Rivens Half", new RivensHalfMathTable()),
    RIVENS("Rivens", new RivensMathTable()),
    TAYLOR("Taylor", new TaylorMathTable());

    private final String name;
    private final MathTable math;

    MathTableRegistry(final String name, final MathTable math) {
        this.name = name;
        this.math = math;
    }

    public MathTable getMath() {
        return math;
    }

    @Override
    public String toString() {
        return name;
    }
}
