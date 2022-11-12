package net.tarasandedevelopment.tarasande.panelsystem.impl.fixed

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.informationsystem.Information
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.panelsystem.base.Alignment
import net.tarasandedevelopment.tarasande.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.value.impl.ValueMode
import net.tarasandedevelopment.tarasande.value.meta.ValueButton

class PanelInformation(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Information", x, y, 75.0, FontWrapper.fontHeight().toDouble(), background = false, resizable = false, fixed = true) {

    val map = HashMap<Information, String>()

    init {
        screenCheatMenu.managerInformation.list.forEach {
            map[it] = it.owner + ": " + it.information
        }
    }

    val elements = ValueMode(this, "Elements", true, *screenCheatMenu.managerInformation.list.map { map[it]!! }.toTypedArray())

    internal fun isSelected(information: Information) = elements.selected.contains(map[information])

    init {
        for (information in screenCheatMenu.managerInformation.list) {
            if (TarasandeMain.get().valueSystem.getValues(information).isNotEmpty()) {
                val name = map[information]!!

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
                if (!isSelected(information)) continue

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
                Alignment.LEFT -> FontWrapper.textShadow(matrices, it, x.toFloat(), y.toFloat() + titleBarHeight + FontWrapper.fontHeight() * index, TarasandeMain.get().clientValues.accentColor.getColor().rgb, offset = 0.5F)
                Alignment.MIDDLE -> FontWrapper.textShadow(matrices, it, x.toFloat() + panelWidth.toFloat() / 2.0f - FontWrapper.getWidth(it).toFloat() / 2.0f, y.toFloat() + titleBarHeight + FontWrapper.fontHeight() * index, TarasandeMain.get().clientValues.accentColor.getColor().rgb, offset = 0.5F)
                Alignment.RIGHT -> FontWrapper.textShadow(matrices, it, x.toFloat() + panelWidth.toFloat() - FontWrapper.getWidth(it).toFloat(), y.toFloat() + titleBarHeight + FontWrapper.fontHeight() * index, TarasandeMain.get().clientValues.accentColor.getColor().rgb, offset = 0.5F)
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