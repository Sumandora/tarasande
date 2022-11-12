package net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.impl.ElementValueComponentColor
import net.tarasandedevelopment.tarasande.util.extension.withAlpha
import java.awt.Color

open class ValueColor(owner: Any, name: String, hue: Float, var sat: Float, var bri: Float, var alpha: Float? = null, manage: Boolean = true) : Value(owner, name, ElementValueComponentColor::class.java, manage) {
    var hue: Float = hue
        get() {
            return if (rainbow) {
                (field + (System.currentTimeMillis() - rainbowStart) % 2500f / 2500f) % 1.0f
            } else {
                field
            }
        }
    var rainbow: Boolean = false
        set(value) {
            field = value
            rainbowStart = System.currentTimeMillis()
        }
    var locked: Boolean = false

    private var rainbowStart = 0L

    fun getColor(): Color {
        var customHue = this.hue

        if (locked && this != TarasandeMain.get().clientValues.accentColor)
            customHue = TarasandeMain.get().clientValues.accentColor.hue

        val hsb = Color.getHSBColor(customHue, sat, bri)
        return hsb.withAlpha(if (alpha == null) 255 else (alpha!! * 255).toInt())
    }

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
        hue = jsonArray[0].asFloat
        sat = jsonArray[1].asFloat
        bri = jsonArray[2].asFloat
        rainbow = jsonArray[3].asBoolean
        locked = jsonArray[4].asBoolean
        if (alpha != null) {
            alpha = jsonArray[5].asFloat
        }
    }
}
