package net.tarasandedevelopment.tarasande.screen.cheatmenu.information

import com.mojang.blaze3d.platform.GlDebugInfo
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information.Information

class InformationCPU : Information("Hardware", "CPU") {

    override fun getMessage() = GlDebugInfo.getCpuInfo()!!
}

class InformationGPU : Information("Hardware", "GPU") {

    override fun getMessage() = GlDebugInfo.getRenderer()!!
}
