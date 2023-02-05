package de.florianmichael.tarasande_litematica.generator

import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat
import de.florianmichael.tarasande_litematica.generator.impl.GeneratorMazes
import de.florianmichael.tarasande_litematica.generator.impl.GeneratorQRCode

object ManagerGenerator : Manager<Generator>() {

    init {
        add(
            GeneratorMazes(),
            GeneratorQRCode()
        )
    }
}

abstract class Generator(val name: String) {

    init {
        object : ValueButton(this, "Perform $name") {
            override fun onClick() {
                perform()
            }
        }
    }

    fun finish() {
        mc.setScreen(null)
        CustomChat.printChatMessage(Text.literal("Added $name as current schematic, goto the Litematica menu to build it"))
    }
    abstract fun perform()
}
