package net.tarasandedevelopment.tarasande.screen.widget.serverpinger

import com.google.common.hash.Hashing
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.SharedConstants
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.texture.MissingSprite
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.tarasandedevelopment.tarasande.event.EventRenderMultiplayerEntry
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import org.apache.commons.lang3.Validate
import su.mandora.event.EventDispatcher
import java.util.*

open class WidgetServerInformation : Panel("Server Information", 305.0, 40.0, background = true, scissor = false) {

    var server: ServerInfo? = null
    private var iconTextureId: Identifier? = null
    private var iconUri: String? = null
    private var icon: NativeImageBackedTexture? = null

    private fun isNewIconValid(newIconUri: String?): Boolean {
        if (newIconUri == null) {
            MinecraftClient.getInstance().textureManager.destroyTexture(this.iconTextureId)
            if (this.icon != null && this.icon!!.image != null) {
                this.icon!!.image!!.close()
            }
            this.icon = null
        } else {
            try {
                val nativeImage = NativeImage.read(newIconUri)
                Validate.validState(nativeImage.width == 64, "Must be 64 pixels wide")
                Validate.validState(nativeImage.height == 64, "Must be 64 pixels high")
                if (this.icon == null) {
                    this.icon = NativeImageBackedTexture(nativeImage)
                } else {
                    this.icon!!.image = nativeImage
                    this.icon!!.upload()
                }
                MinecraftClient.getInstance().textureManager.registerTexture(this.iconTextureId, this.icon)
            } catch (var3: Throwable) {
                return false
            }
        }
        return true
    }

    fun recreateIcon(address: String) {
        if (iconTextureId == null) {
            iconTextureId = Identifier("servers/" + Hashing.sha1().hashUnencodedChars(address) + "/icon")
            val abstractTexture = MinecraftClient.getInstance().textureManager.getOrDefault(iconTextureId, MissingSprite.getMissingSpriteTexture())

            if (abstractTexture != MissingSprite.getMissingSpriteTexture() && abstractTexture is NativeImageBackedTexture) {
                icon = abstractTexture
            }
        }
    }

    override fun renderTitleBar(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderTitleBar(matrices, mouseX, mouseY, delta)

        server?.also {
            FontWrapper.textShadow(matrices, it.name, x.toFloat() + 32, y.toFloat() + titleBarHeight + 1)

            val lines = FontWrapper.wrapLines(it.label, (panelWidth - 34).toInt())
            lines.forEachIndexed { index, text ->
                if (index < 2) {
                    FontWrapper.text(matrices, text, (x + 32).toFloat(), y.toFloat() + titleBarHeight + 12 + 9 * index)
                }
            }

            val correctVersion = it.protocolVersion == SharedConstants.getProtocolVersion()

            val playerLabel = if (correctVersion) if (it.playerCountLabel != null) it.playerCountLabel else Text.empty() else it.version.copy().formatted(Formatting.RED)
            FontWrapper.text(matrices, playerLabel.asOrderedText(), (x + panelWidth - FontWrapper.getWidth(playerLabel.asOrderedText()) - 12).toFloat(), y.toFloat() + titleBarHeight + 1, 8421504)

            var isSuccessPinged = true
            var graphCount = 0
            var playerListLabel = MultiplayerServerListWidget.INCOMPATIBLE_TEXT
            var playerList = it.playerListSummary

            if (!correctVersion) {
                graphCount = 5
            } else if (it.ping != -2L) {
                if (it.ping < 0L) {
                    graphCount = if (it.ping < 0L) 5 else if (it.ping < 150L) 0 else if (it.ping < 300L) 1 else if (it.ping < 600L) 2 else if (it.ping < 1000L) 3 else 4
                }

                if (it.ping < 0L) {
                    playerListLabel = MultiplayerServerListWidget.NO_CONNECTION_TEXT
                    playerList = Collections.emptyList()
                } else {
                    playerListLabel = Text.translatable("multiplayer.status.ping", it.ping)
                    playerList = it.playerListSummary
                }
            } else {
                isSuccessPinged = false
                graphCount = ((Util.getMeasuringTimeMs() / 100L) and 7L).toInt()
                if (graphCount > 4) {
                    graphCount = 8 - graphCount
                }

                playerListLabel = MultiplayerServerListWidget.PINGING_TEXT
                playerList = Collections.emptyList()
            }

            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE)
            DrawableHelper.drawTexture(matrices, (x + panelWidth - 11).toInt(), y.toInt() + titleBarHeight, if (isSuccessPinged) 0F else 10F, (176 + graphCount * 8).toFloat(), 10, 8, 256, 256)

            val string = server!!.icon
            if (string != iconUri) {
                if (isNewIconValid(string)) {
                    iconUri = string
                } else {
                    server!!.icon = null
                }
            }

            RenderSystem.setShaderTexture(0, if (this.icon != null) this.iconTextureId else MultiplayerServerListWidget.UNKNOWN_SERVER_TEXTURE)
            RenderSystem.enableBlend()
            DrawableHelper.drawTexture(matrices, x.toInt(), y.toInt() + titleBarHeight, 0.0f, 0.0f, 32, 32, 32, 32)
            RenderSystem.disableBlend()

            val m = (mouseX - x).toInt()
            val n = (mouseY - (y + titleBarHeight)).toInt()
            if (m >= panelWidth - 15 && m <= panelWidth - 5 && n >= 0 && n <= 8 && playerListLabel != null) {
                MinecraftClient.getInstance().currentScreen?.renderTooltip(matrices, playerListLabel, mouseX, mouseY)
            } else if (m >= panelWidth - FontWrapper.getWidth(playerLabel.asOrderedText()) - 15 - 2 && m <= panelWidth - 15 - 2 && n >= 0 && n <= 8 && playerList != null) {
                MinecraftClient.getInstance().currentScreen?.renderTooltip(matrices, playerList, mouseX, mouseY)
            }

            EventDispatcher.call(EventRenderMultiplayerEntry(matrices!!, x.toInt(), y.toInt(), panelWidth.toInt(), panelHeight.toInt(), mouseX, mouseY, it))
        }
    }
}
