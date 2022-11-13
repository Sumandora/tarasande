package net.tarasandedevelopment.tarasande.systems.screen.informationsystem.impl

import com.mojang.blaze3d.platform.GlDebugInfo
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.Information

class InformationCPU : Information("Hardware", "CPU") {

    override fun getMessage() = GlDebugInfo.getCpuInfo()!!
}

class InformationGPU : Information("Hardware", "GPU") {

    override fun getMessage() = GlDebugInfo.getRenderer()!!
}
