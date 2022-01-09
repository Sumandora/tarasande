package su.mandora.tarasande.screen.menu.information

import net.minecraft.client.MinecraftClient
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.information.Information
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.mixin.accessor.IRenderTickCounter
import su.mandora.tarasande.module.misc.ModuleTickBaseManipulation
import kotlin.math.floor

class InformationTimeShifted : Information("Tick base manipulation", "Time shifted") {
    private val moduleTickBaseManipulation = TarasandeMain.get().managerModule?.get(ModuleTickBaseManipulation::class.java)!!

    override fun getMessage(): String? {
        if(!moduleTickBaseManipulation.enabled) return null
        return moduleTickBaseManipulation.shifted.toString() + " (" + floor(moduleTickBaseManipulation.shifted / ((MinecraftClient.getInstance() as IMinecraftClient).renderTickCounter as IRenderTickCounter).tickTime).toInt() + ")"
    }
}