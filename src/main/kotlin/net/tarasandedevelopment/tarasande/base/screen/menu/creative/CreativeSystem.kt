package net.tarasandedevelopment.tarasande.base.screen.menu.creative

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.mixin.accessor.IInGameHud
import net.tarasandedevelopment.tarasande.screen.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.screen.menu.creative.LightItems
import net.tarasandedevelopment.tarasande.screen.menu.creative.SpecialVanillaItems
import net.tarasandedevelopment.tarasande.screen.menu.creative.cicExploits
import net.tarasandedevelopment.tarasande.screen.menu.creative.spawnerExploits
import net.tarasandedevelopment.tarasande.util.ItemUtil
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import net.tarasandedevelopment.tarasande.value.ValueButton
import net.tarasandedevelopment.tarasande.value.ValueMode

class ManagerCreative : Manager<ExploitCreative>() {

    val globalOwner: GlobalOwner

    init {
        add(
            // Special Vanilla
            SpecialVanillaItems(),
            LightItems(),

            // Spawner Exploits
            *spawnerExploits.toTypedArray(),

            // CreativeItemControl Exploit
            *cicExploits.toTypedArray()
        )

        globalOwner = GlobalOwner(this)
    }
}

class GlobalOwner(managerCreative: ManagerCreative) {

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
        managerCreative.list.forEach {
            object : ValueButtonItem(this, it.name, it.icon) {
                override fun onChange() {
                    super.onChange()

                    MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, it.name, it))
                }
            }
        }

        val settings = mutableListOf("None")
        settings.addAll(this.storages.map { s -> StringUtil.uncoverTranslation(s.translationKey) })

        packager = ValueMode(this, "Spawner Packager", false, *settings.toTypedArray())
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

abstract class ExploitCreative(val name: String, val icon: ItemStack) {

    open fun supportedVersion(): List<ProtocolVersion> = VersionList.getProtocols()

    fun createAction(name: String, icon: ItemStack, action: Action) {
        object : ValueButtonItem(this, name, icon) {
            override fun onChange() {
                action.on()
            }

            override fun isEnabled() = supportedVersion().map { p -> p.version }.contains(TarasandeMain.get().protocolHack.realClientsideVersion) || MinecraftClient.getInstance().isInSingleplayer
        }
    }
}

abstract class ExploitCreativeItem(name: String, icon: ItemStack) : ExploitCreative(name, icon) {

    init {
        createAction("Get Item", this.icon, object : Action {
            override fun on() {
                ItemUtil.give(get())
            }
        })
    }

    abstract fun get(): ItemStack
}

abstract class ExploitCreativeItemSpawner(name: String, icon: ItemStack) : ExploitCreativeItem(name, icon) {

    override fun get(): ItemStack {
        return ItemUtil.packageExploit(getSpawner())
    }

    abstract fun getSpawner(): ItemStack
}

open class ValueButtonItem(owner: Any, name: String, val stack: ItemStack) : ValueButton(owner, name) {

    override fun customRendering(matrices: MatrixStack?, tickDelta: Float) {
        (MinecraftClient.getInstance().inGameHud as IInGameHud).tarasande_invokeRenderHotbarItem(0, 0, tickDelta, matrices, this.stack)
    }
}

interface Action {

    fun on()
}
