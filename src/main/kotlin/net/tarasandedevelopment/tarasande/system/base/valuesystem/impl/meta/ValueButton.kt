package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.meta.ElementWidthValueComponentButton

open class ValueButton(
    owner: Any,
    name: String,
    visible: Boolean = true,
    isEnabled: () -> Boolean = { true },
    manage: Boolean = true
) : Value(owner, name, visible, isEnabled, ElementWidthValueComponentButton::class.java, manage) {
    open fun onClick() {}

    override fun save(): JsonElement? = null
    override fun load(jsonElement: JsonElement) {}
}