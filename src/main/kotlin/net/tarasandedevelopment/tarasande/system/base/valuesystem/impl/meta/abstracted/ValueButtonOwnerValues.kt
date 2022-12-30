package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues

open class ValueButtonOwnerValues(owner: Any, name: String, val valuesOwner: Any, manage: Boolean = true) : ValueButton(owner, name, manage) {
    override fun onChange() {
        MinecraftClient.getInstance().setScreen(ScreenBetterOwnerValues(MinecraftClient.getInstance().currentScreen!!, name, valuesOwner))
    }
}