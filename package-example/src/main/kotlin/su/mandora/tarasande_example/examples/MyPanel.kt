package su.mandora.tarasande_example.examples

import net.minecraft.client.gui.DrawContext
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.panelsystem.Panel

class MyPanel : Panel("My panel", 100.0, 50.0) {
    override fun renderContent(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderContent(context, mouseX, mouseY, delta)
        context.drawText(mc.textRenderer, "Hello, world!", mouseX, mouseY, -1, true)
    }
}