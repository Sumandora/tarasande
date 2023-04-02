package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import su.mandora.tarasande.event.impl.EventKeyBindingIsPressed
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.feature.modulesystem.Module

class ModuleNoFall : Module("No fall", "Prevents or reduces fall damage", ModuleCategory.PLAYER) {

    val mode = ValueMode(this, "Mode", false, "Ground spoof", "Re-Ground", "Sneak-jump")
    val groundSpoofMode = ValueMode(this, "Ground spoof mode", false, "Force on-ground", "Force off-ground", isEnabled = { mode.isSelected(0) })
    private val fallDistance = ValueNumber(this, "Fall distance", 0.0, 3.0, 10.0, 0.1, isEnabled = { !(mode.isSelected(0) && groundSpoofMode.isSelected(1)) })
    private val resetFallDistance = ValueBoolean(this, "Reset fall distance", true, isEnabled = { (mode.isSelected(0) && groundSpoofMode.isSelected(0)) || mode.isSelected(1) })

    private var prevFallDistance = 0.0F

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.SEND && event.packet is PlayerMoveC2SPacket && mode.isSelected(0)) {
                if (mc.player?.fallDistance!! >= fallDistance.value || groundSpoofMode.isSelected(1)) {
                    event.packet.onGround = groundSpoofMode.isSelected(0)
                    if (groundSpoofMode.isSelected(0) && resetFallDistance.value)
                        mc.player?.fallDistance = 0.0F
                }
            }
        }

        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.POST) {
                if (mode.isSelected(1) && mc.player?.fallDistance!! >= fallDistance.value && !mc.player?.lastOnGround!!) {
                    mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.OnGroundOnly(true))
                    if (resetFallDistance.value)
                        mc.player?.fallDistance = 0.0F
                }
                prevFallDistance = mc.player?.fallDistance!!
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (mode.isSelected(2) && event.keyBinding == mc.options.sneakKey && prevFallDistance > fallDistance.value && mc.options.jumpKey.isPressed) {
                event.pressed = !mc.world!!.isSpaceEmpty(mc.player, mc.player?.boundingBox?.offset(0.0, mc.player?.velocity?.y!!, 0.0))
            }
        }
    }
}
