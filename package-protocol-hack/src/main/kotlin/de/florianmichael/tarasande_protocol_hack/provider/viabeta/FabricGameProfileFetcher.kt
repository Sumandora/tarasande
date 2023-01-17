package de.florianmichael.tarasande_protocol_hack.provider.viabeta

import com.mojang.authlib.Agent
import com.mojang.authlib.HttpAuthenticationService
import com.mojang.authlib.ProfileLookupCallback
import com.mojang.authlib.yggdrasil.ProfileNotFoundException
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.provider.GameProfileFetcher
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model.GameProfile
import java.net.Proxy
import java.util.*
import java.util.concurrent.CompletableFuture

class FabricGameProfileFetcher : GameProfileFetcher() {

    private val authenticationService: HttpAuthenticationService = YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString())
    private val sessionService = authenticationService.createMinecraftSessionService()
    private val service = authenticationService.createProfileRepository()

    override fun loadMojangUUID(playerName: String): UUID {
        val future = CompletableFuture<com.mojang.authlib.GameProfile>()
        service.findProfilesByNames(arrayOf<String>(playerName), Agent.MINECRAFT, object : ProfileLookupCallback {
            override fun onProfileLookupSucceeded(gameProfile: com.mojang.authlib.GameProfile) {
                future.complete(gameProfile)
            }

            override fun onProfileLookupFailed(gameProfile: com.mojang.authlib.GameProfile, e: Exception) {
                future.completeExceptionally(e)
            }
        })
        if (!future.isDone) {
            future.completeExceptionally(ProfileNotFoundException())
        }
        return future.get().id
    }

    override fun loadGameProfile(uuid: UUID?): GameProfile {
        val inProfile = com.mojang.authlib.GameProfile(uuid, null)
        val mojangProfile: com.mojang.authlib.GameProfile = sessionService.fillProfileProperties(inProfile, true)
        if (mojangProfile == inProfile) throw ProfileNotFoundException()

        val gameProfile = GameProfile(mojangProfile.name, mojangProfile.id)
        for (entry in mojangProfile.properties.entries()) {
            gameProfile.addProperty(GameProfile.Property(entry.value.name, entry.value.value, entry.value.signature))
        }
        return gameProfile
    }
}
