package net.tarasandedevelopment.tarasande.util.render

import com.mojang.authlib.GameProfile
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.util.DefaultSkinHelper
import net.minecraft.util.dynamic.DynamicSerializableUuid
import net.tarasandedevelopment.tarasande.mixin.accessor.IPlayerSkinProvider
import net.tarasandedevelopment.tarasande.util.player.DummyPlayer

class SkinRenderer(val profile: GameProfile) {

    var player: AbstractClientPlayerEntity? = null

    init {
        val next = GameProfile(DynamicSerializableUuid.getUuidFromProfile(profile), profile.name)

        var skinImage = DefaultSkinHelper.getTexture(next.id)
        var capeImage = skinImage

        (MinecraftClient.getInstance().skinProvider as IPlayerSkinProvider).disableSessionCheckOnce()

        MinecraftClient.getInstance().skinProvider.loadSkin(next, { type, id, texture ->
            when (type) {
                MinecraftProfileTexture.Type.SKIN -> {
                    skinImage = id
                }

                MinecraftProfileTexture.Type.CAPE -> {
                    capeImage = id
                }

                else -> {}
            }

            player = DummyPlayer(next, skinImage, capeImage)
        }, true)
    }
}
