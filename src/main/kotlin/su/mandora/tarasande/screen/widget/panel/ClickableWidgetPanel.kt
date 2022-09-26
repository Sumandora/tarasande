package su.mandora.tarasande.screen.widget.panel

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.util.render.RenderUtil
import java.awt.Color

class ClickableWidgetPanel(val panel: UnmodifiablePanel) : ClickableWidget(panel.x.toInt(), panel.y.toInt(), panel.panelWidth.toInt(), panel.panelWidth.toInt(), Text.of(panel.title)), Element {

    override fun appendNarrations(builder: NarrationMessageBuilder?) {
    }

    init {
        panel.init()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        TarasandeMain.get().blur.bind(true)
        RenderUtil.fill(matrices, panel.x, panel.y, panel.x + panel.panelWidth, panel.y + panel.panelHeight, Color.white.rgb)
        MinecraftClient.getInstance().framebuffer.beginWrite(true)
        panel.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return panel.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        panel.mouseReleased(mouseX, mouseY, button)
        return false
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        return panel.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return panel.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        panel.charTyped(chr, modifiers)
        return false
    }

    fun tick() = panel.tick()

}