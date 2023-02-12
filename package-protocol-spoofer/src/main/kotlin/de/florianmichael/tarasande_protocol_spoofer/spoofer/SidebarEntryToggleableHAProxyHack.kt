package de.florianmichael.tarasande_protocol_spoofer.spoofer

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.haproxy.HAProxyCommand
import io.netty.handler.codec.haproxy.HAProxyMessage
import io.netty.handler.codec.haproxy.HAProxyProtocolVersion
import io.netty.handler.codec.haproxy.HAProxyProxiedProtocol
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntryToggleable
import java.net.InetSocketAddress

class SidebarEntryToggleableHAProxyHack : SidebarEntryToggleable("HA-Proxy hack", "Spoofer") {

    val modifyIP = ValueBoolean(this, "Modify ip", true)
    val ip = ValueText(this, "IP", "127.0.0.1", isEnabled = { modifyIP.value })

    val modifyPort = ValueBoolean(this, "Modify port", false)
    val port = ValueText(this, "Port", "25565", isEnabled = { modifyPort.value })

    val protocolVersion = ValueMode(this, "Protocol version", false, *HAProxyProtocolVersion.values().map { it.name + "(0x" + "%x".format(it.byteValue()) + ")" }.toTypedArray())
    val tcpVersion = ValueMode(this, "TCP version", false, *HAProxyProxiedProtocol.values().toList().subList(1, 3).map { it.name + "(0x" + "%x".format(it.byteValue()) + ")" }.toTypedArray())

    fun createHandler() = object : ChannelInboundHandlerAdapter() {
        override fun channelActive(ctx: ChannelHandlerContext) {
            val localAddress = ctx.channel().localAddress() as InetSocketAddress
            val remoteAddress = ctx.channel().remoteAddress() as InetSocketAddress

            var sourceIP = localAddress.address.hostAddress
            var sourcePort = localAddress.port

            if (modifyIP.value) {
                sourceIP = ip.value
            }

            if (modifyPort.value) {
                sourcePort = port.value.toIntOrNull() ?: run { // If the port wasn't valid, disconnect the user immediately
                    ctx.disconnect().get()
                    return
                }
            }

            try {
                val payload = HAProxyMessage(
                    HAProxyProtocolVersion.values()[protocolVersion.values.indexOf(protocolVersion.getSelected())],
                    HAProxyCommand.PROXY,
                    HAProxyProxiedProtocol.values()[tcpVersion.values.indexOf(tcpVersion.getSelected()) + 1],
                    sourceIP,
                    remoteAddress.address.hostAddress,
                    sourcePort,
                    remoteAddress.port
                )

                ctx.writeAndFlush(payload)
            } catch (t: Throwable) {
                t.printStackTrace()
                ctx.disconnect()
            }
        }
    }
}