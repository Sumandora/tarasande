package net.tarasandedevelopment.tarasande.module.movement

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround
import net.minecraft.util.math.Direction
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.event.Priority
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventMovement
import net.tarasandedevelopment.tarasande.event.EventStep
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.util.extension.times
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleStep : Module("Step", "Allows you to step up blocks", ModuleCategory.MOVEMENT) {

    private val stepHeight = ValueNumber(this, "Step height", 0.0, 1.0, 5.0, 0.1)
    private val mode = ValueMode(this, "Mode", false, "Vanilla", "No cheat")

    private val slowdown = ValueNumber(this, "Slowdown", 0.0, 1.0, 1.0, 0.1)
    private val slowdownTicks = object : ValueNumber(this, "Slowdown ticks", 1.0, 1.0, 10.0, 1.0) {
        override fun isEnabled() = slowdown.value < 1.0
    }
    private val onGroundTicks = ValueNumber(this, "On-ground ticks", 0.0, 0.0, 10.0, 1.0)

    private var stepTick = 0
    private var offGroundTick = 0

    private var prevAge = 0

    override fun onEnable() {
        stepTick = 0
        offGroundTick = 0
        prevAge = 0
    }

    @Priority(1001) // we have to slow the speed down
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventStep -> {
                when (event.state) {
                    EventStep.State.PRE -> {
                        if (mc.player?.age!! - offGroundTick > onGroundTicks.value)
                            event.stepHeight = stepHeight.value.toFloat()
                    }

                    EventStep.State.POST -> {
                        if (event.stepHeight > mc.player?.stepHeight!! && mc.player?.isOnGround == true) {
                            if (mode.isSelected(1)) {
                                mc.networkHandler?.sendPacket(PositionAndOnGround(mc.player?.pos?.x!!, mc.player?.pos?.y?.plus(event.stepHeight * 0.42F)!!, mc.player?.pos?.z!!, false))
                                mc.networkHandler?.sendPacket(PositionAndOnGround(mc.player?.pos?.x!!, mc.player?.pos?.y?.plus(event.stepHeight * 0.75F)!!, mc.player?.pos?.z!!, false))
                            }
                            stepTick = mc.player?.age!!
                        }
                    }
                }
            }

            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE)
                    if (mc.player?.isOnGround == false && mc.player?.age!! - stepTick > 3) {
                        offGroundTick = mc.player?.age!!
                    }
            }

            is EventMovement -> {
                if (event.entity != mc.player)
                    return@Consumer
                if (mc.player?.age!! - stepTick < slowdownTicks.value)
                    event.velocity = (event.velocity * slowdown.value).let { if (slowdown.value != 0.0) it.withAxis(Direction.Axis.Y, event.velocity.y) else it }
            }

            is EventTick -> {
                if (mc.player == null || mc.player?.age!! < prevAge) {
                    stepTick = 0
                    offGroundTick = 0
                }
                prevAge = mc.player?.age ?: return@Consumer
            }
        }
    }

}