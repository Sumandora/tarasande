package net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentMode

open class ValueMode(owner: Any, name: String, private var multiSelection: Boolean, vararg val values: String, manage: Boolean = true) : Value(owner, name, ElementWidthValueComponentMode::class.java, manage) {
    var selected = ArrayList<String>()

    init {
        if (!multiSelection) selected.add(values[0])
    }

    fun select(index: Int) {
        if (!multiSelection) {
            selected.clear()
            selected.add(values[index])
        } else {
            if (selected.contains(values[index])) {
                selected.remove(values[index])
            } else {
                selected.add(values[index])
            }
        }
    }

    fun isSelected(index: Int) = selected.contains(values[index])

    fun anySelected() = selected.isNotEmpty()

    override fun save(): JsonElement? {
        return TarasandeMain.get().gson.toJsonTree(selected)
    }

    override fun load(jsonElement: JsonElement) {
        selected.clear()
        selected.addAll(TarasandeMain.get().gson.fromJson(jsonElement, Array<String>::class.java)!!)
    }
}
