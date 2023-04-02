package su.mandora.tarasande_litematica.screenextension

import fi.dy.masa.litematica.gui.GuiMainMenu
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import su.mandora.tarasande_litematica.generator.ManagerGenerator

class ScreenExtensionButtonListGuiMainMenu : ScreenExtensionButtonList<GuiMainMenu>(GuiMainMenu::class.java) {

    init {
        for (generator in ManagerGenerator.list) {
            add(generator.name, direction = Direction.RIGHT) {
                mc.setScreen(ScreenBetterOwnerValues(generator.name + " generation", mc.currentScreen!!, generator))
            }
        }
    }
}
