package su.mandora.tarasande_rejected_features.information

import com.mojang.blaze3d.platform.GlDebugInfo
import su.mandora.tarasande.system.screen.informationsystem.Information

class InformationCPU : Information("Hardware", "CPU") {

    override fun getMessage() = GlDebugInfo.getCpuInfo()!!
}

class InformationGPU : Information("Hardware", "GPU") {

    override fun getMessage() = GlDebugInfo.getRenderer()!!
}
