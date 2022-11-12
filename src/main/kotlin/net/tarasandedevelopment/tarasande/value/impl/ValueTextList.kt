package net.tarasandedevelopment.tarasande.value.impl

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.screen.cheatmenu.valuecomponent.ElementValueComponentTextList
import net.tarasandedevelopment.tarasande.value.Value

open class ValueTextList(owner: Any, name: String, var value: MutableList<String>, manage: Boolean = true) : Value(owner, name, ElementValueComponentTextList::class.java,  manage) {
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
