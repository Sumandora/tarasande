package su.mandora.tarasande.system.screen.informationsystem.panel

import net.minecraft.client.gui.DrawContext
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.system.base.valuesystem.ManagerValue
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.system.screen.informationsystem.ManagerInformation
import su.mandora.tarasande.system.screen.panelsystem.api.PanelFixed
import su.mandora.tarasande.util.extension.javaruntime.clearAndGC
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.render.helper.Alignment

class PanelInformation(private val informationSystem: ManagerInformation) : PanelFixed("Information", 75.0, FontWrapper.fontHeight().toDouble(), resizable = false) {

    val map = HashMap<Information, String>()
    val text = ArrayList<String>()

    init {
        informationSystem.list.forEach {
            map[it] = it.owner + ": " + it.information
        }
    }

    private val elements = object : ValueMode(this, "Elements", true, *informationSystem.list.map { map[it]!! }.toTypedArray()) {
        override fun onChange(index: Int, oldSelected: Boolean, newSelected: Boolean) {
            text.clearAndGC()
        }
    }

    class InformationValues(panelInformation: PanelInformation) {
        init {
            for (information in ManagerInformation.list) {
                if (ManagerValue.getValues(information).isNotEmpty()) {
                    val name = panelInformation.map[information]!!

                    ValueButtonOwnerValues(this, "$name values", information)
                }
            }
        }
    }

    init {
        ValueButtonOwnerValues(this, "Information values", InformationValues(this))
    }

    fun isSelected(information: Information) = elements.isSelected(map[information]!!)

    override fun renderContent(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        for ((index, it) in text.withIndex()) {
            when (alignment) {
                Alignment.LEFT -> FontWrapper.textShadow(context, it, x.toFloat(), y.toFloat() + titleBarHeight + FontWrapper.fontHeight() * index, TarasandeValues.accentColor.getColor().rgb, offset = 0.5F)
                Alignment.MIDDLE -> FontWrapper.textShadow(context, it, x.toFloat() + panelWidth.toFloat() / 2F - FontWrapper.getWidth(it).toFloat() / 2F, y.toFloat() + titleBarHeight + FontWrapper.fontHeight() * index, TarasandeValues.accentColor.getColor().rgb, offset = 0.5F)
                Alignment.RIGHT -> FontWrapper.textShadow(context, it, x.toFloat() + panelWidth.toFloat() - FontWrapper.getWidth(it).toFloat(), y.toFloat() + titleBarHeight + FontWrapper.fontHeight() * index, TarasandeValues.accentColor.getColor().rgb, offset = 0.5F)
            }
        }
    }

    override fun isVisible(): Boolean {
        text.clear()
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

        return text.isNotEmpty()
    }

}