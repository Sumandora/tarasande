package su.mandora.tarasande.clientmenu

import su.mandora.tarasande.base.clientmenu.ElementMenuScreen
import su.mandora.tarasande.base.clientmenu.ManagerClientMenu
import su.mandora.tarasande.value.ValueMode

class MenuSettingsParent(managerMenu: ManagerClientMenu) {

    val focusedMenuEntry: ValueMode

    init {
        val entries = mutableListOf("None")
        entries.addAll(managerMenu.list.filterIsInstance<ElementMenuScreen>().map { e -> e.name })

        focusedMenuEntry = ValueMode(this, "Focused menu entry", false, *entries.toTypedArray())
    }
}
