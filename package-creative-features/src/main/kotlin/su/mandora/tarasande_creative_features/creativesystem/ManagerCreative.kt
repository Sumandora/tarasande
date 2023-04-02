package su.mandora.tarasande_creative_features.creativesystem

import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.*
import net.minecraft.world.GameMode
import su.mandora.tarasande.Manager
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.screen.panelsystem.ManagerPanel
import su.mandora.tarasande.util.player.chat.CustomChat
import su.mandora.tarasande.util.string.StringUtil
import su.mandora.tarasande_creative_features.creativesystem.impl.*
import su.mandora.tarasande_creative_features.creativesystem.panel.PanelElementsCreative
import su.mandora.tarasande_creative_features.creativesystem.valuecomponent.meta.ValueButtonItem
import su.mandora.tarasande_creative_features.tarasandevalues.CreativeValues

object ManagerCreative : Manager<ExploitCreative>() {

    private val storages = mutableListOf(
        Items.CHEST,
        Items.TRAPPED_CHEST,
        Items.HOPPER,
        Items.FURNACE,
        Items.BLAST_FURNACE,
        Items.DROPPER,
        Items.DISPENSER,
        Items.BARREL
    )

    private val packager: ValueMode

    init {
        add(
            ExploitCreativeSingleShutdownCreeper(this),
            ExploitCreativeCommandBlockSpawner(this),
            ExploitCreativeItemControl(this),
            ExploitCreativeSpecialVanillaItems(this),
            ExploitCreativeLightItems(this)
        )

        packager = ValueMode(this, "Spawner Packager", false, *mutableListOf("None").apply { addAll(storages.map { s -> StringUtil.uncoverTranslation(s.translationKey) }) }.toTypedArray())

        ManagerPanel.add(PanelElementsCreative())
    }

    fun getPackagedItem(): ItemStack? {
        if (!packager.anySelected() || packager.isSelected(0)) return null

        return storages.filterIndexed { index, _ -> packager.isSelected(index) }.firstOrNull()?.defaultStack
    }
}

abstract class ExploitCreative(val parent: Any, val name: String, val icon: ItemStack) {

    fun createAction(name: String, icon: ItemStack, action: () -> Unit) {
        object : ValueButtonItem(parent, name, icon, isEnabled = { mc.interactionManager!!.currentGameMode == GameMode.CREATIVE || CreativeValues.unlockInAllGameModes.value }) {
            override fun onClick() { action() }
        }
    }

    private val placed = "The item was placed in your hotbar"

    open fun give(stack: ItemStack) {
        if (!MinecraftClient.getInstance().player?.abilities!!.creativeMode) return
        if (!MinecraftClient.getInstance().player?.inventory!!.insertStack(stack)) return

        mc.setScreen(null)
        CustomChat.printChatMessage(placed)
    }

    // ReinerWahnsinn Spawner Bypass for Spigot/CraftBukkit 1.8.3
    open fun packageExploit(stack: ItemStack): ItemStack {
        val packager = ManagerCreative.getPackagedItem() ?: return stack

        val packagedItem = ItemStack(packager.item)

        val base = NbtCompound()
        val blockEntityTag = NbtCompound()

        if (packager.item == Items.FURNACE) {
            blockEntityTag.put("BurnTime", NbtShort.of(0))
            blockEntityTag.put("CookTime", NbtShort.of(0))
            blockEntityTag.put("CookTimeTotal", NbtShort.of(0))
            // [..id..]
            blockEntityTag.put("Lock", NbtString.of(""))
        }

        blockEntityTag.put("id", NbtString.of(StringUtil.uncoverTranslation(stack.translationKey).replace(" ", "")))

        val items = NbtList()

        val bypassItem = NbtCompound()
        bypassItem.put("Count", NbtByte.of(1))
        bypassItem.put("Damage", NbtShort.of(stack.damage.toShort()))
        bypassItem.put("id", NbtString.of(StringUtil.uncoverTranslation(stack.translationKey).replace(" ", "")))
        bypassItem.put("Slot", NbtShort.of(0))
        bypassItem.put("tag", stack.nbt)

        items.add(bypassItem)

        blockEntityTag.put("Items", items)
        base.put("BlockEntityTag", blockEntityTag)

        packagedItem.nbt = base

        return packagedItem
    }
}

abstract class ExploitCreativeSingle(parent: Any, name: String, icon: ItemStack) : ExploitCreative(parent, name, icon) {

    init {
        createAction(name, icon) { onPress() }
    }

    abstract fun onPress()
}
