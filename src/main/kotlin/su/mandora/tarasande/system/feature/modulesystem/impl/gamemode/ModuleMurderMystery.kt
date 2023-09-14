package su.mandora.tarasande.system.feature.modulesystem.impl.gamemode

import com.mojang.authlib.GameProfile
import net.minecraft.SharedConstants
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.registry.Registries
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.event.impl.*
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.*
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.feature.modulesystem.impl.combat.ModuleAntiBot
import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.system.screen.informationsystem.ManagerInformation
import su.mandora.tarasande.util.extension.minecraft.packet.isNewWorld
import su.mandora.tarasande.util.math.time.TimeUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.container.ContainerUtil
import su.mandora.tarasande.util.string.StringUtil
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom

class ModuleMurderMystery : Module("Murder mystery", "Finds murderers based on held items", ModuleCategory.GAMEMODE) {

    private val detectionMethod = ValueMode(this, "Detection method", false, "Allow", "Disallow")
    private val allowedItems = object : ValueRegistry<Item>(this, "Allowed items", Registries.ITEM, true, Items.GOLD_INGOT, isEnabled = { detectionMethod.isSelected(0) }) {
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
        override fun onRemove(key: Item) {
            suspects.entries.removeIf { it.value.all { it == key } }
        }
    }
    private val disallowedItems = object : ValueRegistry<Item>(this, "Disallowed items", Registries.ITEM, true, Items.IRON_SWORD, isEnabled = { detectionMethod.isSelected(1) }) {
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
        override fun onAdd(key: Item) {
            suspects.entries.removeIf { it.value.all { it == key } }
        }
    }
    private val murdererColorOverride = ValueColor(this, "Murderer color override", 0.0, 1.0, 1.0, 1.0)
    private val highlightDetectives = ValueBoolean(this, "Highlight detectives", true)
    private val detectiveColorOverride = ValueColor(this, "Bow color override", 0.66, 1.0, 1.0, 1.0, isEnabled = { highlightDetectives.value })
    private val detectiveItems = object : ValueRegistry<Item>(this, "Detective items", Registries.ITEM, true, Items.BOW, isEnabled = { highlightDetectives.value }) {
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }
    private val broadcast = ValueMode(this, "Broadcast", false, "Disabled", "Explanatory", "Legit", "Custom")
    private val customBroadcastMessage = ValueText(this, "Custom broadcast message", "I'm sure it is %s because he held %s", isEnabled = { broadcast.isSelected(3) })
    private val murdererAssistance = ValueBoolean(this, "Murderer assistance", true)
    private val fakeNews = ValueMode(this, "Fake news", false, "Disabled", "Explanatory", "Legit", "Custom")
    private val fakeNewsDelay = ValueNumberRange(this, "Fake news delay", 0.0, 30.0, 60.0, 90.0, 1.0)
    private val customFakeNewsMessage = ValueText(this, "Custom fake news message", "I'm sure it is %s because he held %s", isEnabled = { fakeNews.isSelected(3) })
    private val fakeNewsItems = object : ValueRegistry<Item>(this, "Fake news items", Registries.ITEM, true, Items.IRON_SWORD, isEnabled = { !fakeNews.isSelected(0) && !fakeNews.isSelected(2) }) {
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }

    val suspects = ConcurrentHashMap<GameProfile, Array<Item>>()
    val fakeNewsTimer = TimeUtil()
    var fakeNewsTime = fakeNewsDelay.randomNumber().toLong() * 1000L

    private val messages = ArrayList<String>()

    private var prevItem = 0

    private val moduleAntiBot by lazy { ManagerModule.get(ModuleAntiBot::class.java) }

    init {
        ManagerInformation.apply {
            add(object : Information("Murder Mystery", "Suspected murderers") {
                override fun getMessage(): String? {
                    if (enabled.value)
                        if (suspects.isNotEmpty()) {
                            return "\n" + suspects.entries.joinToString("\n") {
                                it.key.name + " (" + it.value.joinToString(" and ") { item -> StringUtil.uncoverTranslation(item.translationKey) } + "§r)"
                            }
                        }

                    return null
                }
            })

            add(object : Information("Murder Mystery", "Fake news countdown") {
                override fun getMessage(): String? {
                    if (enabled.value)
                        if (!fakeNews.isSelected(0) && isMurderer())
                            return fakeNewsTimer.getTimeLeft(fakeNewsTime).toString()

                    return null
                }
            })
        }
    }

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

