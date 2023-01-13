package net.tarasandedevelopment.tarasande.util.math.rotation

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.RotationValues
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.util.extension.minecraft.minus
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import su.mandora.event.EventDispatcher
import kotlin.math.*

object RotationUtil {

    var fakeRotation: Rotation? = null

    private var didRotate = false

    private var goalMovementYaw: Float? = null

    private var disableNextTeleport = false
    private var cachedRotation: Rotation? = null

    init {
        EventDispatcher.apply {
            add(EventJump::class.java, 9999) {
                if (it.state != EventJump.State.PRE) return@add
                if (goalMovementYaw != null) it.yaw = goalMovementYaw!!
                if (fakeRotation != null)
                    if (RotationValues.correctMovement.isSelected(2) || RotationValues.correctMovement.isSelected(3)) {
                        it.yaw = fakeRotation!!.yaw
                    }
            }
            add(EventVelocityYaw::class.java, 9999) {
                if (goalMovementYaw != null) it.yaw = goalMovementYaw!!
                if (fakeRotation != null)
                    if (RotationValues.correctMovement.isSelected(2) || RotationValues.correctMovement.isSelected(3))
                        it.yaw = fakeRotation!!.yaw
            }
            add(EventInput::class.java, 9999) {
                if (it.input == mc.player?.input)
                    if (fakeRotation != null)
                        if (RotationValues.correctMovement.isSelected(3)) {
                            if (it.movementForward == 0.0F && it.movementSideways == 0.0F) return@add

                            val realYaw = goalMovementYaw ?: mc.player!!.yaw
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
            add(EventCanSprint::class.java) {
                if (fakeRotation != null)
                    if (RotationValues.correctMovement.isSelected(1)) {
                        if(abs(MathHelper.wrapDegrees(fakeRotation?.yaw!! - (goalMovementYaw ?: mc.player?.yaw!!))) > 45.0f)
                            it.canSprint = false // oof
                    }
            }
            add(EventPacket::class.java, 9999) {
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
                        it.packet.yaw = fakeRotation!!.yaw
                        it.packet.pitch = fakeRotation!!.pitch
                    }
                }
            }
            add(EventPollEvents::class.java, 9999) {
                didRotate = true
            }
            add(EventTick::class.java, 9999) {
                if (it.state != EventTick.State.PRE) return@add

                if (!didRotate) {
                    simulateFakeRotationUpdate()
                }

                didRotate = false
            }
            add(EventUpdate::class.java, 9999) {
                when (it.state) {
                    EventUpdate.State.PRE_PACKET -> {
                        cachedRotation = Rotation(mc.player!!)
                        if (fakeRotation != null) {
                            mc.player!!.yaw = fakeRotation!!.yaw
                            mc.player!!.pitch = fakeRotation!!.pitch
                        }
                    }

                    EventUpdate.State.POST -> {
                        mc.player!!.yaw = cachedRotation!!.yaw
                        mc.player!!.pitch = cachedRotation!!.pitch
                    }

                    else -> {}
                }
            }
        }
    }

    private fun simulateFakeRotationUpdate() {
        if (RotationValues.updateRotationsWhenTickSkipping.value)
            if (RotationValues.updateRotationsAccurately.value) {
                for (i in 0..(1000.0 / RenderUtil.deltaTime).roundToInt())
                    updateFakeRotation(true)
            } else {
                updateFakeRotation(true)
            }
    }

    fun updateFakeRotation(fake: Boolean) {
        if (mc.player != null && mc.interactionManager != null) {
            val eventPollEvents = EventPollEvents(Rotation(mc.player!!), fake)
            EventDispatcher.call(eventPollEvents)
            if (eventPollEvents.dirty) {
                fakeRotation = eventPollEvents.rotation
            } else if (fakeRotation != null) {
                val realRotation = Rotation(mc.player!!)
                var rotation = Rotation(fakeRotation!!)
                if (mc.player?.bodyTrackingIncrements == 0)
                    rotation = rotation.smoothedTurn(realRotation, RotationValues.rotateToOriginSpeed)
                rotation = rotation.correctSensitivity()
                if (fakeRotation == rotation) {
                    val actualRotation = Rotation(mc.player!!).correctSensitivity()
                    fakeRotation = null
                    mc.player?.yaw = actualRotation.yaw.also {
                        mc.player?.renderYaw = it
                        mc.player?.lastRenderYaw = it
                        mc.player?.prevYaw = it
                    }
                    mc.player?.pitch = actualRotation.pitch
                } else {
                    fakeRotation = rotation
                }
            }

            val eventGoalMovement = EventGoalMovement(fakeRotation?.yaw ?: mc.player!!.yaw)
            EventDispatcher.call(eventGoalMovement)
            goalMovementYaw =
                if (eventGoalMovement.dirty)
                    eventGoalMovement.yaw
                else
                    null
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
            k += mc.player?.pitch!!
        }
        if (packet.flags.contains(PlayerPositionLookS2CPacket.Flag.Y_ROT)) {
            j += mc.player?.yaw!!
        }
        return Rotation(j, k)
    }
}