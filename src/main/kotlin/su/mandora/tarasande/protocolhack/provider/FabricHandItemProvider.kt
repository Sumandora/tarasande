package su.mandora.tarasande.protocolhack.provider

import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider
import net.minecraft.item.ItemStack

class FabricHandItemProvider : HandItemProvider() {
    companion object {
        var lastUsedItem = ItemStack.EMPTY
    }
}