    private fun accuse(player: PlayerEntity, mainHand: Item, offHand: Item, broadCastMode: Int, customMessage: String) {
        val itemMessage = when {
            mainHand == Items.AIR && offHand == Items.AIR -> StringUtil.uncoverTranslation(mainHand.translationKey) + " and " + StringUtil.uncoverTranslation(offHand.translationKey)
            mainHand == Items.AIR -> StringUtil.uncoverTranslation(mainHand.translationKey)
            offHand == Items.AIR -> StringUtil.uncoverTranslation(offHand.translationKey)
            else -> "a illegal item" // why dafuq are we here
        }
        val message = when (broadCastMode) {
            1 -> {
                TARASANDE_NAME + " suspects " + player.gameProfile.name + (" because he held $itemMessage")
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

    private fun isIllegalItem(item: Item) =
        if (item == Items.AIR || (highlightDetectives.value && detectiveItems.isSelected(item)))
            false
        else when {
            detectionMethod.isSelected(0) -> !allowedItems.isSelected(item)
            detectionMethod.isSelected(1) -> disallowedItems.isSelected(item)
            else -> false
        }

    fun isMurderer(): Boolean {
        return ContainerUtil.getHotbarSlots().any { isIllegalItem(it.item) } || isIllegalItem(mc.player?.inventory?.offHand?.get(0)?.item!!)
    }

    override fun onEnable() {
        suspects.clear()
    }

    init {
        registerEvent(EventPollEvents::class.java) {
            if (messages.isNotEmpty())
                PlayerUtil.sendChatMessage(SharedConstants.stripInvalidChars(messages.removeFirst()), false)
        }

        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (!fakeNews.isSelected(0) && isMurderer() && murdererAssistance.value) {
                    if (fakeNewsTimer.hasReached(fakeNewsTime)) {
                        val realPlayers = mc.world?.players?.filter { PlayerUtil.isAttackable(it) } ?: return@registerEvent
                        val player = realPlayers.randomOrNull() ?: return@registerEvent
                        val randomIllegalItem = fakeNewsItems.entries().randomOrNull()
                        accuse(player, (randomIllegalItem as? Item) ?: Items.AIR, Items.AIR, fakeNews.values.indexOf(fakeNews.getSelected()), customFakeNewsMessage.value)
                        fakeNewsTime = fakeNewsDelay.randomNumber().toLong() * 1000L
                        fakeNewsTimer.reset()
                    }
                } else {
                    fakeNewsTimer.reset()
                }
            }
        }

        registerEvent(EventAttackEntity::class.java, 9999) { event ->
            if (isMurderer() && PlayerUtil.isAttackable(event.entity)) {
                when (event.state) {
                    EventAttackEntity.State.PRE -> {
                        prevItem = mc.player?.inventory?.selectedSlot!!
                        mc.player?.inventory?.selectedSlot = ContainerUtil.getHotbarSlots().indexOfFirst { isIllegalItem(it.item) }
                    }

                    EventAttackEntity.State.POST -> mc.player?.inventory?.selectedSlot = prevItem
                }
            }
        }

        registerEvent(EventIsEntityAttackable::class.java) { event ->
            if(!isMurderer())
                event.attackable = event.attackable && event.entity is PlayerEntity && suspects.containsKey(event.entity.gameProfile)
        }

        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE)
                when (event.packet) {
                    is PlayerRespawnS2CPacket -> {
                        if (event.packet.isNewWorld())
                            suspects.clear()
                    }

                    is EntityEquipmentUpdateS2CPacket -> {
                        val player = mc.world?.getEntityById(event.packet.id)
                        if (player == mc.player) // I swear I almost played a round without this
                            return@registerEvent
                        if (player !is PlayerEntity) return@registerEvent
                        if (suspects.containsKey(player.gameProfile)) return@registerEvent
                        if (moduleAntiBot.isBot(player)) return@registerEvent

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
                                accuse(player, mainHand ?: Items.AIR, offHand ?: Items.AIR, broadcast.values.indexOf(broadcast.getSelected()), customBroadcastMessage.value)
                            }
                        }
                    }
                }
        }

        registerEvent(EventEntityColor::class.java) { event ->
            if (event.entity is PlayerEntity)
                if (suspects.containsKey(event.entity.gameProfile))
                    event.color = murdererColorOverride.getColor()
                else if (highlightDetectives.value && detectiveItems.any { event.entity.inventory.mainHandStack.item == it || event.entity.inventory.offHand[0].item == it })
                    event.color = detectiveColorOverride.getColor()
        }
    }

}
