package net.tarasandedevelopment.tarasande.screen.menu.command

import net.tarasandedevelopment.tarasande.base.command.Command
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.impl.terminal.PanelElementsTerminal
import net.tarasandedevelopment.tarasande.util.player.chat.CommunicationUtil

class CommandNotificationTest : Command("notification") {

    override fun execute(args: Array<String>, panel: PanelElementsTerminal): Boolean {
        CommunicationUtil.printInformation("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam id viverra velit, aliquam porta arcu. In vitae malesuada tellus. Maecenas rutrum sem sed semper tincidunt. Mauris id feugiat libero, a lacinia ante. Vestibulum pulvinar quis erat at porttitor. Maecenas euismod tincidunt leo quis varius. Aenean aliquet metus eu neque dapibus, sed pharetra diam consectetur. ")
        return false
    }
}
