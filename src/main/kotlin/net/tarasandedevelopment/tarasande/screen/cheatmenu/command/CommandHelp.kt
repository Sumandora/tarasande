package net.tarasandedevelopment.tarasande.screen.cheatmenu.command

import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.command.Command
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.impl.terminal.PanelElementsTerminal

class CommandHelp : Command("help") {

    override fun execute(args: Array<String>, panel: PanelElementsTerminal): Boolean {
        panel.screenCheatMenu.managerCommand.list.forEach {
            panel.add(it.name)
        }
        return true
    }
}
