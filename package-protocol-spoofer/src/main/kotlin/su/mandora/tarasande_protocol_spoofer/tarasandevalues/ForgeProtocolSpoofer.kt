package su.mandora.tarasande_protocol_spoofer.tarasandevalues

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventConnectServer
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventRenderMultiplayerEntry
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande_protocol_spoofer.injection.accessor.IServerInfo
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.ForgeCreator
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.IForgeNetClientHandler
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.IForgePayload
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.modern.ModernForgePayload
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.ui.ScreenBetterSlotListForgeInformation
import java.net.InetSocketAddress

object ForgeProtocolSpoofer {

    val forgeInfoTracker = HashMap<InetSocketAddress, IForgePayload>()
    private var currentHandler: IForgeNetClientHandler? = null

    private val enabled = ValueBoolean(this, "Enabled", false)
    val useFML1Cache = ValueBoolean(this, "Use FML1 cache", true)
    private val alwaysShowInformation = ValueBoolean(this, "Always show information", false)

    init {
        EventDispatcher.apply {
            add(EventConnectServer::class.java) {
                if (enabled.value) {
                    currentHandler = ForgeCreator.createNetHandler(it.connection)
                }
            }

            add(EventPacket::class.java, 1) {
                if (!enabled.value || currentHandler == null) return@add

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
                if (enabled.value || alwaysShowInformation.value) {
                    (it.server as IServerInfo).tarasande_getForgePayload()?.also { payload ->
                        val fontHeight = FontWrapper.fontHeight()

                        val yPos = (it.entryHeight / 2F) - fontHeight / 2
                        val text = FontWrapper.trimToWidth("Forge/FML Server", it.x)
                        val endWidth = FontWrapper.getWidth(text) + 4

                        FontWrapper.textShadow(it.matrices, text, (-endWidth).toFloat(), yPos, TarasandeValues.accentColor.getColor().rgb, offset = 0.5F)

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

                                if (GLFW.glfwGetMouseButton(mc.window.handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS) {
                                    mc.setScreen(ScreenBetterSlotListForgeInformation(it.server.address + " (Channels: " + payload.channels.size + ")", mc.currentScreen!!, ScreenBetterSlotListForgeInformation.Type.CHANNEL_LIST, payload))
                                }
                            }

                            it.multiplayerScreen.setTooltip(tooltip.map { line -> line.asOrderedText() })

                            if (payload.installedMods().isNotEmpty() && GLFW.glfwGetMouseButton(mc.window.handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
                                mc.setScreen(ScreenBetterSlotListForgeInformation(it.server.address + " (Mods: " + payload.installedMods().size + ")", mc.currentScreen!!, ScreenBetterSlotListForgeInformation.Type.MOD_LIST, payload))
                            }
                        }
                    }
                }
            }
        }
    }
}
