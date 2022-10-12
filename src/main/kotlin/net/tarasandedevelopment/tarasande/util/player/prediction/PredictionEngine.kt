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
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil

object PredictionEngine {

    fun predictState(count: Int): Pair<ClientPlayerEntity, ArrayList<Vec3d>> {
        /*
         * Irgendwie funktioniert das hier ohne Minecraft code in den Utilklassen?
         * Ich glaub E-Sound ist einfach scheiße
         *
         * Darauf kracht das Crystal! :sunglasses:
         */
        val mc = MinecraftClient.getInstance()

        val selfVelocity = mc.player?.velocity!!

        val wasDevelopment = SharedConstants.isDevelopment
        SharedConstants.isDevelopment = true // prevent that telemetry sender to do any bs
        val playerEntity = object : ClientPlayerEntity(mc,
            mc.world,
            object : ClientPlayNetworkHandler(mc,
                mc.currentScreen,
                mc.networkHandler?.connection,
                mc.player?.gameProfile,
                object : TelemetrySender(null, null, null, null, null) {
                    override fun send() {
                        // lmao no
                    }
                }
            ) {
                override fun sendPacket(packet: Packet<*>?) {
                    // lmao dont send packets
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
                fallDistance = 0.0f
                super.tickMovement()
            }

            override fun fall(heightDifference: Double, onGround: Boolean, state: BlockState?, landedPosition: BlockPos?) {
            }

            override fun spawnSprintingParticles() {
            }

            override fun isCamera(): Boolean {
                return true // 1, 2 Polizei 3, 4 Crystal hier 5, 6 Alte Hex, 7, 8 Crystal kracht
            }

            override fun playSound(sound: SoundEvent?, volume: Float, pitch: Float) {
            }

            override fun playSound(event: SoundEvent?, category: SoundCategory?, volume: Float, pitch: Float) {
            }

            override fun onSwimmingStart() {
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

        val list = ArrayList<Vec3d>()

        for (i in 0 until count) {
            playerEntity.resetPosition()
            playerEntity.age++
            playerEntity.tickMovement()
            list.add(playerEntity.pos)
        }

        // bruder also von einer Skala von Augustus bis Koks ist das hier schon safe Klientus
        mc.player?.velocity = selfVelocity

        return Pair(playerEntity, list)
    }

}