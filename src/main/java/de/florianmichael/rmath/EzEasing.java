package de.florianmichael.rmath;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

// https://easings.net/
public enum EzEasing {

    LINEAR("Linear", x -> x),

    IN_SINE("In sine", x -> (float) (1F - Math.cos((x * Math.PI) / 2F))),
    OUT_SINE("Out sine", x -> (float) (Math.sin((x * Math.PI) / 2F))),
    IN_OUT_SINE("In out sine", x -> (float) (-(Math.cos(Math.PI * x) - 1F) / 2F)),

    IN_QUAD("In quad", x -> x * x),
    OUT_QUAD("Out quad", x -> 1F - (1F - x) * (1F - x)),
    IN_OUT_QUAD("In out quad", x -> x < 0.5F ? 2F * x * x : 1F - (float) Math.pow(-2F * x + 2F, 2F) / 2F),

    IN_CUBIC("In cubic", x -> (float) Math.pow(x, 3F)),
    OUT_CUBIC("Out cubic", x -> (float) (1F - Math.pow(1F - x, 3F))),
    IN_OUT_CUBIC("In out cubic", x -> x < 0.5F ? 4F * x * x * x : 1F - (float) Math.pow(-2F * x + 2F, 3F) / 2F),

    IN_QUART("In quart", x -> (float) Math.pow(x, 4F)),
    OUT_QUART("Out quart", x -> (float) (1F - Math.pow(1F - x, 4F))),
    IN_OUT_QUART("In out quart", x -> x < 0.5F ? 8F * x * x * x * x : 1F - (float) Math.pow(-2F * x + 2F, 4F) / 2F),

    IN_QUINT("In quint", x -> (float) Math.pow(x, 5F)),
    OUT_QUINT("Out quint", x -> (float) (1F - Math.pow(1F - x, 5F))),
    IN_OUT_QUINT("In out quint", x -> x < 0.5F ? 16F * x * x * x * x * x : 1F - (float) Math.pow(-2F * x + 2F, 5F) / 2F),

    IN_EXPO("In expo", x -> x == 0F ? 0F : (float) Math.pow(2F, 10F * x - 10F)),
    OUT_EXPO("Out expo", x -> x == 1F ? 1F : 1F - (float) Math.pow(2F, -10F * x)),
    IN_OUT_EXPO("In out expo", x -> x == 0F ? 0F : x == 1F ? 1F : x < 0.5F ? (float) Math.pow(2F, 20F * x - 10F) / 2F : (2F - (float) Math.pow(2F, -20F * x + 10F)) / 2F),

    IN_CIRC("In circ", x -> 1F - (float) Math.sqrt(1F - (float) Math.pow(x, 2F))),
    OUT_CIRC("Out circ", x -> (float) Math.sqrt(1F - (float) Math.pow(x - 1F, 2F))),
    IN_OUT_CIRC("In out circ", x -> x < 0.5F ? (1F - (float) Math.sqrt(1 - (float) Math.pow(2F * x, 2F))) / 2F : ((float) Math.sqrt(1F - (float) Math.pow(-2F * x + 2F, 2F)) + 1F) / 2F),

    IN_BACK("In back", x -> {
        final float c1 = 1.70158F;
        final float c3 = c1 + 1F;

        return c3 * x * x * x - c1 * x * x;
    }),
    OUT_BACK("Out back", x -> {
        final float c1 = 1.70158F;
        final float c3 = c1 + 1F;

        return 1F + c3 * (float) Math.pow(x - 1F, 3F) + c1 * (float) Math.pow(x - 1F, 2F);
    }),
    IN_OUT_BACK("In out back", x -> {
        final float c1 = 1.70158F;
        final float c2 = c1 * 1.525F;

        return x < 0.5F ? ((float) Math.pow(2F * x, 2F) * ((c2 + 1F) * 2F * x - c2)) / 2F : ((float) Math.pow(2F * x - 2F, 2F) * ((c2 + 1F) * (x * 2F - 2F) + c2) + 2F) / 2F;
    }),

    IN_ELASTIC("In elastic", x -> {
        final float c4 = (2F * (float) Math.PI) / 3F;

        return x == 0F ? 0F : x == 1F ? 1F : (float) -Math.pow(2F, 10F * x - 10F) * (float) Math.sin((x * 10F - 10.75F) * c4);
    }),
    OUT_ELASTIC("Out elastic", x -> {
        final float c4 = (2F * (float) Math.PI) / 3F;

        return x == 0F ? 0F : x == 1F ? 1F : (float) Math.pow(2F, -10F * x) * (float) Math.sin((x * 10F - 0.75F) * c4) + 1F;
    }),
    INT_OUT_ELASTIC("In out elastic", x -> {
        final float c5 = (2F * (float) Math.PI) / 4.5F;

        return x == 0F ? 0F : x == 1F ? 1F : x < 0.5F ? -((float) Math.pow(2F, 20F * x - 10F) * (float) Math.sin((20F * x - 11.125F) * c5)) / 2F : ((float) Math.pow(2F, -20F * x + 10F) * (float) Math.sin((20F * x - 11.125F) * c5)) / 2F + 1F;
    }),

    OUT_BOUNCE("Out bounce", x -> {
        final float n1 = 7.5625F;
        final float d1 = 2.75F;

        if (x < 1F / d1) {
            return n1 * x * x;
        } else if (x < 2F / d1) {
            return n1 * (x -= 1.5F / d1) * x + 0.75F;
        } else if (x < 2.5F / d1) {
            return n1 * (x -= 2.25F / d1) * x + 0.9375F;
        } else {
            return n1 * (x -= 2.625F / d1) * x + 0.984375F;
        }
    }),
    IN_BOUNCE("In bounce", x -> 1F - EzEasing.OUT_BOUNCE.ease(1F - x)),
    IN_OUT_BOUNCE("In out bounce", x -> x < 0.5F ? (1F - EzEasing.OUT_BOUNCE.ease(1F - 2F * x)) / 2F : (1F + EzEasing.OUT_BOUNCE.ease(2F * x - 1F)) / 2F);

    public final String name;
    public final Function<Float, Float> function;

    EzEasing(final String name, final Function<Float, Float> function) {
        this.name = name;
        this.function = function;
    }

    public static List<String> functionNames() {
        return Arrays.stream(values()).map(v -> v.name).collect(Collectors.toList());
    }

    public static float ease(final String functionName, final float x) {
        return byName(functionName).ease(x);
    }

    public static EzEasing byName(final String functionName) {
        for (EzEasing value : values()) {
            if (value.name.equals(functionName)) {
                return value;
            }
        }
        return null;
    }

    public String upperName() {
        return this.name.toUpperCase(Locale.ROOT);
    }

    public float ease(final float x) {
        return this.function.apply(x);
    }
}