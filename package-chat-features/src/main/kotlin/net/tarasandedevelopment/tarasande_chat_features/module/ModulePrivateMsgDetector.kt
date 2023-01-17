package net.tarasandedevelopment.tarasande_chat_features.module

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat
import java.nio.ByteBuffer
import java.util.*

class ModulePrivateMsgDetector : Module("Private msg detector", "Detects private messages", ModuleCategory.EXPLOIT) {

    private val signatures: Multimap<ByteBuffer, UUID> = MultimapBuilder.hashKeys().hashSetValues().build()
    private val seen = HashSet<ByteBuffer>()

    private val notificationsSent = HashSet<String>()
    private var notificationsWhenTick = -1
    private var firstNextTick = false

    fun handleInput(data: ByteBuffer) = seen.add(data)

    fun trackHistoryPart(sender: UUID, data: ByteBuffer) {
        if (seen.contains(data) || signatures.containsEntry(data, sender)) {
            return
        }
        signatures.put(data, sender)
        if (mc.world == null) {
            return
        }
        val names = ArrayList<String>()
        for (uuid in signatures.get(data)) {
            val playerListEntry = mc.world?.getPlayerByUuid(uuid)
            if (playerListEntry != null) {
                names.add(playerListEntry.name.string)
            }
        }

        val joinedNames = names.subList(0, names.size - 1).joinToString(", ") + " and " + names[names.size - 1]
        val message = (if (names.size == 1) names[0] else joinedNames) + " " + (if (names.size > 1) "have" else "has") + " seen a message you haven't!"

        if (mc.player?.age != notificationsWhenTick) {
            notificationsWhenTick = mc.player!!.age
            notificationsSent.clear()
        }

        if (notificationsSent.add(message) && firstNextTick) {
            CustomChat.printChatMessage(message)
        }
        firstNextTick = true
    }
}
