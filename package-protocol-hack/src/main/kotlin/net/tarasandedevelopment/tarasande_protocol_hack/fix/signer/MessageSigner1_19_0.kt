package net.tarasandedevelopment.tarasande_protocol_hack.fix.signer

import net.lenni0451.mcstructs.text.components.StringComponent
import net.lenni0451.mcstructs.text.serializer.TextComponentSerializer
import net.tarasandedevelopment.tarasande_protocol_hack.fix.signer.model.SignatureUpdatable1_19_2
import net.tarasandedevelopment.tarasande_protocol_hack.fix.signer.model.SignatureUpdater1_19_2
import net.tarasandedevelopment.tarasande_protocol_hack.util.JsonSorter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*

object MessageSigner1_19_0 {

    fun sign(signer: MessageSigner1_19_2, decorateText: String, sender: UUID, timeStamp: Instant, salt: Long): ByteArray? {
        return signer.sign(object : SignatureUpdatable1_19_2 {
            override fun update(updater: SignatureUpdater1_19_2?) {
                val data = ByteArray(32)

                val buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN)
                buffer.putLong(salt)
                buffer.putLong(sender.mostSignificantBits).putLong(sender.leastSignificantBits)
                buffer.putLong(timeStamp.epochSecond)

                updater?.update(data)
                updater?.update(JsonSorter.toSortedString(TextComponentSerializer.V1_18.serializeJson(StringComponent(decorateText))).toByteArray(StandardCharsets.UTF_8))
            }
        })
    }
}
