package net.tarasandedevelopment.tarasande_protocol_hack.provider.vialegacy

import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.model.GameProfile
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.providers.GameProfileFetcher
import java.util.*

class FabricGameProfileFetcher : GameProfileFetcher() {

    override fun loadMojangUUID(playerName: String?): UUID {
        return UUID.randomUUID()
    }

    override fun loadGameProfile(uuid: UUID?): GameProfile {
        return GameProfile("", UUID.randomUUID())
    }
}