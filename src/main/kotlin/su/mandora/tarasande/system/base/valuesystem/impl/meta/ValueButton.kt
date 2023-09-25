package su.mandora.tarasande.system.base.valuesystem.impl.meta

import com.google.gson.JsonElement
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.meta.ElementWidthValueComponentButton

open class ValueButton(
    owner: Any,
    name: String,
    visible: Boolean = true,
    isEnabled: () -> Boolean = { true },
    manage: Boolean = true,
    val onClick: () -> Unit, // Last argument for kotlin lambda syntax
) : Value(owner, name, visible, isEnabled, ElementWidthValueComponentButton::class.java, manage) {
    override fun save(): JsonElement? = null
    override fun load(jsonElement: JsonElement) {}
}
