package net.tarasandedevelopment.tarasande.value.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.tarasandedevelopment.tarasande.value.Value
import net.tarasandedevelopment.tarasande.value.impl.Valuecomponent.impl.ElementValueComponentBoolean

open class ValueBoolean(owner: Any, name: String, var value: Boolean, manage: Boolean = true) : Value(owner, name, ElementValueComponentBoolean::class.java, manage) {

    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun load(jsonElement: JsonElement) {
        value = jsonElement.asBoolean
    }
}