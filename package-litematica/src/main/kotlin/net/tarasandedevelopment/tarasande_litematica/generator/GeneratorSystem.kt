package net.tarasandedevelopment.tarasande_litematica.generator

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat

class ManagerGenerator : Manager<Generator>() {

    init {
        add(
            GeneratorMazes(this)
        )
    }
}

abstract class Generator(val parent: Any, val name: String) {

    init {
        object : ValueButton(parent, "Perform $name") {
            override fun onChange() {
                perform()
            }
        }
    }

    fun finish() {
        MinecraftClient.getInstance().setScreen(null)
        CustomChat.printChatMessage(Text.literal("Added $name as current schematic, goto the Litematica menu to build it"))
    }
    abstract fun perform()
}
