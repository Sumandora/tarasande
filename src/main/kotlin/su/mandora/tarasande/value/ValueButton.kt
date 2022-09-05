package su.mandora.tarasande.value

import com.google.gson.JsonElement
import su.mandora.tarasande.base.value.Value

open class ValueButton(owner: Any, name: String, manage: Boolean = true) : Value(owner, name, manage) {
    override fun save(): JsonElement? = null
    override fun load(jsonElement: JsonElement) {}
}