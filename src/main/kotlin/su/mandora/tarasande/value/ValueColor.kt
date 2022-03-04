package su.mandora.tarasande.value

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import su.mandora.tarasande.base.value.Value
import java.awt.Color

open class ValueColor(owner: Any, name: String, hue: Float, var sat: Float, var bri: Float, var alpha: Float = -1.0F) : Value(owner, name) {
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

    private var rainbowStart = 0L

    fun getColor(): Color {
        val hsb = Color.getHSBColor(hue, sat, bri)
        return Color(hsb.red, hsb.green, hsb.blue, if (alpha == -1.0f) 255 else (alpha * 255).toInt())
    }

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        jsonArray.add(hue)
        jsonArray.add(sat)
        jsonArray.add(bri)
        if (alpha != -1.0f) {
            jsonArray.add(alpha)
        }
        jsonArray.add(rainbow)
        return jsonArray
    }

    override fun load(jsonElement: JsonElement) {
        val jsonArray = jsonElement.asJsonArray
        hue = jsonArray[0].asFloat
        sat = jsonArray[1].asFloat
        bri = jsonArray[2].asFloat
        if (alpha != -1.0f) {
            alpha = jsonArray[3].asFloat
            rainbow = jsonArray[4].asBoolean
        } else {
            rainbow = jsonArray[3].asBoolean
        }
    }
}
