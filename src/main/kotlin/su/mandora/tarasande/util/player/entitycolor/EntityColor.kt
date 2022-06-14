package su.mandora.tarasande.util.player.entitycolor

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventEntityColor
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueColor
import java.awt.Color

class EntityColor {

    private val selfColor = ValueColor(TarasandeMain.get().clientValues!!, "Self Color", 0.0f, 1.0f, 1.0f, 1.0f)
    private val friendsColor = ValueColor(TarasandeMain.get().clientValues!!, "Friends Color", 0.0f, 1.0f, 1.0f, 1.0f)
    private val useTeamColor = ValueBoolean(TarasandeMain.get().clientValues!!, "Use Team Color", true)
    private val playerColor = object : ValueColor(TarasandeMain.get().clientValues!!, "Player Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = !useTeamColor.value
    }
    private val animalColor = object : ValueColor(TarasandeMain.get().clientValues!!, "Animal Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = !useTeamColor.value
    }
    private val mobColor = object : ValueColor(TarasandeMain.get().clientValues!!, "Mob Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = !useTeamColor.value
    }

    fun getColor(entity: Entity): Color? {
        var color: Color? = null

        if (!useTeamColor.value) {
            if (entity is PlayerEntity)
                color = playerColor.getColor()

            if (entity is AnimalEntity)
                color = animalColor.getColor()

            if (entity is Monster)
                color = mobColor.getColor()
        }

        if (entity == MinecraftClient.getInstance().player)
            color = selfColor.getColor()
        else if(entity is PlayerEntity && TarasandeMain.get().friends?.isFriend(entity.gameProfile) == true)
            color = friendsColor.getColor()

        val eventEntityColor = EventEntityColor(entity, color)
        TarasandeMain.get().managerEvent?.call(eventEntityColor)
        return eventEntityColor.color
    }

}