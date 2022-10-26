package net.tarasandedevelopment.tarasande.util.dummies

import com.mojang.authlib.GameProfile
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.util.DefaultSkinHelper
import net.minecraft.util.Identifier

class AbstractClientPlayerEntityDummy(val profile: GameProfile, private val skinImage: Identifier, private val capeImage: Identifier) : AbstractClientPlayerEntity(ClientWorldDummy(), profile, null) {

    override fun isSpectator() = false
    override fun isCreative() = false
    override fun hasSkinTexture() = true
    override fun canRenderCapeTexture() = true
    override fun canRenderElytraTexture() = false
    override fun getSkinTexture() = skinImage
    override fun getCapeTexture() = capeImage
    override fun getModel() = DefaultSkinHelper.getModel(profile.id)!!
}
