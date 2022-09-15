package su.mandora.tarasande.util.math.rotation

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.event.*
import su.mandora.tarasande.mixin.accessor.ILivingEntity
import su.mandora.tarasande.mixin.accessor.IPlayerMoveC2SPacket
import su.mandora.tarasande.module.movement.ModuleNoRotate
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.render.RenderUtil
import java.util.function.Consumer
import kotlin.math.*

object RotationUtil {

    var fakeRotation: Rotation? = null

    private var lastMinRotateToOriginSpeed = 1.0
    private var lastMaxRotateToOriginSpeed = 1.0

    private var didRotate = false

    private var goalMovementYaw: Float? = null

    private var disableNextTeleport = false

    init {
        TarasandeMain.get().managerEvent?.add(Pair(1001, Consumer<Event> { event ->
            when (event) {
                is EventJump -> {
                    if (event.state != EventJump.State.PRE) return@Consumer
                    if (goalMovementYaw != null) event.yaw = goalMovementYaw!!
                    if (fakeRotation != null)
                        if (TarasandeMain.get().clientValues?.correctMovement?.isSelected(2)!! || TarasandeMain.get().clientValues?.correctMovement?.isSelected(3)!!)
                            event.yaw = fakeRotation?.yaw!!
                }

                is EventVelocityYaw -> {
                    if (goalMovementYaw != null) event.yaw = goalMovementYaw!!
                    if (fakeRotation != null)
                        if (TarasandeMain.get().clientValues?.correctMovement?.isSelected(2)!! || TarasandeMain.get().clientValues?.correctMovement?.isSelected(3)!!)
                            event.yaw = fakeRotation?.yaw!!
                }

                is EventInput -> {
                    if (fakeRotation != null)
                        if (TarasandeMain.get().clientValues?.correctMovement?.isSelected(3)!!) {
                            if (event.movementForward == 0.0f && event.movementSideways == 0.0f) return@Consumer

                            val realYaw = goalMovementYaw ?: MinecraftClient.getInstance().player?.yaw!!
                            val fakeYaw = fakeRotation?.yaw!!

                            val moveX = event.movementSideways * cos(Math.toRadians(realYaw.toDouble())) - event.movementForward * sin(Math.toRadians(realYaw.toDouble()))
                            val moveZ = event.movementForward * cos(Math.toRadians(realYaw.toDouble())) + event.movementSideways * sin(Math.toRadians(realYaw.toDouble()))

                            var bestMovement: DoubleArray? = null

                            for (forward in -1..1) for (strafe in -1..1) {
                                val newMoveX = strafe * cos(Math.toRadians(fakeYaw.toDouble())) - forward * sin(Math.toRadians(fakeYaw.toDouble()))
                                val newMoveZ = forward * cos(Math.toRadians(fakeYaw.toDouble())) + strafe * sin(Math.toRadians(fakeYaw.toDouble()))

                                val deltaX = newMoveX - moveX
                                val deltaZ = newMoveZ - moveZ

                                val dist = deltaX * deltaX + deltaZ * deltaZ

                                if (bestMovement == null || bestMovement[0] > dist) {
                                    bestMovement = doubleArrayOf(dist, forward.toDouble(), strafe.toDouble())
                                }
                            }

                            if (bestMovement != null) {
                                event.movementForward = round(bestMovement[1]).toInt().toFloat()
                                event.movementSideways = round(bestMovement[2]).toInt().toFloat()
                            }
                        }
                }

                is EventIsWalking -> {
                    if (TarasandeMain.get().clientValues?.correctMovement?.isSelected(1)!! && (fakeRotation != null || goalMovementYaw != null)) {
                        event.walking = (MinecraftClient.getInstance().player?.input?.movementInput?.lengthSquared()!! > 0.8f * 0.8f) && abs(MathHelper.wrapDegrees(PlayerUtil.getMoveDirection() - (fakeRotation?.yaw ?: (goalMovementYaw ?: 0.0f)))) <= 45
                    }
                }

                is EventHasForwardMovement -> {
                    if (TarasandeMain.get().clientValues?.correctMovement?.isSelected(1)!! && (fakeRotation != null || goalMovementYaw != null)) {
                        event.hasForwardMovement = MinecraftClient.getInstance().player?.input?.movementInput?.lengthSquared()!! > 0.8f * 0.8f && abs(MathHelper.wrapDegrees(PlayerUtil.getMoveDirection() - (fakeRotation?.yaw ?: (goalMovementYaw ?: 0.0f)))) <= 45
                    }
                }

                is EventPacket -> {
                    if (event.type == EventPacket.Type.RECEIVE && event.packet is PlayerPositionLookS2CPacket) {
                        disableNextTeleport = true
                        if (fakeRotation != null)
                            fakeRotation = TarasandeMain.get().managerModule?.get(ModuleNoRotate::class.java)?.evaluateNewRotation(event.packet)
                    } else if (event.type == EventPacket.Type.SEND && event.packet is PlayerMoveC2SPacket) {
                        if (fakeRotation != null) {
                            if (disableNextTeleport) { // this code is crap ._.
                                disableNextTeleport = false
                                return@Consumer
                            }
                            val accessor = event.packet as IPlayerMoveC2SPacket
                            accessor.tarasande_setYaw(fakeRotation?.yaw!!)
                            accessor.tarasande_setPitch(fakeRotation?.pitch!!)
                        }
                    }
                }

                // premium code ahead
                is EventPollEvents -> {
                    didRotate = true
                }

                is EventTick -> {
                    if (event.state != EventTick.State.PRE) return@Consumer

                    if (!didRotate) {
                        simulateFakeRotationUpdate()
                    }

                    didRotate = false
                }
            }
        }))
    }

