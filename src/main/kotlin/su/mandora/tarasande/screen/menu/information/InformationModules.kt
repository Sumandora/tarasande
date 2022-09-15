package su.mandora.tarasande.screen.menu.information

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.information.Information
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.mixin.accessor.IRenderTickCounter
import su.mandora.tarasande.module.misc.ModuleMurderMystery
import su.mandora.tarasande.module.misc.ModuleTickBaseManipulation
import su.mandora.tarasande.module.render.ModuleBedESP
import kotlin.math.round

class InformationTimeShifted : Information("Tick base manipulation", "Time shifted") {
    private val moduleTickBaseManipulation = TarasandeMain.get().managerModule?.get(ModuleTickBaseManipulation::class.java)!!

    override fun getMessage(): String? {
        if (!moduleTickBaseManipulation.enabled) return null
        if (moduleTickBaseManipulation.shifted == 0L) return null
        return moduleTickBaseManipulation.shifted.toString() + " (" + round(moduleTickBaseManipulation.shifted / ((MinecraftClient.getInstance() as IMinecraftClient).tarasande_getRenderTickCounter() as IRenderTickCounter).tarasande_getTickTime()).toInt() + ")"
    }
}

class InformationSuspectedMurderers : Information("Murder Mystery", "Suspected murderers") {
    override fun getMessage(): String? {
        val murderMystery = TarasandeMain.get().managerModule?.get(ModuleMurderMystery::class.java)!!
        if (murderMystery.enabled) if (murderMystery.suspects.isNotEmpty()) {
            return "\n" + murderMystery.suspects.entries.joinToString("\n") {
                it.key.name + " (" + it.value.joinToString(" and ") { it.name.string } + "§r)"
            }
        }

        return null
    }
}

class InformationBeds : Information("Bed ESP", "Beds") {
    override fun getMessage(): String? {
        val bedESP = TarasandeMain.get().managerModule?.get(ModuleBedESP::class.java)!!
        if (bedESP.enabled) if (bedESP.calculateBestWay.value) if (bedESP.bedDatas.isNotEmpty()) {
            return "\n" + bedESP.bedDatas.sortedBy {
                MinecraftClient.getInstance().player?.squaredDistanceTo(it.bedParts.let {
                    var vec = Vec3d.ZERO
                    it.forEach { vec = vec.add(Vec3d.ofCenter(it)) }
                    vec.multiply(1.0 / it.size) // ffs there is no divide
                })
            }.joinToString("\n") { it.toString() }
        }

        return null
    }
}