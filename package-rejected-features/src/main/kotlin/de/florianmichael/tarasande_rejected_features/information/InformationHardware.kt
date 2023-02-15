package de.florianmichael.tarasande_rejected_features.information

import com.mojang.blaze3d.platform.GlDebugInfo
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information

class InformationCPU : Information("Hardware", "CPU") {

    override fun getMessage() = GlDebugInfo.getCpuInfo()!!
}

class InformationGPU : Information("Hardware", "GPU") {

    override fun getMessage() = GlDebugInfo.getRenderer()!!
}
