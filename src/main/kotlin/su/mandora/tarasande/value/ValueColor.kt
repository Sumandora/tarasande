package su.mandora.tarasande.value

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.value.Value
import java.awt.Color

open class ValueColor(owner: Any, name: String, hue: Float, var sat: Float, var bri: Float, var alpha: Float? = null, manage: Boolean = true) : Value(owner, name, manage) {
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
        return Color(hsb.red, hsb.green, hsb.blue, if (alpha == null) 255 else (alpha!! * 255).toInt())
    }

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        jsonArray.add(hue)
        jsonArray.add(sat)
        jsonArray.add(bri)
        if (alpha != null) {
            jsonArray.add(alpha)
        }
        jsonArray.add(rainbow)
        jsonArray.add(locked)
        return jsonArray
    }

    override fun load(jsonElement: JsonElement) {
        val jsonArray = jsonElement.asJsonArray
        hue = jsonArray[0].asFloat
        sat = jsonArray[1].asFloat
        bri = jsonArray[2].asFloat
        if (alpha != null) {
            alpha = jsonArray[3].asFloat
            rainbow = jsonArray[4].asBoolean
            locked = jsonArray[5].asBoolean
        } else {
            rainbow = jsonArray[3].asBoolean
            locked = jsonArray[4].asBoolean
        }
    }
}
