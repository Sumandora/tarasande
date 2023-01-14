package net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent

import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.util.render.helper.element.ElementWidth

// The ValueComponents Constructor is found via getDeclaredConstructor(Value::class.java), we have to keep the value as a parameter here
abstract class ElementWidthValueComponent<T : Value>(value: Value) : ElementWidth(0.0) {
    @Suppress("UNCHECKED_CAST")
    val value by lazy { value as T }
}