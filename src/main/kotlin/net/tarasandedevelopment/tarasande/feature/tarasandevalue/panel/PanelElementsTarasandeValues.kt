package net.tarasandedevelopment.tarasande.feature.tarasandevalue.panel

import net.tarasandedevelopment.tarasande.TARASANDE_NAME
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.TarasandeValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements

class PanelElementsTarasandeValues(tarasandeValues: TarasandeValues) : PanelElements<ElementWidthValueComponent<*>>("$TARASANDE_NAME values", 150.0, 100.0) {
    init {
        elementList.addAll(ManagerValue.getValues(tarasandeValues).mapNotNull { it.createValueComponent() })
    }
}