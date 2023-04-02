package su.mandora.tarasande_creative_features.creativesystem.valuecomponent

import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.system.base.valuesystem.Value
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.impl.meta.ElementWidthValueComponentButton
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande_creative_features.creativesystem.valuecomponent.meta.ValueButtonItem

class ElementWidthValueComponentButtonItem(value: Value) : ElementWidthValueComponentButton(value) {

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        RenderUtil.renderItemStack(matrices, 0, 0, delta, (value as ValueButtonItem).icon)
    }

    override fun getHeight(): Double {
        return super.getHeight() + 2
    }
}
