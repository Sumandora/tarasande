package net.tarasandedevelopment.tarasande.module.render

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.AbstractFurnaceScreenHandler
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.event.EventChildren
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.PanelElements
import net.tarasandedevelopment.tarasande.screen.widget.panel.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.value.ValueSpacer

class ModuleFurnaceProgress : Module("Furnace progress", "Indicates the progress in the furnace", ModuleCategory.RENDER) {

    init {
        registerEvent(EventChildren::class.java) { event ->
            if (event.screen !is HandledScreen<*>) return@registerEvent
            if (event.screen.screenHandler !is AbstractFurnaceScreenHandler) return@registerEvent

            val screenHandler = event.screen.screenHandler as AbstractFurnaceScreenHandler

            event.add(ClickableWidgetPanel(object : PanelElements<ElementValueComponent>("Furnace progress", 5.0, 0.0, 100.0, 0.0) {
                private fun addText(input: String) = elementList.add(TarasandeMain.get().screenCheatMenu.managerValueComponent.newInstance(ValueSpacer(this, input)))

                override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
                    elementList.clear()
                    y = MinecraftClient.getInstance().window.scaledHeight / 2f - panelHeight / 2f

                    if (screenHandler.isBurning) {
                        val progress = 23 /* max */ - screenHandler.cookProgress

                        addText("Item smelting finished in: " + (progress / 2 + 1) + " seconds")
                        addText("Fuel power ends in: " + (screenHandler.fuelProgress + 1) + " seconds")
                    } else
                        addText("Waiting...")
                    this.panelHeight = titleBarHeight + 2.0 + (MinecraftClient.getInstance().textRenderer.fontHeight / 2.0 + 2.0) * (if (screenHandler.isBurning) 2 else 1)

                    super.render(matrices, mouseX, mouseY, delta)
                }
            }))
        }
    }
}
