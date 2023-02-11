package net.tarasandedevelopment.tarasande.feature.rotation

import net.tarasandedevelopment.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.event.EventRotation
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.feature.rotation.correctmovement.CorrectMovement
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumberRange
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import su.mandora.event.EventDispatcher
import kotlin.math.roundToInt

object Rotations {

    var fakeRotation: Rotation? = null

    val correctMovement = ValueMode(this, "Correct movement", false, "Off", "Prevent Backwards Sprinting", "Direct", "Silent")
    private val updateRotationsWhenTickSkipping = ValueBoolean(this, "Update rotations when tick skipping", false)
    private val updateRotationsAccurately = object : ValueBoolean(this, "Update rotations accurately", true) {
        override fun isEnabled() = updateRotationsWhenTickSkipping.value
    }
    private val rotateToOriginSpeed = ValueNumberRange(this, "Rotate to origin speed", 0.0, 1.0, 1.0, 1.0, 0.1)
    private val visualizeFakeRotation = ValueBoolean(this, "Visualize fake rotation", false)

    private var rotated = false
    init {
        EventDispatcher.apply {
            add(EventPollEvents::class.java) {
                createRotationEvent()
                rotated = true
            }
            add(EventTick::class.java) { event ->
                if (event.state != EventTick.State.PRE)
                    return@add

                if (!rotated) {
                    // There was no frame in between the last tick and the current one
                    if (updateRotationsWhenTickSkipping.value)
                        if (updateRotationsAccurately.value) {
                            val imaginaryFPS = (1000.0 / RenderUtil.deltaTime).roundToInt()

                            if(imaginaryFPS in 1..mc.options.maxFps.value)
                                repeat(imaginaryFPS) {
                                    createRotationEvent()
                                }
                        } else {
                            createRotationEvent()
                        }
                }

                rotated = false
            }
        }

        // Components
        CorrectMovement(this)
        PacketRotator(this)
        PreventRotationLeak(this) // I love 1.17 ^^
    }

    private fun createRotationEvent() {
        if (mc.player != null && mc.interactionManager != null) {
            val realRotation = Rotation(mc.player!!)
            val eventRotation = EventRotation(realRotation)
            EventDispatcher.call(eventRotation)
            if (eventRotation.dirty) {
                fakeRotation = eventRotation.rotation
            } else if (fakeRotation != null) {
                val rotation = fakeRotation!!
                    .smoothedTurn(realRotation, rotateToOriginSpeed)
                    .correctSensitivity()
                if (fakeRotation == rotation) {
                    val actualRotation = realRotation.correctSensitivity()
                    fakeRotation = null
                    mc.player?.yaw = actualRotation.yaw.also {
                        // Fix render errors
                        mc.player?.renderYaw = it
                        mc.player?.lastRenderYaw = it
                        mc.player?.prevYaw = it
                    }
                    mc.player?.pitch = actualRotation.pitch
                } else {
                    fakeRotation = rotation
                }
            }

            if(visualizeFakeRotation.value)
                fakeRotation?.also {
                    mc.player!!.apply {
                        yaw = it.yaw
                        pitch = it.pitch
                    }
                }
        }
    }

}