package net.tarasandedevelopment.tarasande.value.impl.meta

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.value.Value
import net.tarasandedevelopment.tarasande.value.impl.Valuecomponent.impl.meta.ElementValueComponentSpacer

class ValueSpacer(owner: Any, name: String, manage: Boolean = true) : Value(owner, name, ElementValueComponentSpacer::class.java, manage) {
    override fun save(): JsonElement? = null
    override fun load(jsonElement: JsonElement) {}
}