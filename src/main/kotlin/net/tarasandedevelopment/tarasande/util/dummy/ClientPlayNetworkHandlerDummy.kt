package net.tarasandedevelopment.tarasande.util.dummy

import com.mojang.authlib.GameProfile
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.Packet
import net.tarasandedevelopment.tarasande.util.unsafe.UnsafeProvider

class ClientPlayNetworkHandlerDummy : ClientPlayNetworkHandler(null, null, null, null, null) {

    init {
        error("Don't call the ClientPlayNetworkHandlerDummy constructor, use create() instead")
    }

    override fun sendPacket(packet: Packet<*>?) {
        // Don't send packets
    }

    override fun getProfile(): GameProfile {
        return MinecraftClient.getInstance().session.profile
    }

    companion object {
        // Don't actually create an instance to prevent memory leaks
        fun create() = UnsafeProvider.unsafe.allocateInstance(ClientPlayNetworkHandlerDummy::class.java) as ClientPlayNetworkHandlerDummy
    }

}