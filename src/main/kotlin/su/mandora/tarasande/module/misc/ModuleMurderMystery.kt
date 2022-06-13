package su.mandora.tarasande.module.misc

import com.mojang.authlib.GameProfile
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.util.registry.Registry
import net.minecraft.world.GameMode
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.*
import su.mandora.tarasande.module.combat.ModuleAntiBot
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.string.StringUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueColor
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueRegistry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

class ModuleMurderMystery : Module("Murder mystery", "Finds murders based on held items", ModuleCategory.MISC) {

    private val detectionMethod = ValueMode(this, "Detection method", false, "Allow", "Disallow")
    private val allowedItems = object : ValueRegistry<Item>(this, "Allowed items", Registry.ITEM, Items.FILLED_MAP, Items.BOW, Items.ARROW, Items.ARMOR_STAND, Items.RED_BED, Items.GOLD_INGOT, Items.PAPER, Items.WOODEN_SHOVEL, Items.LIGHT_BLUE_STAINED_GLASS, Items.SNOWBALL, Items.PLAYER_HEAD, Items.COMPASS, Items.RED_BED, Items.TNT) {
        override fun isEnabled() = detectionMethod.isSelected(0)
        override fun filter(key: Item) = key != Items.AIR
        override fun keyToString(key: Any?) = (key as Item).name.string
    }
    private val disallowedItems = object : ValueRegistry<Item>(this, "Disallowed items", Registry.ITEM, Items.IRON_SWORD) {
        override fun isEnabled() = detectionMethod.isSelected(1)
        override fun filter(key: Item) = key != Items.AIR
        override fun keyToString(key: Any?) = (key as Item).name.string
    }
    private val murdererColorOverride = ValueColor(this, "Murderer color override", 0.0f, 1.0f, 1.0f, 1.0f)
    private val bowColorOverride = ValueColor(this, "Bow color override", 0.66f, 1.0f, 1.0f, 1.0f)
    private val broadcast = ValueMode(this, "Broadcast", false, "Disabled", "Explanatory", "Legit")
    private val murdererAssistance = ValueBoolean(this, "Murderer assistance", true)
    private val fakeNews = ValueMode(this, "Broadcast", false, "Disabled", "Explanatory", "Legit")

    val suspects = ConcurrentHashMap<GameProfile, Array<Item>>()
    private val fakeNewsTimer = TimeUtil()
    private var fakeNewsTime = ThreadLocalRandom.current().nextInt(30, 60) * 1000L
    private var switchedSlot = false

    // This method is a proof for my intellectual abilities
    private fun generateLegitSentence(suspect: String): String {
        var sentence = ""

        when (ThreadLocalRandom.current().nextInt(2)) {
            0 -> { // yes im sure (100% suspect, suspect safe, suspect 100% murderer)
                sentence +=
                    when (ThreadLocalRandom.current().nextInt(5)) {
                        0 -> "100% "
                        1 -> "i think "
                        2 -> "murderer "
                        3 -> if (ThreadLocalRandom.current().nextBoolean()) "safe murderer " else "murderer safe "
                        4 -> if (ThreadLocalRandom.current().nextBoolean()) "100% murderer " else "murderer 100% "
                        else -> null
                    }

                if (ThreadLocalRandom.current().nextBoolean())
                    sentence += suspect
                else
                    sentence = "$suspect $sentence"
            }
            1 -> { // i saw suspect kill somebody, this message is super cool when suspect is on the other side of the map and there are 50 walls in between
                sentence += "i saw $suspect kill "
                if (ThreadLocalRandom.current().nextBoolean())
                    sentence += "somebody"
            }
        }
        return sentence.trim()
    }

    private fun accuse(player: PlayerEntity, illegalMainHand: Boolean, illegalOffHand: Boolean, mainHand: Item, offHand: Item, broadCastMode: Int) {
        val message = when (broadCastMode) {
            1 -> {
                val itemMessage = when {
                    illegalMainHand && illegalOffHand -> StringUtil.uncoverTranslation(mainHand.translationKey) + " and " + StringUtil.uncoverTranslation(offHand.translationKey)
                    illegalMainHand -> StringUtil.uncoverTranslation(mainHand.translationKey)
                    illegalOffHand -> StringUtil.uncoverTranslation(offHand.translationKey)
                    else -> null // why dafuq are we here
                }
                TarasandeMain.get().name + " suspects " + player.gameProfile.name + (if (itemMessage != null) " because he held $itemMessage" else "")
            }
            2 -> {
                generateLegitSentence(player.gameProfile.name)
            }
            else -> null
        }
        if (message != null && message.isNotEmpty())
            mc.player?.sendChatMessage(message)
    }

