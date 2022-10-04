package net.tarasandedevelopment.tarasande.base.command

import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.screen.menu.command.CommandNotificationTest
import net.tarasandedevelopment.tarasande.screen.menu.panel.impl.elements.impl.terminal.PanelElementsTerminal

class ManagerCommand : Manager<Command>() {

    init {
        add(
            CommandNotificationTest()
        )
    }

    fun execute(input: String, panel: PanelElementsTerminal) {
        val inputArgs = input.split("( )V")

        this.list.forEach {
            if (it.equals(inputArgs[0])) {
                if (!it.execute(inputArgs.toTypedArray().copyOfRange(1, inputArgs.size), panel)) {
                    panel.add("Executed '" + inputArgs[0] + "'")
                }
                return
            }
        }

        panel.add("This command doesn't exist")
    }
}

abstract class Command(val name: String, private val aliases: Array<String> = arrayOf()) {

    override fun equals(other: Any?): Boolean {
        if (other !is String) return false
        if (other.equals(this.name, true)) return true

        aliases.forEach {
            if (it.equals(other, true)) return true
        }
        return false
    }

    abstract fun execute(args: Array<String>, panel: PanelElementsTerminal): Boolean
    open fun tabComplete(argument: Int): Array<String> {
        return arrayOf()
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + aliases.contentHashCode()
        return result
    }
}
