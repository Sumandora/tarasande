package su.mandora.tarasande.module.misc

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventEntityColor
import su.mandora.tarasande.event.EventPacket
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.util.string.StringUtil
import su.mandora.tarasande.value.ValueColor
import su.mandora.tarasande.value.ValueItem
import su.mandora.tarasande.value.ValueMode
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

class ModuleMurderMystery : Module("Murder mystery", "Finds murders based on held items", ModuleCategory.MISC) {

    private val disallowedItems = ValueItem(this, "Disallowed items", Items.IRON_SWORD)
    private val colorOverride = ValueColor(this, "Color override", 0.0f, 1.0f, 1.0f, 1.0f)
    private val broadcast = ValueMode(this, "Broadcast", false, "Disabled", "Explanatory", "Legit")

    private val suspects = ArrayList<PlayerEntity>()

    /**
     * This method is a proof for my intellectual abilities
     */
    private fun generateLegitSentence(suspect: String): String {
        var sentence = ""

        when (ThreadLocalRandom.current().nextInt(5)) {
            0 -> { // i think suspect
                var i = false
                var think = false
                while(!i && !think) {
                    i = ThreadLocalRandom.current().nextBoolean()
                    think = ThreadLocalRandom.current().nextBoolean()
                }
                if(i)
                    sentence += "i "
                if(think)
                    sentence += "think "

                if(ThreadLocalRandom.current().nextBoolean())
                    sentence += suspect
                else
                    sentence = "$suspect $sentence"
            }
            1 -> { // sus suspect
                sentence = "sus"

                if(ThreadLocalRandom.current().nextBoolean())
                    sentence += suspect
                else
                    sentence = "$suspect $sentence"
            }
            2 -> { // murderer suspect
                sentence = "murderer"

                if(ThreadLocalRandom.current().nextBoolean())
                    sentence += suspect
                else
                    sentence = "$suspect $sentence"
            }
            3 -> { // i saw suspect kill somebody
                sentence += "i saw $suspect"
                if(ThreadLocalRandom.current().nextBoolean())
                    sentence += "kill "
                if(ThreadLocalRandom.current().nextBoolean())
                    sentence += "somebody"
            }
            4 -> { // yes im sure (100% suspect, suspect safe, suspect 100% murderer)
                sentence +=
                    when(ThreadLocalRandom.current().nextInt(3)) {
                        0 -> "100% "
                        1 -> "safe "
                        2 -> "100% murderer "
                        else -> null
                    }

                if(ThreadLocalRandom.current().nextBoolean())
                    sentence += suspect
                else
                    sentence = "$suspect $sentence"
            }
        }
        return sentence.trim()
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE)
                    for (player in mc.world?.players!!) {
                        if(player == mc.player) // i swear i almost played a round without this
                            continue
                        if(suspects.contains(player))
                            continue

                        val mainHand = player.inventory.mainHandStack.item
                        val offHand = player.inventory.offHand[0].item
                        val illegalMainHand = disallowedItems.list.contains(mainHand)
                        val illegalOffHand = disallowedItems.list.contains(offHand)
                        if (illegalMainHand || illegalOffHand) {
                            suspects.add(player)
                            if(!broadcast.isSelected(0)) {
                                val message = when {
                                    broadcast.isSelected(1) -> {
                                        val itemMessage = when {
                                            illegalMainHand && illegalOffHand -> StringUtil.uncoverTranslation(mainHand.translationKey) + " and " + StringUtil.uncoverTranslation(offHand.translationKey)
                                            illegalMainHand -> StringUtil.uncoverTranslation(mainHand.translationKey)
                                            illegalOffHand -> StringUtil.uncoverTranslation(offHand.translationKey)
                                            else -> null // why dafuq are we here
                                        }
                                        TarasandeMain.get().name + " suspects " + player.gameProfile.name + (if(itemMessage != null) " because he held $itemMessage" else "")
                                    }
                                    broadcast.isSelected(2) -> generateLegitSentence(player.gameProfile.name)
                                    else -> null
                                }
                                if(message != null && message.isNotEmpty())
                                    mc.player?.sendChatMessage(message)
                            }
                        }
                    }
            }
            is EventPacket -> {
                if (event.type == EventPacket.Type.RECEIVE)
                    if (event.packet is PlayerRespawnS2CPacket)
                        suspects.clear()
            }
            is EventEntityColor -> {
                if(suspects.contains(event.entity))
                    event.color = colorOverride.getColor()
            }
        }
    }

}