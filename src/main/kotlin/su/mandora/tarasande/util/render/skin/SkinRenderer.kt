package su.mandora.tarasande.util.render.skin

import com.mojang.authlib.GameProfile
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.PlayerSkinDrawer
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.injection.accessor.IPlayerSkinProvider
import java.util.*

class SkinRenderer(val uuid: UUID?, val name: String) {
    private val playerListHud = PlayerListEntry(GameProfile(uuid, name), false)

    init {
        (MinecraftClient.getInstance().skinProvider as IPlayerSkinProvider).tarasande_disableSessionCheckOnce()
        playerListHud.skinTexture // force-load
    }

    fun draw(matrices: MatrixStack, x: Int, y: Int, size: Int) {
        RenderSystem.setShaderTexture(0, playerListHud.skinTexture)
        PlayerSkinDrawer.draw(matrices, x, y, size)
    }
}
