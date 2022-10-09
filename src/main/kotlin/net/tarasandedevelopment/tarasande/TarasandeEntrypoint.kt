package net.tarasandedevelopment.tarasande

import net.fabricmc.loader.api.FabricLoader

object TarasandeEntrypoint {

    var dashLoader = false

    fun onInitialize() {
        dashLoader = FabricLoader.getInstance().isModLoaded("dashloader")
    }
}