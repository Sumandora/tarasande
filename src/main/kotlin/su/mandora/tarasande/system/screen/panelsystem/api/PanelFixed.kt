package su.mandora.tarasande.system.screen.panelsystem.api

import net.minecraft.client.gui.DrawContext
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventRender2D
import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.panelsystem.Panel
import su.mandora.tarasande.system.screen.panelsystem.screen.panelscreen.ScreenPanel
import su.mandora.tarasande.util.extension.minecraft.render.fill
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.render.helper.Alignment

open class PanelFixed(title: String, width: Double, height: Double, background: Boolean = false, resizable: Boolean = true) : Panel(title, width, height, null, null, background, resizable, background) {

    var alignment = Alignment.LEFT

    init {
        EventDispatcher.apply {
            add(EventRender2D::class.java) {
                if (isVisible() && opened)
                    if (mc.currentScreen !is ScreenPanel) {
                        it.context.matrices.push()
                        render(it.context, -1, -1, mc.tickDelta)
                        it.context.matrices.pop()
                    }
            }

            add(EventTick::class.java) {
                if (it.state == EventTick.State.PRE)
                    if (mc.currentScreen !is ScreenPanel)
                        tick()
            }
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val centerX = x + panelWidth / 2
        when {
            centerX < mc.window.scaledWidth * 0.33 -> alignment = Alignment.LEFT
            centerX in mc.window.scaledWidth.let { (it * 0.33)..(it * 0.66) } -> alignment = Alignment.MIDDLE
            centerX > mc.window.scaledWidth * 0.66 -> alignment = Alignment.RIGHT
        }
        blurBackground(context, insideScreen = false)
        super.render(context, mouseX, mouseY, delta)
    }

    override fun renderTitleBar(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        context.fill(x, y, x + panelWidth, y + titleBarHeight, TarasandeValues.accentColor.getColor().rgb)
        when (alignment) {
            Alignment.LEFT -> FontWrapper.textShadow(context, title, x.toFloat() + 1, y.toFloat() + titleBarHeight / 2F - FontWrapper.fontHeight() / 2F, -1)
            Alignment.MIDDLE -> FontWrapper.textShadow(context, title, x.toFloat() + panelWidth.toFloat() / 2F - FontWrapper.getWidth(title).toFloat() / 2F, y.toFloat() + titleBarHeight / 2F - FontWrapper.fontHeight() / 2F, -1)
            Alignment.RIGHT -> FontWrapper.textShadow(context, title, x.toFloat() + panelWidth.toFloat() - FontWrapper.getWidth(title).toFloat(), y.toFloat() + titleBarHeight / 2F - FontWrapper.fontHeight() / 2F, -1)
        }
    }
}