package de.florianmichael.tarasande_creative_features.creativesystem.panel

import de.florianmichael.tarasande_creative_features.creativesystem.ManagerCreative
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements

class PanelElementsCreative : PanelElements<ElementWidthValueComponent<*>>("Creative Exploits", 150.0, 100.0) {

    init {
        elementList.addAll(ManagerValue.getValues(ManagerCreative).mapNotNull { it.createValueComponent() })
    }

    override fun renderTitleBar(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        matrices.push()
        matrices.translate(0F, 0F, 200F) // MC Item zLevel sorting
        super.renderTitleBar(matrices, mouseX, mouseY, delta)
        matrices.pop()
    }
}