    private fun simulateFakeRotationUpdate() {
        if (TarasandeMain.get().clientValues?.updateRotationsWhenTickSkipping?.value!!)
            if (TarasandeMain.get().clientValues?.updateRotationsAccurately?.value!!) {
                for (i in 0..(1000.0 / RenderUtil.deltaTime).roundToInt())
                    updateFakeRotation(true)
            } else {
                updateFakeRotation(true)
            }
    }

    fun updateFakeRotation(fake: Boolean) {
        if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().interactionManager != null) {
            val eventPollEvents = EventPollEvents(Rotation(MinecraftClient.getInstance().player!!), fake)
            TarasandeMain.get().managerEvent?.call(eventPollEvents)
            if (eventPollEvents.dirty) {
                fakeRotation = eventPollEvents.rotation
                lastMinRotateToOriginSpeed = eventPollEvents.minRotateToOriginSpeed
                lastMaxRotateToOriginSpeed = eventPollEvents.maxRotateToOriginSpeed
            } else if (fakeRotation != null) {
                val realRotation = Rotation(MinecraftClient.getInstance().player!!)
                val rotation = Rotation(fakeRotation!!)
                if ((MinecraftClient.getInstance().player as ILivingEntity?)?.tarasande_getBodyTrackingIncrements()!! == 0)
                    rotation.smoothedTurn(realRotation, Pair(lastMinRotateToOriginSpeed, lastMaxRotateToOriginSpeed))
                rotation.correctSensitivity()
                val actualDist = fakeRotation?.fov(rotation)!!
                val gcd = Rotation.getGcd() * 0.15f
                if (actualDist <= gcd / 2 + 0.1 /* little more */) {
                    val actualRotation = Rotation(MinecraftClient.getInstance().player!!)
                    actualRotation.correctSensitivity()
                    fakeRotation = null
                    MinecraftClient.getInstance().player?.yaw = actualRotation.yaw.also {
                        MinecraftClient.getInstance().player?.renderYaw = it
                        MinecraftClient.getInstance().player?.lastRenderYaw = it
                        MinecraftClient.getInstance().player?.prevYaw = it
                    }
                    MinecraftClient.getInstance().player?.pitch = actualRotation.pitch
                } else {
                    fakeRotation = rotation
                }
            }

            val eventGoalMovement = EventGoalMovement(fakeRotation?.yaw ?: MinecraftClient.getInstance().player?.yaw!!)
            TarasandeMain.get().managerEvent?.call(eventGoalMovement)
            goalMovementYaw = if (eventGoalMovement.dirty) eventGoalMovement.yaw
            else null
        }
    }

    fun getRotations(from: Vec3d, to: Vec3d): Rotation {
        val delta = to.subtract(from)
        return getRotations(delta)
    }

    fun getRotations(delta: Vec3d): Rotation {
        return Rotation(getYaw(delta).toFloat(), getPitch(delta.y, delta.horizontalLength()).toFloat())
    }

    fun getYaw(fromX: Double, fromZ: Double, toX: Double, toZ: Double) = getYaw(toX - fromX, toZ - fromZ)
    fun getYaw(deltaX: Double, deltaZ: Double) = getYaw(Vec2f(deltaX.toFloat(), deltaZ.toFloat()))
    fun getYaw(delta: Vec3d) = Math.toDegrees(atan2(delta.z, delta.x)) - 90
    fun getYaw(delta: Vec2f) = Math.toDegrees(atan2(delta.y, delta.x).toDouble()) - 90

    fun getPitch(deltaY: Double, dist: Double) = -Math.toDegrees(atan2(deltaY, dist))

}