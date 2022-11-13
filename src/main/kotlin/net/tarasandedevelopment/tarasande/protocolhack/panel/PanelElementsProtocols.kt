package net.tarasandedevelopment.tarasande.protocolhack.panel

import de.florianmichael.viaprotocolhack.util.VersionList
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.protocolhack.platform.ProtocolHackValues
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.meta.ValueSpacer
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.ElementValueComponent
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.PanelElements
import java.awt.Color

class PanelElementsProtocols : PanelElements<ElementValueComponent>("Protocol Hack", 100.0, 0.0) {

    init {
        TarasandeMain.managerValue().getValues(ProtocolHackValues).forEach {
            it.owner = this
        }

        for (protocol in VersionList.getProtocols()) {
            this.elementList.add(object : ValueSpacer(protocol, protocol.name, 1.0F) {
                override fun onChange() {
                    TarasandeMain.protocolHack().version.value = protocol.version.toDouble()
                    TarasandeMain.instance.protocolHack.update(protocol)
                }

                override fun getColor(): Color? {
                    if (TarasandeMain.protocolHack().targetVersion() == protocol.version) {
                        return TarasandeMain.clientValues().accentColor.getColor()
                    }
                    return super.getColor()
                }
            }.createValueComponent());
        }
    }
}
