package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.pack

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.pack.PackScreen
import net.minecraft.resource.ZipResourcePack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtensionButton
import java.io.File
import java.util.logging.Level


class ScreenExtensionButtonDumpServerPack : ScreenExtensionButton<PackScreen>("Dump server pack", PackScreen::class.java) {
    override fun onClick(current: PackScreen) {
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

    override fun isVisible() = MinecraftClient.getInstance().resourcePackProvider?.serverContainer != null
}