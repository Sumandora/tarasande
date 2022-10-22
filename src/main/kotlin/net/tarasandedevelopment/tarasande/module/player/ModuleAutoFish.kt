package net.tarasandedevelopment.tarasande.module.player

import net.minecraft.item.FishingRodItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.math.BlockPos
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.player.prediction.projectile.ProjectileUtil
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber
import kotlin.math.abs

class ModuleAutoFish : Module("Auto fish", "Automates fishing", ModuleCategory.PLAYER) {

    private val mode = ValueMode(this, "Mode", false, "Data tracker", "Bubbles")
    private val sensitiveDistance = object : ValueNumber(this, "Distance", 0.0, 1.0, 3.0, 0.1) {
        override fun isEnabled(): Boolean = mode.isSelected(1)
    }
    private val timeToWait = object : ValueNumber(this, "Time to wait", 0.0, 100.0, 1000.0, 50.0) {
        override fun isEnabled(): Boolean = mode.isSelected(1)
    }

    private var blocked = false
    private var hasCaught = false
    private var timer = TimeUtil()

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (!blocked && !hasCaught && timer.hasReached(timeToWait.value.toLong()))
                    hasCaught = true
            }
        }

        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE && event.packet is ParticleS2CPacket) {
                if (event.packet.parameters.type == ParticleTypes.BUBBLE && mode.isSelected(1)) {
                    if (mc.player?.fishHook != null) {
                        if (mc.player?.fishHook?.velocity?.horizontalLengthSquared()!! == 0.0 && abs(mc.player?.fishHook?.velocity?.y!!) <= 0.1 && mc.player?.fishHook?.squaredDistanceTo(event.packet.x, event.packet.y, event.packet.z)!! <= sensitiveDistance.value * sensitiveDistance.value) {
                            println("AAAA")
                            blocked = false
                            timer.reset()
                        }
                    }
                }
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.useKey) {
                if (
                    !PlayerUtil.isPlayerMoving() &&

                    (mc.player?.mainHandStack?.item is FishingRodItem ||
                            mc.player?.offHandStack?.item is FishingRodItem)
                )
                    if (mc.player?.fishHook == null) {
                        val lastPos = ProjectileUtil.predict(ItemStack(Items.FISHING_ROD), RotationUtil.fakeRotation ?: Rotation(mc.player!!), false).lastOrNull() ?: return@registerEvent
                        if (mc.world?.getBlockState(BlockPos(lastPos))?.fluidState?.isEmpty == false)
                            event.pressed = true
                    } else if (when {
                            mode.isSelected(0) -> mc.player?.fishHook!!.caughtFish
                            mode.isSelected(1) -> hasCaught
                            else -> false
                        } || (mc.player?.fishHook?.isOnGround == true && mc.player?.fishHook?.isTouchingWater == false && mc.player?.fishHook?.age!! > 20 && abs(mc.player?.fishHook?.velocity?.y!!) <= 0.1)) {
                        event.pressed = true
                        hasCaught = false
                        blocked = true
                    }
            }
        }
    }
}
