package net.tarasandedevelopment.tarasande.value.meta

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.base.value.Value

class ValueSpacer(owner: Any, name: String, manage: Boolean = true) : Value(owner, name, manage) {
    override fun save(): JsonElement? = null
    override fun load(jsonElement: JsonElement) {}
}