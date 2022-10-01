package su.mandora.tarasande.util.player.tagname

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.text.Text
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventDisplayName
import su.mandora.tarasande.event.EventTagName
import su.mandora.tarasande.event.EventTick

object TagName {

    private val hashMap = HashMap<Entity, Text>()

    init {
        TarasandeMain.get().managerEvent.add { event ->
            when (event) {
                is EventTick -> {
                    if (event.state != EventTick.State.PRE) return@add
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

                is EventDisplayName -> {
                    event.displayName = getTagName(event.entity) ?: return@add
                }
            }
        }
    }

    fun getTagName(entity: Entity): Text? {
        return hashMap[entity]
    }

}