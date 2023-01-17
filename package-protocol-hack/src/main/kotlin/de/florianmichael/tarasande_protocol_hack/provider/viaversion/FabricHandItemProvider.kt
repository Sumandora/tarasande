package de.florianmichael.tarasande_protocol_hack.provider.viaversion

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.minecraft.item.Item
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider
import de.florianmichael.vialoadingbase.ViaLoadingBase
import net.minecraft.item.ItemStack
import de.florianmichael.tarasande_protocol_hack.util.inventory.MinecraftViaItemRewriter
import de.florianmichael.vialoadingbase.util.VersionListEnum

class FabricHandItemProvider : HandItemProvider() {

    companion object {
        var lastUsedItem: ItemStack? = null
    }

    override fun getHandItem(info: UserConnection): Item? {
        if (lastUsedItem == null) {
            return null
        }
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.b1_7tob1_7_3)) {
            return super.getHandItem(info)
        }
        return MinecraftViaItemRewriter.minecraftToViaItem(info, lastUsedItem!!, ViaLoadingBase.getTargetVersion().originalVersion)
    }
}
