package net.tarasandedevelopment.tarasande.feature.clientvalue.panel

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.PanelElements

class PanelElementsClientValues(clientValues: ClientValues) : PanelElements<ElementValueComponent>("Client values", 150.0, 100.0) {
    init {
        for (it in TarasandeMain.managerValue().getValues(clientValues)) {
            elementList.add(it.createValueComponent())
        }
    }
}