package su.mandora.tarasande.module.combat

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventIsEntityAttackable
import su.mandora.tarasande.event.EventPacket
import java.util.function.Consumer

class ModuleAntiBot : Module("Anti bot", "Prevents modules from interacting with bots", ModuleCategory.COMBAT) {

    private val realPlayers = ArrayList<PlayerEntity>()

    override fun onDisable() {
        realPlayers.clear()
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPacket -> {
                if(event.type == EventPacket.Type.RECEIVE && event.packet is PlaySoundS2CPacket) {
                    for(entity in mc.world?.entities!!) {
                        if(entity is PlayerEntity && entity.pos?.distanceTo(Vec3d(event.packet.x, event.packet.y, event.packet.z))!! <= 1.0) {
                            realPlayers.add(entity)
                        }
                    }
                }
            }
            is EventIsEntityAttackable -> {
                if(event.entity is PlayerEntity)
                    if(!realPlayers.contains(event.entity))
                        event.attackable = false
            }
        }
    }

}