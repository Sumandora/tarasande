package su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted

import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.meta.ValueButton
import su.mandora.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues

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