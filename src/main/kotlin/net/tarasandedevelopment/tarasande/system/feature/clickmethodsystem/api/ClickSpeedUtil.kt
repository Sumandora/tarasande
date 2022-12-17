package net.tarasandedevelopment.tarasande.system.feature.clickmethodsystem.api

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumberRange
import net.tarasandedevelopment.tarasande.system.feature.clickmethodsystem.ClickMethod
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.min

class ClickSpeedUtil(private val owner: Any, enabledCallback: () -> Boolean, vararg excluded: Class<out ClickMethod>, namePrefix: String = "") {

    private val clickMethods = TarasandeMain.managerClickMethod().getAllExcept(*excluded)

    private val cpsMode = object : ValueMode(owner, namePrefix + "CPS mode", false, *clickMethods.map { it.name }.toTypedArray()) {
        override fun isEnabled() = enabledCallback.invoke()
    }
    private val cps = object : ValueNumberRange(owner, namePrefix + "CPS", 1.0, 8.0, 12.0, 20.0, 1.0) {
        override fun onChange() = reset()
        override fun isEnabled() = selected().cpsBased && enabledCallback.invoke()
    }

    private val timeUtil = TimeUtil()

    private var targetedCPS = 0.0

    fun reset() {
        targetedCPS = if (cps.minValue == cps.maxValue) cps.minValue else ThreadLocalRandom.current().nextDouble(cps.minValue, cps.maxValue)
        for (clickMethod in clickMethods)
            clickMethod.reset(targetedCPS)
        timeUtil.reset()
    }

    fun getClicks(): Int {
        if (timeUtil.hasReached(1000L)) {
            targetedCPS = if (cps.minValue == cps.maxValue) cps.minValue else ThreadLocalRandom.current().nextDouble(cps.minValue, cps.maxValue)
            timeUtil.reset()
        }

        return min(selected().getClicks(targetedCPS), 10 /* safety */)
    }

    private fun selected() = clickMethods[cpsMode.values.indexOf(cpsMode.selected[0])]
}