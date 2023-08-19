package su.mandora.tarasande.system.screen.panelsystem.impl.button

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.panelsystem.Panel
import su.mandora.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper

class PanelButton(x: Int, y: Int, val width: Int, val height: Int, private val text: String, private val pressAction: (button: Int) -> Unit) : Panel("Button", width.toDouble(), height.toDouble(), scissor = true) {

    init {
        this.x = x.toDouble()
        this.y = y.toDouble()
        panelWidth = width.toDouble()
        panelHeight = height.toDouble()
    }

    companion object {
        fun createButtonWidget(x: Int, y: Int, width: Int, height: Int, text: String, pressAction: (button: Int) -> Unit): ClickableWidgetPanel {
            return ClickableWidgetPanel(PanelButton(x, y, width, height, text, pressAction), true)
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        panelWidth = width.toDouble()
        panelHeight = height.toDouble()
        super.render(context, mouseX, mouseY, delta)
    }

    override fun renderContent(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val middleX = x + panelWidth / 2.0
        val middleY = y + titleBarHeight + (panelHeight - titleBarHeight) / 2.0

        FontWrapper.textShadow(context,
            text,
            middleX.toFloat(),
            (middleY - FontWrapper.fontHeight() * 0.25F).toFloat(),
            -1,
            centered = true,
            scale = 0.75F)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return if (RenderUtil.isHovered(mouseX, mouseY, x, y + titleBarHeight, x + panelWidth, y + panelHeight)) {
            mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1F))
            pressAction(button)
            true
        } else
            false
    }
}