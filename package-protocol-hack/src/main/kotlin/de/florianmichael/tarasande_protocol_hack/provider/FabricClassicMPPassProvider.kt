package de.florianmichael.tarasande_protocol_hack.provider

import com.google.common.hash.Hashing
import com.google.common.io.Resources
import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.connection.UserConnection
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.provider.ClassicMPPassProvider
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.provider.OldAuthProvider
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.HandshakeStorage
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues
import java.net.InetAddress
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.logging.Level

class FabricClassicMPPassProvider : ClassicMPPassProvider() {

    override fun getMpPass(user: UserConnection): String {
        if (ProtocolHackValues.betaCraftAuth.value && user.has(HandshakeStorage::class.java)) {
            val handshakeStorage = user.get(HandshakeStorage::class.java)
            return getBetaCraftMpPass(user, user.protocolInfo.username!!, handshakeStorage!!.hostname, handshakeStorage.port)!!
        }
        return super.getMpPass(user)
    }

    private fun getBetaCraftMpPass(user: UserConnection, username: String, serverIp: String, port: Int): String? {
        try {
            val server = InetAddress.getByName(serverIp).hostAddress + ":" + port
            Via.getManager().providers.get(OldAuthProvider::class.java)!!.sendAuthRequest(user, Hashing.sha1().hashBytes(server.toByteArray()).toString())
            val mppass = Resources.toString(URL("http://api.betacraft.uk/getmppass.jsp?user=$username&server=$server"), StandardCharsets.UTF_8)
            return if (mppass.contains("FAILED") || mppass.contains("SERVER NOT FOUND")) "0" else mppass
        } catch (e: Throwable) {
            Via.getPlatform().logger.log(Level.WARNING, "An unknown error occurred while authenticating with BetaCraft", e)
        }
        return "0"
    }
}
