package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentNumber

open class ValueNumber : Value {

    val min: Double
    var value: Double
        set(value) {
            val prevValue = field
            field = value
            onChange(prevValue, value)
        }
    val max: Double
    val increment: Double
    val exceed: Boolean

    constructor(owner: Any, name: String, min: Double, value: Double, max: Double, increment: Double, exceed: Boolean = true, manage: Boolean = true) : super(owner, name, ElementWidthValueComponentNumber::class.java, manage) {
        this.min = min
        this.value = value
        this.max = max
        this.increment = increment
        this.exceed = exceed
    }

    open fun onChange(oldValue: Double?, newValue: Double) {}

    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun load(jsonElement: JsonElement) {
        value = jsonElement.asDouble
    }
}
