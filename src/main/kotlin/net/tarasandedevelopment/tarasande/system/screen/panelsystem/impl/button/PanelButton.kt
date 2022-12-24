package net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.button

import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper

class PanelButton(x: Int, y: Int, val width: Int, val height: Int, private val text: String, private val pressAction: (button: Int) -> Unit) : Panel("Button", width.toDouble(), height.toDouble(), scissor = true) {

    init {
        this.x = x.toDouble()
        this.y = y.toDouble()
    }

    companion object {
        fun createButton(x: Int, y: Int, width: Int, height: Int, text: String, pressAction: (button: Int) -> Unit): ClickableWidgetPanel {
            return ClickableWidgetPanel(PanelButton(x, y, width, height, text, pressAction), true)
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        panelWidth = width.toDouble()
        panelHeight = height.toDouble()
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun renderContent(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val middleX = x + panelWidth / 2.0
        val middleY = y + titleBarHeight + (panelHeight - titleBarHeight) / 2.0

        FontWrapper.textShadow(matrices,
            text,
            middleX.toFloat(),
            (middleY - FontWrapper.fontHeight() * 0.25F).toFloat(),
            -1,
            centered = true,
            scale = 0.75F)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return if (RenderUtil.isHovered(mouseX, mouseY, x, y + titleBarHeight, x + panelWidth, y + panelHeight)) {
            MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F))
            pressAction(button)
            true
        } else
            false
    }
}