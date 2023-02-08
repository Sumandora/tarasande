package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.entity.LivingEntity
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.tarasandedevelopment.tarasande.event.EventAttackEntity
import net.tarasandedevelopment.tarasande.event.EventInput
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.minecraft.minus
import net.tarasandedevelopment.tarasande.util.extension.minecraft.prevPos

class ModuleWTap : Module("W-Tap", "Automatically W/S-Taps for you", ModuleCategory.COMBAT) {

    private val mode = ValueMode(this, "Mode", false, "W-Tap", "S-Tap", "Packet")
    private val packets = object : ValueNumber(this, "Packets", 2.0, 2.0, 10.0, 2.0) {
        override fun isEnabled() = mode.isSelected(2)
    }
    private val maximalHurtTime = ValueNumber(this, "Maximal hurt time", 1.0, 5.0, 10.0, 1.0)

    private var changeBinds = false

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.POST) {
                changeBinds = false
            }
        }

        registerEvent(EventAttackEntity::class.java) { event ->
            if (event.state != EventAttackEntity.State.PRE)
                return@registerEvent
            if (event.entity !is LivingEntity || event.entity.hurtTime >= maximalHurtTime.value)
                return@registerEvent
            if (mode.isSelected(2)) {
                if (mc.player?.isSprinting!!) mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING))

                repeat((packets.value - 2.0).toInt()) {
                    if (it % 2 == 0) mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING))
                    else mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING))
                }

                if (mc.player?.isSprinting!!) mc.networkHandler?.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING))
            } else {
                val prevPosition = mc.player?.prevPos()!!
                val currentPosition = mc.player?.pos!!

                val entityPosition = event.entity.pos

                if((entityPosition - prevPosition).horizontalLength() > (entityPosition - currentPosition).horizontalLength()) // We are walking towards the entity
                    changeBinds = true
            }
        }

        registerEvent(EventInput::class.java, 10000) { event ->
            if (event.input == mc.player?.input) {
                if (changeBinds) {
                    when {
                        mode.isSelected(0) -> {
                            event.movementForward = 0.0F
                        }

                        mode.isSelected(1) -> {
                            event.movementForward *= -1.0F
                        }
                    }
                }
            }
        }
    }

}