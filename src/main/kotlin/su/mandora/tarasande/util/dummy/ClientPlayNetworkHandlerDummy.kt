package su.mandora.tarasande.util.dummy

import com.mojang.authlib.GameProfile
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.packet.Packet
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.resource.featuretoggle.FeatureSet
import su.mandora.tarasande.mc
import su.mandora.tarasande.util.unsafe.UnsafeProvider

class ClientPlayNetworkHandlerDummy private constructor() : ClientPlayNetworkHandler(null, null, null, null, null, null) {

    override fun sendPacket(packet: Packet<*>?) {
        // Don't send packets
    }

    override fun getProfile(): GameProfile {
        return mc.session.profile
    }

    override fun getEnabledFeatures(): FeatureSet {
        return FeatureFlags.DEFAULT_ENABLED_FEATURES
    }

    companion object {
        fun create() = UnsafeProvider.unsafe.allocateInstance(ClientPlayNetworkHandlerDummy::class.java) as ClientPlayNetworkHandlerDummy
    }

}