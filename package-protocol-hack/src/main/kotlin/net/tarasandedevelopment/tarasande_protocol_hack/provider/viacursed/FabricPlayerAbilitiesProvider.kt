package net.tarasandedevelopment.tarasande_protocol_hack.provider.viacursed

import de.florianmichael.viacursed.protocol.protocol1_16to20w14infinite.provider.PlayerAbilitiesProvider
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.ProtocolHackValues

class FabricPlayerAbilitiesProvider : PlayerAbilitiesProvider() {

    override fun getFlySpeed(): Float {
        if (ProtocolHackValues.emulateWrongPlayerAbilities.value) return super.getFlySpeed()
        return MinecraftClient.getInstance().player?.abilities!!.flySpeed
    }
    override fun getWalkSpeed(): Float {
        if (ProtocolHackValues.emulateWrongPlayerAbilities.value) return super.getWalkSpeed()
        return MinecraftClient.getInstance().player?.abilities!!.walkSpeed
    }
}
