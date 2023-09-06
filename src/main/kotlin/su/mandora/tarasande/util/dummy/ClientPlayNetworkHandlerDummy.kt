package su.mandora.tarasande.util.dummy

import com.mojang.authlib.GameProfile
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.packet.Packet
import net.minecraft.resource.featuretoggle.FeatureSet
import su.mandora.tarasande.mc
import su.mandora.tarasande.unsafe

class ClientPlayNetworkHandlerDummy private constructor() : ClientPlayNetworkHandler(null, null, null, null, null, null) {

    override fun sendPacket(packet: Packet<*>?) {
        // Don't send packets
    }

    override fun getProfile(): GameProfile? {
        return mc.networkHandler?.profile
    }

    override fun getEnabledFeatures(): FeatureSet? {
        return mc.networkHandler?.enabledFeatures
    }

    companion object {
        fun create() = unsafe.allocateInstance(ClientPlayNetworkHandlerDummy::class.java) as ClientPlayNetworkHandlerDummy
    }

}