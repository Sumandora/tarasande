package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.minecraft.registry.Registry
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.impl.ElementWidthValueComponentFocusableRegistry
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import java.util.concurrent.CopyOnWriteArrayList

abstract class ValueRegistry<T>(
    owner: Any,
    name: String,
    private val registry: Registry<T>,
    private val multiSelection: Boolean,
    vararg keys: T,
    visible: Boolean = true,
    isEnabled: () -> Boolean = { true },
    manage: Boolean = true
) : Value(owner, name, visible, isEnabled, ElementWidthValueComponentFocusableRegistry::class.java, manage) {

    private val list = CopyOnWriteArrayList<T>()

    init {
        list.addAll(keys)
    }

    fun isSelected(key: Any?) = list.contains(key)

    fun add(wrappedKey: WrappedKey<*>) {
        @Suppress("UNCHECKED_CAST")
        add(wrappedKey.key as T)
    }
    fun add(key: T) {
        if(!multiSelection) {
            list.forEach(::remove)
        }
        list.add(key)
        onAdd(key)

    }
    fun remove(key: Any?) {
        list.remove(key)
        @Suppress("UNCHECKED_CAST")
        onRemove(key as T)
    }

    fun entries(): Array<Any> = list.toArray()
    fun any(predicate: (T) -> Boolean): Boolean {
        if (list.isEmpty()) return false
        for (element in list)
            if (predicate(element))
                return true
        return false
    }

    fun anySelected() = list.isNotEmpty()
    fun randomOrNull() = list.randomOrNull()

    fun getSelected(): T =
        if (!multiSelection)
            list.first()
        else
            throw UnsupportedOperationException()

    abstract fun getTranslationKey(key: Any?): String

    open fun filter(key: T) = true
    open fun onAdd(key: T) {}
    open fun onRemove(key: T) {}

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

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        list.forEach { jsonArray.add(getTranslationKey(it)) }
        return jsonArray
    }

    override fun load(jsonElement: JsonElement) {
        val jsonArray = jsonElement.asJsonArray
        list.clear()
        jsonArray.mapNotNull { e -> registry.first { getTranslationKey(it) == e.asString } }.forEach {
            add(it)
        }
    }

    class WrappedKey<T>(val key: T, val string: String)
}