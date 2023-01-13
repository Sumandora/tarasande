package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.impl.ElementWidthValueComponentFocusableText

open class ValueText : Value {

    var value: String
        set(value) {
            val oldValue = field
            field = value
            onChange(oldValue, value)
        }

    constructor(owner: Any, name: String, value: String, manage: Boolean = true) : super(owner, name, ElementWidthValueComponentFocusableText::class.java, manage) {
        this.value = value
    }

    open fun onChange(oldText: String?, newText: String) {}

    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun load(jsonElement: JsonElement) {
        value = jsonElement.asString
    }
}
