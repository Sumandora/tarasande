package net.tarasandedevelopment.tarasande_rejected_features.module

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.AbstractFurnaceScreenHandler
import net.tarasandedevelopment.tarasande.event.EventChildren
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueSpacer
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.ElementWidthValueComponent
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements
import net.tarasandedevelopment.tarasande.util.extension.mc

class ModuleFurnaceProgress : Module("Furnace progress", "Indicates the progress in the furnace", ModuleCategory.RENDER) {

    init {
        registerEvent(EventChildren::class.java) { event ->
            if (event.screen !is HandledScreen<*>) return@registerEvent
            if ((event.screen as HandledScreen<*>).screenHandler !is AbstractFurnaceScreenHandler) return@registerEvent

            val screenHandler = (event.screen as HandledScreen<*>).screenHandler as AbstractFurnaceScreenHandler

            event.elements.add(ClickableWidgetPanel(object : PanelElements<ElementWidthValueComponent >("Furnace progress", 100.0, 0.0) {
                private fun addText(input: String) = elementList.add(ValueSpacer(this, input, manage = false).createValueComponent())

                override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
                    elementList.clear()
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
    }
}
