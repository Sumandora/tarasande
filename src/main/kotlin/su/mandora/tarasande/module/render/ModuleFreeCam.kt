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
import su.mandora.tarasande.mixin.accessor.IKeyBinding
import su.mandora.tarasande.util.extension.plus
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleFreeCam : Module("Free cam", "Allows you to clientsidedly fly around freely", ModuleCategory.RENDER) {

    private val speed = ValueNumber(this, "Speed", 0.1, 1.0, 5.0, 0.1)
    private val lockRotation = ValueBoolean(this, "Lock rotation", true)

    private var position: Vec3d? = null
    private var beginRotation: Rotation? = null
    private var rotation: Rotation? = null

    private var prevCameraPos: Vec3d? = null

    private var perspective: Perspective? = null

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
        prevCameraPos = null
    }

    @Priority(1) // let all of this stuff get overridden
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventCameraOverride -> {
                if (position == null || rotation == null)
                    onEnable()
                mc.options.perspective = Perspective.THIRD_PERSON_BACK
                val accessor = event.camera as ICamera

                accessor.tarasande_invokeSetPos((prevCameraPos ?: position)?.lerp(position, mc.tickDelta.toDouble()).also {
                    prevCameraPos = it
                })
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
                    if (position == null || rotation == null)
                        onEnable()
                    var yMotion = 0.0
                    if ((mc.options.jumpKey as IKeyBinding).tarasande_forceIsPressed())
                        yMotion += speed.value
                    if ((mc.options.sneakKey as IKeyBinding).tarasande_forceIsPressed())
                        yMotion -= speed.value
                    var velocity = (mc.player as IEntity).tarasande_invokeMovementInputToVelocity(Vec3d(
                        MathUtil.roundAwayFromZero(PlayerUtil.input.movementSideways.toDouble()),
                        0.0,
                        MathUtil.roundAwayFromZero(PlayerUtil.input.movementForward.toDouble())
                    ), speed.value.toFloat(), rotation?.yaw!!)
                    velocity = Vec3d(velocity?.x!!, yMotion, velocity.z)
                    position = position!! + velocity
                }
            }

            is EventInput -> {
                event.cancelled = true
            }

            is EventKeyBindingIsPressed -> {
                if (!PlayerUtil.movementKeys.contains(event.keyBinding))
                    event.pressed = false
            }
        }
    }

}