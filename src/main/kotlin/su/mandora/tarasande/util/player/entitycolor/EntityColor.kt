package su.mandora.tarasande.util.player.entitycolor

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventEntityColor
import su.mandora.tarasande.event.EventTeamColor
import su.mandora.tarasande.module.render.ModuleESP
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueColor
import java.awt.Color

class EntityColor(private val moduleESP: ModuleESP) {

    private val modifyTeamColor = ValueBoolean(moduleESP, "Modify team color", true)
    private val selfColor = object : ValueColor(moduleESP, "Self Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = modifyTeamColor.value
    }
    private val friendsColor = object : ValueColor(moduleESP, "Friends Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = modifyTeamColor.value
    }
    private val useTeamColor = object : ValueBoolean(moduleESP, "Use Team Color", true) {
        override fun isEnabled() = modifyTeamColor.value
    }
    private val playerColor = object : ValueColor(moduleESP, "Player Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = modifyTeamColor.value && !useTeamColor.value
    }
    private val animalColor = object : ValueColor(moduleESP, "Animal Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = modifyTeamColor.value && !useTeamColor.value
    }
    private val mobColor = object : ValueColor(moduleESP, "Mob Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = modifyTeamColor.value && !useTeamColor.value
    }

    init {
        TarasandeMain.get().managerEvent.add { event ->
            if (event is EventTeamColor) {
                val c = TarasandeMain.get().managerModule.get(ModuleESP::class.java).entityColor.getColor(event.entity)
                if (c != null)
                    event.teamColor = c.rgb
            }
        }
    }

    fun getColor(entity: Entity): Color? {
        if (!moduleESP.enabled || !modifyTeamColor.value)
            return null
        var color: Color? = null

        if (!useTeamColor.value) {
            if (entity is PlayerEntity) color = playerColor.getColor()

            if (entity is AnimalEntity) color = animalColor.getColor()

            if (entity is Monster) color = mobColor.getColor()
        }

        if (entity == MinecraftClient.getInstance().player) color = selfColor.getColor()
        else if (entity is PlayerEntity && TarasandeMain.get().friends.isFriend(entity.gameProfile)) color = friendsColor.getColor()

        val eventEntityColor = EventEntityColor(entity, color)
        TarasandeMain.get().managerEvent.call(eventEntityColor)
        return eventEntityColor.color
    }

}