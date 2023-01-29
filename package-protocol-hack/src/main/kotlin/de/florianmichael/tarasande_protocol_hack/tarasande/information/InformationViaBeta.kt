package de.florianmichael.tarasande_protocol_hack.tarasande.information

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.tarasande_protocol_hack.TarasandeProtocolHack
import de.florianmichael.viabeta.api.BetaProtocolAccess
import de.florianmichael.viabeta.api.BetaProtocols
import de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.storage.ExtMessageTypesStorage
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information

class InformationViaBeta1_7_6or1_7_10EntityTracker : Information("Via Beta", ProtocolVersion.v1_7_6.name + " Entity Tracker") {

    override fun getMessage(): String? = BetaProtocolAccess.getTrackedEntities1_7_6_10(TarasandeProtocolHack.viaConnection)
}

class InformationViaBeta1_7_6or1_7_10VirtualHolograms : Information("Via Beta", ProtocolVersion.v1_7_6.name + " Virtual Holograms") {

    override fun getMessage(): String? = BetaProtocolAccess.getVirtualHolograms1_7_6_10(TarasandeProtocolHack.viaConnection)
}

class InformationViaBeta1_5_2EntityTracker : Information("Via Beta", BetaProtocols.r1_5_2.name + " Entity Tracker") {

    override fun getMessage(): String? = BetaProtocolAccess.getTrackedEntities1_5_2(TarasandeProtocolHack.viaConnection)
}

class InformationViaBeta1_2_4or1_2_5EntityTracker : Information("Via Beta", BetaProtocols.r1_2_4tor1_2_5.name + " Entity Tracker") {

    override fun getMessage(): String? = BetaProtocolAccess.getTrackedEntities1_2_4_5(TarasandeProtocolHack.viaConnection)
}

class InformationViaBeta1_1WorldSeed : Information("Via Beta", BetaProtocols.r1_1.name + " World Seed") {

    override fun getMessage(): String? = BetaProtocolAccess.getWorldSeed1_1(TarasandeProtocolHack.viaConnection)
}

class InformationViaBetaC0_30CPE_MessageTypesExtension : Information("Via Beta", BetaProtocols.c0_30cpe.name + " Message Types Extension") {

    override fun getMessage(): String? {
        if (TarasandeProtocolHack.viaConnection == null) return null
        val messageTypeStorage = TarasandeProtocolHack.viaConnection!!.get(ExtMessageTypesStorage::class.java) ?: return null
        val list = messageTypeStorage.asDisplayList

        if (list.isEmpty()) return null
        return list.joinToString("\n")
    }
}
