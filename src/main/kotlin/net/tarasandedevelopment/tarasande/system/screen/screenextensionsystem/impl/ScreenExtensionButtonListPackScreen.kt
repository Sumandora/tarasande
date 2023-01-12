package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.gui.screen.pack.PackScreen
import net.minecraft.resource.ZipResourcePack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import net.tarasandedevelopment.tarasande.util.extension.mc
import java.io.File
import java.util.logging.Level

class ScreenExtensionButtonListPackScreen : ScreenExtensionButtonList<PackScreen>(PackScreen::class.java) {

    init {
        add("Dump server pack", { mc.serverResourcePackProvider?.serverContainer != null }) {
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
                    TarasandeMain.logger.log(Level.WARNING, "Wasn't able to copy $name to " + target.absolutePath)
                }
            }
        }

        add("Unload server pack", { mc.serverResourcePackProvider?.serverContainer != null }) {
            mc.serverResourcePackProvider.clear()
        }
    }
}
