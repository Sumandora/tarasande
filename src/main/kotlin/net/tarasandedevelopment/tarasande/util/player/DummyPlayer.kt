package net.tarasandedevelopment.tarasande.util.player

import com.mojang.authlib.GameProfile
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.util.DefaultSkinHelper
import net.minecraft.util.Identifier
import net.tarasandedevelopment.tarasande.util.world.DummyWorld

class DummyPlayer(val profile: GameProfile, private val skinImage: Identifier, private val capeImage: Identifier) : AbstractClientPlayerEntity(DummyWorld(), profile, null) {

    override fun isSpectator() = false
    override fun isCreative() = false
    override fun hasSkinTexture() = true
    override fun canRenderCapeTexture() = true
    override fun canRenderElytraTexture() = false
    override fun getSkinTexture() = skinImage
    override fun getCapeTexture() = capeImage
    override fun getModel() = DefaultSkinHelper.getModel(profile.id)!!
}
