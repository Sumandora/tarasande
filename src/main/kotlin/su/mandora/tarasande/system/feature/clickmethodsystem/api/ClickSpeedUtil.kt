package su.mandora.tarasande.system.feature.clickmethodsystem.api

import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumberRange
import su.mandora.tarasande.system.feature.clickmethodsystem.ClickMethod
import su.mandora.tarasande.system.feature.clickmethodsystem.ManagerClickMethod
import su.mandora.tarasande.util.math.TimeUtil
import kotlin.math.min

class ClickSpeedUtil(private val owner: Any, enabledCallback: () -> Boolean, vararg excluded: Class<out ClickMethod>, namePrefix: String = "") {

    private val clickMethods = ManagerClickMethod.getAllExcept(*excluded)

    private val cpsMode = ValueMode(owner, namePrefix + "CPS mode", false, *clickMethods.map { it.name }.toTypedArray(), isEnabled = enabledCallback)

    private val cps = object : ValueNumberRange(owner, namePrefix + "CPS", 1.0, 8.0, 12.0, 20.0, 1.0, isEnabled = { enabledCallback() && selected().cpsBased }) {
        override fun onMinValueChange(oldMinValue: Double?, newMinValue: Double) {
            reset()
        }

        override fun onMaxValueChange(oldMaxValue: Double?, newMaxValue: Double) {
            reset()
        }
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