package su.mandora.tarasande.system.feature.modulesystem.impl.render

import net.minecraft.client.input.Input
import net.minecraft.client.option.Perspective
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.*
import su.mandora.tarasande.feature.rotation.api.Rotation
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
import su.mandora.tarasande.util.extension.minecraft.math.toVec3d
import su.mandora.tarasande.util.extension.minecraft.setMovementForward
import su.mandora.tarasande.util.extension.minecraft.setMovementSideways
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.prediction.copy
import su.mandora.tarasande.util.string.StringUtil

class ModuleFreeCam : Module("Free cam", "Allows you to freely move the camera", ModuleCategory.RENDER) {

    private val speed = ValueNumber(this, "Speed", 0.1, 1.0, 5.0, 0.1)
    private val keepMovement = ValueBoolean(this, "Keep movement", false)
    private val blockInteraction = ValueBoolean(this, "Block interaction", false)

    private var position: Vec3d? = null
    private var rotation: Rotation? = null

    private var prevCameraPos: Vec3d? = null

    private var perspective: Perspective? = null
    private var firstInput: Input? = null

    private var sprinting: Boolean? = null

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
            firstInput = mc.player!!.input.copy()
            sprinting = mc.options.sprintKey.isPressed // let modulesprint override this
        }
    }

    override fun onDisable() {
        mc.options.perspective = perspective ?: Perspective.FIRST_PERSON
        prevCameraPos = null
        firstInput = null
        sprinting = null
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
                    if (mc.options.jumpKey.isPressed)
                        yMotion += speed.value
                    if (mc.options.sneakKey.isPressed)
                        yMotion -= speed.value
                    var velocity = Entity.movementInputToVelocity(PlayerUtil.computeMovementInput().toVec3d(flipped = true), speed.value.toFloat(), rotation?.yaw!!)
                    velocity = Vec3d(velocity?.x!!, yMotion, velocity.z)
                    prevCameraPos = position
                    position = position!! + velocity
                }
            }
        }

        registerEvent(EventInput::class.java, 1) { event ->
            if (firstInput == null)
                onEnable()
            else {
                if (event.input == mc.player?.input) {
                    if (!keepMovement.value) {
                        event.input.setMovementForward(0F)
                        event.input.setMovementSideways(0F)
                        event.input.jumping = false
                        event.input.sneaking = false
                    } else {
                        firstInput!!.also {
                            event.input.setMovementForward(it.movementForward)
                            event.input.setMovementSideways(it.movementSideways)
                            event.input.jumping = it.jumping
                            event.input.sneaking = it.sneaking
                        }
                    }
                }
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java, 1) { event ->
            if (event.keyBinding == mc.options.sprintKey)
                if (sprinting == null)
                    onEnable()
                else
                    event.pressed = keepMovement.value && sprinting!!
        }

        registerEvent(EventUpdateTargetedEntity::class.java, 1) { event ->
            if (event.state == EventUpdateTargetedEntity.State.POST)
                if (!(mc.gameRenderer as IGameRenderer).tarasande_isSelfInflicted() && position != null && rotation != null)
                    if (blockInteraction.value) {
                        mc.targetedEntity = null
                        mc.crosshairTarget = PlayerUtil.rayCast(position!!, position!! + rotation?.forwardVector()!! * (mc.gameRenderer as IGameRenderer).tarasande_getReach())
                    }
        }
    }
}