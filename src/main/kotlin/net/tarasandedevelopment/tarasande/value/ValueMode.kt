package net.tarasandedevelopment.tarasande.value

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.value.Value

open class ValueMode(owner: Any, name: String, private var multiSelection: Boolean, vararg val settings: String, manage: Boolean = true) : Value(owner, name, manage) {
    var selected = ArrayList<String>()

    init {
        if (!multiSelection) selected.add(settings[0])
    }

    fun select(index: Int) {
        if (!multiSelection) {
            selected.clear()
            selected.add(settings[index])
        } else {
            if (selected.contains(settings[index])) {
                selected.remove(settings[index])
            } else {
                selected.add(settings[index])
            }
        }
    }

    fun isSelected(index: Int): Boolean {
        return selected.contains(settings[index])
    }

    fun anySelected() = selected.isNotEmpty()

    override fun save(): JsonElement? {
        return TarasandeMain.get().gson.toJsonTree(selected)
    }

    override fun load(jsonElement: JsonElement) {
        selected.clear()
        selected.addAll(TarasandeMain.get().gson.fromJson(jsonElement, Array<String>::class.java)!!)
    }
}
