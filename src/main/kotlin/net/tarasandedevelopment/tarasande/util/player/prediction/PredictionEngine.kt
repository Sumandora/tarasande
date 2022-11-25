package net.tarasandedevelopment.tarasande.util.player.prediction

import net.minecraft.SharedConstants
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.Input
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.recipebook.ClientRecipeBook
import net.minecraft.client.util.telemetry.TelemetrySender
import net.minecraft.network.Packet
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.stat.StatHandler
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.injection.accessor.prediction.IParticleManager
import net.tarasandedevelopment.tarasande.injection.accessor.prediction.ISoundSystem
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil

object PredictionEngine {

    fun predictState(count: Int): Pair<ClientPlayerEntity, ArrayList<Vec3d>> {
        val mc = MinecraftClient.getInstance()

        val selfVelocity = mc.player?.velocity!!

        val wasDevelopment = SharedConstants.isDevelopment
        SharedConstants.isDevelopment = true
        val soundSystem = MinecraftClient.getInstance().soundManager.soundSystem as ISoundSystem
        val wasSoundDisabled = soundSystem.tarasande_isDisabled()
        soundSystem.tarasande_setDisabled(true)
        val playerEntity = object : ClientPlayerEntity(mc,
            mc.world,
            object : ClientPlayNetworkHandler(mc,
                mc.currentScreen,
                mc.networkHandler?.connection,
                mc.player?.gameProfile,
                object : TelemetrySender(null, null, null, null, null) {
                    override fun send() {
                    }
                }
            ) {
                override fun sendPacket(packet: Packet<*>?) {
                }
            },
            StatHandler(),
            ClientRecipeBook(),
            false,
            false
        ) {
            override fun getHealth(): Float {
                return maxHealth // we are invincible
            }

            override fun tickMovement() {
                fallDistance = 0.0F
                super.tickMovement()
            }

            override fun fall(heightDifference: Double, onGround: Boolean, state: BlockState?, landedPosition: BlockPos?) {
            }

            override fun isCamera(): Boolean {
                return true
            }

            override fun playSound(sound: SoundEvent?, volume: Float, pitch: Float) {
            }

            override fun playSound(event: SoundEvent?, category: SoundCategory?, volume: Float, pitch: Float) {
            }
        }
        SharedConstants.isDevelopment = wasDevelopment
        playerEntity.input = object : Input() {
            override fun tick(slowDown: Boolean, f: Float) {
                pressingForward = mc.player?.input?.pressingForward == true
                pressingBack = mc.player?.input?.pressingBack == true
                pressingLeft = mc.player?.input?.pressingLeft == true
                pressingRight = mc.player?.input?.pressingRight == true
                movementForward = mc.player?.input?.movementForward!!
                movementSideways = mc.player?.input?.movementSideways!!

                jumping = mc.player?.input?.jumping == true
                sneaking = mc.player?.input?.sneaking == true
            }
        }
        playerEntity.init()
        playerEntity.copyPositionAndRotation(mc.player)
        playerEntity.copyFrom(mc.player)
        if (RotationUtil.fakeRotation != null) {
            playerEntity.yaw = RotationUtil.fakeRotation?.yaw!!
            playerEntity.pitch = RotationUtil.fakeRotation?.pitch!!
        }
        playerEntity.isOnGround = mc.player?.isOnGround == true
        playerEntity.velocity = mc.player?.velocity
        playerEntity.pose = mc.player?.pose
        playerEntity.autoJumpEnabled = mc.player?.isAutoJumpEnabled == true
        playerEntity.ticksToNextAutojump = mc.player!!.ticksToNextAutojump
        playerEntity.jumpingCooldown = mc.player!!.jumpingCooldown
        playerEntity.horizontalCollision = mc.player?.horizontalCollision == true
        playerEntity.verticalCollision = mc.player?.verticalCollision == true
        playerEntity.collidedSoftly = mc.player?.collidedSoftly == true
        playerEntity.movementSpeed = mc.player?.movementSpeed!!
        playerEntity.airStrafingSpeed = mc.player?.airStrafingSpeed!!
        playerEntity.forwardSpeed = mc.player?.forwardSpeed!!
        playerEntity.sidewaysSpeed = mc.player?.sidewaysSpeed!!
        playerEntity.upwardSpeed = mc.player?.upwardSpeed!!
        playerEntity.speed = mc.player?.speed!!
        playerEntity.horizontalSpeed = mc.player?.horizontalSpeed!!
        playerEntity.submergedInWater = mc.player?.isSubmergedInWater == true
        playerEntity.touchingWater = mc.player?.isTouchingWater == true
        playerEntity.isSwimming = mc.player?.isSwimming == true

        val list = ArrayList<Vec3d>()

        val prevParticlesEnabled = (MinecraftClient.getInstance().particleManager as IParticleManager).tarasande_isParticlesEnabled() // race conditions :c
        (MinecraftClient.getInstance().particleManager as IParticleManager).tarasande_setParticlesEnabled(false)
        for (i in 0 until count) {
            playerEntity.resetPosition()
            playerEntity.age++
            playerEntity.tick()
            list.add(playerEntity.pos)
        }
        (MinecraftClient.getInstance().particleManager as IParticleManager).tarasande_setParticlesEnabled(prevParticlesEnabled)

        mc.player?.velocity = selfVelocity

        soundSystem.tarasande_setDisabled(wasSoundDisabled)

        return Pair(playerEntity, list)
    }
}
