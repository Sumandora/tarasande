package su.mandora.tarasande.util.player.prediction

import net.minecraft.SharedConstants
import net.minecraft.block.BlockState
import net.minecraft.client.input.Input
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.recipebook.ClientRecipeBook
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.stat.StatHandler
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.injection.accessor.ILivingEntity
import su.mandora.tarasande.injection.accessor.prediction.IParticleManager
import su.mandora.tarasande.injection.accessor.prediction.ISoundSystem
import su.mandora.tarasande.mc
import su.mandora.tarasande.util.dummy.ClientPlayNetworkHandlerDummy
import su.mandora.tarasande.util.extension.minecraft.math.copy
import su.mandora.tarasande.util.extension.minecraft.math.minus

object PredictionEngine {

    fun predictState(count: Int, baseEntity: PlayerEntity = mc.player!!, input: Input? = null, abortWhen: (ClientPlayerEntity) -> Boolean = { false }): Pair<ClientPlayerEntity, ArrayList<Vec3d>> {
        val selfVelocity = baseEntity.velocity.copy()
        val localVelocity = mc.player?.velocity?.copy()

        val wasDevelopment = SharedConstants.isDevelopment
        SharedConstants.isDevelopment = true

        val soundSystem = mc.soundManager.soundSystem as ISoundSystem
        val wasSoundDisabled = soundSystem.tarasande_isDisabled()
        soundSystem.tarasande_setDisabled(true)

        val playerEntity = object : ClientPlayerEntity(
            mc,
            mc.world,
            ClientPlayNetworkHandlerDummy.create(),
            StatHandler(),
            ClientRecipeBook(),
            false,
            false
        ) {
            override fun getHealth(): Float {
                return maxHealth // we are invincible
            }

            override fun tickMovement() {
                fallDistance = 0F
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

        @Suppress("NAME_SHADOWING")
        val input = input ?: if (baseEntity == mc.player) mc.player?.input!! else getClosestInput(baseEntity)

        playerEntity.input = object : Input() {
            override fun tick(slowDown: Boolean, f: Float) {
                movementForward = input.movementForward
                movementSideways = input.movementSideways

                pressingForward = input.pressingForward
                pressingBack = input.pressingBack

                pressingLeft = input.pressingLeft
                pressingRight = input.pressingRight

                jumping = input.jumping
                sneaking = input.sneaking

                if (slowDown) {
                    movementSideways *= f
                    movementForward *= f
                }
            }
        }

        playerEntity.init()
        playerEntity.copyPositionAndRotation(baseEntity)
        playerEntity.copyFrom(baseEntity)

        if (playerEntity == mc.player) {
            Rotations.fakeRotation?.also {
                playerEntity.yaw = it.yaw
                playerEntity.pitch = it.pitch
            }
        }

        playerEntity.isOnGround = baseEntity.isOnGround // scary

        if (baseEntity == mc.player) {
            playerEntity.velocity = baseEntity.velocity
        } else {
            playerEntity.velocity = Vec3d.ZERO.copy()
        }

        playerEntity.pose = baseEntity.pose
        playerEntity.jumpingCooldown = baseEntity.jumpingCooldown
        playerEntity.submergedInWater = baseEntity.isSubmergedInWater
        playerEntity.touchingWater = baseEntity.isTouchingWater
        playerEntity.isSwimming = baseEntity.isSwimming

        if (baseEntity == mc.player) {
            playerEntity.autoJumpEnabled = mc.player?.autoJumpEnabled == true
            playerEntity.ticksToNextAutojump = mc.player?.ticksToNextAutojump!!
        } else {
            playerEntity.autoJumpEnabled = false // Who plays with that?
        }

        val list = ArrayList<Vec3d>()

        val prevParticlesEnabled = (mc.particleManager as IParticleManager).tarasande_isParticlesEnabled() // race conditions :c
        (mc.particleManager as IParticleManager).tarasande_setParticlesEnabled(false)

        for (i in 0 until count) {
            playerEntity.resetPosition()
            playerEntity.age++
            playerEntity.tick()
            list.add(playerEntity.pos)
            if (abortWhen.invoke(playerEntity))
                break
        }

        (mc.particleManager as IParticleManager).tarasande_setParticlesEnabled(prevParticlesEnabled)

        baseEntity.velocity = selfVelocity

        mc.player?.velocity = localVelocity // certain modifications assume that there is only one ClientPlayerEntity

        soundSystem.tarasande_setDisabled(wasSoundDisabled)

        return Pair(playerEntity, list)
    }

    val allInputs = run {
        val list = ArrayList<Input>()
        for (forward in -1..1)
            for (sideways in -1..1)
                list.add(Input(forward.toFloat(), sideways.toFloat()))
        list.toTypedArray()
    }


    private fun getClosestInput(baseEntity: PlayerEntity): Input {
        val prevServerPos = (baseEntity as ILivingEntity).tarasande_prevServerPos() ?: return Input(0F, 0F)
        val velocity = Vec3d(baseEntity.serverX, baseEntity.serverY, baseEntity.serverZ) - prevServerPos

        var best: Pair<Input, Double>? = null
        for (input in allInputs) {
            @Suppress("NAME_SHADOWING")
            val input = input.with(!baseEntity.isOnGround, baseEntity.isSneaking)
            val standStill = input.movementForward == 0F && input.movementSideways == 0F
            if (velocity.horizontalLengthSquared() > 0.0 && standStill)
                continue

            val nextPos =
                if (standStill)
                    Vec3d(0.0, 0.0, 0.0)
                else
                    Entity.movementInputToVelocity(input.movementInput.let { Vec3d(it.x.toDouble(), 0.0, it.y.toDouble()) }, 1F, baseEntity.serverYaw.toFloat())

            val distance = velocity.distanceTo(nextPos)
            if (best == null || best.second > distance)
                best = Pair(input, distance)
        }
        return best!!.first
    }

}

fun Input(movementForward: Float, movementSideways: Float) = Input().also {
    it.movementForward = movementForward
    it.movementSideways = movementSideways

    it.pressingForward = movementForward > 0.0
    it.pressingBack = movementForward < 0.0

    it.pressingLeft = movementSideways > 0.0
    it.pressingRight = movementSideways < 0.0
}

fun Input.with(jumping: Boolean = this.jumping, sneaking: Boolean = this.sneaking): Input {
    return Input(this.movementForward, this.movementSideways).also {
        it.jumping = jumping
        it.sneaking = sneaking
    }
}
