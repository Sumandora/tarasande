package su.mandora.tarasande.util.render.animation

import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber

class Animator(val owner: Any) {
    var easing = EasingFunction.LINEAR

    val speedIn = ValueNumber(owner, "Speed: in", 0.001, 0.005, 0.02, 0.001)
    val speedOut = ValueNumber(owner, "Speed: out", 0.001, 0.005, 0.02, 0.001)

    init {
        object : ValueMode(owner, "Easing function", false, *EasingFunction.entries.map { it.functionName }.toTypedArray()) {
            override fun onChange(index: Int, oldSelected: Boolean, newSelected: Boolean) {
                easing = EasingFunction.entries.first { it.functionName == getSelected() }
            }
        }
    }
}