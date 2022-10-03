package net.tarasandedevelopment.tarasande.base.creative

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.creative.SpecialVanillaItems
import net.tarasandedevelopment.tarasande.mixin.accessor.IInGameHud
import net.tarasandedevelopment.tarasande.screen.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.value.ValueButton

class ManagerCreative : Manager<ExploitCreative>() {

    val globalOwner: GlobalOwner

    init {
        add(
            SpecialVanillaItems()
        )

        globalOwner = GlobalOwner(this)
    }
}

class GlobalOwner(managerCreative: ManagerCreative) {

    init {
        managerCreative.list.forEach {
            object : ValueButtonItem(this, it.name, it.icon) {
                override fun onChange() {
                    super.onChange()

                    MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, it.name + " Creative Items", it))
                }
            }
        }
    }
}

abstract class ExploitCreative(val name: String, val icon: ItemStack) {

    fun createAction(name: String, icon: ItemStack, action: Action) {
        object : ValueButtonItem(this, name, icon) {
            override fun onChange() {
                action.on()
            }
        }
    }
}

open class ValueButtonItem(owner: Any, name: String, val stack: ItemStack) : ValueButton(owner, name) {

    override fun customRendering(matrices: MatrixStack?, tickDelta: Float) {
        (MinecraftClient.getInstance().inGameHud as IInGameHud).tarasande_invokeRenderHotbarItem(0, 0, tickDelta, matrices, this.stack)
    }
}

interface Action {

    fun on()
}
