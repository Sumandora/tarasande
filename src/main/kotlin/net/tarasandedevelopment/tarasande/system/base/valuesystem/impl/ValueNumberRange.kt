package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentNumberRange
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max
import kotlin.math.min

open class ValueNumberRange : Value {

    val min: Double
    var minValue: Double
        set(value) {
            val prevValue = field
            field = value
            onMinValueChange(prevValue, value)
        }
    var maxValue: Double
        set(value) {
            val prevValue = field
            field = value
            onMaxValueChange(prevValue, value)
        }
    val max: Double
    val increment: Double
    val exceed: Boolean

    constructor(owner: Any, name: String, min: Double, minValue: Double, maxValue: Double, max: Double, increment: Double, exceed: Boolean = true, manage: Boolean = true) : super(owner, name, ElementWidthValueComponentNumberRange::class.java, manage) {
        this.min = min
        this.minValue = minValue
        this.maxValue = maxValue
        this.max = max
        this.increment = increment
        this.exceed = exceed
    }

    fun randomNumber(): Double {
        return if (minValue == maxValue) minValue else ThreadLocalRandom.current().nextDouble(min(minValue, maxValue), max(minValue, maxValue))
    }

    fun interpolate(t: Double): Double {
        return minValue + (maxValue - minValue) * t
    }

    open fun onMinValueChange(oldMinValue: Double?, newMinValue: Double) {}
    open fun onMaxValueChange(oldMaxValue: Double?, newMaxValue: Double) {}

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        jsonArray.add(minValue)
        jsonArray.add(maxValue)
        return jsonArray
    }

    override fun load(jsonElement: JsonElement) {
        val jsonArray: JsonArray = jsonElement.asJsonArray
        minValue = jsonArray.get(0).asDouble
        maxValue = jsonArray.get(1).asDouble
    }

}
