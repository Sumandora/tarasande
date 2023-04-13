package su.mandora.tarasande.util.render.animation

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// https://easings.net/
@Suppress("unused")
enum class EasingFunction(val functionName: String, val ease: (Double) -> Double) {
    LINEAR("Linear", { x -> x }),
    IN_SINE("In sine", { x -> (1.0 - cos(x * Math.PI / 2.0)) }),
    OUT_SINE("Out sine", { x -> sin(x * Math.PI / 2.0) }),
    IN_OUT_SINE("In out sine", { x -> -(cos(Math.PI * x) - 1.0) / 2.0 }),
    IN_QUAD("In quad", { x -> x * x }),
    OUT_QUAD("Out quad", { x -> 1.0 - (1.0 - x) * (1.0 - x) }),
    IN_OUT_QUAD("In out quad", { x -> if (x < 0.5) 2.0 * x * x else 1.0 - (-2.0 * x + 2.0).pow(2.0) / 2.0 }),
    IN_CUBIC("In cubic", { x -> x.pow(3.0) }),
    OUT_CUBIC("Out cubic", { x -> (1.0 - (1.0 - x).pow(3.0)) }),
    IN_OUT_CUBIC("In out cubic", { x -> if (x < 0.5) 4.0 * x * x * x else 1.0 - (-2.0 * x + 2.0).pow(3.0) / 2.0 }),
    IN_QUART("In quart", { x -> x.pow(4.0) }),
    OUT_QUART("Out quart", { x -> (1.0 - (1.0 - x).pow(4.0)) }),
    IN_OUT_QUART("In out quart", { x -> if (x < 0.5) 8.0 * x * x * x * x else 1.0 - (-2.0 * x + 2.0).pow(4.0) / 2.0 }),
    IN_QUINT("In quint", { x -> x.pow(5.0) }),
    OUT_QUINT("Out quint", { x -> (1.0 - (1.0 - x).pow(5.0)) }),
    IN_OUT_QUINT("In out quint", { x -> if (x < 0.5) 16.0 * x * x * x * x * x else 1.0 - (-2.0 * x + 2.0).pow(5.0) / 2.0 }),
    IN_EXPO("In expo", { x -> if (x == 0.0) 0.0 else 2.0.pow(10.0 * x - 10.0) }),
    OUT_EXPO("Out expo", { x -> if (x == 1.0) 1.0 else 1.0 - 2.0.pow(-10.0 * x) }),
    IN_OUT_EXPO("In out expo", { x -> if (x == 0.0) 0.0 else if (x == 1.0) 1.0 else if (x < 0.5) 2.0.pow(20.0 * x - 10.0) / 2.0 else (2.0 - 2.0.pow(-20 * x + 10)) / 2 }),
    IN_CIRC("In circ", { x -> 1.0 - sqrt(1.0 - x.pow(2.0)) }),
    OUT_CIRC("Out circ", { x -> sqrt(1.0 - (x - 1.0).pow(2.0)) }),
    IN_OUT_CIRC("In out circ", { x -> if (x < 0.5) (1.0 - sqrt(1 - (2.0 * x).pow(2.0))) / 2.0 else (sqrt(1.0 - (-2.0 * x + 2.0).pow(2.0)) + 1.0) / 2.0 }),
    IN_BACK("In back", { x ->
        val c1 = 1.70158
        val c3 = c1 + 1.0
        c3 * x * x * x - c1 * x * x
    }),
    OUT_BACK("Out back", { x ->
        val c1 = 1.70158
        val c3 = c1 + 1
        1.0 + c3 * (x - 1).pow(3.0) + c1 * (x - 1).pow(2.0)
    }),
    IN_OUT_BACK("In out back", { x ->
        val c1 = 1.70158
        val c2 = c1 * 1.525
        if (x < 0.5) (2.0 * x).pow(2.0) * ((c2 + 1.0) * 2.0 * x - c2) / 2.0 else ((2.0 * x - 2.0).pow(2.0) * ((c2 + 1.0) * (x * 2.0 - 2.0) + c2) + 2.0) / 2.0
    }),
    IN_ELASTIC("In elastic", { x ->
        val c4 = 2.0 * Math.PI / 3.0
        when (x) {
            0.0 -> 0.0
            1.0 -> 1.0
            else -> -(2.0.pow(10.0 * x - 10.0)) * sin((x * 10.0 - 10.75) * c4)
        }
    }),
    OUT_ELASTIC("Out elastic", { x ->
        val c4 = 2.0 * Math.PI / 3.0
        when (x) {
            0.0 -> 0.0
            1.0 -> 1.0
            else -> 2.0.pow(-10.0 * x) * sin((x * 10.0 - 0.75) * c4) + 1.0
        }
    }),
    INT_OUT_ELASTIC("In out elastic", { x ->
        val c5 = 2.0 * Math.PI / 4.5
        if (x == 0.0) 0.0
        else if (x == 1.0) 1.0
        else if (x < 0.5) -(2.0.pow(20.0 * x - 10.0) * sin((20.0 * x - 11.125) * c5)) / 2.0
        else 2.0.pow(-20.0 * x + 10.0) * sin((20.0 * x - 11.125) * c5) / 2.0 + 1.0
    }),
    OUT_BOUNCE("Out bounce", { x ->
        @Suppress("NAME_SHADOWING")
        var x = x
        val n1 = 7.5625
        val d1 = 2.75
        if (x < 1.0 / d1) {
            n1 * x * x
        } else if (x < 2.0 / d1) {
            n1 * ((1.5 / d1).let { x -= it; x }) * x + 0.75
        } else if (x < 2.5 / d1) {
            n1 * ((2.25 / d1).let { x -= it; x }) * x + 0.9375
        } else {
            n1 * ((2.625 / d1).let { x -= it; x }) * x + 0.984375
        }
    }),
    IN_BOUNCE("In bounce", { x -> 1.0 - OUT_BOUNCE.ease(1.0 - x) }),
    IN_OUT_BOUNCE("In out bounce", { x -> if (x < 0.5) (1.0 - OUT_BOUNCE.ease(1.0 - 2.0 * x)) / 2.0 else (1.0 + OUT_BOUNCE.ease(2.0 * x - 1.0)) / 2.0 });
}