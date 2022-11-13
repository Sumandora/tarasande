package net.tarasandedevelopment.tarasande.systems.screen.informationsystem.panel

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.render.helper.Alignment

class PanelInformation(private val informationSystem: ManagerInformation) : Panel("Information", 75.0, FontWrapper.fontHeight().toDouble(), background = false, resizable = false, fixed = true) {

    val map = HashMap<Information, String>()

    init {
        informationSystem.list.forEach {
            map[it] = it.owner + ": " + it.information
        }
    }

    val elements = ValueMode(this, "Elements", true, *informationSystem.list.map { map[it]!! }.toTypedArray())

    internal fun isSelected(information: Information) = elements.selected.contains(map[information])

    init {
        for (information in informationSystem.list) {
            if (TarasandeMain.managerValue().getValues(information).isNotEmpty()) {
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
        for (owner in informationSystem.getAllOwners()) {

            val cache = ArrayList<String>()
            val informationList = informationSystem.getAllInformation(owner)
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
                Alignment.LEFT -> FontWrapper.textShadow(matrices, it, x.toFloat(), y.toFloat() + titleBarHeight + FontWrapper.fontHeight() * index, TarasandeMain.clientValues().accentColor.getColor().rgb, offset = 0.5F)
                Alignment.MIDDLE -> FontWrapper.textShadow(matrices, it, x.toFloat() + panelWidth.toFloat() / 2.0f - FontWrapper.getWidth(it).toFloat() / 2.0f, y.toFloat() + titleBarHeight + FontWrapper.fontHeight() * index, TarasandeMain.clientValues().accentColor.getColor().rgb, offset = 0.5F)
                Alignment.RIGHT -> FontWrapper.textShadow(matrices, it, x.toFloat() + panelWidth.toFloat() - FontWrapper.getWidth(it).toFloat(), y.toFloat() + titleBarHeight + FontWrapper.fontHeight() * index, TarasandeMain.clientValues().accentColor.getColor().rgb, offset = 0.5F)
            }
        }
    }

    override fun isVisible(): Boolean {
        for (information in informationSystem.list)
            if (information.getMessage() != null)
                return true
        return false
    }

}