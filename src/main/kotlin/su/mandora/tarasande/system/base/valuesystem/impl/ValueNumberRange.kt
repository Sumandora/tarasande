package su.mandora.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentNumberRange
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max
import kotlin.math.min

open class ValueNumberRange(
    owner: Any,
    name: String,
    val min: Double,
    minValue: Double,
    maxValue: Double,
    val max: Double,
    val increment: Double,
    val exceed: Boolean = true,
    visible: Boolean = true,
    isEnabled: () -> Boolean = { true },
    manage: Boolean = true
) : Value(owner, name, visible, isEnabled, ElementWidthValueComponentNumberRange::class.java, manage) {

    var minValue = minValue
        set(value) {
            val prevValue = field
            field = value
            onMinValueChange(prevValue, value)
        }
    var maxValue = maxValue
        set(value) {
            val prevValue = field
            field = value
            onMaxValueChange(prevValue, value)
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
