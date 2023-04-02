package su.mandora.tarasande_litematica

import su.mandora.tarasande_litematica.screenextension.ScreenExtensionButtonListGuiMainMenu
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
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
