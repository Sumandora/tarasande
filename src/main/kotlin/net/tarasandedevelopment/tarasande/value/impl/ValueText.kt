package net.tarasandedevelopment.tarasande.value.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.tarasandedevelopment.tarasande.value.Value
import net.tarasandedevelopment.tarasande.value.impl.Valuecomponent.impl.ElementValueComponentText

open class ValueText(owner: Any, name: String, var value: String, manage: Boolean = true) : Value(owner, name, ElementValueComponentText::class.java, manage) {
    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun load(jsonElement: JsonElement) {
        value = jsonElement.asString
    }
}
