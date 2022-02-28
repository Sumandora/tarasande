package su.mandora.tarasande.value

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry
import su.mandora.tarasande.base.value.Value
import java.util.concurrent.CopyOnWriteArrayList

open class ValueItem(owner: Any, name: String, vararg items: Item) : Value(owner, name) {

    var list = CopyOnWriteArrayList<Item>()

    init {
        list.addAll(items)
    }

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        list.forEach { jsonArray.add(Registry.ITEM.indexOf(it)) }
        return jsonArray
    }

    override fun load(jsonElement: JsonElement) {
        val jsonArray = jsonElement.asJsonArray
        list.clear()
        jsonArray.forEach { list.add(Registry.ITEM.get(it.asInt)) }
    }

    open fun filter(item: Item) = true
}