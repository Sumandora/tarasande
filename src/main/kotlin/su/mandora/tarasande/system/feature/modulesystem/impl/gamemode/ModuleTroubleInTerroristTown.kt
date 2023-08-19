package su.mandora.tarasande.system.feature.modulesystem.impl.gamemode

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket
import net.minecraft.registry.Registries
import su.mandora.tarasande.event.impl.EventEntityHurt
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.maxReach
import su.mandora.tarasande.util.player.chat.CustomChat

class ModuleTroubleInTerroristTown : Module("Trouble in terrorist town", "Uses assumptions to find traitors in TTT", ModuleCategory.GAMEMODE) {

    private val distance = ValueNumber(this, "Distance", 0.0, maxReach, 10.0, 0.5)
    private val rotationThreshold = ValueNumber(this, "Rotation threshold", 0.0, 0.5, 1.0, 0.01)
    private val items = object : ValueRegistry<Item>(this, "Items", Registries.ITEM, true, Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD) {
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }

    private fun scanForTraitors(entity: PlayerEntity): ArrayList<PlayerEntity>? {
        val traitors = ArrayList<PlayerEntity>()

        for (player in mc.world?.players?.filter { it != entity } ?: return null) {
            if (player.squaredDistanceTo(entity) <= distance.value * distance.value && RotationUtil.getRotations(player.eyePos, entity.eyePos).fov(Rotation(player)) <= Rotation.MAXIMUM_DELTA * rotationThreshold.value)
                if (items.isSelected(player.mainHandStack.item)) {
                    // This player is probably a traitor
                    traitors.add(player)
                }
        }

        return traitors
    }

    init {
        registerEvent(EventEntityHurt::class.java) { event ->
            if (event.entity !is PlayerEntity)
                return@registerEvent

            val traitors = scanForTraitors(event.entity) ?: return@registerEvent

            if (traitors.isNotEmpty())
                CustomChat.printChatMessage(event.entity.gameProfile.name + " is being attacked by " + traitors.joinToString { it.gameProfile.name })
        }

        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE && event.packet is HealthUpdateS2CPacket) {
                if (event.packet.health >= (mc.player?.health ?: return@registerEvent))
                    return@registerEvent
                val traitors = scanForTraitors(mc.player!!) ?: return@registerEvent

                if (traitors.isNotEmpty())
                    CustomChat.printChatMessage("You are being attacked by " + traitors.joinToString { it.gameProfile.name })
            }
        }
    }

}