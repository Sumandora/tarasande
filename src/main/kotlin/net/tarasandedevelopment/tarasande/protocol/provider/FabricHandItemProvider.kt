package net.tarasandedevelopment.tarasande.protocol.provider

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.minecraft.item.Item
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider
import net.minecraft.item.ItemStack
import net.tarasandedevelopment.tarasande.protocol.util.MinecraftViaItemRewriter

class FabricHandItemProvider : HandItemProvider() {

    companion object {
        var lastUsedItem: ItemStack = ItemStack.EMPTY
    }

    override fun getHandItem(info: UserConnection?): Item? {
        val item = MinecraftViaItemRewriter.minecraftToViaItem(lastUsedItem)
        println(item)
        return item
    }
}
