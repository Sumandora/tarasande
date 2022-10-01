package net.tarasandedevelopment.tarasande.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.tarasandedevelopment.tarasande.base.value.Value

open class ValueBoolean(owner: Any, name: String, var value: Boolean, manage: Boolean = true) : Value(owner, name, manage) {

    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun load(jsonElement: JsonElement) {
        value = jsonElement.asBoolean
    }
}