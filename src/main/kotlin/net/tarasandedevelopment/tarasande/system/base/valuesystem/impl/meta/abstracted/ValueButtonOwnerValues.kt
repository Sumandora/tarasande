package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.ValueButton
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.util.extension.mc

open class ValueButtonOwnerValues(owner: Any, name: String, val valuesOwner: Any, manage: Boolean = true) : ValueButton(owner, name, manage) {
    override fun onChange() {
        mc.setScreen(ScreenBetterOwnerValues(mc.currentScreen!!, name, valuesOwner))
    }
}