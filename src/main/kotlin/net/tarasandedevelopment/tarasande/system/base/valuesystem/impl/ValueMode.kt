package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import net.tarasandedevelopment.tarasande.gson
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentMode

open class ValueMode(owner: Any, name: String, private val multiSelection: Boolean, vararg val values: String, manage: Boolean = true) : Value(owner, name, ElementWidthValueComponentMode::class.java, manage) {
    private val selected = ArrayList<String>()

    init {
        if (!multiSelection) selected.add(values[0])
    }

    fun select(index: Int) {
        val prevSelected = isSelected(index)
        if (!multiSelection) {
            selected.clear()
            selected.add(values[index])
        } else {
            if (prevSelected) {
                this.selected.remove(values[index])
            } else {
                this.selected.add(values[index])
            }
        }
        onChange(index, prevSelected, isSelected(index))
    }

    fun getSelected(): String =
        if(!multiSelection)
            selected.first()
        else
            throw UnsupportedOperationException()
    fun isSelected(index: Int) = selected.contains(values[index])
    fun isSelected(entry: String) = selected.contains(entry)

    fun anySelected() = selected.isNotEmpty()

    open fun onChange(index: Int, oldSelected: Boolean, newSelected: Boolean) {}

    override fun save(): JsonElement? {
        return gson.toJsonTree(selected)
    }

    override fun load(jsonElement: JsonElement) {
        selected.clear()
        try {
            selected.addAll(gson.fromJson(jsonElement, Array<String>::class.java)!!)
        } catch (jsonSyntaxException: JsonSyntaxException) {
            if (!multiSelection)
                select(0)
            throw jsonSyntaxException
        }
    }
}
