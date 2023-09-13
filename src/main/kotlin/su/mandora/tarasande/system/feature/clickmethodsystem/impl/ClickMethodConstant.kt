package su.mandora.tarasande.system.feature.clickmethodsystem.impl

import su.mandora.tarasande.system.feature.clickmethodsystem.ClickMethod
import kotlin.math.round

class ClickMethodConstant : ClickMethod("Constant", true) {

    private var time = System.currentTimeMillis()

    override fun getClicks(targetedCPS: Double): Int {
        val ticks = round((System.currentTimeMillis() - time) / (1000.0 / targetedCPS)).toInt()
        time += (ticks * (1000.0 / targetedCPS)).toLong()
        return ticks
    }

    override fun reset(targetedCPS: Double) {
        time = System.currentTimeMillis() - (1000.0 / targetedCPS).toLong() // The idea is that, it resets it not to 0 clicks but to 1 click means it will get the first hit
    }
}
