package net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.meta

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.impl.meta.ElementValueComponentSpacer

class ValueSpacer(owner: Any, name: String, manage: Boolean = true) : Value(owner, name, ElementValueComponentSpacer::class.java, manage) {
    override fun save(): JsonElement? = null
    override fun load(jsonElement: JsonElement) {}
}