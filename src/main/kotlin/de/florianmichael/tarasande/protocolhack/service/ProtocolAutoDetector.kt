/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 12.02.22, 21:12
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */
package de.florianmichael.tarasande.protocolhack.service

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.viaprotocolhack.ViaProtocolHack
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.util.concurrent.Future
import net.minecraft.network.*
import net.minecraft.network.listener.ClientQueryPacketListener
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket
import net.minecraft.server.ServerMetadata
import net.minecraft.text.Text
import java.net.InetSocketAddress
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.logging.Level

object ProtocolAutoDetector {

    private val SERVER_VER = CacheBuilder.newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build(CacheLoader.from { address: InetSocketAddress ->
            val future: CompletableFuture<ProtocolVersion> = CompletableFuture<ProtocolVersion>()
            try {
                val clientConnection = ClientConnection(NetworkSide.CLIENTBOUND)
                val ch: ChannelFuture = Bootstrap()
                    .group(ClientConnection.CLIENT_IO_GROUP.get())
                    .channel(NioSocketChannel::class.java)
                    .handler(object : ChannelInitializer<Channel>() {
                        override fun initChannel(channel: Channel) {
                            try {
                                channel.config().setOption(ChannelOption.TCP_NODELAY, true)
                                channel.config().setOption(ChannelOption.IP_TOS, 0x18) // Stolen from Velocity, low delay, high reliability
                            } catch (ignored: ChannelException) {
                            }
                            channel.pipeline()
                                .addLast("timeout", ReadTimeoutHandler(30))
                                .addLast("splitter", SplitterHandler())
                                .addLast("decoder", DecoderHandler(NetworkSide.CLIENTBOUND))
                                .addLast("prepender", SizePrepender())
                                .addLast("encoder", PacketEncoder(NetworkSide.SERVERBOUND))
                                .addLast("packet_handler", clientConnection)
                        }
                    })
                    .connect(address)
                ch.addListener { future1: Future<in Void?> ->
                    if (!future1.isSuccess) {
                        future.completeExceptionally(future1.cause())
                    } else {
                        ch.channel().eventLoop().execute {
                            // needs to execute after channel init
                            clientConnection.packetListener = object : ClientQueryPacketListener {
                                override fun onResponse(packet: QueryResponseS2CPacket) {
                                    val meta: ServerMetadata = packet.serverMetadata
                                    var version: ServerMetadata.Version? = null
                                    if (meta.version.also {
                                            if (it != null) {
                                                version = it
                                            }
                                        } != null) {
                                        val ver = ProtocolVersion.getProtocol(version!!.protocolVersion)
                                        future.complete(ver)
                                        ViaProtocolHack.instance().logger().info("Auto-detected $ver for $address")
                                    } else {
                                        future.completeExceptionally(IllegalArgumentException("Null version in query response"))
                                    }
                                    clientConnection.disconnect(Text.empty())
                                }

                                override fun onPong(packet: QueryPongS2CPacket) {
                                    clientConnection.disconnect(Text.literal("Pong not requested!"))
                                }

                                override fun onDisconnected(reason: Text) {
                                    future.completeExceptionally(IllegalStateException(reason.string))
                                }

                                override fun getConnection(): ClientConnection {
                                    return clientConnection
                                }
                            }
                            clientConnection.send(HandshakeC2SPacket(address.hostString, address.port, NetworkState.STATUS))
                            clientConnection.send(QueryRequestC2SPacket())
                        }
                    }
                }
            } catch (throwable: Throwable) {
                future.completeExceptionally(throwable)
            }
            future
        })

    fun detectVersion(address: InetSocketAddress): CompletableFuture<ProtocolVersion> {
        return try {
            SERVER_VER.get(address)
        } catch (e: ExecutionException) {
            ViaProtocolHack.instance().logger().log(Level.WARNING, "Protocol auto detector error: ", e)
            CompletableFuture.completedFuture(null)
        }
    }
}