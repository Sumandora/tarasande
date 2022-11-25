package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.meta.ElementWidthValueComponentButton

open class ValueButton(owner: Any, name: String, manage: Boolean = true) : Value(owner, name, ElementWidthValueComponentButton::class.java, manage) {
    override fun save(): JsonElement? = null
    override fun load(jsonElement: JsonElement) {}
}