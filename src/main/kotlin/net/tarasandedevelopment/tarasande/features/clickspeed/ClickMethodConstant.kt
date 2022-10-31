package net.tarasandedevelopment.tarasande.features.clickspeed

import net.tarasandedevelopment.tarasande.base.features.clickspeed.ClickMethod
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import kotlin.math.round

class ClickMethodConstant : ClickMethod("Constant", true) {

    private val timeUtil = TimeUtil()

    override fun getClicks(targetedCPS: Double): Int {
        val ticks = round((System.currentTimeMillis() - timeUtil.time) / (1000.0 / targetedCPS)).toInt()
        timeUtil.time += (ticks * (1000.0 / targetedCPS)).toLong()
        return ticks
    }

    override fun reset(targetedCPS: Double) {
        timeUtil.time = System.currentTimeMillis() - (1000.0 / targetedCPS).toLong() // The idea is that, it resets it not to 0 clicks but to 1 click means it will get the first hit
    }
}
