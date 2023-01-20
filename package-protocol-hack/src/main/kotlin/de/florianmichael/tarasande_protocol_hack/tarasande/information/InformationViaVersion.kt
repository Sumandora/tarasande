package de.florianmichael.tarasande_protocol_hack.tarasande.information

import de.florianmichael.tarasande_protocol_hack.TarasandeProtocolHack
import de.florianmichael.vialoadingbase.ViaLoadingBase
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information

class InformationViaVersionProtocolVersion : Information("Via Version", "Protocol Version") {

    override fun getMessage(): String? = ViaLoadingBase.getTargetVersion()?.getName()
}

class InformationViaVersionProtocolsInPipeline : Information("Via Version", "Protocols in pipeline") {
    private val displayMode = ValueMode(this, "Display mode", false, "Names", "Size")

    override fun getMessage(): String? {
        val names = TarasandeProtocolHack.viaConnection?.protocolInfo?.pipeline?.pipes()?.map { p -> p.javaClass.simpleName } ?: return null
        if (names.isEmpty()) return null

        return if (displayMode.isSelected(0)) {
            "\n" + names.joinToString("\n")
        } else {
            names.size.toString()
        }
    }
}
