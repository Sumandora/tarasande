package su.mandora.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.entity.LivingEntity
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import su.mandora.tarasande.event.impl.EventAttackEntity
import su.mandora.tarasande.event.impl.EventInput
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.math.minus
import su.mandora.tarasande.util.extension.minecraft.prevPos
import su.mandora.tarasande.util.extension.minecraft.setMovementForward
import su.mandora.tarasande.util.extension.minecraft.setMovementSideways

class ModuleWTap : Module("W-Tap", "Automatically W/S-Taps for you", ModuleCategory.COMBAT) {

    private val mode = ValueMode(this, "Mode", false, "W-Tap", "S-Tap", "Packet")
    private val packets = ValueNumber(this, "Packets", 2.0, 2.0, 10.0, 2.0, isEnabled = { mode.isSelected(2) })
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
                val prevEntityPosition = event.entity.prevPos()

                val selfColliding = (entityPosition - prevPosition).horizontalLength() > (entityPosition - currentPosition).horizontalLength()
                val otherColliding = (currentPosition - prevEntityPosition).horizontalLength() > (currentPosition - entityPosition).horizontalLength()

                if (selfColliding && otherColliding) // Are the duellists running into each other?
                    changeBinds = true
            }
        }

        registerEvent(EventInput::class.java, 10000) { event ->
            if (event.input == mc.player?.input) {
                if (changeBinds) {
                    when {
                        mode.isSelected(0) -> {
                            event.input.setMovementForward(0F)
                            event.input.setMovementSideways(0F)
                        }

                        mode.isSelected(1) -> {
                            event.input.setMovementForward(event.input.movementForward * -1F)
                            event.input.setMovementSideways(event.input.movementSideways * -1F)
                        }
                    }
                }
            }
        }
    }

}