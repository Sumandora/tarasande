package net.tarasandedevelopment.tarasande.system.feature.clickmethodsystem.api

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumberRange
import net.tarasandedevelopment.tarasande.system.feature.clickmethodsystem.ClickMethod
import net.tarasandedevelopment.tarasande.system.feature.clickmethodsystem.ManagerClickMethod
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import kotlin.math.min

class ClickSpeedUtil(private val owner: Any, enabledCallback: () -> Boolean, vararg excluded: Class<out ClickMethod>, namePrefix: String = "") {

    private val clickMethods = ManagerClickMethod.getAllExcept(*excluded)

    private val cpsMode = object : ValueMode(owner, namePrefix + "CPS mode", false, *clickMethods.map { it.name }.toTypedArray()) {
        override fun isEnabled() = enabledCallback.invoke()
    }

    private val cps = object : ValueNumberRange(owner, namePrefix + "CPS", 1.0, 8.0, 12.0, 20.0, 1.0) {
        override fun onMinValueChange(oldMinValue: Double?, newMinValue: Double) {
            reset()
        }

        override fun onMaxValueChange(oldMaxValue: Double?, newMaxValue: Double) {
            reset()
        }

        override fun isEnabled() = selected().cpsBased && enabledCallback.invoke()
    }

    private val timeUtil = TimeUtil()

    private var targetedCPS: Double? = null

    fun reset() {
        targetedCPS = cps.randomNumber()
        for (clickMethod in clickMethods)
            clickMethod.reset(targetedCPS!!)
        timeUtil.reset()
    }

    fun getClicks(): Int {
        if (targetedCPS == null || timeUtil.hasReached(1000L)) {
            targetedCPS = cps.randomNumber()
            timeUtil.reset()
        }

        return min(selected().getClicks(targetedCPS!!), 10 /* safety */)
    }

    private fun selected() = clickMethods[cpsMode.values.indexOf(cpsMode.getSelected())]
}