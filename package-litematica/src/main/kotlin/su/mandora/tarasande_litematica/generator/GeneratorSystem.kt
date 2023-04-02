package su.mandora.tarasande_litematica.generator

import net.minecraft.text.Text
import su.mandora.tarasande.Manager
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.meta.ValueButton
import su.mandora.tarasande.util.player.chat.CustomChat
import su.mandora.tarasande_litematica.generator.impl.GeneratorMazes
import su.mandora.tarasande_litematica.generator.impl.GeneratorQRCode

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
