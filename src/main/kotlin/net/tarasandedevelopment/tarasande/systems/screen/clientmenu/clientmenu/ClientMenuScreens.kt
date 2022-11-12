package net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.haproxy.HAProxyCommand
import io.netty.handler.codec.haproxy.HAProxyMessage
import io.netty.handler.codec.haproxy.HAProxyProtocolVersion
import io.netty.handler.codec.haproxy.HAProxyProxiedProtocol
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.mixin.accessor.forgefaker.IServerInfo
import net.tarasandedevelopment.tarasande.protocolhack.platform.ProtocolHackValues
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.events.impl.EventPacket
import net.tarasandedevelopment.events.impl.EventRenderMultiplayerEntry
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.ElementCategory
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.ElementMenuScreen
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.ElementMenuToggle
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.accountmanager.ScreenBetterSlotListAccountManager
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.forgefaker.IForgeNetClientHandler
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.forgefaker.payload.IForgePayload
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.forgefaker.payload.modern.ModernForgePayload
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.forgefaker.ui.ScreenBetterSlotListForgeInformation
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.`package`.ScreenBetterSlotListPackages
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.protocol.ScreenBetterSlotListProtocolHack
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.proxy.ScreenBetterProxy
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import org.lwjgl.glfw.GLFW
import java.net.InetSocketAddress
import java.util.*

class ElementMenuScreenAccountManager : ElementMenuScreen("Account Manager", ElementCategory.GENERAL) {

    val screenBetterSlotListAccountManager = ScreenBetterSlotListAccountManager()

    override fun getScreen(): Screen {
        return this.screenBetterSlotListAccountManager.apply { prevScreen = MinecraftClient.getInstance().currentScreen }
    }
}

class ElementMenuScreenProxySystem : ElementMenuScreen("Proxy System", ElementCategory.GENERAL) {

    override fun getScreen(): Screen {
        return ScreenBetterProxy(MinecraftClient.getInstance().currentScreen)
    }
}

class ElementMenuScreenProtocolHack : ElementMenuScreen("Protocol Hack", ElementCategory.GENERAL) {

    private val screenBetterSlotListProtocolHack = ScreenBetterSlotListProtocolHack()

    init {
        TarasandeMain.get().valueSystem.getValues(ProtocolHackValues).forEach {
            it.owner = this
        }
    }

    override fun getScreen(): Screen {
        return this.screenBetterSlotListProtocolHack.apply { prevScreen = MinecraftClient.getInstance().currentScreen }
    }
}

class ElementMenuScreenPackages : ElementMenuScreen("Packages", ElementCategory.GENERAL) {

    private val screenBetterSlotListPackages = ScreenBetterSlotListPackages()

    override fun getScreen(): Screen {
        return this.screenBetterSlotListPackages.apply { prevScreen = MinecraftClient.getInstance().currentScreen }
    }

    override fun visible(): Boolean {
        return TarasandeMain.get().packageSystem.list.isNotEmpty()
    }
}

class ElementMenuToggleBungeeHack : ElementMenuToggle("Bungee Hack", ElementCategory.EXPLOITS) {

    private val endIP = ValueText(this, "End IP", "127.0.0.1")
    private val customUUID = ValueBoolean(this, "Custom UUID", false)
    private val uuid = object : ValueText(this, "UUID", UUID.randomUUID().toString()) {

        override fun isEnabled() = customUUID.value
    }

    private val zero = "\u0000"
    private fun stripID(input: String) = input.replace("-", "")

    init {
        TarasandeMain.get().eventSystem.add(EventPacket::class.java) { event ->
            if (event.type != EventPacket.Type.SEND) return@add
            if (event.packet !is HandshakeC2SPacket) return@add
            if (state) {
                var uuid = MinecraftClient.getInstance().session.uuid
                if (this.customUUID.value)
                    uuid = this.uuid.value

                event.packet.address += this.zero + this.endIP.value + this.zero + this.stripID(uuid)
            }
        }
    }

    override fun onToggle(state: Boolean) {
        // state check in event listener
    }
}

class ElementMenuToggleForgeFaker : ElementMenuToggle("Forge Faker", ElementCategory.EXPLOITS) {

    val forgeInfoTracker = HashMap<InetSocketAddress, IForgePayload>()
    var currentHandler: IForgeNetClientHandler? = null

    val useFML1Cache = ValueBoolean(this, "Use FML1 cache", true)

