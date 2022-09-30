package su.mandora.tarasande.module.player

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.event.EventMovement
import su.mandora.tarasande.event.EventPacket
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.mixin.accessor.IPlayerMoveC2SPacket
import su.mandora.tarasande.mixin.accessor.IVec3d
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleNoFall : Module("No fall", "Prevents or reduces fall damage", ModuleCategory.PLAYER) {

    private val mode = ValueMode(this, "Mode", false, "Ground spoof", "Re-Ground", "Drag down", "Sneak-jump")
    private val groundSpoofMode = object : ValueMode(this, "Ground spoof mode", false, "Force on-ground", "Force off-ground") {
        override fun isEnabled() = mode.isSelected(0)
    }
    private val fallDistance = object : ValueNumber(this, "Fall distance", 0.0, 3.0, 10.0, 0.1) {
        override fun isEnabled() = !mode.isSelected(2) && !(mode.isSelected(0) && groundSpoofMode.isSelected(1))
    }
    private val motion = object : ValueNumber(this, "Motion", 1.0, 50.0, 50.0, 1.0) {
        override fun isEnabled() = mode.isSelected(2)
    }
    private val resetFallDistance = object : ValueBoolean(this, "Reset fall distance", true) {
        override fun isEnabled() = (mode.isSelected(0) && groundSpoofMode.isSelected(0)) || mode.isSelected(1)
    }

    private var prevFallDistance = 0.0f

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPacket -> {
                if (event.type == EventPacket.Type.SEND && event.packet is PlayerMoveC2SPacket && mode.isSelected(0)) {
                    if (mc.player?.fallDistance!! >= fallDistance.value || groundSpoofMode.isSelected(1)) {
                        (event.packet as IPlayerMoveC2SPacket).tarasande_setOnGround(groundSpoofMode.isSelected(0))
                        if (resetFallDistance.value)
                            mc.player?.fallDistance = 0.0f
                    }
                }
            }

            is EventUpdate -> {
                if (event.state == EventUpdate.State.POST) {
                    if (mode.isSelected(1) && mc.player?.fallDistance!! >= fallDistance.value) {
                        mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.OnGroundOnly(true))
                        if (resetFallDistance.value)
                            mc.player?.fallDistance = 0.0f
                    }
                    prevFallDistance = mc.player?.fallDistance!!
                }
            }

            is EventMovement -> {
                if (event.entity != mc.player)
                    return@Consumer
                if (mode.isSelected(2))
                    (event.velocity as IVec3d).tarasande_setY(-motion.value)
            }

            is EventKeyBindingIsPressed -> {
                if (mode.isSelected(3) && event.keyBinding == mc.options.sneakKey && prevFallDistance > fallDistance.value && mc.options.jumpKey.isPressed) {
                    event.pressed = !MinecraftClient.getInstance().world!!.isSpaceEmpty(mc.player, mc.player?.boundingBox?.offset(0.0, mc.player?.velocity?.y!!, 0.0))
                }
            }
        }
    }
}