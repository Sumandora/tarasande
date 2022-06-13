package su.mandora.tarasande.value

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.minecraft.util.registry.Registry
import su.mandora.tarasande.base.value.Value
import java.util.concurrent.CopyOnWriteArrayList

abstract class ValueRegistry<T>(owner: Any, name: String, val registry: Registry<T>, vararg keys: T) : Value(owner, name) {

    var list = CopyOnWriteArrayList<T>()

    init {
        list.addAll(keys)
    }

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        list.forEach { jsonArray.add(keyToString(it)) }
        return jsonArray
    }

    override fun load(jsonElement: JsonElement) {
        val jsonArray = jsonElement.asJsonArray
        list.clear()
        list.addAll(registry.entrySet.filter { jsonArray.contains(JsonPrimitive(keyToString(it))) }.map { it.value })
        jsonArray.forEach { list.add(registry.get(it.asInt)) }
    }

    open fun filter(key: T) = true
    abstract fun keyToString(key: Any?): String

    fun updateSearchResults(text: String, max: Int): ArrayList<WrappedKey<T>> {
        var count = 0
        val list = ArrayList<WrappedKey<T>>()
        for (key in registry) {
            if (count >= max)
                break
            if (filter(key) && keyToString(key).contains(text, true) && this.list.none { it == key }) {
                list.add(WrappedKey(key, keyToString(key)))
                count++
            }
        }
        return list
    }

    fun add(wrappedKey: WrappedKey<*>) {
        list.add(wrappedKey.key as T)
    }

}

class WrappedKey<T>(val key: T, val string: String)