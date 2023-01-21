package de.florianmichael.tarasande_protocol_hack.tarasande.information

import de.florianmichael.tarasande_protocol_hack.TarasandeProtocolHack
import de.florianmichael.viabeta.api.BetaProtocolAccess
import de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.storage.ExtMessageTypesStorage
import de.florianmichael.vialoadingbase.util.VersionListEnum
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information

class InformationViaBeta1_7_6or1_7_10EntityTracker : Information("Via Beta", VersionListEnum.r1_7_6tor1_7_10.getName() + " Entity Tracker") {

    override fun getMessage(): String? = BetaProtocolAccess.getTrackedEntities1_7_6_10(TarasandeProtocolHack.viaConnection)
}

class InformationViaBeta1_7_6or1_7_10VirtualHolograms : Information("Via Beta", VersionListEnum.r1_7_6tor1_7_10.getName() + " Virtual Holograms") {

    override fun getMessage(): String? = BetaProtocolAccess.getVirtualHolograms1_7_6_10(TarasandeProtocolHack.viaConnection)
}

class InformationViaBeta1_5_2EntityTracker : Information("Via Beta", VersionListEnum.r1_5_2.getName() + " Entity Tracker") {

    override fun getMessage(): String? = BetaProtocolAccess.getTrackedEntities1_5_2(TarasandeProtocolHack.viaConnection)
}

class InformationViaBeta1_2_4or1_2_5EntityTracker : Information("Via Beta", VersionListEnum.r1_2_4tor1_2_5.getName() + " Entity Tracker") {

    override fun getMessage(): String? = BetaProtocolAccess.getTrackedEntities1_2_4_5(TarasandeProtocolHack.viaConnection)
}

class InformationViaBeta1_1WorldSeed : Information("Via Beta", VersionListEnum.r1_1.getName() + " World Seed") {

    override fun getMessage(): String? = BetaProtocolAccess.getWorldSeed1_1(TarasandeProtocolHack.viaConnection)
}

class InformationViaBetaC0_30CPE_MessageTypesExtension : Information("Via Beta", VersionListEnum.c0_30cpe.getName() + " Message Types Extension") {

    override fun getMessage(): String? {
        if (TarasandeProtocolHack.viaConnection == null) return null
        val messageTypeStorage = TarasandeProtocolHack.viaConnection!!.get(ExtMessageTypesStorage::class.java) ?: return null
        val list = messageTypeStorage.asDisplayList

        if (list.isEmpty()) return null
        return list.joinToString("\n")
    }
}
