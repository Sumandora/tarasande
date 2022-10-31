package net.tarasandedevelopment.tarasande.features.protocol.provider

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.minecraft.item.Item
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider
import net.minecraft.item.ItemStack
import net.tarasandedevelopment.tarasande.features.protocol.util.MinecraftViaItemRewriter

class FabricHandItemProvider : HandItemProvider() {

    companion object {
        var lastUsedItem: ItemStack = ItemStack.EMPTY
    }

    override fun getHandItem(info: UserConnection?): Item? {
        return MinecraftViaItemRewriter.minecraftToViaItem(lastUsedItem)
    }
}
