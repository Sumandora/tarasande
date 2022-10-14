package net.tarasandedevelopment.tarasande.util.math.rotation

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.eventsystem.Event
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.mixin.accessor.ILivingEntity
import net.tarasandedevelopment.tarasande.mixin.accessor.IPlayerMoveC2SPacket
import net.tarasandedevelopment.tarasande.util.extension.minus
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import java.util.function.Consumer
import kotlin.math.*

object RotationUtil {

    var fakeRotation: Rotation? = null

    private var lastMinRotateToOriginSpeed = 1.0
    private var lastMaxRotateToOriginSpeed = 1.0

    private var didRotate = false

    private var goalMovementYaw: Float? = null

    private var disableNextTeleport = false
    private var cachedRotation: Rotation? = null

    init {
        TarasandeMain.get().eventDispatcher.also {
            it.add(EventJump::class.java, 9999) {
                if (it.state != EventJump.State.PRE) return@add
                if (goalMovementYaw != null) it.yaw = goalMovementYaw!!
                if (fakeRotation != null)
                    if (TarasandeMain.get().clientValues.correctMovement.isSelected(2) || TarasandeMain.get().clientValues.correctMovement.isSelected(3))
                        it.yaw = fakeRotation!!.yaw
            }
            it.add(EventVelocityYaw::class.java, 9999) {
                if (goalMovementYaw != null) it.yaw = goalMovementYaw!!
                if (fakeRotation != null)
                    if (TarasandeMain.get().clientValues.correctMovement.isSelected(2) || TarasandeMain.get().clientValues.correctMovement.isSelected(3))
                        it.yaw = fakeRotation!!.yaw
            }
            it.add(EventInput::class.java, 9999) {
                if (it.input == MinecraftClient.getInstance().player?.input)
                    if (fakeRotation != null)
                        if (TarasandeMain.get().clientValues.correctMovement.isSelected(3)) {
                            if (it.movementForward == 0.0f && it.movementSideways == 0.0f) return@add

                            val realYaw = goalMovementYaw ?: MinecraftClient.getInstance().player!!.yaw
                            val fakeYaw = fakeRotation!!.yaw

                            val moveX = it.movementSideways * cos(Math.toRadians(realYaw.toDouble())) - it.movementForward * sin(Math.toRadians(realYaw.toDouble()))
                            val moveZ = it.movementForward * cos(Math.toRadians(realYaw.toDouble())) + it.movementSideways * sin(Math.toRadians(realYaw.toDouble()))

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
                                it.movementForward = round(bestMovement[1]).toInt().toFloat()
                                it.movementSideways = round(bestMovement[2]).toInt().toFloat()
                            }
                        }
            }
            it.add(EventIsWalking::class.java, 9999) {
                if (TarasandeMain.get().clientValues.correctMovement.isSelected(1)) {
                    it.walking = PlayerUtil.isPlayerMoving() && abs(MathHelper.wrapDegrees(PlayerUtil.getMoveDirection() - (fakeRotation?.yaw ?: (goalMovementYaw ?: return@add)))) <= 45
                }
            }
            it.add(EventHasForwardMovement::class.java, 9999) {
                if (TarasandeMain.get().clientValues.correctMovement.isSelected(1)) {
                    it.hasForwardMovement = PlayerUtil.isPlayerMoving() && abs(MathHelper.wrapDegrees(PlayerUtil.getMoveDirection() - (fakeRotation?.yaw ?: (goalMovementYaw ?: return@add)))) <= 45
                }
            }
            it.add(EventPacket::class.java, 9999) {
                if (it.type == EventPacket.Type.RECEIVE && it.packet is PlayerPositionLookS2CPacket) {
                    disableNextTeleport = true
                    if (fakeRotation != null)
                        fakeRotation = evaluateNewRotation(it.packet)
                } else if (it.type == EventPacket.Type.SEND && it.packet is PlayerMoveC2SPacket) {
                    if (fakeRotation != null) {
                        if (disableNextTeleport) { // this code is crap ._.
                            disableNextTeleport = false
                            return@add
                        }
                        val accessor = it.packet as IPlayerMoveC2SPacket
                        accessor.tarasande_setYaw(fakeRotation!!.yaw)
                        accessor.tarasande_setPitch(fakeRotation!!.pitch)
                    }
                }
            }
            it.add(EventPollEvents::class.java, 9999) {
                didRotate = true
            }
            it.add(EventTick::class.java, 9999) {
                if (it.state != EventTick.State.PRE) return@add

                if (!didRotate) {
                    simulateFakeRotationUpdate()
                }

                didRotate = false
            }
            it.add(EventUpdate::class.java, 9999) {
                when (it.state) {
                    EventUpdate.State.PRE_PACKET -> {
                        cachedRotation = Rotation(MinecraftClient.getInstance().player!!)
                        if (fakeRotation != null) {
                            MinecraftClient.getInstance().player!!.yaw = fakeRotation!!.yaw
                            MinecraftClient.getInstance().player!!.pitch = fakeRotation!!.pitch
                        }
                    }

                    EventUpdate.State.POST -> {
                        MinecraftClient.getInstance().player!!.yaw = cachedRotation!!.yaw
                        MinecraftClient.getInstance().player!!.pitch = cachedRotation!!.pitch
                    }

                    else -> {}
                }
            }
        }
    }

    private fun simulateFakeRotationUpdate() {
        if (TarasandeMain.get().clientValues.updateRotationsWhenTickSkipping.value)
            if (TarasandeMain.get().clientValues.updateRotationsAccurately.value) {
                for (i in 0..(1000.0 / RenderUtil.deltaTime).roundToInt())
                    updateFakeRotation(true)
            } else {
                updateFakeRotation(true)
            }
    }

    fun updateFakeRotation(fake: Boolean) {
        if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().interactionManager != null) {
            val eventPollEvents = EventPollEvents(Rotation(MinecraftClient.getInstance().player!!), fake)
            TarasandeMain.get().eventDispatcher.call(eventPollEvents)
            if (eventPollEvents.dirty) {
                fakeRotation = eventPollEvents.rotation
                lastMinRotateToOriginSpeed = eventPollEvents.minRotateToOriginSpeed
                lastMaxRotateToOriginSpeed = eventPollEvents.maxRotateToOriginSpeed
            } else if (fakeRotation != null) {
                val realRotation = Rotation(MinecraftClient.getInstance().player!!)
                val rotation = Rotation(fakeRotation!!)
                if ((MinecraftClient.getInstance().player as ILivingEntity).tarasande_getBodyTrackingIncrements() == 0)
                    rotation.smoothedTurn(realRotation, Pair(lastMinRotateToOriginSpeed, lastMaxRotateToOriginSpeed))
                rotation.correctSensitivity()
                val actualDist = fakeRotation!!.fov(rotation)
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

            val eventGoalMovement = EventGoalMovement(fakeRotation?.yaw ?: MinecraftClient.getInstance().player!!.yaw)
            TarasandeMain.get().eventDispatcher.call(eventGoalMovement)
            goalMovementYaw = if (eventGoalMovement.dirty) eventGoalMovement.yaw
            else null
        }
    }

    fun getRotations(from: Vec3d, to: Vec3d): Rotation {
        val delta = to - from
        return getRotations(delta)
    }

    fun getRotations(delta: Vec3d): Rotation {
        return Rotation(getYaw(delta).toFloat(), getPitch(delta.y, delta.horizontalLength()).toFloat())
    }

    fun getYaw(fromX: Double, fromZ: Double, toX: Double, toZ: Double) = getYaw(toX - fromX, toZ - fromZ)
    fun getYaw(deltaX: Double, deltaZ: Double) = getYaw(Vec2f(deltaX.toFloat(), deltaZ.toFloat()))
    fun getYaw(delta: Vec3d) = Math.toDegrees(atan2(delta.z, delta.x)) - 90
    fun getYaw(delta: Vec2f) = Math.toDegrees(atan2(delta.y, delta.x).toDouble()) - 90

    private fun getPitch(deltaY: Double, dist: Double) = -Math.toDegrees(atan2(deltaY, dist))

    fun evaluateNewRotation(packet: PlayerPositionLookS2CPacket): Rotation {
        var j = packet.yaw
        var k = packet.pitch
        if (packet.flags.contains(PlayerPositionLookS2CPacket.Flag.X_ROT)) {
            k += MinecraftClient.getInstance().player?.pitch!!
        }
        if (packet.flags.contains(PlayerPositionLookS2CPacket.Flag.Y_ROT)) {
            j += MinecraftClient.getInstance().player?.yaw!!
        }
        // The pitch calculation is literally mojang dev iq overload, kept for historic reasons
        val rot = Rotation(j, k)
        rot.yaw %= 360.0f
        rot.pitch = MathHelper.clamp(rot.pitch, -90.0f, 90.0f) % 360.0f
        return rot
    }
}