    init {
        TarasandeMain.get().eventSystem.also {
            it.add(EventPacket::class.java, 1) {
                if (!state || currentHandler == null) return@add

                if (it.type == EventPacket.Type.SEND) {
                    if (it.packet is HandshakeC2SPacket) {
                        it.packet.address += currentHandler!!.handshakeMark()
                    }

                    if (it.packet is CustomPayloadC2SPacket) {
                        if (it.packet.channel == CustomPayloadC2SPacket.BRAND) {
                            it.packet.data = PacketByteBuf(Unpooled.buffer()).writeString("fml,forge")
                        }
                    }
                }

                if (it.type == EventPacket.Type.RECEIVE) {
                    if (currentHandler!!.onIncomingPacket(it.packet!!)) {
                        it.cancelled = true
                    }
                }
            }

            it.add(EventRenderMultiplayerEntry::class.java) {
                (it.server as IServerInfo).tarasande_getForgePayload()?.also { payload ->
                    val fontHeight = FontWrapper.fontHeight()

                    val yPos = (it.entryHeight / 2F) - fontHeight / 2
                    val text = FontWrapper.trimToWidth("Forge/FML Server", it.x)
                    val endWidth = FontWrapper.getWidth(text) + 4

                    FontWrapper.textShadow(it.matrices, text, (-endWidth).toFloat(), yPos, TarasandeMain.get().clientValues.accentColor.getColor().rgb, offset = 0.5F)

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

                        MinecraftClient.getInstance().currentScreen?.renderTooltip(it.matrices, tooltip, it.mouseX - it.x, it.mouseY - it.y)

                        if (payload.installedMods().isNotEmpty() && GLFW.glfwGetMouseButton(MinecraftClient.getInstance().window.handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
                            MinecraftClient.getInstance().setScreen(ScreenBetterSlotListForgeInformation(MinecraftClient.getInstance().currentScreen!!, it.server.address + " (Mods: " + payload.installedMods().size + ")", ScreenBetterSlotListForgeInformation.Type.MOD_LIST, payload))
                        }
                    }
                }
            }
        }
    }

    override fun onToggle(state: Boolean) {
        // state check in event listener
    }
}

class ElementMenuToggleHAProxyHack : ElementMenuToggle("HA-Proxy Hack", ElementCategory.EXPLOITS) {

    val modifyIP = ValueBoolean(this, "Modify ip", true)
    val ip = object : ValueText(this, "IP", "1.3.3.7") {
        override fun isEnabled() = modifyIP.value
    }

    val modifyPort = ValueBoolean(this, "Modify port", false)
    val port = object : ValueNumber(this, "Port", 1.0, 25565.0, 65535.0, 1E-2) {
        override fun isEnabled() = modifyPort.value
    }

    val protocolVersion = ValueMode(this, "Protocol version", false, "V1 (16)", "V2 (32)")
    val tcpVersion = ValueMode(this, "TCP version", false, "4 (17)", "6 (33)")

    val handler = object : ChannelInboundHandlerAdapter() {
        override fun channelActive(ctx: ChannelHandlerContext) {
            val socketAddress = ctx.channel().remoteAddress() as InetSocketAddress
            var destinationIP = socketAddress.address.hostAddress

            if (modifyIP.value) {
                destinationIP = ip.value
            }

            var destinationPort = socketAddress.port
            if (modifyPort.value) {
                destinationPort = port.value.toInt()
            }

            val payload = HAProxyMessage(
                HAProxyProtocolVersion.values()[protocolVersion.settings.indexOf(protocolVersion.selected[0])],
                HAProxyCommand.PROXY,
                HAProxyProxiedProtocol.values()[tcpVersion.settings.indexOf(tcpVersion.selected[0]) + 1],
                destinationIP,
                socketAddress.address.hostAddress,
                destinationPort,
                socketAddress.port
            )

            ctx.writeAndFlush(payload)
        }
    }

    override fun onToggle(state: Boolean) {
        // state check in event listener
    }
}

// https://github.dev/QuiltMC/quilt-standard-libraries/tree/1.19/library/core/networking/src/main/java/org/quiltmc/qsl/networking
class ElementMenuToggleQuiltFaker : ElementMenuToggle("Quilt Faker", ElementCategory.EXPLOITS) {

    private val quiltHandshake = Identifier("registry_sync/handshake")

    init {
        TarasandeMain.get().eventSystem.add(EventPacket::class.java) {
            if (!state) return@add

            if (it.type == EventPacket.Type.RECEIVE && it.packet is CustomPayloadS2CPacket) {
                if (it.packet.channel == quiltHandshake) {
                    val data = it.packet.data

                    var count = data.readVarInt()
                    var highestSupported = -1

                    while (--count > 0) {
                        val version = data.readVarInt()
                        if (version > highestSupported) {
                            highestSupported = version
                        }
                    }

                    val buffer = PacketByteBuf(Unpooled.buffer())
                    buffer.writeVarInt(highestSupported)

                    MinecraftClient.getInstance().networkHandler!!.sendPacket(CustomPayloadC2SPacket(quiltHandshake, buffer))
                }
            }
        }
    }

    override fun onToggle(state: Boolean) {
        // state check in event listener
    }
}

class ElementMenuToggleClientBrandSpoofer : ElementMenuToggle("Client brand spoofer", ElementCategory.EXPLOITS) {

    private val clientBrand = ValueText(this, "Client brand", "vanilla")

    init {
        TarasandeMain.get().eventSystem.add(EventPacket::class.java) {
            if (!state) return@add

            if (it.type == EventPacket.Type.SEND && it.packet is CustomPayloadC2SPacket) {
                if (it.packet.channel == CustomPayloadC2SPacket.BRAND) {
                    it.packet.data = PacketByteBuf(Unpooled.buffer()).writeString(clientBrand.value)
                }
            }
        }
    }

    override fun onToggle(state: Boolean) {
        // state check in event listener
    }
}
