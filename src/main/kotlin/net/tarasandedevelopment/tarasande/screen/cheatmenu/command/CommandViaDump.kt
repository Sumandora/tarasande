package net.tarasandedevelopment.tarasande.screen.cheatmenu.command

import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.command.ViaCommandSender
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.command.Command
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.impl.terminal.PanelElementsTerminal
import java.util.*

class CommandViaDump : Command("viadump") {

    override fun execute(args: Array<String>, panel: PanelElementsTerminal): Boolean {
        Via.getManager().commandHandler.getSubCommand("dump")!!.execute(ViaDumpBypassSender(panel), arrayOf())
        return true
    }

    class ViaDumpBypassSender(val panel: PanelElementsTerminal) : ViaCommandSender {
        override fun hasPermission(permission: String?): Boolean {
            return true
        }

        override fun sendMessage(msg: String?) {
            if (msg == null) return
            Formatting.strip(msg)?.let {
                panel.add(it)
            }

            if (msg.contains("https://")) {
                MinecraftClient.getInstance().keyboard.clipboard = msg.split("https://")[1]
            }
        }

        override fun getUUID(): UUID = UUID.fromString(MinecraftClient.getInstance().session.uuid)
        override fun getName(): String = MinecraftClient.getInstance().session.username
    }
}
