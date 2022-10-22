package net.tarasandedevelopment.tarasande.protocol.util

import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.minecraft.item.DataItem
import com.viaversion.viaversion.api.minecraft.item.Item
import com.viaversion.viaversion.libs.opennbt.tag.builtin.*
import com.viaversion.viaversion.protocols.protocol1_10to1_9_3.Protocol1_10To1_9_3_4
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.item.ItemRewriter
import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.SharedConstants
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IProtocolManagerImpl_Protocol

object MinecraftViaItemRewriter {

    private val itemMappings = ArrayList<Pair<Int, ViaItemRewriterImpl>>()

    fun minecraftToViaItem(stack: ItemStack) = minecraftToViaItem(stack, SharedConstants.getProtocolVersion())

    fun minecraftToViaItem(stack: ItemStack, targetVersion: Int): DataItem? {
        if (itemMappings.isEmpty()) {
            itemMappings.addAll(this.allItemMappings()) // Generates the remapper for the first time
        }
        if (stack == ItemStack.EMPTY) return null

        var via = DataItem()
        via.setIdentifier(Registry.ITEM.getRawId(stack.item))
        via.setAmount(stack.count)
        via.setData(stack.damage.toShort())

        if (stack.nbt != null) {
            val subNbt = CompoundTag()

            stack.nbt!!.keys.forEach { subNbt.put(it, minecraftToViaNBT(stack.nbt!!.get(it)!!)) }
            via.setTag(subNbt)
        }

        for (itemMapping in itemMappings) {
            if (itemMapping.first < targetVersion) continue
            via = itemMapping.second.remapItem(via) as DataItem
        }

        return via
    }

    private fun allItemMappings(): List<Pair<Int, ViaItemRewriterImpl>> {
        val list = ArrayList<Pair<Int, ViaItemRewriterImpl>>()
        (Via.getManager().protocolManager as IProtocolManagerImpl_Protocol).protocolhack_getProtocols().filter { p -> p.second.itemRewriter != null }.forEach {
            list.add(Pair(it.first, object : ViaItemRewriterImpl {
                override fun remapItem(dataItem: Item): Item? {
                    return it.second.itemRewriter!!.handleItemToServer(dataItem)
                }
            }))
        }
        list.add(Pair(VersionList.R1_9_4.version, object : ViaItemRewriterImpl {
            override fun remapItem(dataItem: Item): Item? {
                return Via.getManager().protocolManager.getProtocol(Protocol1_10To1_9_3_4::class.java)!!.itemRewriter!!.handleItemToServer(dataItem)
            }
        }))
        list.add(Pair(VersionList.R1_8.version, object : ViaItemRewriterImpl {
            override fun remapItem(dataItem: Item): Item {
                com.viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter.toServer(dataItem)
                return dataItem
            }
        }))
        list.add(Pair(LegacyProtocolVersion.R1_7_10.version, object : ViaItemRewriterImpl {
            override fun remapItem(dataItem: Item): Item {
                ItemRewriter.toServer(dataItem)
                return dataItem
            }
        }))
        return list
    }

    interface ViaItemRewriterImpl {
        fun remapItem(dataItem: Item): Item?
    }

    private fun minecraftToViaNBT(minecraft: NbtElement): Tag {
        when (minecraft) {
            is NbtByte -> { return ByteTag(minecraft.byteValue()) }
            is NbtDouble -> { return DoubleTag(minecraft.doubleValue()) }
            is NbtInt -> { return IntTag(minecraft.intValue()) }
            is NbtLong -> { return LongTag(minecraft.longValue()) }
            is NbtShort -> { return ShortTag(minecraft.shortValue()) }
            is NbtFloat -> { return FloatTag(minecraft.floatValue()) }
            is NbtString -> { return StringTag(minecraft.asString()) }
            is NbtIntArray -> { return IntArrayTag(minecraft.intArray) }
            is NbtLongArray -> { return LongArrayTag(minecraft.longArray) }
            is NbtList -> {
                val tag = ListTag()
                minecraft.forEach { tag.add(minecraftToViaNBT(it)) }
                return tag
            }
            is NbtCompound -> {
                val tag = CompoundTag()
                minecraft.keys.forEach { tag.put(it, minecraftToViaNBT(minecraft.get(it)!!)) }
                return tag
            }
        }
        return CompoundTag()
    }
}
