package net.tarasandedevelopment.tarasande.module.combat

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventAttackEntity
import net.tarasandedevelopment.tarasande.mixin.accessor.IVec3d
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber

class ModuleCriticals : Module("Criticals", "Forces critical hits", ModuleCategory.COMBAT) {

    private val mode = ValueMode(this, "Mode", false, "Packet", "Jump", "Off-ground", "Crack")
    private val offset = object : ValueNumber(this, "Offset", 0.0, 0.1, 1.0, 0.01) {
        override fun isEnabled() = mode.isSelected(0)
    }
    private val motion = object : ValueNumber(this, "Motion", 0.0, 0.1, 1.0, 0.01) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val particles = object : ValueMode(this, "Particles", true, "Critical hit", "Enchanted hit") {
        override fun isEnabled() = mode.isSelected(3)
    }

    init {
        registerEvent(EventAttackEntity::class.java) { event ->
            if(mc.player?.isInLava!! || mc.player?.isInSwimmingPose!!)
                return@registerEvent
            when {
                mode.isSelected(0) -> {
                    if(mc.player?.isOnGround!!) {
                        mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(mc.player?.x!!, mc.player?.y!! + offset.value, mc.player?.z!!, false))
                        mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(mc.player?.x!!, mc.player?.y!! + offset.value / 2, mc.player?.z!!, false))
                    } else {
                        mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(mc.player?.x!!, mc.player?.y!! - offset.value, mc.player?.z!!, false))
                    }
                }
                mode.isSelected(1) -> {
                    if(!mc.player?.isOnGround!!)
                        return@registerEvent
                    mc.player?.jump()
                    mc.player?.velocity?.y = mc.player?.velocity?.y!! * motion.value
                }
                mode.isSelected(2) -> {
                    if(!mc.player?.isOnGround!!)
                        return@registerEvent
                    mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.OnGroundOnly(false))
                }
                mode.isSelected(3) -> {
                    if(particles.isSelected(0))
                        mc.player?.addCritParticles(event.entity)
                    if(particles.isSelected(1))
                        mc.player?.addEnchantedHitParticles(event.entity)
                }
            }
        }
    }
}
