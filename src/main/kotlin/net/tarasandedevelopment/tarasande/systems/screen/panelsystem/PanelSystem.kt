package net.tarasandedevelopment.tarasande.systems.screen.panelsystem

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventRender2D
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.systems.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.file.FileCheatMenu
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.impl.fixed.*
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.screen.impl.ScreenBetterParentValues
import net.tarasandedevelopment.tarasande.util.extension.withAlpha
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.render.helper.Alignment
import net.tarasandedevelopment.tarasande.util.render.helper.DragInfo
import net.tarasandedevelopment.tarasande.util.render.helper.IElement
import org.lwjgl.glfw.GLFW
import su.mandora.events.EventDispatcher
import java.awt.Color
import kotlin.math.min
import kotlin.math.round

class ManagerPanel(fileSystem: ManagerFile) : Manager<Panel>() {

    val screenCheatMenu: ScreenCheatMenu
    var panelInsertY = 5.0

    init {
        add(
            PanelArmor(),
            PanelEffects(),
            PanelHypixelOverlay(),
            PanelInventory(),
            PanelMousepad(),
            PanelNotifications(),
            PanelRadar(),
            PanelWatermark()
        )

        screenCheatMenu = ScreenCheatMenu(this)
        EventDispatcher.add(EventSuccessfulLoad::class.java, 9999) {
            fileSystem.add(FileCheatMenu(this))
        }
    }

    fun reorderPanels(panel: Panel, index: Int) {
        if(!list.contains(panel))
            return // Closeable panels (this was a feature, which got scrapped)
        list.remove(panel)
        list.add(index, panel)
    }

    override fun insert(obj: Panel, index: Int) {
        obj.x = 5.0
        obj.y = panelInsertY
        panelInsertY += obj.titleBarHeight + 5.0
        super.insert(obj, index)
    }

}

