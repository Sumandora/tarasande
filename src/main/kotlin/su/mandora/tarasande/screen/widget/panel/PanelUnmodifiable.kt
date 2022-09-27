package su.mandora.tarasande.screen.widget.panel

import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.screen.menu.panel.Panel

open class PanelUnmodifiable(name: String, x: Double, y: Double, width: Double, height: Double) : Panel(name, x, y, width, height, width, height) {

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        opened = true
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return false
    }

}