package su.mandora.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.impl.ElementWidthValueComponentFocusableTextList

open class ValueTextList(
    owner: Any,
    name: String,
    private val value: ArrayList<String>,
    visible: Boolean = true,
    isEnabled: () -> Boolean = { true },
    manage: Boolean = true
) : Value(owner, name, visible, isEnabled, ElementWidthValueComponentFocusableTextList::class.java, manage) {

    fun add(text: String) {
        value.add(text)
        onAdd(text)
    }

    fun remove(text: String) {
        value.remove(text)
        onRemove(text)
    }

    fun entries(): Array<String> = value.toTypedArray()

    open fun onAdd(text: String) {}
    open fun onRemove(text: String) {}

    override fun save(): JsonElement? {
        val array = JsonArray()
        value.forEach {
            array.add(it)
        }
        return array
    }

    override fun load(jsonElement: JsonElement) {
        value.clear()
        jsonElement.asJsonArray.forEach {
            add(it.asString)
        }
    }
}
