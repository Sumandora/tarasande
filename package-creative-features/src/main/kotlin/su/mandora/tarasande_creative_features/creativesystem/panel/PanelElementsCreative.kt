package su.mandora.tarasande_creative_features.creativesystem.panel

import su.mandora.tarasande_creative_features.creativesystem.ManagerCreative
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.ManagerValue
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import su.mandora.tarasande.system.screen.panelsystem.api.PanelElements

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
