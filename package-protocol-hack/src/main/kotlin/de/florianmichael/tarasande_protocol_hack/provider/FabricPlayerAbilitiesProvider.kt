package de.florianmichael.tarasande_protocol_hack.provider

import net.tarasandedevelopment.tarasande.mc
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues
import de.florianmichael.viasnapshot.protocol.protocol1_16to20w14infinite.provider.PlayerAbilitiesProvider

class FabricPlayerAbilitiesProvider : PlayerAbilitiesProvider() {

    override fun getFlySpeed(): Float {
        if (!ProtocolHackValues.emulateWrongPlayerAbilities.value) return super.getFlySpeed()
        return mc.player?.abilities!!.flySpeed
    }
    override fun getWalkSpeed(): Float {
        if (!ProtocolHackValues.emulateWrongPlayerAbilities.value) return super.getWalkSpeed()
        return mc.player?.abilities!!.walkSpeed
    }
}