open class Panel(
    val title: String,
    val minWidth: Double,
    val minHeight: Double,
    val maxWidth: Double? = null,
    val maxHeight: Double? = null,
    private val background: Boolean = true,
    private val resizable: Boolean = true,
    internal val fixed: Boolean = false,
    private val scissor: Boolean = false
) : IElement {

    // Fixed panels
    constructor(title: String, width: Double, height: Double, background: Boolean = false) : this(title, width, height, null, null, background, false, true, background)
    // Sidebar panels
    constructor(title: String, width: Double) : this(title, width, 0.0, null, null, true, false, false, true)

    var x = 0.0
    var y = 0.0

    private val dragInfo = DragInfo()
    private val resizeInfo = DragInfo()
    var panelWidth = minWidth
    var panelHeight = minHeight

    protected var scrollOffset = 0.0
    private var scrollSpeed = 0.0

    protected var alignment: Alignment = Alignment.LEFT
    var opened = false
    var modifiable = true

    val titleBarHeight = FontWrapper.fontHeight()

    init {
        if (fixed) {
            EventDispatcher.apply {
                add(EventRender2D::class.java) {
                    if (isVisible() && opened)
                        if (MinecraftClient.getInstance().currentScreen != TarasandeMain.managerPanel().screenCheatMenu) {
                            it.matrices.push()
                            render(it.matrices, -1, -1, MinecraftClient.getInstance().tickDelta)
                            it.matrices.pop()
                        }
                }

                add(EventTick::class.java) {
                    if (it.state == EventTick.State.PRE)
                        if (MinecraftClient.getInstance().currentScreen != TarasandeMain.managerPanel().screenCheatMenu)
                            tick()
                }
            }
        }
    }

    override fun init() {
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (fixed) {
            when {
                x + panelWidth / 2 <= MinecraftClient.getInstance().window.scaledWidth * 0.33 -> alignment = Alignment.LEFT
                x + panelWidth / 2 > MinecraftClient.getInstance().window.scaledWidth * 0.33 && x + panelWidth / 2 < MinecraftClient.getInstance().window.scaledWidth * 0.66 -> alignment = Alignment.MIDDLE
                x + panelWidth / 2 > MinecraftClient.getInstance().window.scaledWidth * 0.66 -> alignment = Alignment.RIGHT
            }
        }

        scrollOffset = MathHelper.clamp(scrollOffset + scrollSpeed, min(-(getMaxScrollOffset() - (panelHeight - titleBarHeight - 5)), 0.0), 0.0)
        scrollSpeed = MathHelper.clamp(scrollSpeed - scrollSpeed * RenderUtil.deltaTime * 0.01, -100.0, 100.0)

        if (opened) {
            if (background) {
                matrices?.push()
                TarasandeMain.managerBlur().bind(true)
                RenderUtil.fill(matrices, x, y, x + panelWidth, y + (if (opened && isVisible()) panelHeight else titleBarHeight).toDouble(), -1)
                MinecraftClient.getInstance().framebuffer.beginWrite(true)

                val accent = TarasandeMain.clientValues().accentColor.getColor()
                RenderUtil.fill(matrices, x, y + FontWrapper.fontHeight(), x + panelWidth, y + panelHeight, RenderUtil.colorInterpolate(accent, Color(Int.MIN_VALUE).withAlpha(0), 0.3, 0.3, 0.3, 0.7).rgb)
                matrices?.pop()
            }

            if (scissor) {
                GlStateManager._enableScissorTest()
                val scaleFactor = MinecraftClient.getInstance().window?.scaleFactor!!.toInt()
                GlStateManager._scissorBox(
                    (x * scaleFactor).toInt(),
                    (MinecraftClient.getInstance()?.window?.height!! - (y + panelHeight) * scaleFactor).toInt(),
                    (panelWidth * scaleFactor).toInt(),
                    (panelHeight * scaleFactor).toInt()
                )
            }

            matrices?.push()
            matrices?.translate(0.0, scrollOffset, 0.0)
            renderContent(matrices, mouseX, mouseY, delta)
            matrices?.pop()

            if (scissor) {
                GlStateManager._disableScissorTest()
            }
        }

        renderTitleBar(matrices, mouseX, mouseY, delta)

        if (dragInfo.dragging) {
            x = round(mouseX - dragInfo.xOffset)
            y = round(mouseY - dragInfo.yOffset)
        }

        x = MathHelper.clamp(x, 0.0, MinecraftClient.getInstance().window.scaledWidth.toDouble() - panelWidth)
        y = MathHelper.clamp(y, 0.0, MinecraftClient.getInstance().window.scaledHeight.toDouble() - if (opened) panelHeight else titleBarHeight.toDouble())

        if (resizeInfo.dragging) {
            panelWidth = MathHelper.clamp(mouseX + resizeInfo.xOffset, 0.0, MinecraftClient.getInstance().window.scaledWidth.toDouble()) - x
            panelHeight = MathHelper.clamp(mouseY + resizeInfo.yOffset, 0.0, MinecraftClient.getInstance().window.scaledHeight.toDouble()) - y
        }

        panelWidth = MathHelper.clamp(panelWidth, minWidth, maxWidth ?: MinecraftClient.getInstance().window.scaledWidth.toDouble())
        panelHeight = MathHelper.clamp(panelHeight, minHeight, maxHeight ?: MinecraftClient.getInstance().window.scaledHeight.toDouble())
    }

    open fun renderTitleBar(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        matrices?.push()
        RenderUtil.fill(matrices, x, y, x + panelWidth, y + titleBarHeight, TarasandeMain.clientValues().accentColor.getColor().rgb)
        when (alignment) {
            Alignment.LEFT -> FontWrapper.textShadow(matrices, title, x.toFloat() + 1, y.toFloat() + titleBarHeight / 2f - FontWrapper.fontHeight() / 2f, -1)
            Alignment.MIDDLE -> FontWrapper.textShadow(matrices, title, x.toFloat() + panelWidth.toFloat() / 2.0f - FontWrapper.getWidth(title).toFloat() / 2.0F, y.toFloat() + titleBarHeight / 2f - FontWrapper.fontHeight() / 2f, -1)
            Alignment.RIGHT -> FontWrapper.textShadow(matrices, title, x.toFloat() + panelWidth.toFloat() - FontWrapper.getWidth(title).toFloat(), y.toFloat() + titleBarHeight / 2f - FontWrapper.fontHeight() / 2f, -1)
        }
        matrices?.pop()
    }

    open fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + (if (opened) panelHeight else titleBarHeight.toDouble()))) {
            if (modifiable && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + titleBarHeight.toDouble())) {
                    dragInfo.setDragInfo(true, mouseX - x, mouseY - y)
                }
                if (resizable && RenderUtil.isHovered(mouseX, mouseY, x + panelWidth - 5, y + panelHeight - 5, x + panelWidth + 5, y + panelHeight + 5)) {
                    resizeInfo.setDragInfo(true, mouseX - (x + panelWidth - 2), mouseY - (y + panelHeight - 2))
                }
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + titleBarHeight.toDouble())) opened = !opened
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                if (TarasandeMain.managerValue().getValues(this).isNotEmpty()) {
                    MinecraftClient.getInstance().setScreen(ScreenBetterParentValues(MinecraftClient.getInstance().currentScreen!!, "Values of \"" + this.title + "\"", this))
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
        if (opened && RenderUtil.isHovered(mouseX, mouseY, x, y + titleBarHeight, x + panelWidth, y + panelHeight)) {
            scrollSpeed += amount * 3
            return true
        }
        return false
    }

    open fun getMaxScrollOffset() = 0.0

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

    open fun isVisible() = true
}