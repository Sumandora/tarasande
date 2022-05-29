package su.mandora.tarasande.screen.menu.information

import net.minecraft.client.MinecraftClient
import net.minecraft.text.TranslatableText
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.information.Information
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.mixin.accessor.IRenderTickCounter
import su.mandora.tarasande.module.misc.ModuleMurderMystery
import su.mandora.tarasande.module.misc.ModuleTickBaseManipulation
import kotlin.math.floor

class InformationTimeShifted : Information("Tick base manipulation", "Time shifted") {
    private val moduleTickBaseManipulation = TarasandeMain.get().managerModule?.get(ModuleTickBaseManipulation::class.java)!!

    override fun getMessage(): String? {
        if (!moduleTickBaseManipulation.enabled)
            return null
        if (moduleTickBaseManipulation.shifted == 0L)
            return null
        return moduleTickBaseManipulation.shifted.toString() + " (" + floor(moduleTickBaseManipulation.shifted / ((MinecraftClient.getInstance() as IMinecraftClient).renderTickCounter as IRenderTickCounter).tickTime).toInt() + ")"
    }
}

class InformationMurderer : Information("Murder Mystery", "Suspected murderers") {
    override fun getMessage(): String? {
        val murderMystery = TarasandeMain.get().managerModule?.get(ModuleMurderMystery::class.java)!!
        if (murderMystery.enabled)
            if (murderMystery.suspects.isNotEmpty()) {
                return "\n" + murderMystery.suspects.entries.joinToString("\n") { it.key.name + " (" + it.value.joinToString(" and ") { (it.name as TranslatableText).string } + ")" }
            }

        return null
    }
}