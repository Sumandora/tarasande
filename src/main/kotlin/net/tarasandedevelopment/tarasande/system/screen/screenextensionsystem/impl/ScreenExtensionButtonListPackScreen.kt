package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.pack.PackScreen
import net.minecraft.resource.ZipResourcePack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import java.io.File
import java.util.logging.Level

class ScreenExtensionButtonListPackScreen : ScreenExtensionButtonList<PackScreen>(PackScreen::class.java) {

    init {
        add("Dump server pack", { MinecraftClient.getInstance().resourcePackProvider?.serverContainer != null }) {
            MinecraftClient.getInstance().resourcePackProvider?.serverContainer?.apply {
                val base = (this.packFactory.get() as ZipResourcePack).base

                var target = File(MinecraftClient.getInstance().resourcePackDir, base.name + ".zip")
                var counter = 1
                while (target.exists()) {
                    target = File(MinecraftClient.getInstance().resourcePackDir, base.name + "($counter).zip")
                    counter++
                }
                try {
                    // The pack provider, will always make ZipResourcePacks
                    base.copyTo(target)
                } catch (t: Throwable) {
                    t.printStackTrace()
                    TarasandeMain.get().logger.log(Level.WARNING, "Wasn't able to copy $name to " + target.absolutePath)
                }
            }
        }

        add("Unload server pack", { MinecraftClient.getInstance().resourcePackProvider != null }) {
            MinecraftClient.getInstance().resourcePackProvider.clear()
        }
    }
}
