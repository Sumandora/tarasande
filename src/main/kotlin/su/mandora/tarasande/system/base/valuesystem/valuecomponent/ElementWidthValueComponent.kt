package su.mandora.tarasande.system.base.valuesystem.valuecomponent

import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.util.render.helper.element.ElementWidth

// The ValueComponents Constructor is found via getDeclaredConstructor(Value::class.java), we have to keep the value as a parameter here
abstract class ElementWidthValueComponent<T : Value>(value: Value) : ElementWidth(0.0) {
    @Suppress("UNCHECKED_CAST")
    val value by lazy { value as T }
}