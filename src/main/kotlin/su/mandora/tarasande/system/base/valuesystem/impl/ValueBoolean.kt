package su.mandora.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentBoolean

open class ValueBoolean(
    owner: Any,
    name: String,
    value: Boolean,
    visible: Boolean = true,
    isEnabled: () -> Boolean = { true },
    manage: Boolean = true
) : Value(owner, name, visible, isEnabled, ElementWidthValueComponentBoolean::class.java, manage) {

    var value = value
        set(value) {
            val prevValue = field
            field = value
            onChange(prevValue, value)
        }

    open fun onChange(oldValue: Boolean?, newValue: Boolean) {}

    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun load(jsonElement: JsonElement) {
        value = jsonElement.asBoolean
    }
}