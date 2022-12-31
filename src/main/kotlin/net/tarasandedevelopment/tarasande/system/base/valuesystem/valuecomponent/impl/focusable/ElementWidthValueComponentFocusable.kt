package net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable

import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent

abstract class ElementWidthValueComponentFocusable(value: Value) : ElementWidthValueComponent(value) {

    abstract fun isFocused(): Boolean

}