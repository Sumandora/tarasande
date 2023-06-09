package su.mandora.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentNumber

open class ValueNumber(
    owner: Any,
    name: String,
    val min: Double,
    value: Double,
    val max: Double,
    val increment: Double,
    val exceed: Boolean = true,
    visible: Boolean = true,
    isEnabled: () -> Boolean = { true },
    manage: Boolean = true
) : Value(owner, name, visible, isEnabled, ElementWidthValueComponentNumber::class.java, manage) {

    var value = value
        set(value) {
            val prevValue = field
            field = value
            onChange(prevValue, value)
        }

    open fun onChange(oldValue: Double?, newValue: Double) {}

    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun load(jsonElement: JsonElement) {
        value = jsonElement.asDouble
    }
}
