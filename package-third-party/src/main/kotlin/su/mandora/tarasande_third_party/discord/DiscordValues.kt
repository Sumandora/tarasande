package su.mandora.tarasande_third_party.discord

import club.minnced.discord.rpc.DiscordEventHandlers
import club.minnced.discord.rpc.DiscordEventHandlers.OnReady
import club.minnced.discord.rpc.DiscordRPC
import club.minnced.discord.rpc.DiscordRichPresence
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventConnectServer
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.logger
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.util.extension.javaruntime.Thread
import java.net.InetSocketAddress

object DiscordValues {

    private var discordRPCCallbackHandler: Thread? = null

    private val enabled = object : ValueBoolean(this, "Enabled", false) {
        override fun onChange(oldValue: Boolean?, newValue: Boolean) {
            if (newValue) {
                val handlers = DiscordEventHandlers()

                handlers.ready = OnReady { logger.info("Connected to Discord RPC via " + it.username) }
                lib.Discord_Initialize("1074056915262378055", handlers, true, "")

                updateDetails()
                discordRPCCallbackHandler = Thread("Discord-RPC-Callback-Handler") {
                    while (!Thread.currentThread().isInterrupted) {
                        lib.Discord_RunCallbacks()
                        try {
                            Thread.sleep(2000)
                        } catch (_: InterruptedException) {}
                    }
                }.apply { start() }
            } else if (oldValue != null) {
                lib.Discord_ClearPresence()
                lib.Discord_Shutdown()
                discordRPCCallbackHandler?.interrupt()
            }
        }
    }
    private val showConnectedAddress = ValueBoolean(this, "Show connected address", false)

    private val lib = DiscordRPC.INSTANCE

    init {
        EventDispatcher.apply {
            add(EventConnectServer::class.java) {
                if (enabled.value && showConnectedAddress.value) {
                    updateDetails("Playing on " + (it.connection.address as InetSocketAddress).hostName)
                }
            }
            add(EventDisconnect::class.java) {
                if (enabled.value) updateDetails()
            }
        }
    }

    private fun updateDetails(text: String = "Minecraft Protocol and Utility Mod") {
        lib.Discord_UpdatePresence(DiscordRichPresence().apply {
            startTimestamp = System.currentTimeMillis() / 1000 // epoch second
            largeImageKey = "icon"
            details = text
        })
    }
}