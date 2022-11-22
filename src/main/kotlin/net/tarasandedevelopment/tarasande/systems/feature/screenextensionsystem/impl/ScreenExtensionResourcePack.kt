package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.pack.PackScreen
import net.minecraft.resource.ZipResourcePack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtensionButton
import java.io.File


class ScreenExtensionResourcePackConvertServerPacksToFiles : ScreenExtensionButton<PackScreen>("Dump server pack", PackScreen::class.java) {
    override fun onClick(current: PackScreen) {
        MinecraftClient.getInstance().resourcePackProvider?.apply {
            serverContainer?.apply {
                val base = (this.packFactory.get() as ZipResourcePack).base

                var target = File(MinecraftClient.getInstance().resourcePackDir, base.name + ".zip")
                var counter = 1
                while(target.exists()) {
                    target = File(MinecraftClient.getInstance().resourcePackDir, base.name + "($counter).zip")
                    counter++
                }
                try {
                    // The pack provider, will always make ZipResourcePacks
                    base.copyTo(target)
                } catch (t: Throwable) {
                    t.printStackTrace()
                    TarasandeMain.get().logger.error("Wasn't able to copy $name to " + target.absolutePath)
                }
            }
            clear()
        }
    }

    override fun isVisible() = MinecraftClient.getInstance().resourcePackProvider != null
}