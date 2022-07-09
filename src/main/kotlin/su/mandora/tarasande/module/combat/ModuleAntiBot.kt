package su.mandora.tarasande.module.combat

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.EntityS2CPacket
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventIsEntityAttackable
import su.mandora.tarasande.event.EventPacket
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleAntiBot : Module("Anti bot", "Prevents modules from interacting with bots", ModuleCategory.COMBAT) {
    private val checks = object : ValueMode(this, "Checks", true, "Sound", "Ground") {
        override fun onChange() = onDisable()
    }
    private val soundDistance = object : ValueNumber(this, "Sound distance", 0.0, 1.0, 1.0, 0.1) {
        override fun isEnabled() = checks.isSelected(0)
        override fun onChange() = passedSound.clear()
    }
    private val groundMode = object : ValueMode(this, "Ground mode", false, "On ground", "Off ground") {
        override fun isEnabled() = checks.isSelected(1)
        override fun onChange() = passedGround.clear()
    }

    private val passedSound = ArrayList<PlayerEntity>()
    private val passedGround = ArrayList<PlayerEntity>()

    override fun onDisable() {
        passedSound.clear()
        passedGround.clear()
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPacket -> {
                if (event.type == EventPacket.Type.RECEIVE) {
                    when (event.packet) {
                        is PlayerRespawnS2CPacket -> {
                            onDisable() // prevent memory leak
                        }
                        is PlaySoundS2CPacket -> {
                            for (entity in mc.world?.entities!!) {
                                if (entity is PlayerEntity && entity.pos?.squaredDistanceTo(Vec3d(event.packet.x, event.packet.y, event.packet.z))!! <= soundDistance.value * soundDistance.value) {
                                    passedSound.add(entity)
                                }
                            }
                        }
                        is EntityS2CPacket -> {
                            if (mc.world == null) return@Consumer

                            val entity = event.packet.getEntity(mc.world)
                            if (entity is PlayerEntity) if (groundMode.isSelected(0) && event.packet.isOnGround || groundMode.isSelected(1) && !event.packet.isOnGround) {
                                passedGround.add(entity)
                            }
                        }
                    }
                }
            }
            is EventIsEntityAttackable -> {
                if (event.attackable && event.entity != null) if (isBot(event.entity)) event.attackable = false
            }
        }
    }

    fun isBot(entity: Entity): Boolean {
        if (!enabled) return false
        if (entity is PlayerEntity) {
            if (checks.isSelected(0) && !passedSound.contains(entity)) return true
            if (checks.isSelected(1) && !passedGround.contains(entity)) return true
        }
        return false
    }

}