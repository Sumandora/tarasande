package su.mandora.tarasande.system.feature.clickmethodsystem.impl

import su.mandora.tarasande.system.feature.clickmethodsystem.ClickMethod
import su.mandora.tarasande.util.math.TimeUtil
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.round
import kotlin.math.sqrt

class ClickMethodDynamic : ClickMethod("Dynamic", true) {

    private var remainder = 0
    private val timeUtil = TimeUtil()

    override fun getClicks(targetedCPS: Double): Int {
        return if (ThreadLocalRandom.current().nextDouble(1.0) > sqrt(remainder + 4.0) / 3.0) { // choke (something around remainder = 9 forces it impossible)
            remainder++
            0
        } else {
            val ticks = round((System.currentTimeMillis() - timeUtil.time) / (1000.0 / (targetedCPS + remainder))).toInt()
            timeUtil.time += (ticks * (1000.0 / (targetedCPS + remainder))).toLong()
            remainder = 0
            ticks
        }
    }

    override fun reset(targetedCPS: Double) {
        timeUtil.time = System.currentTimeMillis() - (1000.0 / targetedCPS).toLong() // The idea is that, it resets it not to 0 clicks but to 1 click means it will get the first hit
        remainder = 0
    }
}
