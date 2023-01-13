package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentColor
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.withAlpha
import java.awt.Color

open class ValueColor : Value {

    var hue: Double
        get() {
            return if (rainbow) {
                (field + (System.currentTimeMillis() - rainbowStart) % 2500f / 2500F) % 1.0F
            } else {
                field
            }
        }
        set(value) {
            val oldValue = field
            field = value
            onHueChange(oldValue, value)
        }
    var sat: Double
        set(value) {
            val oldValue = field
            field = value
            onSatChange(oldValue, value)
        }
    var bri: Double
        set(value) {
            val oldValue = field
            field = value
            onBriChange(oldValue, value)
        }
    var alpha: Double?
        set(value) {
            val oldValue = field
            field = value
            onAlphaChange(oldValue, value)
        }

    constructor(owner: Any, name: String, hue: Double, sat: Double, bri: Double, alpha: Double? = null, manage: Boolean = true) : super(owner, name, ElementWidthValueComponentColor::class.java, manage) {
        this.sat = sat
        this.bri = bri
        this.alpha = alpha
        this.hue = hue
    }

    var rainbow: Boolean = false
        set(value) {
            field = value
            rainbowStart = System.currentTimeMillis()
        }
    var locked: Boolean = false

    private var rainbowStart = 0L

    fun getColor(): Color {
        val hue =
            if (locked && this != ClientValues.accentColor)
                ClientValues.accentColor.hue
            else
                hue

        val hsb = Color.getHSBColor(hue.toFloat(), sat.toFloat(), bri.toFloat())
        return hsb.withAlpha(if (alpha == null) 255 else (alpha!! * 255).toInt())
    }

    fun onHueChange(oldHue: Double?, newHue: Double) {}
    fun onSatChange(oldSat: Double?, newSat: Double) {}
    fun onBriChange(oldBri: Double?, newBri: Double) {}
    fun onAlphaChange(oldAlpha: Double?, newAlpha: Double?) {}

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        jsonArray.add(hue)
        jsonArray.add(sat)
        jsonArray.add(bri)
        jsonArray.add(rainbow)
        jsonArray.add(locked)
        if (alpha != null) {
            jsonArray.add(alpha)
        }
        return jsonArray
    }

    override fun load(jsonElement: JsonElement) {
        val jsonArray = jsonElement.asJsonArray
        hue = jsonArray[0].asDouble
        sat = jsonArray[1].asDouble
        bri = jsonArray[2].asDouble
        rainbow = jsonArray[3].asBoolean
        locked = jsonArray[4].asBoolean
        if (alpha != null) {
            alpha = jsonArray[5].asDouble
        }
    }
}
