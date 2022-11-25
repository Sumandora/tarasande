package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventEntityColor
import net.tarasandedevelopment.tarasande.event.EventEntityHurt
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueRegistry
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.impl.fixed.PanelNotifications
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import java.awt.Color

class ModuleTroubleInTerrioristTown : Module("Trouble in terrorist town", "Uses assumptions to find out traitors in TTT", ModuleCategory.MISC) {

    private val distance = ValueNumber(this, "Distance", 0.0, 6.0, 10.0, 0.5)
    private val rotationThreshold = ValueNumber(this, "Rotation threshold", 0.0, 0.5, 1.0, 0.01)
    private val items = object : ValueRegistry<Item>(this, "Items", Registry.ITEM, Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD) {
        override fun filter(key: Item) = key != Items.AIR
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }

    private val map = HashMap<PlayerEntity, ArrayList<PlayerEntity>>()

    init {
        TarasandeMain.managerInformation().add(object : Information("Trouble in terrorist town", "Suspected traitors") {
            override fun getMessage(): String? {
                if (enabled && map.isNotEmpty())
                    return map.entries.joinToString("\n") { it.key.gameProfile.name + " [" + it.value.joinToString { it.gameProfile.name } + "]" }

                return null
            }
        })
    }

    private fun scanForTraitors(entity: PlayerEntity, death: Boolean): ArrayList<PlayerEntity>? {
        val traitors = ArrayList<PlayerEntity>()

        for(player in mc.world?.players?.filter { it != entity } ?: return null) {
            if(player.distanceTo(entity) <= distance.value && RotationUtil.getRotations(player.eyePos, entity.eyePos).fov(Rotation(player)) <= Rotation.MAXIMUM_DELTA * rotationThreshold.value)
                if(items.list.contains(player.mainHandStack.item)) {
                    // This player is probably a traitor
                    if(death) {
                        map[player] = map.getOrDefault(player, ArrayList()).apply { if(!contains(entity)) add(entity) }
                    }

                    traitors.add(player)
                }
        }

        return traitors
    }

    init {
        registerEvent(EventEntityHurt::class.java) { event ->
            if(event.entity !is PlayerEntity)
                return@registerEvent

            val traitors = scanForTraitors(event.entity, event.death) ?: return@registerEvent

            if(traitors.isNotEmpty())
                PanelNotifications.notify(event.entity.gameProfile.name + " is being attacked by " + traitors.joinToString { it.gameProfile.name })
        }

        registerEvent(EventPacket::class.java) { event ->
            if(event.type == EventPacket.Type.RECEIVE && event.packet is HealthUpdateS2CPacket) {
                if(event.packet.health >= mc.player?.health!!)
                    return@registerEvent
                val traitors = scanForTraitors(mc.player!!, event.packet.health <= 0.0f) ?: return@registerEvent

                if(traitors.isNotEmpty())
                    PanelNotifications.notify("You are being attacked by " + traitors.joinToString { it.gameProfile.name })
            }
        }

        registerEvent(EventEntityColor::class.java) { event ->
            if(map.containsKey(event.entity))
                event.color = Color.red
        }

        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE)
                if (event.packet is PlayerRespawnS2CPacket)
                    map.clear()
        }
    }

}