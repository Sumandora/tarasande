package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted

import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues

open class ValueButtonOwnerValues(
    owner: Any,
    name: String,
    val valuesOwner: Any,
    visible: Boolean = true,
    isEnabled: () -> Boolean = { true },
    manage: Boolean = true
) : ValueButton(owner, name, visible, isEnabled, manage) {
    override fun onClick() {
        mc.setScreen(ScreenBetterOwnerValues(name, mc.currentScreen!!, valuesOwner))
    }
}