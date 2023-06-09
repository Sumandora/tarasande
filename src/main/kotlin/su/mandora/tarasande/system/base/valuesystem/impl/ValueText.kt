package su.mandora.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.impl.ElementWidthValueComponentFocusableText

open class ValueText(
    owner: Any,
    name: String,
    value: String,
    visible: Boolean = true,
    isEnabled: () -> Boolean = { true },
    manage: Boolean = true
) : Value(owner, name, visible, isEnabled, ElementWidthValueComponentFocusableText::class.java, manage) {

    var value = value
        set(value) {
            val oldValue = field
            field = value
            onChange(oldValue, value)
        }

    open fun onChange(oldText: String?, newText: String) {}

    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun load(jsonElement: JsonElement) {
        value = jsonElement.asString
    }
}
