package net.tarasandedevelopment.tarasande.module.player

import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleAntiFall : Module("Anti fall", "Tries to force a setback when you are falling", ModuleCategory.PLAYER) {

    private val fallDistance = ValueNumber(this, "Fall distance", 0.0, 6.0, 20.0, 0.1)
    private val void = ValueBoolean(this, "Void", false)
    private val mode = ValueMode(this, "Mode", false, "Setback", "Jump")
    private val jumpMultiplier = object : ValueNumber(this, "Jump multiplier", 0.0, 1.0, 10.0, 0.1) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val boost = object : ValueNumber(this, "Boost", 0.0, 1.0, 10.0, 0.1) {
        override fun isEnabled() = mode.isSelected(1)
    }

    private var lastOnGroundPos: Vec3d? = null
    private var wasOnGround = false

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventUpdate && event.state == EventUpdate.State.PRE) {
            if (mc.player?.isOnGround == true) {
                lastOnGroundPos = mc.player?.pos
                wasOnGround = true
            } else if (mc.player?.fallDistance!! > fallDistance.value && (!void.value || run {
                    var y = 0
                    while (true)
                        if (mc.world?.isAir(mc.player?.blockPos?.add(0, -y, 0)?.also { if (it.y < 0) return@run true }) == true) {
                            y++
                        } else {
                            return@run false
                        }
                    @Suppress("UNREACHABLE_CODE") // thx kotlin compiler, if I remove this, I get a syntax error
                    true
                })) {
                if (wasOnGround) {
                    when {
                        mode.isSelected(0) -> {
                            if (lastOnGroundPos != null) {
                                mc.player?.setPosition(lastOnGroundPos?.add(0.0, 1.0, 0.0))
                                mc.player?.velocity = Vec3d(0.0, 0.0, 0.0)
                            }
                        }

                        mode.isSelected(1) -> {
                            mc.player?.jump()
                            mc.player?.velocity = mc.player?.velocity?.multiply(boost.value, jumpMultiplier.value, boost.value)
                        }
                    }
                }
                wasOnGround = false
            }
        }
    }
}