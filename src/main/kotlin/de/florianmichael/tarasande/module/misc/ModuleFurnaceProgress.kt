package de.florianmichael.tarasande.module.misc

import de.florianmichael.tarasande.event.EventChildren
import de.florianmichael.tarasande.util.render.RenderUtil.ourStack
import de.florianmichael.tarasande.util.render.RenderUtil.text
import de.florianmichael.tarasande.util.render.RenderUtil.useMyStack
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.AbstractFurnaceScreenHandler
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.screen.menu.panel.Panel
import su.mandora.tarasande.screen.widget.panel.ClickableWidgetPanel
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
                        useMyStack(matrices)
                        if (screenHandler.isBurning) {
                            // 23 = max
                            val progress: Int = 23 - screenHandler.cookProgress
                            val width = text("Item smelting finished in: " + (progress / 2 + 1) + " seconds", 0f, 0f)
                            panelWidth = (width + 2).toDouble()
                            text("Fuel Power ends in: " + (screenHandler.fuelProgress + 1) + " seconds", 0f, (font.fontHeight + 2).toFloat())
                        } else {
                            text("Waiting...", 0f, 0f)
                            panelWidth = 100.0
                        }
                        ourStack()
                        matrices.pop()
                    }
                }))
            }
        }
    }
}
