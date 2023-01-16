package de.florianmichael.tarasande_protocol_spoofer.spoofer

import de.florianmichael.tarasande_protocol_spoofer.TarasandeProtocolSpoofer
import de.florianmichael.tarasande_protocol_spoofer.viaversion.ViaVersionUtil
import io.netty.buffer.Unpooled
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import net.minecraft.text.LiteralTextContent
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntryToggleable
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat
import su.mandora.event.EventDispatcher
import java.nio.charset.StandardCharsets

class SidebarEntryToggleableTeslaClientFaker : SidebarEntryToggleable("Tesla client faker", "Spoofer") {

    private val username = ValueText(this, "Username", "") // Sumandora
    private val password = ValueText(this, "Password", "") // helloworld
    private val teslaBuild = ValueText(this, "Tesla build", "7351A105")
    private val fakeWeCui = ValueBoolean(this, "Fake we-cui", true)
    private val weCuiVersion = ValueText(this, "We-cui version", "v|4")
    //http://teslacraft.org/launcher/TeslaCraft.jar

    init {
        EventDispatcher.add(EventPacket::class.java) { event ->
            if(!enabled.value) return@add

            when (event.type) {
                EventPacket.Type.SEND -> {
                    when(event.packet) {
                        is CustomPayloadC2SPacket -> {
                            val packet = event.packet as CustomPayloadC2SPacket
                            if(packet.channel.toString() == "minecraft:register") {
                                packet.data = PacketByteBuf(Unpooled.buffer()).writeByteArray("WECUI\u0000tesla:client".toByteArray(StandardCharsets.US_ASCII))
                            }
                            if (TarasandeProtocolSpoofer.tarasandeProtocolHackLoaded) {
                                if (ViaVersionUtil.spoofTeslaClientCustomPayload("REGISTER", "WECUI\u0000tesla:client".toByteArray(StandardCharsets.US_ASCII))) {
                                    event.cancelled = true
                                }
                            }
                            if (fakeWeCui.value) {
                                val weCuiPayload = CustomPayloadC2SPacket(Identifier("WECUI"), PacketByteBuf(Unpooled.buffer()).writeString(weCuiVersion.value))

                                if (TarasandeProtocolSpoofer.tarasandeProtocolHackLoaded) {
                                    if (!ViaVersionUtil.spoofTeslaClientCustomPayload("WECUI", weCuiVersion.value.toByteArray(StandardCharsets.UTF_8)))
                                        MinecraftClient.getInstance().player?.networkHandler?.sendPacket(weCuiPayload)
                                } else
                                    MinecraftClient.getInstance().player?.networkHandler?.sendPacket(weCuiPayload)
                            }
                        }

                        is HandshakeC2SPacket -> {
                            // This is only executed, if we join teslacraft.... who cares about such checks
                            (event.packet as HandshakeC2SPacket).address = "teslacraft.org"
                        }

                        is LoginHelloC2SPacket -> {
                            var data: String = username.value
                            //@formatter:off
                            // These 2 lines are only executed, if we join teslacraft.... who cares about such checks
                            data += "\u0000Tesla\u0000" + teslaBuild.value
                            data += '\u0000' + password.value
                            //@formatter:on
                            (event.packet as LoginHelloC2SPacket).name = data
                        }
                    }
                }
                EventPacket.Type.RECEIVE -> {
                    if (event.packet is CustomPayloadS2CPacket) {
                        val packet = (event.packet as CustomPayloadS2CPacket)
                        if(packet.channel.toString() == "tesla:client") {
                            CustomChat.printChatMessage(MutableText.of(LiteralTextContent(("Tesla Client Request: " + packet.data.writtenBytes.decodeToString()).also { println(it) })))
                        }
                    }
                }
            }
        }
    }
}
