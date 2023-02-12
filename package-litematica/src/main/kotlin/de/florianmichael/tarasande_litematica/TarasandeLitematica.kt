package de.florianmichael.tarasande_litematica

import de.florianmichael.tarasande_litematica.screenextension.ScreenExtensionButtonListGuiMainMenu
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.TARASANDE_NAME
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import su.mandora.event.EventDispatcher
import java.util.logging.Logger

class TarasandeLitematica : ClientModInitializer {
    private val logger = Logger.getLogger("$TARASANDE_NAME-litematica")!!

    override fun onInitializeClient() {
        if (!FabricLoader.getInstance().isModLoaded("litematica")) {
            logger.warning("$TARASANDE_NAME Litematica is not designed to run without 'Litematica' installed")
            return
        }
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerScreenExtension.add(ScreenExtensionButtonListGuiMainMenu())
        }
    }
}
