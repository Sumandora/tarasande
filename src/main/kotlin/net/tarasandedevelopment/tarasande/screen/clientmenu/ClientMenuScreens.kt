package net.tarasandedevelopment.tarasande.screen.clientmenu

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
import net.minecraft.util.Util
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenu
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenuScreen
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenuTitle
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.ElementMenuToggle
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.mixin.accessor.ICustomPayloadC2SPacket
import net.tarasandedevelopment.tarasande.mixin.accessor.IHandshakeC2SPacket
import net.tarasandedevelopment.tarasande.protocol.platform.ProtocolHackValues
import net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.ScreenBetterAccountManager
import net.tarasandedevelopment.tarasande.screen.clientmenu.addon.ScreenBetterAddons
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.IForgeNetClientHandler
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.IForgePayload
import net.tarasandedevelopment.tarasande.screen.clientmenu.protocol.ScreenBetterProtocolHack
import net.tarasandedevelopment.tarasande.screen.clientmenu.proxy.ScreenBetterProxy
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber
import net.tarasandedevelopment.tarasande.value.ValueText
import org.spongepowered.include.com.google.common.io.Files
import java.io.File
import java.net.InetSocketAddress
import java.util.*

class ElementMenuScreenAccountManager : ElementMenuScreen("Account Manager") {

    val screenBetterAccountManager = ScreenBetterAccountManager()

    override fun getScreen(): Screen {
        this.screenBetterAccountManager.prevScreen = MinecraftClient.getInstance().currentScreen
        return this.screenBetterAccountManager
    }
}

class ElementMenuScreenProxySystem : ElementMenuScreen("Proxy System") {

    override fun getScreen(): Screen {
        return ScreenBetterProxy(MinecraftClient.getInstance().currentScreen)
    }
}

class ElementMenuScreenProtocolHack : ElementMenuScreen("Protocol Hack") {

    private val screenBetterProtocolHack = ScreenBetterProtocolHack()

    init {
        TarasandeMain.get().managerValue.getValues(ProtocolHackValues).forEach {
            it.owner = this
        }
    }

    override fun getScreen(): Screen {
        screenBetterProtocolHack.prevScreen = MinecraftClient.getInstance().currentScreen
        return this.screenBetterProtocolHack
    }
}

class ElementMenuScreenAddons : ElementMenuScreen("Addons") {

    private val screenBetterAddons = ScreenBetterAddons()

    override fun getScreen(): Screen {
        screenBetterAddons.prevScreen = MinecraftClient.getInstance().currentScreen
        return this.screenBetterAddons
    }

    override fun visible(): Boolean {
        return TarasandeMain.get().managerAddon.list.isNotEmpty()
    }
}

class ElementMenuToggleBungeeHack : ElementMenuToggle("Bungee Hack") {

    private val endIP = ValueText(this, "End IP", "127.0.0.1")
    private val customUUID = ValueBoolean(this, "Custom UUID", false)
    private val uuid = object : ValueText(this, "UUID", UUID.randomUUID().toString()) {

        override fun isEnabled() = customUUID.value
    }

    private val zero = "\u0000"
    private fun stripID(input: String) = input.replace("-", "")

    init {
        TarasandeMain.get().eventDispatcher.add(EventPacket::class.java) { event ->
            if (event.type != EventPacket.Type.SEND) return@add
            if (event.packet !is HandshakeC2SPacket) return@add
            if(state) {
                var uuid = MinecraftClient.getInstance().session.uuid
                if (this.customUUID.value)
                    uuid = this.uuid.value

                (event.packet as IHandshakeC2SPacket).tarasande_extendAddress(this.zero + this.endIP.value + this.zero + this.stripID(uuid))
            }
        }
    }

    override fun onToggle(state: Boolean) {
        // state check in event listener
    }
}

class ElementMenuToggleForgeFaker : ElementMenuToggle("Forge Faker") {

    val forgeInfoTracker = HashMap<InetSocketAddress, IForgePayload>()
    var currentHandler: IForgeNetClientHandler? = null

    val useFML1Cache = ValueBoolean(this, "Use FML1 cache", true)

    init {
        TarasandeMain.get().eventDispatcher.add(EventPacket::class.java, 1) {
            if (!state || currentHandler == null) return@add

            if (it.type == EventPacket.Type.SEND) {
                if (it.packet is HandshakeC2SPacket) {
                    (it.packet as IHandshakeC2SPacket).tarasande_extendAddress(currentHandler!!.handshakeMark())
                }

                if (it.packet is CustomPayloadC2SPacket) {
                    if (it.packet.channel == CustomPayloadC2SPacket.BRAND) {
                        (it.packet as ICustomPayloadC2SPacket).setData(PacketByteBuf(Unpooled.buffer()).writeString("fml,forge"))
                    }
                }
            }

            if (it.type == EventPacket.Type.RECEIVE) {
                if (currentHandler!!.onIncomingPacket(it.packet!!)) {
                    it.cancelled = true
                }
            }
        }
    }

    override fun onToggle(state: Boolean) {
        // state check in event listener
    }
}

class ElementMenuToggleHAProxyHack : ElementMenuToggle("HA-Proxy Hack") {

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

class ElementMenuFritzBoxReconnect : ElementMenu("Fritz!Box Reconnect") {
    private val scriptName = "ip_changer_fritzbox.vbs"
    private val script = File(TarasandeMain.get().rootDirectory, scriptName)

    init {
        if (visible())
            if (!script.exists())
                Files.write(TarasandeMain::class.java.getResourceAsStream(scriptName)?.readAllBytes() ?: error("$scriptName not found"), script)
    }

    override fun onClick(mouseButton: Int) {
        val builder = ProcessBuilder("wscript", this.script.absolutePath)

        builder.directory(TarasandeMain.get().rootDirectory)
        builder.start()
    }

    open class SubTitle(private val parent: ElementMenuFritzBoxReconnect) : ElementMenuTitle("Special") {
        override fun visible() = parent.visible()
    }

    override fun visible() = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS
}
