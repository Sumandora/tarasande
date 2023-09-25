package su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted

import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.meta.ValueButton
import su.mandora.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import su.mandora.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterPanel

open class ValueButtonOwnerValues(
    owner: Any,
    name: String,
    private val valuesOwner: Any,
    visible: Boolean = true,
    isEnabled: () -> Boolean = { true },
    manage: Boolean = true
) : ValueButton(owner, name, visible, isEnabled, manage, { mc.setScreen(ScreenBetterOwnerValues(name, mc.currentScreen!!, valuesOwner)) })