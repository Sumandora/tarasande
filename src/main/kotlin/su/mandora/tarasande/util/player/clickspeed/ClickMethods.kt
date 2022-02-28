package su.mandora.tarasande.util.player.clickspeed

import net.minecraft.client.MinecraftClient
import su.mandora.tarasande.base.util.player.clickspeed.ClickMethod
import su.mandora.tarasande.util.math.TimeUtil
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.round
import kotlin.math.sqrt

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

class ClickMethodCooldown : ClickMethod("Cooldown", false) {
    override fun getClicks(targetedCPS: Double): Int = if (MinecraftClient.getInstance().player?.getAttackCooldownProgress(0.0F)!! >= 1.0F) 1 else 0
    override fun reset(targetedCPS: Double) = Unit // nothing there
}