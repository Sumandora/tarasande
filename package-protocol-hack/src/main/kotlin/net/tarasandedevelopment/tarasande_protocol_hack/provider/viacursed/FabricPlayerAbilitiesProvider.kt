package net.tarasandedevelopment.tarasande_protocol_hack.provider.viacursed

import de.florianmichael.viacursed.protocol.snapshot.protocol1_16to20w14infinite.provider.PlayerAbilitiesProvider
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.ProtocolHackValues

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
