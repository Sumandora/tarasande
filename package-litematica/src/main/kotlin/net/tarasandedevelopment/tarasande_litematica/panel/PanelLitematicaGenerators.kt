package net.tarasandedevelopment.tarasande_litematica.panel

import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements
import net.tarasandedevelopment.tarasande_litematica.generator.ManagerGenerator

class PanelLitematicaGenerators(generatorSystem: ManagerGenerator) : PanelElements<ElementWidthValueComponent<*>>("Litematica Generators", 150.0, 100.0) {

    init {
        ManagerValue.getValues(generatorSystem).forEach {
            elementList.add(it.createValueComponent())
        }
    }
}
