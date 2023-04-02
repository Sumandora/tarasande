package su.mandora.tarasande.system.feature.clickmethodsystem

import su.mandora.tarasande.Manager
import su.mandora.tarasande.system.feature.clickmethodsystem.impl.ClickMethodConstant
import su.mandora.tarasande.system.feature.clickmethodsystem.impl.ClickMethodCooldown
import su.mandora.tarasande.system.feature.clickmethodsystem.impl.ClickMethodDynamic

object ManagerClickMethod : Manager<Class<out ClickMethod>>() {

    init {
        add(
            ClickMethodConstant::class.java,
            ClickMethodDynamic::class.java,
            ClickMethodCooldown::class.java
        )
    }

    fun getAllExcept(vararg excluded: Class<out ClickMethod>): List<ClickMethod> {
        return list.filter { !excluded.contains(it) }.map { it.getDeclaredConstructor().newInstance() }
    }
}

abstract class ClickMethod(val name: String, val cpsBased: Boolean) {
    abstract fun getClicks(targetedCPS: Double): Int
    abstract fun reset(targetedCPS: Double)
}
