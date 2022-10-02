package net.tarasandedevelopment.tarasande.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.tarasandedevelopment.tarasande.base.value.Value

open class ValueText(owner: Any, name: String, var value: String, manage: Boolean = true) : Value(owner, name, manage) {
    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun load(jsonElement: JsonElement) {
        value = jsonElement.asString
    }
}