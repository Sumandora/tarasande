package net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.screen.ScreenBetter
import java.awt.Color
import java.io.File
import java.util.function.Consumer

class ScreenBetterFileChooser(
    prevScreen: Screen,
    directory: File,
    val consumer: Consumer<File>
) : ScreenBetter("FileChooser", prevScreen) {

    private var clickableWidgetPanel: ClickableWidgetPanel? = null
    private var currentDirectory = directory

    override fun init() {
        super.init()
        this.addDrawableChild(ClickableWidgetPanel(object : Panel("File Chooser", 0.0, 0.0, 0.0, 0.0, background = true, scissor = true) {

            private var cachedFiles = listOf<File>()

            fun files(): List<File> {
                val files = ArrayList<File?>()
                files.add(currentDirectory.parentFile)
                currentDirectory.listFiles()?.forEach {
                    files.add(it)
                }

                if (files.any { !cachedFiles.contains(it) }) {
                    cachedFiles = files.filterNotNull().sortedBy { !it.isDirectory }
                }
                return cachedFiles
            }

            override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
                @Suppress("NAME_SHADOWING")
                val mouseY = mouseY - scrollOffset

                val hovered = RenderUtil.isHovered(mouseX, mouseY, x, y + titleBarHeight, x + panelWidth, y + panelHeight)
                if (hovered) {
                    var height = titleBarHeight.toDouble()

                    for (file in this.files()) {
                        if (RenderUtil.isHovered(mouseX, mouseY, x, y + height, x + panelWidth, y + height + FontWrapper.fontHeight() * 0.5)) {
                            if (file.isFile) {
                                consumer.accept(file)
                            } else {
                                currentDirectory = file
                                cachedFiles = ArrayList()
                                this.createDimensions()
                            }
                            break
                        }
                        height += (FontWrapper.fontHeight() * 0.5) + 1
                    }
                }
                return super.mouseClicked(mouseX, mouseY, button)
            }

            override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
                FontWrapper.text(matrices, "Current path: " + currentDirectory.absolutePath, 1F, 1F)

                super.render(matrices, mouseX, mouseY, delta)
            }

            override fun renderContent(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
                var height = titleBarHeight.toDouble() + 1
                for (file in this.files()) {
                    if (((y + height + scrollOffset)) > y && (height + scrollOffset) < panelHeight) {
                        var color = Color.white
                        if (file.isDirectory) {
                            color = ClientValues.accentColor.getColor()
                        }
                        if (file.isHidden) {
                            color = color.darker().darker()
                        }

                        FontWrapper.text(matrices, if (file == currentDirectory.parentFile) ".." else file.name, x.toFloat() + 1, (y + height).toFloat(), color.rgb, 0.5F)
                    }
                    height += (FontWrapper.fontHeight() * 0.5) + 1
                }
                super.renderContent(matrices, mouseX, mouseY, delta)
            }

            override fun getMaxScrollOffset(): Double {
                return (FontWrapper.fontHeight() * 0.5 + 1) * (this.files().size - 1)
            }

            private fun createDimensions() {
                this.panelWidth = this.files().maxOf { FontWrapper.getWidth(it.name) * 0.5 + 6.0 }.coerceAtLeast(mc.window.scaledWidth * 0.4).coerceAtLeast(FontWrapper.getWidth(this.title) + 5.0)
                this.panelHeight = titleBarHeight + ((FontWrapper.fontHeight() * 0.5 + 1) * (this.files().size)).coerceAtMost(mc.window.scaledHeight * 0.75)

                this.x = (mc.window.scaledWidth / 2) - (this.panelWidth / 2)
                this.y = mc.window.scaledHeight / 2 - (this.panelHeight / 2)
            }

            override fun init() {
                super.init()

                createDimensions()
            }
        }).also { clickableWidgetPanel = it })
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        this.clickableWidgetPanel?.mouseReleased(mouseX, mouseY, button)
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        this.clickableWidgetPanel?.mouseScrolled(mouseX, mouseY, amount)
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        this.clickableWidgetPanel?.keyPressed(keyCode, scanCode, modifiers)
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        this.clickableWidgetPanel?.charTyped(chr, modifiers)
        return super.charTyped(chr, modifiers)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        if (mc.world != null) {
            var prevScreen = prevScreen
            while (prevScreen is ScreenBetterOwnerValues)
                prevScreen = prevScreen.prevScreen
            prevScreen?.render(matrices, -1, -1, delta)
        }

        super.render(matrices, mouseX, mouseY, delta)
    }
}
