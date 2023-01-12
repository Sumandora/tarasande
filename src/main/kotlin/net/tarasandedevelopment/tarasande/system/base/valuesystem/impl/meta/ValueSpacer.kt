package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.meta.ElementWidthValueComponentSpacer
import java.awt.Color

open class ValueSpacer(owner: Any, name: String, val scale: Float = 0.5F, manage: Boolean = true) : Value(owner, name, ElementWidthValueComponentSpacer::class.java, manage) {
    override fun save(): JsonElement? = null
    override fun load(jsonElement: JsonElement) {}

    override fun onChange() {
        onChange(-1)
    }

    open fun getColor(hovered: Boolean): Color = if (hovered) ClientValues.accentColor.getColor() else Color.white
    open fun onChange(mouseButton: Int) {
    }
}