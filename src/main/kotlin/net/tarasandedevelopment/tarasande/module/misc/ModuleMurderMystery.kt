package net.tarasandedevelopment.tarasande.module.misc

import com.mojang.authlib.GameProfile
import net.minecraft.SharedConstants
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.module.combat.ModuleAntiBot
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import net.tarasandedevelopment.tarasande.value.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

class ModuleMurderMystery : Module("Murder mystery", "Finds murders based on held items", ModuleCategory.MISC) {

    private val detectionMethod = ValueMode(this, "Detection method", false, "Allow", "Disallow")
    private val allowedItems = object : ValueRegistry<Item>(this, "Allowed items", Registry.ITEM, Items.GOLD_INGOT) {
        override fun isEnabled() = detectionMethod.isSelected(0)
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }
    private val disallowedItems = object : ValueRegistry<Item>(this, "Disallowed items", Registry.ITEM, Items.IRON_SWORD) {
        override fun isEnabled() = detectionMethod.isSelected(1)
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }
    private val murdererColorOverride = ValueColor(this, "Murderer color override", 0.0f, 1.0f, 1.0f, 1.0f)
    private val highlightDetectives = ValueBoolean(this, "Highlight detectives", false)
    private val detectiveColorOverride = object : ValueColor(this, "Bow color override", 0.66f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = highlightDetectives.value
    }
    private val detectiveItems = object : ValueRegistry<Item>(this, "Detective items", Registry.ITEM, Items.BOW) {
        override fun isEnabled() = highlightDetectives.value
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }
    private val broadcast = ValueMode(this, "Broadcast", false, "Disabled", "Explanatory", "Legit", "Custom")
    private val customBroadcastMessage = object : ValueText(this, "Custom fake news message", "I'm sure it is %s because he held %s") {
        override fun isEnabled() = broadcast.isSelected(3)
    }
    internal val murdererAssistance = ValueBoolean(this, "Murderer assistance", true)
    internal val fakeNews = ValueMode(this, "Fake news", false, "Disabled", "Explanatory", "Legit", "Custom")
    private val customFakeNewsMessage = object : ValueText(this, "Custom fake news message", "I'm sure it is %s because he held %s") {
        override fun isEnabled() = fakeNews.isSelected(3)
    }
    private val fakeNewsItems = object : ValueRegistry<Item>(this, "Fake news items", Registry.ITEM, Items.IRON_SWORD) {
        override fun isEnabled() = !fakeNews.isSelected(0)
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }

    val suspects = ConcurrentHashMap<GameProfile, Array<Item>>()
    internal val fakeNewsTimer = TimeUtil()
    internal var fakeNewsTime = ThreadLocalRandom.current().nextInt(30, 60) * 1000L
    private var switchedSlot = false

    private val messages = ArrayList<String>()

    // This method is a proof for my intellectual abilities
    private fun generateLegitSentence(suspect: String): String {
        var sentence = ""

        when (ThreadLocalRandom.current().nextInt(2)) {
            0 -> { // yes im sure (100% suspect, suspect safe, suspect 100% murderer)
                sentence += when (ThreadLocalRandom.current().nextInt(5)) {
                    0 -> "100% "
                    1 -> "i think "
                    2 -> "murderer "
                    3 -> if (ThreadLocalRandom.current().nextBoolean()) "safe murderer " else "murderer safe "
                    4 -> if (ThreadLocalRandom.current().nextBoolean()) "100% murderer " else "murderer 100% "
                    else -> null
                }

                if (ThreadLocalRandom.current().nextBoolean()) sentence += suspect
                else sentence = "$suspect $sentence"
            }

            1 -> { // I saw suspect kill somebody, this message is super cool when suspect is on the other side of the map and there are 50 walls in between
                sentence += "i saw $suspect kill "
                if (ThreadLocalRandom.current().nextBoolean()) sentence += "somebody"
            }
        }
        return sentence.trim()
    }

    private fun accuse(player: PlayerEntity, illegalMainHand: Boolean, illegalOffHand: Boolean, mainHand: Item, offHand: Item, broadCastMode: Int, customMessage: String) {
        val itemMessage = when {
            illegalMainHand && illegalOffHand -> StringUtil.uncoverTranslation(mainHand.translationKey) + " and " + StringUtil.uncoverTranslation(offHand.translationKey)
            illegalMainHand -> StringUtil.uncoverTranslation(mainHand.translationKey)
            illegalOffHand -> StringUtil.uncoverTranslation(offHand.translationKey)
            else -> "a illegal item" // why dafuq are we here
        }
        val message = when (broadCastMode) {
            1 -> {
                TarasandeMain.get().name + " suspects " + player.gameProfile.name + (" because he held $itemMessage")
            }

            2 -> {
                generateLegitSentence(player.gameProfile.name)
            }

            3 -> {
                try {
                    customMessage.format(player.gameProfile.name, itemMessage)
                } catch (exception: IllegalFormatConversionException) {
                    exception.printStackTrace()
                    customMessage // fallback to not doing anything at all
                }
            }

            else -> null
        }
        if (!message.isNullOrEmpty()) messages.add(message)
    }

