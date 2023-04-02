package su.mandora.tarasande.system.base.valuesystem.impl.meta

import com.google.gson.JsonElement
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.meta.ElementWidthValueComponentSpacer
import java.awt.Color

open class ValueSpacer(
    owner: Any,
    name: String,
    val scale: Float = 0.5F,
    visible: Boolean = true,
    isEnabled: () -> Boolean = { true },
    manage: Boolean = true
) : Value(owner, name, visible, isEnabled, ElementWidthValueComponentSpacer::class.java, manage) {
    override fun save(): JsonElement? = null
    override fun load(jsonElement: JsonElement) {}

    open fun getColor(hovered: Boolean): Color = if (hovered) TarasandeValues.accentColor.getColor() else Color.white
    open fun onClick(mouseButton: Int) {}
}