    private fun isIllegalItem(item: Item) = if (item == Items.AIR) false else when {
        detectionMethod.isSelected(0) -> !allowedItems.list.contains(item)
        detectionMethod.isSelected(1) -> disallowedItems.list.contains(item)
        else -> false
    }

    private fun isMurderer(): Boolean {
        for (slot in 0 until PlayerInventory.getHotbarSize()) {
            if (isIllegalItem(mc.player?.inventory?.main?.get(slot)?.item!!))
                return true
        }

        return isIllegalItem(mc.player?.inventory?.offHand?.get(0)?.item!!)
    }

    override fun onEnable() {
        suspects.clear()
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE) {
                    if (mc.world?.players?.size!! <= 1) return@Consumer
                    if (mc.interactionManager?.currentGameMode != GameMode.SPECTATOR)
                        if (!fakeNews.isSelected(0) && isMurderer() && murdererAssistance.value) {
                            if (fakeNewsTimer.hasReached(fakeNewsTime)) {
                                var player: PlayerEntity? = null
                                while (player == null || player == mc.player) {
                                    player = mc.world?.players?.get(ThreadLocalRandom.current().nextInt(mc.world?.players?.size!!))
                                }
                                @Suppress("BooleanLiteralArgument")
                                accuse(player, true, false, Items.IRON_SWORD, Items.AIR, fakeNews.settings.indexOf(fakeNews.selected[0]))
                                fakeNewsTime = ThreadLocalRandom.current().nextInt(30, 60) * 1000L
                                fakeNewsTimer.reset()
                            }
                        } else {
                            fakeNewsTime = ThreadLocalRandom.current().nextInt(30, 60) * 1000L
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
                            if ((mc.player?.inventory?.selectedSlot!! != sword).also { switchedSlot = it })
                                mc.networkHandler?.sendPacket(UpdateSelectedSlotC2SPacket(sword))
                        }
                        EventAttackEntity.State.POST -> if (switchedSlot) mc.networkHandler?.sendPacket(UpdateSelectedSlotC2SPacket(mc.player?.inventory?.selectedSlot!!))
                    }
                }
            }
            is EventIsEntityAttackable -> {
                if (event.entity is PlayerEntity && !isMurderer() && !suspects.contains(event.entity.gameProfile))
                    return@Consumer
            }
            is EventPacket -> {
                if (event.type == EventPacket.Type.RECEIVE)
                    if (event.packet is PlayerRespawnS2CPacket) {
                        suspects.clear()
                    } else if (event.packet is EntityEquipmentUpdateS2CPacket) {
                        val player = mc.world?.getEntityById(event.packet.id)
                        if (player == mc.player) // i swear i almost played a round without this
                            return@Consumer
                        if (player !is PlayerEntity)
                            return@Consumer
                        if (suspects.contains(player.gameProfile))
                            return@Consumer
                        if (TarasandeMain.get().managerModule?.get(ModuleAntiBot::class.java)?.isBot(player)!!)
                            return@Consumer

                        var mainHand: Item? = null
                        var offHand: Item? = null
                        for (pair in event.packet.equipmentList) {
                            if (pair.first == EquipmentSlot.MAINHAND)
                                mainHand = pair.second.item
                            else if (pair.first == EquipmentSlot.OFFHAND)
                                offHand = pair.second.item
                        }

                        val illegalMainHand = if (mainHand != null) isIllegalItem(mainHand) else false
                        val illegalOffHand = if (offHand != null) isIllegalItem(offHand) else false

                        if (illegalMainHand || illegalOffHand) {
                            suspects[player.gameProfile] = when {
                                illegalMainHand && mainHand != null && illegalOffHand && offHand != null -> arrayOf(mainHand, offHand)
                                illegalMainHand && mainHand != null -> arrayOf(mainHand)
                                illegalOffHand && offHand != null -> arrayOf(offHand)
                                else -> arrayOf()
                            }
                            if (!broadcast.isSelected(0)) {
                                accuse(player, illegalMainHand, illegalOffHand, mainHand ?: Items.AIR, offHand ?: Items.AIR, broadcast.settings.indexOf(broadcast.selected[0]))
                            }
                        }
                    }
            }
            is EventEntityColor -> {
                if (event.entity is PlayerEntity)
                    if (suspects.containsKey(event.entity.gameProfile))
                        event.color = murdererColorOverride.getColor()
                    else if (event.entity.inventory.mainHandStack.item == Items.BOW || event.entity.inventory.offHand[0].item == Items.BOW)
                        event.color = bowColorOverride.getColor()
            }
        }
    }

}