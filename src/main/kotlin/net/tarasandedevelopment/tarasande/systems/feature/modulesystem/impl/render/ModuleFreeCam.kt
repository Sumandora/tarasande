package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render

import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.KeyboardInput
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.Perspective
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.plus
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

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
                map[keyBinding] = keyBinding.pressed
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

    init {
        registerEvent(EventCameraOverride::class.java, 1) { event ->
            if (position == null || rotation == null)
                onEnable()
            mc.options.perspective = Perspective.THIRD_PERSON_BACK
            event.camera.pos = (prevCameraPos ?: position)?.lerp(position, mc.tickDelta.toDouble())
            event.camera.setRotation(rotation?.yaw!!, rotation?.pitch!!)
        }

        registerEvent(EventPollEvents::class.java, 1) { event ->
            if (beginRotation == null)
                onEnable()
            mc.player?.yaw = beginRotation?.yaw!!
            mc.player?.pitch = beginRotation?.pitch!!
            if (lockRotation.value && !event.dirty) {
                event.rotation = beginRotation!!
                // We return ourselves in onDisable
                event.minRotateToOriginSpeed = 0.0
                event.maxRotateToOriginSpeed = 0.0
            }
        }

        registerEvent(EventMouseDelta::class.java, 1) { event ->
            rotation = rotation?.plus(Rotation.calculateRotationChange(-event.deltaX, -event.deltaY))?.correctSensitivity() // clamp
            event.deltaX = 0.0
            event.deltaY = 0.0
        }

        registerEvent(EventUpdate::class.java, 1) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (position == null || rotation == null)
                    onEnable()
                var yMotion = 0.0
                if (mc.options.jumpKey.pressed)
                    yMotion += speed.value
                if (mc.options.sneakKey.pressed)
                    yMotion -= speed.value
                var velocity = Entity.movementInputToVelocity(Vec3d(
                    MathUtil.roundAwayFromZero(input.movementSideways.toDouble()),
                    0.0,
                    MathUtil.roundAwayFromZero(input.movementForward.toDouble())
                ), speed.value.toFloat(), rotation?.yaw!!)
                velocity = Vec3d(velocity?.x!!, yMotion, velocity.z)
                prevCameraPos = position
                position = position!! + velocity
            }
        }

        registerEvent(EventInput::class.java, 1) { event ->
            if (firstInput == null || firstRealInput == null)
                onEnable()
            if (event.input == MinecraftClient.getInstance().player?.input) {
                input.tick(event.slowDown, event.slowdownAmount)
                if (!keepMovement.value) {
                    event.movementForward = 0.0f
                    event.movementSideways = 0.0f
                }
            } else if (event.input == PlayerUtil.input) {
                event.cancelled = true
            }
            if (keepMovement.value) {
                if (event.input != input) {
                    event.movementForward = firstRealInput?.first!!
                    event.movementSideways = firstRealInput?.second!!
                    event.cancelled = false
                }
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java, 1) { event ->
            if (map.isEmpty())
                onEnable()
            if (!PlayerUtil.movementKeys.contains(event.keyBinding))
                event.pressed = keepMovement.value && map[event.keyBinding] ?: false
        }
    }
}