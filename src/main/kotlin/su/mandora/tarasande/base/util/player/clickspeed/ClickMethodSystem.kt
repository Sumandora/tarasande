package su.mandora.tarasande.base.util.player.clickspeed

import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.util.player.clickspeed.ClickMethodConstant
import su.mandora.tarasande.util.player.clickspeed.ClickMethodCooldown
import su.mandora.tarasande.util.player.clickspeed.ClickMethodDynamic

abstract class ClickMethod(val name: String, val cpsBased: Boolean) {
    abstract fun getClicks(targetedCPS: Double): Int
    abstract fun reset(targetedCPS: Double)
}

class ManagerClickMethod : Manager<Class<out ClickMethod>>() {

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