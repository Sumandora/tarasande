package su.mandora.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.screen.pack.PackScreen
import net.minecraft.resource.ZipResourcePack
import su.mandora.tarasande.logger
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import java.io.File
import java.util.logging.Level

class ScreenExtensionButtonListPackScreen : ScreenExtensionButtonList<PackScreen>(PackScreen::class.java) {

    init {
        add(Button("Dump server pack", { mc.serverResourcePackProvider?.serverContainer != null }) {
            mc.serverResourcePackProvider?.serverContainer?.apply {
                // The pack provider, will always make ZipResourcePacks
                val base = (this.createResourcePack() as ZipResourcePack).backingZipFile

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
        })

        add(Button("Unload server pack", { mc.serverResourcePackProvider?.serverContainer != null }) {
            mc.serverResourcePackProvider.clear()
        })
    }
}
