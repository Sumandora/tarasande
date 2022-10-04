package net.tarasandedevelopment.tarasande.screen.menu.command

import net.tarasandedevelopment.tarasande.base.command.Command
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.impl.terminal.PanelElementsTerminal
import net.tarasandedevelopment.tarasande.util.chat.CommunicationUtil

class CommandNotificationTest : Command("notification") {

    override fun execute(args: Array<String>, panel: PanelElementsTerminal): Boolean {
        CommunicationUtil.printInformation("Test")
        return false
    }
}
