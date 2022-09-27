package su.mandora.tarasande.screen

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.screen.widget.panel.ClickableWidgetPanel
import su.mandora.tarasande.screen.widget.panel.PanelUnmodifiable
import su.mandora.tarasande.util.render.screen.ScreenBetter

class TestScreen(prevScreen: Screen?) : ScreenBetter(prevScreen) {

    override fun init() {
        super.init()
        this.addDrawableChild(ClickableWidgetPanel(object : PanelUnmodifiable("Tarasande ist voll cool", 150.0, 150.0, 500.0, 300.0) {
            override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
                textRenderer.drawWithShadow(matrices, "Hello, World", x.toFloat() + 10.0f, y.toFloat() + titleBarHeight + 10.0f, -1)
            }
        }))
    }

}