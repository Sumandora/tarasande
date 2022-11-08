package net.tarasandedevelopment.tarasande.screen.base

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.screen.widget.panel.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import java.awt.Color
import java.io.File
import java.util.function.Consumer

class ScreenBetterFileChooser(
    prevScreen: Screen,
    directory: File,
    val consumer: Consumer<File>
) : ScreenBetter(prevScreen) {

    private var clickableWidgetPanel: ClickableWidgetPanel? = null
    private var currentDirectory = directory

    init {
        if (prevScreen is ScreenCheatMenu)
            prevScreen.disableAnimation = true
    }

    override fun init() {
        super.init()
        if (MinecraftClient.getInstance().world == null && TarasandeMain.get().clientValues.clientMenuBackButtons.value) {
            this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.of("<-")) {
                close()
            })
        }

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
                val mouseY = mouseY - scrollOffset

                val hovered = RenderUtil.isHovered(mouseX, mouseY, x, y + titleBarHeight, x + panelWidth, y + panelHeight)
                if (hovered) {
                    var height = titleBarHeight.toDouble()

                    for (file in this.files()) {
                        if (RenderUtil.isHovered(mouseX, mouseY, x, y + height, x + panelWidth, y + height + RenderUtil.font().fontHeight() * 0.5)) {
                            if (file.isFile) {
                                consumer.accept(file)
                            } else {
                                currentDirectory = file
                                cachedFiles = ArrayList()
                                this.createDimensions()
                            }
                            break
                        }
                        height += (RenderUtil.font().fontHeight() * 0.5) + 1
                    }
                }
                return super.mouseClicked(mouseX, mouseY, button)
            }

            override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
                RenderUtil.font().text(matrices, "Current path: " + currentDirectory.absolutePath, 1F, 1F)

                super.render(matrices, mouseX, mouseY, delta)
            }

            override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
                var height = titleBarHeight.toDouble() + 1
                for (file in this.files()) {
                    if ( ((y + height + scrollOffset)) > y && (height + scrollOffset) < panelHeight) {
                        var color = Color.white
                        if (file.isDirectory) {
                            color = TarasandeMain.get().clientValues.accentColor.getColor()
                        }
                        if (file.isHidden) {
                            color = color.darker().darker()
                        }

                        RenderUtil.font().text(matrices, if (file == currentDirectory.parentFile) ".." else file.name, x.toFloat() + 1, (y + height).toFloat(), color.rgb, 0.5F)
                    }
                    height += (RenderUtil.font().fontHeight() * 0.5) + 1
                }
                super.renderContent(matrices, mouseX, mouseY, delta)
            }

            override fun getMaxScrollOffset(): Double {
                return (RenderUtil.font().fontHeight() * 0.5 + 1) * (this.files().size - 1)
            }

            private fun createDimensions() {
                this.panelWidth = this.files().maxOf { RenderUtil.font().getWidth(it.name) * 0.5 + 6.0 }.coerceAtLeast(MinecraftClient.getInstance().window?.scaledWidth!! * 0.4).coerceAtLeast(RenderUtil.font().getWidth(this.title) + 5.0)
                this.panelHeight = titleBarHeight + ((RenderUtil.font().fontHeight() * 0.5 + 1) * (this.files().size)).coerceAtMost(MinecraftClient.getInstance().window.scaledHeight * 0.75)

                this.x = (MinecraftClient.getInstance().window.scaledWidth / 2) - (this.panelWidth / 2)
                this.y = MinecraftClient.getInstance().window.scaledHeight / 2 - (this.panelHeight / 2)
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

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        if (MinecraftClient.getInstance().world != null) {
            var prevScreen = prevScreen
            while (prevScreen is ScreenBetterParentPopupSettings)
                prevScreen = prevScreen.prevScreen
            prevScreen?.render(matrices, -1, -1, delta)
        }

        super.render(matrices, mouseX, mouseY, delta)
    }
}
