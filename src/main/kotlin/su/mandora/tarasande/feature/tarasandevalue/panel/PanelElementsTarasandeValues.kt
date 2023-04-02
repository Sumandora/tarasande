package su.mandora.tarasande.feature.tarasandevalue.panel

import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.valuesystem.ManagerValue
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import su.mandora.tarasande.system.screen.panelsystem.api.PanelElements

class PanelElementsTarasandeValues(tarasandeValues: TarasandeValues) : PanelElements<ElementWidthValueComponent<*>>("$TARASANDE_NAME values", 150.0, 100.0) {
    init {
        elementList.addAll(ManagerValue.getValues(tarasandeValues).mapNotNull { it.createValueComponent() })
    }
}