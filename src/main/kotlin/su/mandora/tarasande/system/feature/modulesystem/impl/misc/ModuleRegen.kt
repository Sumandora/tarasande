package su.mandora.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.math.TimeUtil

class ModuleRegen : Module("Regen", "Increases the rate of regeneration", ModuleCategory.MISC) {

    private val packets = ValueNumber(this, "Packets", 1.0, 5.0, 100.0, 1.0)
    private val delay = ValueNumber(this, "Delay", 0.0, 0.0, 1000.0, 50.0)
    private val preventIllegalPackets = ValueBoolean(this, "Prevent illegal packets", true)

    private val timeUtil = TimeUtil()

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.POST) {
                if (timeUtil.hasReached(delay.value.toLong())) {
                    if (preventIllegalPackets.value) { // This will lead to more flags, but prevents simple protocol checks
                        var onGround = !mc.player?.isOnGround!!
                        repeat(packets.value.toInt()) {
                            mc.networkHandler?.sendPacket(OnGroundOnly(onGround))
                            onGround = !onGround
                        }
                        mc.player?.lastOnGround = onGround
                    } else {
                        repeat(packets.value.toInt()) {
                            mc.networkHandler?.sendPacket(OnGroundOnly(mc.player?.isOnGround!!))
                        }
                    }
                    timeUtil.reset()
                }
            }
        }
    }

}