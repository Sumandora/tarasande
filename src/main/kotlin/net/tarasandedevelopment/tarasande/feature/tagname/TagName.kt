package net.tarasandedevelopment.tarasande.feature.tagname

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.events.impl.EventTagName
import net.tarasandedevelopment.events.impl.EventTick

class TagName {

    private val hashMap = HashMap<Entity, Text>()

    init {
        TarasandeMain.get().eventSystem.add(EventTick::class.java) {
            if (it.state != EventTick.State.PRE) return@add
            if (MinecraftClient.getInstance().world == null) {
                hashMap.clear()
                return@add
            }
            hashMap.entries.removeIf { !MinecraftClient.getInstance().world!!.entities.contains(it.key) }
            for (entity in MinecraftClient.getInstance().world!!.entities) {
                val eventTagName = EventTagName(entity, entity.displayName)
                TarasandeMain.get().eventSystem.call(eventTagName)
                hashMap[entity] = eventTagName.displayName
            }
        }
    }

    fun getTagName(entity: Entity): Text? {
        return hashMap[entity]
    }

}