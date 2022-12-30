package net.tarasandedevelopment.tarasande.system.feature.modulesystem.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.Command
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule

class CommandToggle(private val moduleSystem: ManagerModule) : Command("toggle") {

    @Suppress("NAME_SHADOWING")
    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.then(this.argument("module", StringArgumentType.greedyString())?.suggests { _, builder ->
            moduleSystem.list.forEach {
                if (it.name.startsWith(builder.remaining)) {
                    builder.suggest(it.name)
                }
            }
            return@suggests builder.buildFuture()
        }?.executes {
            moduleSystem.list.firstOrNull { module -> module.name == StringArgumentType.getString(it, "module") }?.apply {
                switchState()
                printChatMessage(name + " is now " + if (enabled) "enabled" else "disabled")
                return@executes SUCCESS
            }
            return@executes ERROR
        })
    }
}
