package su.mandora.tarasande.module.render

import net.minecraft.client.option.Perspective
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.event.Priority
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.*
import su.mandora.tarasande.mixin.accessor.ICamera
import su.mandora.tarasande.mixin.accessor.IEntity
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleFreeCam : Module("Free cam", "Allows you to clientsidedly fly around freely", ModuleCategory.RENDER) {

    private val speed = ValueNumber(this, "Speed", 0.1, 1.0, 5.0, 0.1)
    private val lockRotation = ValueBoolean(this, "Lock rotation", true)

    private var position: Vec3d? = null
    private var beginRotation: Rotation? = null
    private var rotation: Rotation? = null
    private var velocity: Vec3d? = null

    private var perspective: Perspective? = null
    private var yMotion = 0.0

    override fun onEnable() {
        if (mc.player != null) {
            position = mc.gameRenderer.camera.pos
            rotation = Rotation(mc.player!!)
            beginRotation = rotation
            perspective = mc.options.perspective
        }
    }

    override fun onDisable() {
        mc.player?.yaw = beginRotation?.yaw!!
        mc.player?.pitch = beginRotation?.pitch!!
        mc.options.perspective = perspective
    }

    @Priority(1) // let all of this stuff get overridden
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventCameraOverride -> {
                if (position == null || rotation == null)
                    onEnable()
                mc.options.perspective = Perspective.THIRD_PERSON_BACK
                val accessor = event.camera as ICamera

                accessor.tarasande_invokeSetPos(if (velocity != null) position?.add(velocity?.multiply(mc.tickDelta.toDouble())) else position)
                accessor.tarasande_invokeSetRotation(rotation?.yaw!!, rotation?.pitch!!)
            }

            is EventPollEvents -> {
                if (beginRotation == null)
                    onEnable()
                rotation = event.rotation
                if (lockRotation.value) {
                    event.rotation = beginRotation!!
                    // We return ourselves in onDisable
                    event.minRotateToOriginSpeed = 0.0
                    event.maxRotateToOriginSpeed = 0.0
                }
            }

            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE) {
                    if (velocity != null)
                        position = position?.add(velocity)
                }
            }

            is EventInput -> {
                if (rotation == null)
                    onEnable()
                velocity = (mc.player as IEntity).tarasande_invokeMovementInputToVelocity(Vec3d(event.movementSideways.toDouble(), 0.0, event.movementForward.toDouble()), speed.value.toFloat(), rotation?.yaw!!)
                velocity = Vec3d(velocity?.x!!, yMotion, velocity?.z!!)
                yMotion = 0.0
                event.cancelled = true
            }

            is EventKeyBindingIsPressed -> {
                if (event.pressed)
                    when (event.keyBinding) {
                        mc.options.jumpKey -> {
                            yMotion += speed.value
                            event.pressed = false
                        }

                        mc.options.sneakKey -> {
                            yMotion -= speed.value
                            event.pressed = false
                        }
                    }
            }
        }
    }

}