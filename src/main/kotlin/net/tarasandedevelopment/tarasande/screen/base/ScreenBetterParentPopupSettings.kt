package net.tarasandedevelopment.tarasande.screen.base

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.PanelElements
import net.tarasandedevelopment.tarasande.screen.widget.panel.ClickableWidgetPanel
import java.util.*

class ScreenBetterParentPopupSettings(parent: Screen, val titleName: String, val owner: Any) : ScreenBetter(parent) {

    private var clickableWidgetPanel: ClickableWidgetPanel? = null

    init {
        if (parent is ScreenCheatMenu)
            parent.disableAnimation = true
    }

    override fun init() {
        super.init()
        if (MinecraftClient.getInstance().world == null && TarasandeMain.get().clientValues.clientMenuBackButtons.value) {
            this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.of("<-")) {
                close()
            })
        }

        this.addDrawableChild(ClickableWidgetPanel(object : PanelElements<ElementValueComponent>(this.titleName, 0.0, 0.0, 0.0, 0.0) {

            override fun init() {
                for (it in TarasandeMain.get().valueSystem.getValues(owner))
                    elementList.add(TarasandeMain.get().screenCheatMenu.managerValueComponent.newInstance(it)!!)
                super.init()

                this.panelWidth = 300.0
                this.panelHeight = getMaxScrollOffset() + titleBarHeight + 5 /* this is the padding for letting you scroll down a bit more than possible */
                val max = MinecraftClient.getInstance().window.scaledHeight
                if (this.panelHeight >= max)
                    this.panelHeight = max.toDouble()

                this.x = (MinecraftClient.getInstance().window.scaledWidth / 2) - 150.0
                this.y = MinecraftClient.getInstance().window.scaledHeight / 2 - (this.panelHeight / 2)
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
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
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

    override fun hoveredElement(mouseX: Double, mouseY: Double): Optional<Element> {
        return Optional.of(clickableWidgetPanel ?: return Optional.empty())
    }

    override fun getFocused(): Element? {
        return clickableWidgetPanel
    }

}
