package net.tarasandedevelopment.tarasande.value

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.base.value.Value

open class ValueButton(owner: Any, name: String, manage: Boolean = true) : Value(owner, name, manage) {
    override fun save(): JsonElement? = null
    override fun load(jsonElement: JsonElement) {}
}