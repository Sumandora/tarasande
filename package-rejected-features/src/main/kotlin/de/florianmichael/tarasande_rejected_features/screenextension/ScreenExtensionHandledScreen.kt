package de.florianmichael.tarasande_rejected_features.screenextension

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.AbstractFurnaceScreenHandler
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueSpacer
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtension

class ScreenExtensionHandledScreen : ScreenExtension<HandledScreen<*>>(HandledScreen::class.java) {

    override fun createElements(screen: HandledScreen<*>): MutableList<Element> {
        if (screen.screenHandler is AbstractFurnaceScreenHandler) {
            val screenHandler = screen.screenHandler as AbstractFurnaceScreenHandler

            return mutableListOf(ClickableWidgetPanel(object : PanelElements<ElementWidthValueComponent<*>>("Furnace progress", 100.0, 0.0) {
                private fun addText(input: String) = elementList.add(ValueSpacer(this, input, manage = false).createValueComponent())

                override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
                    elementList.clear()
                    x = 3.0
                    y = mc.window.scaledHeight / 2f - panelHeight / 2f

                    if (screenHandler.isBurning) {
                        addText("Item smelting finished in: " + ((screenHandler.propertyDelegate.get(3) - screenHandler.propertyDelegate.get(2)) / 20) + " seconds")
                        addText("Fuel power ends in: " + (screenHandler.propertyDelegate.get(0) / 20) + " seconds")
                    } else
                        addText("Waiting...")

                    super.render(matrices, mouseX, mouseY, delta)
                    this.panelHeight = titleBarHeight + getMaxScrollOffset() + 2.0
                }
            }))
        }
        return mutableListOf()
    }
}
