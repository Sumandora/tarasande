package net.tarasandedevelopment.tarasande.module.misc

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.AbstractFurnaceScreenHandler
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventChildren
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.screen.widget.panel.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import java.util.function.Consumer

class ModuleFurnaceProgress : Module("Furnace progress", "Indicates the progress in the furnace.", ModuleCategory.MISC) {

    val eventConsumer = Consumer<Event> {
        when (it) {
            is EventChildren -> {
                if (it.screen !is HandledScreen<*>) return@Consumer
                if (it.screen.screenHandler !is AbstractFurnaceScreenHandler) return@Consumer

                val screenHandler = it.screen.screenHandler as AbstractFurnaceScreenHandler
                val font = MinecraftClient.getInstance().textRenderer

                it.add(ClickableWidgetPanel(object : Panel("Furnace Progress", 0.0, 0.0, 0.0, 0.0, null, null, true) {
                    override fun init() {
                        super.init()
                        val maxHeight: Double = (2.0 + 1.0) /* Element Size + Bar Height as Double because Kotlin */ * (font.fontHeight + 2)
                        x = 5.0
                        y = MinecraftClient.getInstance().window.scaledHeight / 2f - maxHeight / 2f
                        panelWidth = 100.0
                        panelHeight = maxHeight
                    }

                    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
                        matrices!!.push()
                        matrices.translate(x + 2, y + font.fontHeight + 4, 0.0)
                        if (screenHandler.isBurning) {
                            // 23 = max
                            val progress: Int = 23 - screenHandler.cookProgress
                            val width = RenderUtil.text(matrices, "Item smelting finished in: " + (progress / 2 + 1) + " seconds", 0f, 0f)
                            panelWidth = (width + 2).toDouble()
                            RenderUtil.text(matrices, "Fuel Power ends in: " + (screenHandler.fuelProgress + 1) + " seconds", 0f, (font.fontHeight + 2).toFloat())
                        } else {
                            RenderUtil.text(matrices, "Waiting...", 0f, 0f)
                            panelWidth = 100.0
                        }
                        matrices.pop()
                    }
                }))
            }
        }
    }
}
