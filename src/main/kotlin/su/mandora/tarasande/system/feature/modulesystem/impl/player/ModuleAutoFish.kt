package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.item.FishingRodItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.particle.ParticleTypes
import su.mandora.tarasande.event.impl.EventKeyBindingIsPressed
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.BlockPos
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.prediction.projectile.ProjectileUtil
import kotlin.math.abs

class ModuleAutoFish : Module("Auto fish", "Automates fishing", ModuleCategory.PLAYER) {

    private val mode = ValueMode(this, "Mode", false, "Data tracker", "Bubbles")
    private val sensitiveDistance = ValueNumber(this, "Distance", 0.0, 1.0, 3.0, 0.1, isEnabled = { mode.isSelected(1) })
    private val timeToWait = ValueNumber(this, "Time to wait", 0.0, 100.0, 1000.0, 50.0, isEnabled = { mode.isSelected(1) })

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
                            blocked = false
                            timer.reset()
                        }
                    }
                }
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.useKey) {
                if (!PlayerUtil.isPlayerMoving())
                    if (mc.player?.mainHandStack?.item is FishingRodItem ||
                        mc.player?.offHandStack?.item is FishingRodItem)
                        if (mc.player?.fishHook == null) {
                            val lastPos = ProjectileUtil.predict(ItemStack(Items.FISHING_ROD), Rotations.fakeRotation ?: Rotation(mc.player!!), false).lastOrNull() ?: return@registerEvent
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
