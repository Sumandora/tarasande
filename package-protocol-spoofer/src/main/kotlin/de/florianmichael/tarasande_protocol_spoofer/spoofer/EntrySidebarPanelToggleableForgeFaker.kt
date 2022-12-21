package de.florianmichael.tarasande_protocol_spoofer.spoofer

import de.florianmichael.tarasande_protocol_spoofer.accessor.IServerInfo
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.ForgeCreator
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.IForgeNetClientHandler
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.IForgePayload
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.modern.ModernForgePayload
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.ui.ScreenBetterSlotListForgeInformation
import io.netty.buffer.Unpooled
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventConnectServer
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventRenderMultiplayerEntry
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.EntrySidebarPanelToggleable
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.ManagerEntrySidebarPanel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher
import java.net.InetSocketAddress

class EntrySidebarPanelToggleableForgeFaker(sidebar: ManagerEntrySidebarPanel) : EntrySidebarPanelToggleable(sidebar, "Forge Faker", "Spoofer") {

    val forgeInfoTracker = HashMap<InetSocketAddress, IForgePayload>()
    private var currentHandler: IForgeNetClientHandler? = null

    val useFML1Cache = ValueBoolean(this, "Use FML1 cache", true)
    val autoDetectFmlHandlerByViaVersion = ValueBoolean(this, "Auto detect fml handler by ViaVersion", true)
    val fmlHandler = object : ValueMode(this, "FML Handler", false, "FML1", "Modern v2", "Modern v3", "Modern v4") {
        override fun isEnabled() = !autoDetectFmlHandlerByViaVersion.value
    }
    private val alwaysShowInformation = ValueBoolean(this, "Always show information", false)

    init {
        EventDispatcher.apply {
            add(EventConnectServer::class.java) {
                if (state.value) {
                    currentHandler = ForgeCreator.createNetHandler(it.connection)
                }
            }

            add(EventPacket::class.java, 1) {
                if (!state.value || currentHandler == null) return@add

                if (it.type == EventPacket.Type.SEND) {
                    if (it.packet is HandshakeC2SPacket) {
                        (it.packet as HandshakeC2SPacket).address += currentHandler!!.handshakeMark()
                    }

                    if (it.packet is CustomPayloadC2SPacket) {
                        if ((it.packet as CustomPayloadC2SPacket).channel == CustomPayloadC2SPacket.BRAND) {
                            (it.packet as CustomPayloadC2SPacket).data = PacketByteBuf(Unpooled.buffer()).writeString("fml,forge")
                        }
                    }
                }

                if (it.type == EventPacket.Type.RECEIVE) {
                    if (currentHandler!!.onIncomingPacket(it.packet!!)) {
                        it.cancelled = true
                    }
                }
            }

            add(EventRenderMultiplayerEntry::class.java) {
                if (state.value || alwaysShowInformation.value) {
                    (it.server as IServerInfo).tarasande_getForgePayload()?.also { payload ->
                        val fontHeight = FontWrapper.fontHeight()

                        val yPos = (it.entryHeight / 2F) - fontHeight / 2
                        val text = FontWrapper.trimToWidth("Forge/FML Server", it.x)
                        val endWidth = FontWrapper.getWidth(text) + 4

                        FontWrapper.textShadow(it.matrices, text, (-endWidth).toFloat(), yPos, TarasandeMain.clientValues().accentColor.getColor().rgb, offset = 0.5F)

                        if (RenderUtil.isHovered(it.mouseX.toDouble(), it.mouseY.toDouble(), it.x - endWidth.toDouble(), it.y + yPos.toDouble(), it.x - 4.0, it.y + yPos + fontHeight.toDouble())) {
                            val tooltip = ArrayList<Text>()

                            if (payload.installedMods().isNotEmpty()) {
                                tooltip.add(Text.of("Left mouse for Mods: " + payload.installedMods().size))
                            } else {
                                tooltip.add(Text.of("No mods available?"))
                            }

                            if (payload is ModernForgePayload) {
                                tooltip.add(Text.of("FML Network Version: " + payload.fmlNetworkVersion))
                                tooltip.add(Text.of("Right mouse for Channels: " + payload.channels.size))

                                if (GLFW.glfwGetMouseButton(MinecraftClient.getInstance().window.handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS) {
                                    MinecraftClient.getInstance().setScreen(ScreenBetterSlotListForgeInformation(MinecraftClient.getInstance().currentScreen!!, it.server.address + " (Channels: " + payload.channels.size + ")", ScreenBetterSlotListForgeInformation.Type.CHANNEL_LIST, payload))
                                }
                            }

                            it.multiplayerScreen.setTooltip(tooltip.map { line -> line.asOrderedText() })

                            if (payload.installedMods().isNotEmpty() && GLFW.glfwGetMouseButton(MinecraftClient.getInstance().window.handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
                                MinecraftClient.getInstance().setScreen(ScreenBetterSlotListForgeInformation(MinecraftClient.getInstance().currentScreen!!, it.server.address + " (Mods: " + payload.installedMods().size + ")", ScreenBetterSlotListForgeInformation.Type.MOD_LIST, payload))
                            }
                        }
                    }
                }
            }
        }
    }
}
