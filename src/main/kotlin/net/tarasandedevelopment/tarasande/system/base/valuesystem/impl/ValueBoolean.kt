package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentBoolean

open class ValueBoolean : Value {

    var value: Boolean
        set(value) {
            val prevValue = field
            field = value
            onChange(prevValue, value)
        }

    constructor(owner: Any, name: String, value: Boolean, manage: Boolean = true) : super(owner, name, ElementWidthValueComponentBoolean::class.java, manage) {
        this.value = value
    }

    open fun onChange(oldValue: Boolean?, newValue: Boolean) {}

    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun load(jsonElement: JsonElement) {
        value = jsonElement.asBoolean
    }
}