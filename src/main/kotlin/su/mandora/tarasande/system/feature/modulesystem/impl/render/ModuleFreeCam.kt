package su.mandora.tarasande.system.feature.modulesystem.impl.render

import net.minecraft.client.input.KeyboardInput
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.Perspective
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.*
import su.mandora.tarasande.injection.accessor.IGameRenderer
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.system.screen.informationsystem.ManagerInformation
import su.mandora.tarasande.util.extension.minecraft.math.plus
import su.mandora.tarasande.util.extension.minecraft.math.times
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.string.StringUtil

class ModuleFreeCam : Module("Free cam", "Allows you to freely move the camera", ModuleCategory.RENDER) {

    private val speed = ValueNumber(this, "Speed", 0.1, 1.0, 5.0, 0.1)
    private val keepMovement = ValueBoolean(this, "Keep movement", false)
    private val blockInteraction = ValueBoolean(this, "Block interaction", false)

    private var position: Vec3d? = null
    private var rotation: Rotation? = null

    private var prevCameraPos: Vec3d? = null

    private var perspective: Perspective? = null
    private var firstRealInput: Pair<Float, Float>? = null
    private var firstInput: Pair<Float, Float>? = null
    private var map = HashMap<KeyBinding, Boolean>()

    private val input = KeyboardInput(mc.options)

    private val capturedKeys = arrayOf(mc.options.jumpKey, mc.options.sneakKey, mc.options.sprintKey)

    init {
        ManagerInformation.add(object : Information("Free cam", "Camera position") {
            private val decimalPlacesX = ValueNumber(this, "Decimal places x", 0.0, 1.0, 5.0, 1.0)
            private val decimalPlacesY = ValueNumber(this, "Decimal places y", 0.0, 1.0, 5.0, 1.0)
            private val decimalPlacesZ = ValueNumber(this, "Decimal places z", 0.0, 1.0, 5.0, 1.0)

            override fun getMessage(): String? {
                if (!enabled.value)
                    return null

                return position?.let {
                    StringUtil.round(it.x, decimalPlacesX.value.toInt()) + " " +
                            StringUtil.round(it.y, decimalPlacesY.value.toInt()) + " " +
                            StringUtil.round(it.z, decimalPlacesZ.value.toInt())
                }
            }
        })
    }

    override fun onEnable() {
        if (mc.player != null) {
            position = mc.gameRenderer.camera.pos
            rotation = mc.gameRenderer.camera.let { Rotation(it.yaw, it.pitch) }
            perspective = mc.options.perspective
            firstRealInput = PlayerUtil.input.let { Pair(MathUtil.roundAwayFromZero(it.movementForward.toDouble()).toFloat(), MathUtil.roundAwayFromZero(it.movementSideways.toDouble()).toFloat()) }
            firstInput = mc.player!!.input.let { Pair(MathUtil.roundAwayFromZero(it.movementForward.toDouble()).toFloat(), MathUtil.roundAwayFromZero(it.movementSideways.toDouble()).toFloat()) }
            for (keyBinding in mc.options.allKeys.filter { capturedKeys.contains(it) })
                map[keyBinding] = keyBinding.pressed
        }
    }

    override fun onDisable() {
        mc.options.perspective = perspective ?: Perspective.FIRST_PERSON
        prevCameraPos = null
        firstRealInput = null
        firstInput = null
        map.clear()
    }

    init {
        registerEvent(EventCameraOverride::class.java, 1) { event ->
            if (position == null || rotation == null)
                onEnable()
            else {
                mc.options.perspective = Perspective.THIRD_PERSON_BACK
                event.camera.pos = (prevCameraPos ?: position)?.lerp(position, mc.tickDelta.toDouble())
                event.camera.setRotation(rotation?.yaw!!, rotation?.pitch!!)
            }
        }

        registerEvent(EventMouseDelta::class.java, 1) { event ->
            if (rotation == null)
                onEnable()
            else {
                rotation = Rotation.calculateNewRotation(rotation!!, Pair(event.deltaX, event.deltaY)).correctSensitivity() // clamp
                event.deltaX = 0.0
                event.deltaY = 0.0
            }
        }

        registerEvent(EventUpdate::class.java, 1) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (position == null || rotation == null)
                    onEnable()
                else {
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
        }

        registerEvent(EventInput::class.java, 1) { event ->
            if (firstInput == null || firstRealInput == null)
                onEnable()
            else {
                if (event.input == mc.player?.input) {
                    if (!keepMovement.value) {
                        event.movementForward = 0F
                        event.movementSideways = 0F
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
        }

        registerEvent(EventKeyBindingIsPressed::class.java, 1) { event ->
            if (map.isEmpty())
                onEnable()
            else {
                if (capturedKeys.contains(event.keyBinding))
                    event.pressed = keepMovement.value && map[event.keyBinding] ?: false
            }
        }

        registerEvent(EventUpdateTargetedEntity::class.java, 1) { event ->
            if (event.state == EventUpdateTargetedEntity.State.POST)
                if (!(mc.gameRenderer as IGameRenderer).tarasande_isSelfInflicted() && position != null && rotation != null)
                    if (blockInteraction.value) {
                        mc.targetedEntity = null
                        mc.crosshairTarget = PlayerUtil.rayCast(position!!, position!! + rotation?.forwardVector()!! * (mc.gameRenderer as IGameRenderer).tarasande_getReach())
                    }
        }

        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.PRE && mc.player != null)
                input.tick(false, 1F)
        }
    }
}