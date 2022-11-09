package net.tarasandedevelopment.tarasande.screen.cheatmenu.information

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket
import net.minecraft.network.packet.s2c.play.MessageHeaderS2CPacket
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information.Information
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.features.module.exploit.ModuleTickBaseManipulation
import net.tarasandedevelopment.tarasande.features.module.misc.ModuleMurderMystery
import net.tarasandedevelopment.tarasande.features.module.player.ModuleAntiAFK
import net.tarasandedevelopment.tarasande.features.module.render.ModuleBedESP
import net.tarasandedevelopment.tarasande.util.extension.div
import net.tarasandedevelopment.tarasande.util.extension.plus
import java.util.*
import kotlin.math.round
import kotlin.math.roundToInt

class InformationTimeShifted : Information("Tick base manipulation", "Time shifted") {
    private val moduleTickBaseManipulation = TarasandeMain.get().managerModule.get(ModuleTickBaseManipulation::class.java)

    override fun getMessage(): String? {
        if (!moduleTickBaseManipulation.enabled) return null
        if (moduleTickBaseManipulation.shifted == 0L) return null
        return moduleTickBaseManipulation.shifted.toString() + " (" + round(moduleTickBaseManipulation.shifted / MinecraftClient.getInstance().renderTickCounter.tickTime).toInt() + ")"
    }
}

class InformationSuspectedMurderers : Information("Murder Mystery", "Suspected murderers") {
    override fun getMessage(): String? {
        val moduleMurderMystery = TarasandeMain.get().managerModule.get(ModuleMurderMystery::class.java)
        if (moduleMurderMystery.enabled)
            if (moduleMurderMystery.suspects.isNotEmpty()) {
                return "\n" + moduleMurderMystery.suspects.entries.joinToString("\n") {
                    it.key.name + " (" + it.value.joinToString(" and ") { it.name.string } + "§r)"
                }
            }

        return null
    }
}

class InformationFakeNewsCountdown : Information("Murder Mystery", "Fake news countdown") {
    override fun getMessage(): String? {
        val moduleMurderMystery = TarasandeMain.get().managerModule.get(ModuleMurderMystery::class.java)
        if (moduleMurderMystery.enabled)
            if (!moduleMurderMystery.fakeNews.isSelected(0) && moduleMurderMystery.isMurderer() && moduleMurderMystery.murdererAssistance.value)
                return (moduleMurderMystery.fakeNewsTime - (System.currentTimeMillis() - moduleMurderMystery.fakeNewsTimer.time)).toString()

        return null
    }
}

class InformationBeds : Information("Bed ESP", "Beds") {
    override fun getMessage(): String? {
        val moduleBedESP = TarasandeMain.get().managerModule.get(ModuleBedESP::class.java)
        if (moduleBedESP.enabled) if (moduleBedESP.calculateBestWay.value) if (moduleBedESP.bedDatas.isNotEmpty()) {
            return "\n" + moduleBedESP.bedDatas.sortedBy {
                MinecraftClient.getInstance().player?.squaredDistanceTo(it.bedParts.let {
                    var vec = Vec3d.ZERO
                    it.forEach { vec += Vec3d.ofCenter(it) }
                    vec / it.size
                })
            }.joinToString("\n") { it.toString() }.let { it.substring(0, it.length - 1) }
        }

        return null
    }
}

class InformationAntiAFKCountdown : Information("Anti AFK", "Jump countdown") {
    override fun getMessage(): String? {
        val moduleAntiAFK = TarasandeMain.get().managerModule.get(ModuleAntiAFK::class.java)
        if (moduleAntiAFK.enabled)
            return (((moduleAntiAFK.delay.value * 1000L) - (System.currentTimeMillis() - moduleAntiAFK.timer.time)) / 1000.0).roundToInt().toString()

        return null
    }
}

class InformationDetectedMessages : Information("Private msg detector", "Detected messages") {

    private val signatures: Multimap<UUID, ByteArray> = MultimapBuilder.hashKeys().hashSetValues().build()
    private val conversation = ArrayList<String>()

    init {
        TarasandeMain.get().managerEvent.add(EventPacket::class.java) { event ->
            if (event.type != EventPacket.Type.RECEIVE) return@add

            if (event.packet is MessageHeaderS2CPacket) {
                val sender = MinecraftClient.getInstance().world!!.getPlayerByUuid(event.packet.header.sender)
                val senderName = sender?.name?.string
                val signature = event.packet.headerSignature.data

                if (sender != null) {
                    this.signatures.put(sender.uuid, signature)
                    conversation.add("$senderName just sent a private message!")
                }
            }

            if (event.packet is ChatMessageS2CPacket) {
                for (entry in event.packet.message.signedBody.lastSeenMessages.entries) {
                    if (entry.profileId == event.packet.message.signedHeader.sender)
                        continue

                    val signaturesLocal = this.signatures.get(event.packet.message.signedHeader.sender)

                    for (bytes in signaturesLocal) {
                        if (Arrays.equals(bytes, entry.lastSignature.data)) {
                            val receiver = MinecraftClient.getInstance().world!!.getPlayerByUuid(event.packet.message.signedHeader.sender)
                            val sender = MinecraftClient.getInstance().world!!.getPlayerByUuid(entry.profileId)

                            if (sender != receiver) {
                                conversation.add(receiver!!.name.string + " received a private message from " + sender!!.name.string + "!")
                            }
                        }
                    }
                }
            }
        }
        TarasandeMain.get().managerEvent.add(EventDisconnect::class.java) {
            conversation.clear()
        }
    }

    override fun getMessage(): String? {
        if (conversation.isNotEmpty())
            return "\n" + conversation.subList(0, conversation.size - 1).joinToString("\n")

        return null
    }
}
