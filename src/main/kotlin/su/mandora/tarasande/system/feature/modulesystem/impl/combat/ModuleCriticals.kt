package su.mandora.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import su.mandora.tarasande.event.impl.EventAttackEntity
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleCriticals : Module("Criticals", "Forces critical hits", ModuleCategory.COMBAT) {

    private val mode = ValueMode(this, "Mode", false, "Packet", "Jump", "Off-ground", "Crack")
    private val ascendOffset = ValueNumber(this, "Ascend offset", 0.0, 0.1, 1.0, 0.01, isEnabled = { mode.isSelected(0) })
    private val descendOffset = ValueNumber(this, "Descend offset", 0.0, 0.1, 1.0, 0.01, isEnabled = { mode.isSelected(0) })
    private val motion = ValueNumber(this, "Motion", 0.0, 0.1, 1.0, 0.01, isEnabled = { mode.isSelected(1) })
    private val particles = ValueMode(this, "Particles", true, "Critical hit", "Enchanted hit", isEnabled = { mode.isSelected(3) })

    init {
        registerEvent(EventAttackEntity::class.java) { event ->
            if (mc.player?.isInLava!! || mc.player?.isInSwimmingPose!!)
                return@registerEvent
            when {
                mode.isSelected(0) -> {
                    if (mc.player?.isOnGround!!) {
                        mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(mc.player?.x!!, mc.player?.y!! + ascendOffset.value, mc.player?.z!!, false))
                        mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(mc.player?.x!!, mc.player?.y!! + ascendOffset.value - descendOffset.value, mc.player?.z!!, false))
                    } else {
                        mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(mc.player?.x!!, mc.player?.y!! - descendOffset.value, mc.player?.z!!, false))
                    }
                }

                mode.isSelected(1) -> {
                    if (!mc.player?.isOnGround!!)
                        return@registerEvent
                    mc.player?.jump()
                    mc.player?.velocity = mc.player?.velocity?.multiply(1.0, motion.value, 1.0)
                }

                mode.isSelected(2) -> {
                    if (!mc.player?.isOnGround!! || !mc.player?.lastOnGround!!)
                        return@registerEvent
                    mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.OnGroundOnly(false))
                }

                mode.isSelected(3) -> {
                    if (particles.isSelected(0))
                        mc.player?.addCritParticles(event.entity)
                    if (particles.isSelected(1))
                        mc.player?.addEnchantedHitParticles(event.entity)
                }
            }
        }
    }
}
