package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentTextList

open class ValueTextList(owner: Any, name: String, var value: MutableList<String>, manage: Boolean = true) : Value(owner, name, ElementWidthValueComponentTextList::class.java,  manage) {
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
