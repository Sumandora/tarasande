package net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentMode

open class ValueMode(owner: Any, name: String, private var multiSelection: Boolean, vararg val settings: String, manage: Boolean = true) : Value(owner, name, ElementWidthValueComponentMode::class.java, manage) {
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

    fun isSelected(index: Int) = selected.contains(settings[index])

    fun anySelected() = selected.isNotEmpty()

    override fun save(): JsonElement? {
        return TarasandeMain.instance.gson.toJsonTree(selected)
    }

    override fun load(jsonElement: JsonElement) {
        selected.clear()
        selected.addAll(TarasandeMain.instance.gson.fromJson(jsonElement, Array<String>::class.java)!!)
    }
}
