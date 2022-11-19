package net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentText

open class ValueText(owner: Any, name: String, var value: String, manage: Boolean = true) : Value(owner, name, ElementWidthValueComponentText::class.java, manage) {
    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun load(jsonElement: JsonElement) {
        value = jsonElement.asString
    }
}
