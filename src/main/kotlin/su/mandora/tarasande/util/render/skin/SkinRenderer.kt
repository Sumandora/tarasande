package su.mandora.tarasande.util.render.skin

import com.mojang.authlib.GameProfile
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.PlayerSkinDrawer
import net.minecraft.client.network.PlayerListEntry
import su.mandora.tarasande.injection.accessor.IPlayerSkinProvider
import java.util.*

class SkinRenderer(val uuid: UUID?, val name: String) {
    private val playerListHud = PlayerListEntry(GameProfile(uuid, name), false)

    init {
        (MinecraftClient.getInstance().skinProvider as IPlayerSkinProvider).tarasande_disableSessionCheckOnce()
        playerListHud.skinTexture // force-load
    }

    fun draw(context: DrawContext, x: Int, y: Int, size: Int) {
        PlayerSkinDrawer.draw(context, playerListHud.skinTexture, x, y, size)
    }
}
