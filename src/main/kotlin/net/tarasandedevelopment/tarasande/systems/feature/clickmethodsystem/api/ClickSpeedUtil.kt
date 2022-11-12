package net.tarasandedevelopment.tarasande.systems.feature.clickmethodsystem.api

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumberRange
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Supplier
import kotlin.math.min

class ClickSpeedUtil(private val owner: Any, isVisible: Supplier<Boolean>, vararg excluded: Class<out net.tarasandedevelopment.tarasande.systems.feature.clickmethodsystem.ClickMethod>) {

    private val clickMethods = TarasandeMain.managerClickMethod().getAllExcept(*excluded)

    private val cpsMode = object : ValueMode(owner, "CPS mode", false, *clickMethods.map { it.name }.toTypedArray()) {
        override fun isEnabled() = isVisible.get()
    }
    private val cps = object : ValueNumberRange(owner, "CPS", 1.0, 8.0, 12.0, 20.0, 1.0) {
        override fun onChange() = reset()
        override fun isEnabled() = clickMethods[cpsMode.settings.indexOf(cpsMode.selected[0])].cpsBased && isVisible.get()
    }

    private val timeUtil = TimeUtil()

    private var targetedCPS = 0.0

    fun reset() {
        targetedCPS = if (cps.minValue == cps.maxValue) cps.minValue else ThreadLocalRandom.current().nextDouble(cps.minValue, cps.maxValue)
        for (clickMethod in clickMethods)
            clickMethod.reset(targetedCPS)
    }

    fun getClicks(): Int {
        if (timeUtil.hasReached(1000L)) {
            targetedCPS = if (cps.minValue == cps.maxValue) cps.minValue else ThreadLocalRandom.current().nextDouble(cps.minValue, cps.maxValue)
            timeUtil.reset()
        }

        return min(clickMethods[cpsMode.settings.indexOf(cpsMode.selected[0])].getClicks(targetedCPS), 10 /* safety */)
    }
}