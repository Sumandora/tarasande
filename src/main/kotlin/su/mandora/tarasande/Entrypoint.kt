package su.mandora.tarasande

import net.fabricmc.api.ClientModInitializer

class Entrypoint : ClientModInitializer {
    override fun onInitializeClient() {
        println("Hello, world!")
    }
}