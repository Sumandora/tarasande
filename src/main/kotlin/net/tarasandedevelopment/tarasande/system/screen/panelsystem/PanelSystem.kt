package net.tarasandedevelopment.tarasande.system.screen.panelsystem

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.event.EventRender2D
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.screen.blursystem.ManagerBlur
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.file.FilePanels
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.fixed.*
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.panelscreen.ScreenPanel
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.withAlpha
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.render.helper.Alignment
import net.tarasandedevelopment.tarasande.util.render.helper.DragInfo
import net.tarasandedevelopment.tarasande.util.render.helper.IElement
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher
import java.awt.Color
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.round

object ManagerPanel : Manager<Panel>() {

    val screenPanel: ScreenPanel
    private var panelInsertY = 5.0

    init {
        add(
            PanelArmor(),
            PanelEffects(),
            PanelInventory(),
            PanelMousepad(),
            PanelRadar(),
            PanelWatermark()
        )

        screenPanel = ScreenPanel(this)
        EventDispatcher.add(EventSuccessfulLoad::class.java, 9999) {
            ManagerFile.add(FilePanels(this))
        }
    }

    fun reorderPanels(panel: Panel, index: Int) {
        if (!list.contains(panel))
            return // Closeable panels (this was a feature, which got scrapped)
        if (index >= list.size)
            return // Panels have been removed?
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
    val fixed: Boolean = false,
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
    var usedInScreen = false

    val titleBarHeight = FontWrapper.fontHeight()

    init {
        if (fixed) {
            EventDispatcher.apply {
                add(EventRender2D::class.java) {
                    if (isVisible() && opened)
                        if (mc.currentScreen != ManagerPanel.screenPanel) {
                            it.matrices.push()
                            render(it.matrices, -1, -1, mc.tickDelta)
                            it.matrices.pop()
                        }
                }

                add(EventTick::class.java) {
                    if (it.state == EventTick.State.PRE)
                        if (mc.currentScreen != ManagerPanel.screenPanel)
                            tick()
                }
            }
        }
    }

    override fun init() {
    }

    private fun align() {
        // TODO Improve this code.. I'm curious how other people implement this
        val horizontalAlignments = ArrayList<Double>()
        val verticalAlignments = ArrayList<Double>()
        val tolerance = 5

        val panels = ManagerPanel.list.filter { it != this }

        val horizontalAlignablePanels = panels.filter { abs((y + effectivePanelHeight() / 2.0) - (it.y + it.effectivePanelHeight() / 2.0)) < (it.effectivePanelHeight() + effectivePanelHeight()) / 2.0 + tolerance }
        val verticalAlignablePanels = panels.filter { abs((x + panelWidth / 2.0) - (it.x + it.panelWidth / 2.0)) < (it.panelWidth + panelWidth) / 2.0 + tolerance }

        run {
            val panel = horizontalAlignablePanels.minByOrNull { abs(it.x + it.panelWidth - x) } ?: return@run
            val alignment = panel.x + panel.panelWidth
            horizontalAlignments.add(alignment)
        }
        run {
            val panel = horizontalAlignablePanels.minByOrNull { abs((it.x - panelWidth) - x) } ?: return@run
            val alignment = panel.x - panelWidth
            horizontalAlignments.add(alignment)
        }
        run {
            val panel = verticalAlignablePanels.minByOrNull { abs(it.y - y) } ?: return@run
            val alignment = panel.y
            verticalAlignments.add(alignment)
        }
        run {
            val panel = verticalAlignablePanels.minByOrNull { abs(it.y + it.effectivePanelHeight() - y) } ?: return@run
            val alignment = panel.y + panel.effectivePanelHeight()
            verticalAlignments.add(alignment)
        }
        run {
            val panel = verticalAlignablePanels.minByOrNull { abs((it.y - effectivePanelHeight()) - y) } ?: return@run
            val alignment = panel.y - effectivePanelHeight()
            verticalAlignments.add(alignment)
        }

        horizontalAlignments.minByOrNull { abs(x - it) }?.also {
            if (abs(x - it) < tolerance) x = it
        }

        verticalAlignments.minByOrNull { abs(y - it) }?.also {
            if (abs(y - it) < tolerance) y = it
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (fixed) {
            when {
                x + panelWidth / 2 <= mc.window.scaledWidth * 0.33 -> alignment = Alignment.LEFT
                x + panelWidth / 2 > mc.window.scaledWidth * 0.33 && x + panelWidth / 2 < mc.window.scaledWidth * 0.66 -> alignment = Alignment.MIDDLE
                x + panelWidth / 2 > mc.window.scaledWidth * 0.66 -> alignment = Alignment.RIGHT
            }
        }

        scrollOffset = MathHelper.clamp(scrollOffset + scrollSpeed, min(-(getMaxScrollOffset() - (panelHeight - titleBarHeight - 5)), 0.0), 0.0)
        scrollSpeed = MathHelper.clamp(scrollSpeed - scrollSpeed * RenderUtil.deltaTime * 0.01, -100.0, 100.0)

        if (opened) {
            if (background) {
                matrices.push()
                val previousFramebuffer = GlStateManager.getBoundFramebuffer()
                ManagerBlur.bind(true, usedInScreen)
                RenderUtil.fill(matrices, x, y, x + panelWidth, y + effectivePanelHeight(), -1)
                GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, previousFramebuffer)

                val accent = ClientValues.accentColor.getColor()
                RenderUtil.fill(matrices, x, y + titleBarHeight, x + panelWidth, y + panelHeight, RenderUtil.colorInterpolate(accent, Color(Int.MIN_VALUE).withAlpha(0), 0.3, 0.3, 0.3, 0.7).rgb)
                matrices.pop()
            }

            if (scissor) {
                GlStateManager._enableScissorTest()
                val scaleFactor = mc.window.scaleFactor.toInt()
                GlStateManager._scissorBox(
                    (x * scaleFactor).toInt(),
                    (mc.window.height - (y + panelHeight) * scaleFactor).toInt(),
                    (panelWidth * scaleFactor).toInt(),
                    (panelHeight * scaleFactor).toInt()
                )
            }

            matrices.push()
            matrices.translate(0.0, scrollOffset, 0.0)
            renderContent(matrices, mouseX, mouseY, delta)
            matrices.pop()

            if (scissor) {
                GlStateManager._disableScissorTest()
            }
        }

        renderTitleBar(matrices, mouseX, mouseY, delta)

        if (dragInfo.dragging) {
            x = round(mouseX - dragInfo.xOffset)
            y = round(mouseY - dragInfo.yOffset)
            if (Screen.hasAltDown())
                align()
        }

        x = MathHelper.clamp(x, 0.0, mc.window.scaledWidth.toDouble() - panelWidth)
        y = MathHelper.clamp(y, 0.0, mc.window.scaledHeight.toDouble() - effectivePanelHeight())

        if (resizeInfo.dragging) {
            panelWidth = MathHelper.clamp(mouseX + resizeInfo.xOffset, 0.0, mc.window.scaledWidth.toDouble()) - x
            panelHeight = MathHelper.clamp(mouseY + resizeInfo.yOffset, 0.0, mc.window.scaledHeight.toDouble()) - y
        }

        panelWidth = MathHelper.clamp(panelWidth, minWidth, maxWidth ?: mc.window.scaledWidth.toDouble())
        panelHeight = MathHelper.clamp(panelHeight, minHeight, maxHeight ?: mc.window.scaledHeight.toDouble())
    }

    open fun renderTitleBar(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        RenderUtil.fill(matrices, x, y, x + panelWidth, y + titleBarHeight, ClientValues.accentColor.getColor().rgb)
        when (alignment) {
            Alignment.LEFT -> FontWrapper.textShadow(matrices, title, x.toFloat() + 1, y.toFloat() + titleBarHeight / 2f - FontWrapper.fontHeight() / 2f, -1)
            Alignment.MIDDLE -> FontWrapper.textShadow(matrices, title, x.toFloat() + panelWidth.toFloat() / 2.0F - FontWrapper.getWidth(title).toFloat() / 2.0F, y.toFloat() + titleBarHeight / 2f - FontWrapper.fontHeight() / 2f, -1)
            Alignment.RIGHT -> FontWrapper.textShadow(matrices, title, x.toFloat() + panelWidth.toFloat() - FontWrapper.getWidth(title).toFloat(), y.toFloat() + titleBarHeight / 2f - FontWrapper.fontHeight() / 2f, -1)
        }
    }

    open fun renderContent(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + effectivePanelHeight())) {
            if (modifiable && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + titleBarHeight.toDouble())) {
                    dragInfo.setDragInfo(true, mouseX - x, mouseY - y)
                }
                if (resizable && RenderUtil.isHovered(mouseX, mouseY, x + panelWidth - 5, y + panelHeight - 5, x + panelWidth + 5, y + panelHeight + 5)) {
                    resizeInfo.setDragInfo(true, mouseX - (x + panelWidth - 2), mouseY - (y + panelHeight - 2))
                }
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                if (RenderUtil.isHovered(mouseX, mouseY, x, y, x + panelWidth, y + titleBarHeight.toDouble()))
                    opened = !opened
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                val valueOwner = getValueOwner()
                if (ManagerValue.getValues(valueOwner).isNotEmpty()) {
                    mc.setScreen(ScreenBetterOwnerValues(this.title, mc.currentScreen!!, valueOwner))
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

    open fun getValueOwner(): Any = this

    fun effectivePanelHeight() = if (opened) panelHeight else titleBarHeight.toDouble()
}