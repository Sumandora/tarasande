package net.tarasandedevelopment.tarasande.screen.menu.panel

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.screen.menu.utils.DragInfo
import net.tarasandedevelopment.tarasande.screen.menu.utils.IElement
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import org.lwjgl.glfw.GLFW
import java.awt.Color
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.round

open class Panel(val title: String, var x: Double, var y: Double, val minWidth: Double, val minHeight: Double, val maxWidth: Double? = null, val maxHeight: Double? = null, private val background: Boolean = true) : IElement {

    private val dragInfo = DragInfo()
    private val resizeInfo = DragInfo()
    var panelWidth = minWidth
    var panelHeight = minHeight

    protected var scrollOffset = 0.0
    private var scrollSpeed = 0.0

    protected var alignment: Alignment = Alignment.LEFT
    internal var opened = false
    internal var modifiable = true

    internal val titleBarHeight = MinecraftClient.getInstance().textRenderer.fontHeight

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        scrollOffset = MathHelper.clamp(scrollOffset + scrollSpeed, min(-(getMaxScrollOffset() - (panelHeight - MinecraftClient.getInstance().textRenderer.fontHeight - 5)), 0.0), 0.0)
        scrollSpeed = MathHelper.clamp(scrollSpeed - scrollSpeed * RenderUtil.deltaTime * 0.01, -100.0, 100.0)

        if (opened) {
            if (background) {
                matrices?.push()
                val accent = TarasandeMain.get().clientValues.accentColor.getColor()
                RenderUtil.fill(matrices, x, y + MinecraftClient.getInstance().textRenderer.fontHeight, x + panelWidth, y + panelHeight, RenderUtil.colorInterpolate(accent, Color(Int.MIN_VALUE).let { Color(it.red, it.green, it.blue, 0) }, 0.3, 0.3, 0.3, 0.7).rgb)
                matrices?.pop()
            }

            matrices?.push()
            matrices?.translate(0.0, scrollOffset, 0.0)
            renderContent(matrices, mouseX, mouseY, delta)
            matrices?.pop()
        }

        renderTitleBar(matrices, mouseX, mouseY, delta)

        if (dragInfo.dragging) {
            x = round(mouseX - dragInfo.xOffset)
            y = round(mouseY - dragInfo.yOffset)
        }

        x = MathHelper.clamp(x, 0.0, MinecraftClient.getInstance().window.scaledWidth.toDouble() - panelWidth)
        y = MathHelper.clamp(y, 0.0, MinecraftClient.getInstance().window.scaledHeight.toDouble() - panelHeight)

        if (resizeInfo.dragging) {
            panelWidth = MathHelper.clamp(mouseX + resizeInfo.xOffset, 0.0, MinecraftClient.getInstance().window.scaledWidth.toDouble()) - x
            panelHeight = MathHelper.clamp(mouseY + resizeInfo.yOffset, 0.0, MinecraftClient.getInstance().window.scaledHeight.toDouble()) - y
        }

        panelWidth = MathHelper.clamp(panelWidth, minWidth, maxWidth ?: MinecraftClient.getInstance().window.scaledWidth.toDouble())
        panelHeight = MathHelper.clamp(panelHeight, minHeight, maxHeight ?: MinecraftClient.getInstance().window.scaledHeight.toDouble())
    }

    open fun renderTitleBar(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        matrices?.push()
        RenderUtil.fill(matrices, x, y, x + panelWidth, y + titleBarHeight, TarasandeMain.get().clientValues.accentColor.getColor().rgb)
        when (alignment) {
            Alignment.LEFT -> MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, title, x.toFloat() + 1, y.toFloat() + titleBarHeight / 2f - MinecraftClient.getInstance().textRenderer.fontHeight / 2f, Color.white.rgb)
            Alignment.MIDDLE -> MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, title, x.toFloat() + panelWidth.toFloat() / 2.0f - MinecraftClient.getInstance().textRenderer.getWidth(title).toFloat() / 2.0F, y.toFloat() + titleBarHeight / 2f - MinecraftClient.getInstance().textRenderer.fontHeight / 2f, Color.white.rgb)
            Alignment.RIGHT -> MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, title, x.toFloat() + panelWidth.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(title).toFloat(), y.toFloat() + titleBarHeight / 2f - MinecraftClient.getInstance().textRenderer.fontHeight / 2f, Color.white.rgb)
        }
        matrices?.pop()
    }

    open fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!modifiable)
            return false
        val mouseX = floor(mouseX)
        val mouseY = floor(mouseY)
        if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + (if (opened) panelHeight else titleBarHeight.toDouble()))) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + titleBarHeight.toDouble())) {
                    dragInfo.setDragInfo(true, mouseX - x, mouseY - y)
                }
                if (RenderUtil.isHovered(mouseX, mouseY, x + panelWidth - 5, y + panelHeight - 5, x + panelWidth + 5, y + panelHeight + 5)) {
                    resizeInfo.setDragInfo(true, mouseX - (x + panelWidth - 2), mouseY - (y + panelHeight - 2))
                }
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + titleBarHeight.toDouble())) opened = !opened
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                if (TarasandeMain.get().managerValue.getValues(this).isNotEmpty()) {
                    MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, "Settings of \"" + this.title + "\"", this))
                }
            }
            return true
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if (button == 0) {
            dragInfo.setDragInfo(false, 0.0, 0.0)
            resizeInfo.setDragInfo(false, 0.0, 0.0)
        }
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + panelHeight)) {
            scrollSpeed += amount * 3
            return true
        }
        return false
    }

    open fun getMaxScrollOffset(): Double = 0.0

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return false
    }

    override fun charTyped(chr: Char, modifiers: Int) {
    }

    override fun tick() {
    }

    override fun onClose() {
        dragInfo.setDragInfo(false, 0.0, 0.0)
        resizeInfo.setDragInfo(false, 0.0, 0.0)
    }

    override fun getHeight() = 0.0 // never used
}

enum class Alignment {
    LEFT, MIDDLE, RIGHT
}
