package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.minecraft.registry.Registry
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.impl.ElementWidthValueComponentFocusableRegistry
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import java.util.concurrent.CopyOnWriteArrayList

abstract class ValueRegistry<T>(owner: Any, name: String, private val registry: Registry<T>, vararg keys: T, manage: Boolean = true) : Value(owner, name, ElementWidthValueComponentFocusableRegistry::class.java, manage) {

    var list = CopyOnWriteArrayList<T>()

    init {
        list.addAll(keys)
    }

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        list.forEach { jsonArray.add(getTranslationKey(it)) }
        return jsonArray
    }

    override fun load(jsonElement: JsonElement) {
        val jsonArray = jsonElement.asJsonArray
        list.clear()
        list.addAll(registry.filter { key -> jsonArray.any { it.asString.equals(getTranslationKey(key)) } })
    }

    open fun filter(key: T) = true
    abstract fun getTranslationKey(key: Any?): String

    fun updateSearchResults(text: String, max: Int): ArrayList<WrappedKey<T>> {
        var count = 0
        val list = ArrayList<WrappedKey<T>>()
        for (key in registry) {
            if (count >= max) break
            val translation = StringUtil.uncoverTranslation(getTranslationKey(key))
            if (filter(key) && translation.contains(text, true) && this.list.none { it == key }) {
                list.add(WrappedKey(key, translation))
                count++
            }
        }
        return list
    }

    fun add(wrappedKey: WrappedKey<*>) {
        @Suppress("UNCHECKED_CAST")
        list.add(wrappedKey.key as T)
    }

    class WrappedKey<T>(val key: T, val string: String)

}