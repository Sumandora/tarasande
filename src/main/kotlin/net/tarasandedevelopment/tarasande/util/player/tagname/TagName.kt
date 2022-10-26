package net.tarasandedevelopment.tarasande.util.player.tagname

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventTagName
import net.tarasandedevelopment.tarasande.event.EventTick

class TagName {

    private val hashMap = HashMap<Entity, Text>()

    init {
        TarasandeMain.get().managerEvent.add(EventTick::class.java) {
            if (it.state != EventTick.State.PRE) return@add
            if (MinecraftClient.getInstance().world == null) {
                hashMap.clear()
                return@add
            }
            hashMap.entries.removeIf { !MinecraftClient.getInstance().world!!.entities.contains(it.key) }
            for (entity in MinecraftClient.getInstance().world!!.entities) {
                val eventTagName = EventTagName(entity, entity.displayName)
                TarasandeMain.get().managerEvent.call(eventTagName)
                hashMap[entity] = eventTagName.displayName
            }
        }
    }

    fun getTagName(entity: Entity): Text? {
        return hashMap[entity]
    }

}