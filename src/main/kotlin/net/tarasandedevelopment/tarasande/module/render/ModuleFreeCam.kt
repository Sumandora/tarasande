package net.tarasandedevelopment.tarasande.module.render

import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.KeyboardInput
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.Perspective
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.event.Priority
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.mixin.accessor.ICamera
import net.tarasandedevelopment.tarasande.mixin.accessor.IEntity
import net.tarasandedevelopment.tarasande.mixin.accessor.IKeyBinding
import net.tarasandedevelopment.tarasande.util.extension.plus
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleFreeCam : Module("Free cam", "Allows you to clientsidedly fly around freely", ModuleCategory.RENDER) {

    private val speed = ValueNumber(this, "Speed", 0.1, 1.0, 5.0, 0.1)
    private val lockRotation = ValueBoolean(this, "Lock rotation", true)
    private val keepMovement = ValueBoolean(this, "Keep movement", false)

    private var position: Vec3d? = null
    private var beginRotation: Rotation? = null
    private var rotation: Rotation? = null

    private var prevCameraPos: Vec3d? = null

    private var perspective: Perspective? = null
    private var firstRealInput: Pair<Float, Float>? = null
    private var firstInput: Pair<Float, Float>? = null
    private var map = HashMap<KeyBinding, Boolean>()

    private val input = KeyboardInput(mc.options)

    override fun onEnable() {
        if (mc.player != null) {
            position = mc.gameRenderer.camera.pos
            rotation = Rotation(mc.player!!)
            beginRotation = rotation
            perspective = mc.options.perspective
            firstRealInput = PlayerUtil.input.let { Pair(MathUtil.roundAwayFromZero(it.movementForward.toDouble()).toFloat(), MathUtil.roundAwayFromZero(it.movementSideways.toDouble()).toFloat()) }
            firstInput = mc.player?.input?.let { Pair(MathUtil.roundAwayFromZero(it.movementForward.toDouble()).toFloat(), MathUtil.roundAwayFromZero(it.movementSideways.toDouble()).toFloat()) }
            for (keyBinding in mc.options.allKeys.filter { !PlayerUtil.movementKeys.contains(it) })
                map[keyBinding] = keyBinding.isPressed
        }
    }

    override fun onDisable() {
        mc.player?.yaw = beginRotation?.yaw!!
        mc.player?.pitch = beginRotation?.pitch!!
        mc.options.perspective = perspective
        prevCameraPos = null
        firstRealInput = null
        firstInput = null
        map.clear()
    }

    @Priority(1) // let all of this stuff get overridden
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventCameraOverride -> {
                if (position == null || rotation == null)
                    onEnable()
                mc.options.perspective = Perspective.THIRD_PERSON_BACK
                val accessor = event.camera as ICamera

                accessor.tarasande_invokeSetPos((prevCameraPos ?: position)?.lerp(position, mc.tickDelta.toDouble()))
                accessor.tarasande_invokeSetRotation(rotation?.yaw!!, rotation?.pitch!!)
            }

            is EventPollEvents -> {
                if (beginRotation == null)
                    onEnable()
                rotation = Rotation(mc.player!!)
                if (lockRotation.value && !event.dirty) {
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
                        MathUtil.roundAwayFromZero(input.movementSideways.toDouble()),
                        0.0,
                        MathUtil.roundAwayFromZero(input.movementForward.toDouble())
                    ), speed.value.toFloat(), rotation?.yaw!!)
                    velocity = Vec3d(velocity?.x!!, yMotion, velocity.z)
                    prevCameraPos = position
                    position = position!! + velocity
                }
            }

            is EventInput -> {
                if (firstInput == null || firstRealInput == null)
                    onEnable()
                if (event.input == MinecraftClient.getInstance().player?.input)
                    input.tick(event.slowDown, event.slowdownAmount)
                if (keepMovement.value)
                    if (event.input == MinecraftClient.getInstance().player?.input) {
                        if (firstInput != null) {
                            event.movementForward = firstInput?.first!!
                            event.movementSideways = firstInput?.second!!
                        }
                    } else if (event.input == PlayerUtil.input) {
                        if (firstRealInput != null) {
                            event.movementForward = firstRealInput?.first!!
                            event.movementSideways = firstRealInput?.second!!
                        }
                    }
            }

            is EventKeyBindingIsPressed -> {
                if (map.isEmpty())
                    onEnable()
                if (!PlayerUtil.movementKeys.contains(event.keyBinding) && (!keepMovement.value || event.keyBinding != mc.options.sneakKey /* safewalk */))
                    event.pressed = keepMovement.value && map[event.keyBinding] ?: false
            }
        }
    }

}