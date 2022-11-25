package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentBoolean

open class ValueBoolean(owner: Any, name: String, var value: Boolean, manage: Boolean = true) : Value(owner, name, ElementWidthValueComponentBoolean::class.java, manage) {

    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun load(jsonElement: JsonElement) {
        value = jsonElement.asBoolean
    }
}