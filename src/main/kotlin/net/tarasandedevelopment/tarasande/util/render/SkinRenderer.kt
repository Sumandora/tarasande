package net.tarasandedevelopment.tarasande.util.render

import com.mojang.authlib.GameProfile
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.util.DefaultSkinHelper
import net.tarasandedevelopment.tarasande.injection.accessor.IPlayerSkinProvider
import net.tarasandedevelopment.tarasande.util.dummy.AbstractClientPlayerEntityDummy

class SkinRenderer(val profile: GameProfile) {

    var player: AbstractClientPlayerEntity? = null

    init {
        // TODO do we need to copy this?
        val next = GameProfile(profile.id, profile.name)

        var skinImage = DefaultSkinHelper.getTexture(next.id)
        var capeImage = skinImage

        (MinecraftClient.getInstance().skinProvider as IPlayerSkinProvider).tarasande_disableSessionCheckOnce()

        MinecraftClient.getInstance().skinProvider.loadSkin(next, { type, id, _ ->
            when (type) {
                MinecraftProfileTexture.Type.SKIN -> {
                    skinImage = id
                }

                MinecraftProfileTexture.Type.CAPE -> {
                    capeImage = id
                }

                else -> {}
            }

            player = AbstractClientPlayerEntityDummy(next, skinImage, capeImage)
        }, true)
    }
}
