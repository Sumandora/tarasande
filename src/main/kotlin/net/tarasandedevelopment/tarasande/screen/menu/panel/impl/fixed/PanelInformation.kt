package net.tarasandedevelopment.tarasande.screen.menu.panel.impl.fixed

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.screen.menu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.menu.panel.Alignment
import net.tarasandedevelopment.tarasande.screen.menu.panel.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.ValueButton
import net.tarasandedevelopment.tarasande.value.ValueMode

class PanelInformation(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Information", x, y, 75.0, MinecraftClient.getInstance().textRenderer.fontHeight.toDouble(), resizable = false, fixed = true) {

    var visible = ValueMode(this, "Elements", true, *screenCheatMenu.managerInformation.list.map { i -> i.owner + ": " + i.information }.toTypedArray())

    init {
        for (information in screenCheatMenu.managerInformation.list) {
            if (TarasandeMain.get().managerValue.getValues(information).isNotEmpty()) {
                val name = information.owner + ": " + information.information

                object : ValueButton(this, "$name settings") {
                    override fun onChange() {
                        MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, name, information))
                    }
                }
            }
        }
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val text = ArrayList<String>()
        for (owner in TarasandeMain.get().screenCheatMenu.managerInformation.getAllOwners()) {

            val cache = ArrayList<String>()
            val informationList = TarasandeMain.get().screenCheatMenu.managerInformation.getAllInformation(owner)
            for (information in informationList) {
                if (!visible.selected.contains(information.owner + ": " + information.information)) continue

                val message = information.getMessage()
                if (message != null) {
                    if (message.contains("\n")) {
                        val parts = message.split("\n")
                        if (parts.isNotEmpty()) {
                            if (parts[0].isNotEmpty()) cache.add("[" + information.information + "] " + parts[0])
                            else cache.add("[" + information.information + "]")
                            if (parts.size > 1) {
                                cache.addAll(parts.subList(1, parts.size))
                            }
                        }
                    } else {
                        cache.add("[" + information.information + "] $message")
                    }
                }
            }
            if (cache.isNotEmpty()) {
                text.add("[$owner]")
                text.addAll(cache)
                text.add("")
            }
        }

        for ((index, it) in text.withIndex()) {
            when (alignment) {
                Alignment.LEFT -> RenderUtil.drawWithSmallShadow(matrices, it, x.toFloat(), y.toFloat() + titleBarHeight + MinecraftClient.getInstance().textRenderer.fontHeight * index, TarasandeMain.get().clientValues.accentColor.getColor().rgb)
                Alignment.MIDDLE -> RenderUtil.drawWithSmallShadow(matrices, it, x.toFloat() + panelWidth.toFloat() / 2.0f - MinecraftClient.getInstance().textRenderer.getWidth(it).toFloat() / 2.0f, y.toFloat() + titleBarHeight + MinecraftClient.getInstance().textRenderer.fontHeight * index, TarasandeMain.get().clientValues.accentColor.getColor().rgb)
                Alignment.RIGHT -> RenderUtil.drawWithSmallShadow(matrices, it, x.toFloat() + panelWidth.toFloat() - MinecraftClient.getInstance().textRenderer.getWidth(it).toFloat(), y.toFloat() + titleBarHeight + MinecraftClient.getInstance().textRenderer.fontHeight * index, TarasandeMain.get().clientValues.accentColor.getColor().rgb)
            }
        }
    }

    override fun isVisible(): Boolean {
        for (information in TarasandeMain.get().screenCheatMenu.managerInformation.list)
            if (information.getMessage() != null)
                return true
        return false
    }

}