package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.Direction
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventMovement
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNoFall : Module("No fall", "Prevents or reduces fall damage", ModuleCategory.PLAYER) {

    val mode = ValueMode(this, "Mode", false, "Ground spoof", "Re-Ground", "Drag down", "Sneak-jump")
    val groundSpoofMode = ValueMode(this, "Ground spoof mode", false, "Force on-ground", "Force off-ground", isEnabled = { mode.isSelected(0) })
    private val fallDistance = ValueNumber(this, "Fall distance", 0.0, 3.0, 10.0, 0.1, isEnabled = { !mode.isSelected(2) && !(mode.isSelected(0) && groundSpoofMode.isSelected(1)) })
    private val motion = ValueNumber(this, "Motion", 1.0, 50.0, 50.0, 1.0, isEnabled = { mode.isSelected(2) })
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

        registerEvent(EventMovement::class.java) { event ->
            if (event.entity != mc.player)
                return@registerEvent

            if (mode.isSelected(2))
                event.velocity = event.velocity.withAxis(Direction.Axis.Y, -motion.value)
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (mode.isSelected(3) && event.keyBinding == mc.options.sneakKey && prevFallDistance > fallDistance.value && mc.options.jumpKey.isPressed) {
                event.pressed = !mc.world!!.isSpaceEmpty(mc.player, mc.player?.boundingBox?.offset(0.0, mc.player?.velocity?.y!!, 0.0))
            }
        }
    }
}
