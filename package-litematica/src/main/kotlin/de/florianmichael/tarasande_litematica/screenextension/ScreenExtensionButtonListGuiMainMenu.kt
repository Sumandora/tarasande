package de.florianmichael.tarasande_litematica.screenextension

import de.florianmichael.tarasande_litematica.generator.ManagerGenerator
import fi.dy.masa.litematica.gui.GuiMainMenu
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList

class ScreenExtensionButtonListGuiMainMenu : ScreenExtensionButtonList<GuiMainMenu>(GuiMainMenu::class.java) {

    init {
        for (generator in ManagerGenerator.list) {
            add(generator.name, direction = Direction.RIGHT) {
                mc.setScreen(ScreenBetterOwnerValues(generator.name + " generation", mc.currentScreen!!, generator))
            }
        }
    }
}
