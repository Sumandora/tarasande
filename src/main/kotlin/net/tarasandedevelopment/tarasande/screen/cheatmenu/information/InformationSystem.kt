package net.tarasandedevelopment.tarasande.screen.cheatmenu.information

import com.mojang.blaze3d.platform.GlDebugInfo
import net.minecraft.util.Util
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information.Information
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed.PanelInformation

class InformationCPU : Information("System", "CPU") {

    override fun getMessage() = GlDebugInfo.getCpuInfo()!!
}

class InformationGPU : Information("System", "GPU") {

    override fun getMessage() = GlDebugInfo.getRenderer()!!
}

class InformationPortage : Information("System", "Portage") {

    companion object {
        fun isGenlopInstalled(): Boolean {
            if (Util.getOperatingSystem() != Util.OperatingSystem.LINUX)
                return false

            return try {
                Runtime.getRuntime().exec("genlop")
                true
            } catch (t: Throwable) {
                false
            }
        }
    }

    private var lastState = ""

    init {
        Thread({
            while (true) {
                Thread.sleep(100L)

                if (!TarasandeMain.get().screenCheatMenu.panels.filterIsInstance<PanelInformation>().first().isSelected(this)) {
                    lastState = ""
                    continue
                }

                lastState = try {
                    String(Runtime.getRuntime().exec("genlop -c -n").inputStream.readAllBytes())
                } catch (t: Throwable) {
                    t.toString()
                }
            }
        }, "Genlop query thread").start()
    }

    override fun getMessage(): String? {
        if (lastState.contains("no working merge found.") || lastState.isEmpty())
            return null
        return "\n" + lastState
    }
}
