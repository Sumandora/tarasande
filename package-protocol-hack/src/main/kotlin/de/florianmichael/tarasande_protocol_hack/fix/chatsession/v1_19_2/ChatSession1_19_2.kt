package de.florianmichael.tarasande_protocol_hack.fix.chatsession.v1_19_2

import com.viaversion.viaversion.api.connection.StoredObject
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature
import com.viaversion.viaversion.api.minecraft.ProfileKey
import de.florianmichael.tarasande_protocol_hack.fix.chatsession.all_model.MessageMetadata1_19_all
import de.florianmichael.tarasande_protocol_hack.fix.chatsession.all_model.MessageSigner1_19_all
import de.florianmichael.tarasande_protocol_hack.fix.chatsession.all_model.SignatureUpdatable1_19_all
import de.florianmichael.tarasande_protocol_hack.fix.chatsession.all_model.SignatureUpdater1_19_all
import de.florianmichael.tarasande_protocol_hack.fix.chatsession.v1_19_2.model.MessageBody1_19_2
import de.florianmichael.tarasande_protocol_hack.fix.chatsession.v1_19_2.model.MessageHeader1_19_2
import java.security.PrivateKey
import java.security.SecureRandom
import java.util.*

class ChatSession1_19_2(user: UserConnection, val profileKey: ProfileKey, privateKey: PrivateKey) : StoredObject(user) {

    val saltGenerator = SecureRandom()
    var signer = MessageSigner1_19_all.create(privateKey, "SHA256withRSA")

    private var precedingSignature: ByteArray? = null

    fun sign(sender: UUID, messageMetadata: MessageMetadata1_19_all, lastSeenMessages: Array<PlayerMessageSignature>): ByteArray {
        val header = MessageHeader1_19_2(precedingSignature, sender)
        val body = MessageBody1_19_2(messageMetadata, lastSeenMessages)

        precedingSignature = signer.sign(object : SignatureUpdatable1_19_all {
            override fun update(updater: SignatureUpdater1_19_all?) {
                header.update(updater!!, body.digestBytes())
            }
        })
        return precedingSignature!!
    }
}
