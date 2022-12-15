package net.tarasandedevelopment.tarasande_protocol_hack.provider.vialegacy

import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.provider.UUIDProvider
import net.minecraft.client.MinecraftClient

class FabricUUIDProvider : UUIDProvider() {

    override fun getPlayerUuid() = MinecraftClient.getInstance().session.uuidOrNull
}
