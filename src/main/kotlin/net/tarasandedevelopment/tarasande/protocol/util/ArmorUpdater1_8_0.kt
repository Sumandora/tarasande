package net.tarasandedevelopment.tarasande.protocol.util

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.api.type.Type
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ArmorType
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8
import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IClientConnection_Protocol
import java.util.*

object ArmorUpdater1_8_0 {

    // this is a list of all 1.9 armor items with their id
    private val ids = HashMap<Item, Int>()

    private var oldArmor = 0.0

    init {
        //@formatter:off
        arrayOf(
            Items.LEATHER_HELMET,       Items.CHAINMAIL_HELMET,         Items.IRON_HELMET,          Items.DIAMOND_HELMET,       Items.GOLDEN_HELMET,
            Items.LEATHER_CHESTPLATE,   Items.CHAINMAIL_CHESTPLATE,     Items.IRON_CHESTPLATE,      Items.DIAMOND_CHESTPLATE,   Items.GOLDEN_CHESTPLATE,
            Items.LEATHER_LEGGINGS,     Items.CHAINMAIL_LEGGINGS,       Items.IRON_LEGGINGS,        Items.DIAMOND_LEGGINGS,     Items.GOLDEN_LEGGINGS,
            Items.LEATHER_BOOTS,        Items.CHAINMAIL_BOOTS,          Items.IRON_BOOTS,           Items.DIAMOND_BOOTS,        Items.GOLDEN_BOOTS
        ).forEach { item ->
            MinecraftViaItemRewriter.minecraftToViaItem(item.defaultStack, VersionList.R1_8.version)?.also {
                ids[item] = it.identifier()
            }
        }
        //@formatter:on
    }

    fun update() {
        var armor = 0.0
        if (MinecraftClient.getInstance().player == null) return
        if (MinecraftClient.getInstance().isInSingleplayer) return

        MinecraftClient.getInstance().player!!.inventory.armor.forEach {
            ids[it.item]?.also { id ->
                armor += ArmorType.findById(id).armorPoints
            }
        }

        if (armor == oldArmor) return
        oldArmor = armor

        val entityPropertiesPacket = PacketWrapper.create(ClientboundPackets1_9.ENTITY_PROPERTIES, (MinecraftClient.getInstance().networkHandler!!.connection as IClientConnection_Protocol).tarasande_getViaConnection())

        entityPropertiesPacket.write(Type.VAR_INT, MinecraftClient.getInstance().player!!.id)
        entityPropertiesPacket.write(Type.INT, 1)
        entityPropertiesPacket.write(Type.STRING, "generic.armor")
        entityPropertiesPacket.write(Type.DOUBLE, 0.0)
        entityPropertiesPacket.write(Type.VAR_INT, 1)
        entityPropertiesPacket.write(Type.UUID, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"))
        entityPropertiesPacket.write(Type.DOUBLE, armor)
        entityPropertiesPacket.write(Type.BYTE, 0.toByte())

        try {
            entityPropertiesPacket.scheduleSend(Protocol1_9To1_8::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
