package su.mandora.tarasande.feature.entitycolor

import net.minecraft.entity.Entity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventEntityColor
import su.mandora.tarasande.feature.friend.Friends
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import java.awt.Color

class EntityColor {

    private val modifyTeamColor = ValueBoolean(this, "Modify team color", true)
    private val selfColor = ValueColor(this, "Self Color", 0.0, 1.0, 1.0, 1.0, isEnabled = { modifyTeamColor.value })
    private val friendsColor = ValueColor(this, "Friends Color", 0.0, 1.0, 1.0, 1.0, isEnabled = { modifyTeamColor.value })
    private val useTeamColor = ValueBoolean(this, "Use Team Color", true, isEnabled = { modifyTeamColor.value })
    private val playerColor = ValueColor(this, "Player Color", 0.0, 1.0, 1.0, 1.0, isEnabled = { useTeamColor.isEnabled() && !useTeamColor.value })
    private val animalColor = ValueColor(this, "Animal Color", 0.0, 1.0, 1.0, 1.0, isEnabled = { useTeamColor.isEnabled() && !useTeamColor.value })
    private val mobColor = ValueColor(this, "Mob Color", 0.0, 1.0, 1.0, 1.0, isEnabled = { useTeamColor.isEnabled() && !useTeamColor.value })

    fun getColor(entity: Entity): Color? {
        if (!modifyTeamColor.value)
            return null
        var color: Color? = null

        if (!useTeamColor.value) {
            if (entity is PlayerEntity) color = playerColor.getColor()

            if (entity is AnimalEntity) color = animalColor.getColor()

            if (entity is Monster) color = mobColor.getColor()
        }

        if (entity == mc.player) color = selfColor.getColor()
        else if (entity is PlayerEntity && Friends.isFriend(entity.gameProfile)) color = friendsColor.getColor()

        val eventEntityColor = EventEntityColor(entity, color)
        EventDispatcher.call(eventEntityColor)
        return eventEntityColor.color
    }

}