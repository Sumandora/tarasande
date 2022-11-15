package net.tarasandedevelopment.tarasande.protocolhack.platform

import com.viaversion.viaversion.api.connection.UserConnection
import de.florianmichael.vialegacy.IViaLegacyProvider
import de.florianmichael.vialegacy.netty.ForwardMessageToByteEncoder
import de.florianmichael.vialegacy.netty._1_6_4._1_6_4PacketDecoder
import de.florianmichael.vialegacy.netty._1_6_4._1_6_4PacketEncoder
import io.netty.channel.Channel
import net.minecraft.client.MinecraftClient
import net.minecraft.network.encryption.PacketDecryptor
import net.minecraft.network.encryption.PacketEncryptor
import net.tarasandedevelopment.tarasande.protocolhack.TarasandeProtocolHack
import javax.crypto.Cipher

class ViaLegacyTarasandePlatform(private val protocolHack: TarasandeProtocolHack) : IViaLegacyProvider {

    var decryptionKey: Cipher? = null
    var encryptionKey: Cipher? = null

    override fun currentVersion(): Int {
        return protocolHack.clientsideVersion
    }

    override fun fixPipelineOrder_1_6(channel: Channel, decoder: String, encoder: String) {
        channel.pipeline().addBefore(decoder, "decrypt", PacketDecryptor(decryptionKey))
        channel.pipeline().addBefore(encoder, "encrypt", PacketEncryptor(encryptionKey))
    }

    override fun rewriteElements_1_6(connection: UserConnection, channel: Channel, decoder: String, encoder: String) {
        channel.pipeline().addBefore("splitter", decoder, _1_6_4PacketDecoder(connection))
        channel.pipeline().addBefore("prepender", encoder, _1_6_4PacketEncoder())

        channel.pipeline().replace("prepender", "prepender", ForwardMessageToByteEncoder())
        channel.pipeline().remove("splitter")
    }

    override fun sendJoinServer_1_2_5(serverId: String) {
        MinecraftClient.getInstance().sessionService.joinServer(
            MinecraftClient.getInstance().session.profile,
            MinecraftClient.getInstance().session.accessToken,
            serverId
        )
    }
}
