package net.tarasandedevelopment.tarasande.base.util.player.clickspeed

import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.util.player.clickspeed.ClickMethodConstant
import net.tarasandedevelopment.tarasande.util.player.clickspeed.ClickMethodCooldown
import net.tarasandedevelopment.tarasande.util.player.clickspeed.ClickMethodDynamic

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