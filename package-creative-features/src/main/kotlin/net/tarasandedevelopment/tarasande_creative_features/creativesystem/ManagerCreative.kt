package net.tarasandedevelopment.tarasande_creative_features.creativesystem

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.ManagerPanel
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import net.tarasandedevelopment.tarasande_creative_features.creativesystem.impl.ExploitCreativeCommandBlockSpawner
import net.tarasandedevelopment.tarasande_creative_features.creativesystem.impl.ExploitCreativeItemControl
import net.tarasandedevelopment.tarasande_creative_features.creativesystem.impl.ExploitCreativeLightItems
import net.tarasandedevelopment.tarasande_creative_features.creativesystem.impl.ExploitCreativeSpecialVanillaItems
import net.tarasandedevelopment.tarasande_creative_features.creativesystem.panel.PanelElementsCreative
import net.tarasandedevelopment.tarasande_creative_features.creativesystem.valuecomponent.meta.ValueButtonItem

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
            ExploitCreativeCommandBlockSpawner(this),

            ExploitCreativeItemControl(this),

            ExploitCreativeSpecialVanillaItems(this),
            ExploitCreativeLightItems(this),
        )

        val settings = mutableListOf("None")
        settings.addAll(this.storages.map { s -> StringUtil.uncoverTranslation(s.translationKey) })

        packager = ValueMode(this, "Spawner Packager", false, *settings.toTypedArray())

        ManagerPanel.add(PanelElementsCreative())
    }

    fun getPackagedItem(): ItemStack? {
        if (!packager.anySelected() || packager.isSelected(0)) return null

        this.storages.forEachIndexed { index, item ->
            if (packager.isSelected(index))
                return item.defaultStack
        }

        return null
    }
}

abstract class ExploitCreative(val parent: Any, val name: String, val icon: ItemStack) {

    fun createAction(name: String, icon: ItemStack, action: () -> Unit) {
        object : ValueButtonItem(parent, name, icon) {
            override fun onClick() {
                action()
            }
        }
    }
}

abstract class ExploitCreativeSingle(parent: Any, name: String, icon: ItemStack) : ExploitCreative(parent, name, icon) {

    init {
        createAction(name, icon) {
            onPress()
        }
    }

    abstract fun onPress()
}
