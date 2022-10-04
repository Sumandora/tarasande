package net.tarasandedevelopment.tarasande.util.player.chat

import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.impl.notification.PanelElementsNotification
import net.tarasandedevelopment.tarasande.value.ValueSpacer

object CommunicationUtil {

    private val information = Formatting.GREEN.toString() + "Information" + Formatting.WHITE + " "

    fun printInformation(module: Module, message: String) {
        val end = StringBuilder()
        end.append(information)

        end.append(module.name)
        end.append(": ")
        end.append(message)

        add(end.toString())
    }

    fun printInformation(message: String) {
        val end = StringBuilder()

        end.append(information)
        end.append(message)

        add(end.toString())
    }

    internal fun add(message: String) {
        val panel = TarasandeMain.get().screenCheatMenu.panels.filterIsInstance<PanelElementsNotification>().first()

        panel.elementList.add(0, TarasandeMain.get().screenCheatMenu.managerValueComponent.newInstance(ValueSpacer(panel, message))!!)
    }
}
