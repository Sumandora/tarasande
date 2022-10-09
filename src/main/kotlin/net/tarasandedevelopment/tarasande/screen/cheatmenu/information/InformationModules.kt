package net.tarasandedevelopment.tarasande.screen.cheatmenu.information

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information.Information
import net.tarasandedevelopment.tarasande.mixin.accessor.IMinecraftClient
import net.tarasandedevelopment.tarasande.mixin.accessor.IRenderTickCounter
import net.tarasandedevelopment.tarasande.module.exploit.ModuleTickBaseManipulation
import net.tarasandedevelopment.tarasande.module.misc.ModuleMurderMystery
import net.tarasandedevelopment.tarasande.module.render.ModuleBedESP
import net.tarasandedevelopment.tarasande.util.extension.div
import net.tarasandedevelopment.tarasande.util.extension.plus
import kotlin.math.round

class InformationTimeShifted : Information("Tick base manipulation", "Time shifted") {
    private val moduleTickBaseManipulation = TarasandeMain.get().managerModule.get(ModuleTickBaseManipulation::class.java)

    override fun getMessage(): String? {
        if (!moduleTickBaseManipulation.enabled) return null
        if (moduleTickBaseManipulation.shifted == 0L) return null
        return moduleTickBaseManipulation.shifted.toString() + " (" + round(moduleTickBaseManipulation.shifted / ((MinecraftClient.getInstance() as IMinecraftClient).tarasande_getRenderTickCounter() as IRenderTickCounter).tarasande_getTickTime()).toInt() + ")"
    }
}

class InformationSuspectedMurderers : Information("Murder Mystery", "Suspected murderers") {
    override fun getMessage(): String? {
        val murderMystery = TarasandeMain.get().managerModule.get(ModuleMurderMystery::class.java)
        if (murderMystery.enabled)
            if (murderMystery.suspects.isNotEmpty()) {
                return "\n" + murderMystery.suspects.entries.joinToString("\n") {
                    it.key.name + " (" + it.value.joinToString(" and ") { it.name.string } + "§r)"
                }
            }

        return null
    }
}

class InformationFakeNewsCountdown : Information("Murder Mystery", "Fake news countdown") {
    override fun getMessage(): String? {
        val murderMystery = TarasandeMain.get().managerModule.get(ModuleMurderMystery::class.java)
        if (murderMystery.enabled)
            if (!murderMystery.fakeNews.isSelected(0) && murderMystery.isMurderer() && murderMystery.murdererAssistance.value)
                return (murderMystery.fakeNewsTime - (System.currentTimeMillis() - murderMystery.fakeNewsTimer.time)).toString()

        return null
    }
}

class InformationBeds : Information("Bed ESP", "Beds") {
    override fun getMessage(): String? {
        val bedESP = TarasandeMain.get().managerModule.get(ModuleBedESP::class.java)
        if (bedESP.enabled) if (bedESP.calculateBestWay.value) if (bedESP.bedDatas.isNotEmpty()) {
            return "\n" + bedESP.bedDatas.sortedBy {
                MinecraftClient.getInstance().player?.squaredDistanceTo(it.bedParts.let {
                    var vec = Vec3d.ZERO
                    it.forEach { vec += Vec3d.ofCenter(it) }
                    vec / it.size
                })
            }.joinToString("\n") { it.toString() }.let { it.substring(0, it.length - 1) }
        }

        return null
    }
}