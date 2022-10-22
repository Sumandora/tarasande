package net.tarasandedevelopment.tarasande.screen.cheatmenu.information

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information.Information
import net.tarasandedevelopment.tarasande.module.exploit.ModuleTickBaseManipulation
import net.tarasandedevelopment.tarasande.module.misc.ModuleMurderMystery
import net.tarasandedevelopment.tarasande.module.player.ModuleAntiAFK
import net.tarasandedevelopment.tarasande.module.render.ModuleBedESP
import net.tarasandedevelopment.tarasande.util.extension.div
import net.tarasandedevelopment.tarasande.util.extension.plus
import kotlin.math.round
import kotlin.math.roundToInt

class InformationTimeShifted : Information("Tick base manipulation", "Time shifted") {
    private val moduleTickBaseManipulation = TarasandeMain.get().managerModule.get(ModuleTickBaseManipulation::class.java)

    override fun getMessage(): String? {
        if (!moduleTickBaseManipulation.enabled) return null
        if (moduleTickBaseManipulation.shifted == 0L) return null
        return moduleTickBaseManipulation.shifted.toString() + " (" + round(moduleTickBaseManipulation.shifted / MinecraftClient.getInstance().renderTickCounter.tickTime).toInt() + ")"
    }
}

class InformationSuspectedMurderers : Information("Murder Mystery", "Suspected murderers") {
    override fun getMessage(): String? {
        val moduleMurderMystery = TarasandeMain.get().managerModule.get(ModuleMurderMystery::class.java)
        if (moduleMurderMystery.enabled)
            if (moduleMurderMystery.suspects.isNotEmpty()) {
                return "\n" + moduleMurderMystery.suspects.entries.joinToString("\n") {
                    it.key.name + " (" + it.value.joinToString(" and ") { it.name.string } + "§r)"
                }
            }

        return null
    }
}

class InformationFakeNewsCountdown : Information("Murder Mystery", "Fake news countdown") {
    override fun getMessage(): String? {
        val moduleMurderMystery = TarasandeMain.get().managerModule.get(ModuleMurderMystery::class.java)
        if (moduleMurderMystery.enabled)
            if (!moduleMurderMystery.fakeNews.isSelected(0) && moduleMurderMystery.isMurderer() && moduleMurderMystery.murdererAssistance.value)
                return (moduleMurderMystery.fakeNewsTime - (System.currentTimeMillis() - moduleMurderMystery.fakeNewsTimer.time)).toString()

        return null
    }
}

class InformationBeds : Information("Bed ESP", "Beds") {
    override fun getMessage(): String? {
        val moduleBedESP = TarasandeMain.get().managerModule.get(ModuleBedESP::class.java)
        if (moduleBedESP.enabled) if (moduleBedESP.calculateBestWay.value) if (moduleBedESP.bedDatas.isNotEmpty()) {
            return "\n" + moduleBedESP.bedDatas.sortedBy {
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

class InformationAntiAFKCountdown : Information("Anti AFK", "Jump countdown") {
    override fun getMessage(): String? {
        val moduleAntiAFK = TarasandeMain.get().managerModule.get(ModuleAntiAFK::class.java)
        if (moduleAntiAFK.enabled)
            return (((moduleAntiAFK.delay.value * 1000L) - (System.currentTimeMillis() - moduleAntiAFK.timer.time)) / 1000.0).roundToInt().toString()

        return null
    }
}