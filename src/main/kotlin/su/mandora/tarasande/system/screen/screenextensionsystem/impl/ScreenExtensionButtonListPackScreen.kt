package su.mandora.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.screen.pack.PackScreen
import net.minecraft.client.resource.server.ServerResourcePackManager
import su.mandora.tarasande.logger
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import java.io.File
import java.util.logging.Level

class ScreenExtensionButtonListPackScreen : ScreenExtensionButtonList<PackScreen>(PackScreen::class.java) {

    init {
        add(Button("Dump server pack", { mc.serverResourcePackProvider.manager.packs.isNotEmpty() }) {
            mc.serverResourcePackProvider.manager.packs.forEach {
                dumpServerPack(it)
            }
        })

        add(Button("Unload server pack", { mc.serverResourcePackProvider.manager.packs.isNotEmpty() }) {
            mc.serverResourcePackProvider.clear()
        })
    }

    fun dumpServerPack(packEntry: ServerResourcePackManager.PackEntry) {
        // The pack provider, will always make ZipResourcePacks
        val base = (packEntry.path ?: return).toFile()

        val name = mc.currentServerEntry?.address ?: base.name

        var target = File(mc.resourcePackDir.toFile(), "$name.zip")
        var counter = 1
        while (target.exists()) {
            target = File(mc.resourcePackDir.toFile(), "$name ($counter).zip")
            counter++
        }
        try {
            base.copyTo(target)
        } catch (t: Throwable) {
            t.printStackTrace()
            logger.log(Level.WARNING, "Wasn't able to copy $name to " + target.absolutePath)
        }
    }
}