    private fun isIllegalItem(item: Item) = if (item == Items.AIR || (highlightDetectives.value && detectiveItems.list.contains(item))) false else when {
        detectionMethod.isSelected(0) -> !allowedItems.list.contains(item)
        detectionMethod.isSelected(1) -> disallowedItems.list.contains(item)
        else -> false
    }

    internal fun isMurderer(): Boolean {
        for (slot in 0 until PlayerInventory.getHotbarSize()) {
            if (isIllegalItem(mc.player?.inventory?.main?.get(slot)?.item!!)) return true
        }

        return isIllegalItem(mc.player?.inventory?.offHand?.get(0)?.item!!)
    }

    override fun onEnable() {
        suspects.clear()
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPollEvents -> {
                if (messages.isNotEmpty())
                    PlayerUtil.sendChatMessage(SharedConstants.stripInvalidChars(messages.removeFirst()))
            }

            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE) {
                    if (!fakeNews.isSelected(0) && isMurderer() && murdererAssistance.value) {
                        if (fakeNewsTimer.hasReached(fakeNewsTime)) {
                            var player: PlayerEntity? = null
                            val realPlayers = mc.world?.players?.filter { PlayerUtil.isAttackable(it) } ?: return@Consumer
                            if (realPlayers.size <= 1)
                                return@Consumer
                            while (player == null || player == mc.player) {
                                player = realPlayers[ThreadLocalRandom.current().nextInt(realPlayers.size)]
                            }
                            val randomIllegalItem = fakeNewsItems.list.randomOrNull()
                            accuse(player, randomIllegalItem != null, false, randomIllegalItem ?: Items.AIR, Items.AIR, fakeNews.settings.indexOf(fakeNews.selected[0]), customFakeNewsMessage.value)
                            fakeNewsTime = ThreadLocalRandom.current().nextInt(30, 60) * 1000L
                            fakeNewsTimer.reset()
                        }
                    } else {
                        fakeNewsTimer.reset()
                    }
                }
            }

            is EventAttackEntity -> {
                if (murdererAssistance.value && isMurderer()) {
                    when (event.state) {
                        EventAttackEntity.State.PRE -> {
                            var sword = 0
                            for (slot in 0 until PlayerInventory.getHotbarSize()) {
                                if (isIllegalItem(mc.player?.inventory?.main?.get(slot)?.item!!)) {
                                    sword = slot
                                    break
                                }
                            }
                            if ((mc.player?.inventory?.selectedSlot!! != sword).also { switchedSlot = it }) mc.networkHandler?.sendPacket(UpdateSelectedSlotC2SPacket(sword))
                        }

                        EventAttackEntity.State.POST -> if (switchedSlot) mc.networkHandler?.sendPacket(UpdateSelectedSlotC2SPacket(mc.player?.inventory?.selectedSlot!!))
                    }
                }
            }

            is EventIsEntityAttackable -> {
                if (!isMurderer()) {
                    if (event.entity !is PlayerEntity) {
                        return@Consumer
                    }
                    event.attackable = event.attackable && suspects.containsKey(event.entity.gameProfile)
                }
            }

            is EventPacket -> {
                if (event.type == EventPacket.Type.RECEIVE) if (event.packet is PlayerRespawnS2CPacket) {
                    suspects.clear()
                } else if (event.packet is EntityEquipmentUpdateS2CPacket) {
                    val player = mc.world?.getEntityById(event.packet.id)
                    if (player == mc.player) // I swear I almost played a round without this
                        return@Consumer
                    if (player !is PlayerEntity) return@Consumer
                    if (suspects.containsKey(player.gameProfile)) return@Consumer
                    if (TarasandeMain.get().managerModule.get(ModuleAntiBot::class.java).isBot(player)) return@Consumer

                    var mainHand: Item? = null
                    var offHand: Item? = null
                    for (pair in event.packet.equipmentList) {
                        if (pair.first == EquipmentSlot.MAINHAND) mainHand = pair.second.item
                        else if (pair.first == EquipmentSlot.OFFHAND) offHand = pair.second.item
                    }

                    val illegalMainHand = if (mainHand != null) isIllegalItem(mainHand) else false
                    val illegalOffHand = if (offHand != null) isIllegalItem(offHand) else false

                    if (illegalMainHand || illegalOffHand) {
                        @Suppress("KotlinConstantConditions") // I could write the offhand branch into the else, but I prefer the style
                        suspects[player.gameProfile] = when {
                            illegalMainHand && illegalOffHand -> arrayOf(mainHand!!, offHand!!)
                            illegalMainHand -> arrayOf(mainHand!!)
                            illegalOffHand -> arrayOf(offHand!!)
                            else -> arrayOf()
                        }
                        if (!broadcast.isSelected(0)) {
                            accuse(player, illegalMainHand, illegalOffHand, mainHand ?: Items.AIR, offHand ?: Items.AIR, broadcast.settings.indexOf(broadcast.selected[0]), customBroadcastMessage.value)
                        }
                    }
                }
            }

            is EventEntityColor -> {
                if (event.entity is PlayerEntity) if (suspects.containsKey(event.entity.gameProfile)) event.color = murdererColorOverride.getColor()
                else if (highlightDetectives.value && detectiveItems.list.any { event.entity.inventory.mainHandStack.item == it || event.entity.inventory.offHand[0].item == it }) event.color = detectiveColorOverride.getColor()
            }
        }
    }

}