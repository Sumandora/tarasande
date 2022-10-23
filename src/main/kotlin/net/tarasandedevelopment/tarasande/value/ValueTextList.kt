package net.tarasandedevelopment.tarasande.value

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.base.value.Value

open class ValueTextList(owner: Any, name: String, var value: MutableList<String>, manage: Boolean = true) : Value(owner, name, manage) {
    override fun save(): JsonElement? {
        val array = JsonArray()
        value.forEach {
            array.add(it)
        }
        return array
    }

    override fun load(jsonElement: JsonElement) {
        value.clear()

        jsonElement.asJsonArray.forEach {
            value.add(it.asString)
        }
    }
}
