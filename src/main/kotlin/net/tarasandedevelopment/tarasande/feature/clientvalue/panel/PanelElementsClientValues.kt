package net.tarasandedevelopment.tarasande.feature.clientvalue.panel

import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements

class PanelElementsClientValues(clientValues: ClientValues) : PanelElements<ElementWidthValueComponent>("Client values", 150.0, 100.0) {
    init {
        for (it in ManagerValue.getValues(clientValues)) {
            elementList.add(it.createValueComponent())
        }
    }
}