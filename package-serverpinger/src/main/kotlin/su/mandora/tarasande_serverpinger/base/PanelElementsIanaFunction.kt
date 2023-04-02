package su.mandora.tarasande_serverpinger.base

import su.mandora.tarasande_serverpinger.util.IPAPI
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.meta.ValueSpacer
import su.mandora.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import su.mandora.tarasande.system.screen.panelsystem.api.PanelElements
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import kotlin.math.max

class PanelElementsIanaFunction : PanelElements<ElementWidthValueComponent<*>>("Iana function", 200.0, 20.0) {
    private val autoRequest = object : ValueBoolean(this, "Auto request", true) {
        override fun onChange(oldValue: Boolean?, newValue: Boolean) {
            elementList.clear()
        }
    }
    private var address = ""

    fun update(address: String) {
        this.address = address
        if (autoRequest.value) {
            update()
        }
    }

    private fun update() {
        IPAPI.request(address) { output ->
            elementList.clear()
            output.forEach {
                addText(it)
            }
            var maxWidth = 0F
            for (string in output) {
                val stringWidth = FontWrapper.getWidth(string) * 0.75
                if (stringWidth > maxWidth) maxWidth = stringWidth.toFloat()
            }
            panelWidth = max(maxWidth + 5.0, minWidth)
        }

    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        panelHeight = titleBarHeight + elementList.sumOf { (FontWrapper.fontHeight() * (it.value as ValueSpacer).scale) + 2.0 } + 2.0
        y = mc.currentScreen!!.height - panelHeight - 5.0

        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val hovered = RenderUtil.isHovered(mouseX, mouseY, x, y + titleBarHeight, x + panelWidth, y + panelHeight)
        if (hovered) {
            update()
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun renderContent(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (elementList.isEmpty()) {
            addText("Enable auto requests or left-click the panel")
        }
        super.renderContent(matrices, mouseX, mouseY, delta)
    }

    private fun addText(content: String) = elementList.add(ValueSpacer(this, content, scale = 0.75F, manage = false).createValueComponent())
}
