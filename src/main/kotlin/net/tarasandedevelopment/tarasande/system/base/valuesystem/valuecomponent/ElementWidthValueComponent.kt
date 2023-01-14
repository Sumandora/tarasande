package net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent

import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.util.render.helper.element.ElementWidth

abstract class ElementWidthValueComponent<T : Value>(value: Value) : ElementWidth(0.0) {
    @Suppress("UNCHECKED_CAST")
    val value by lazy { value as T }
}