package de.florianmichael.tarasande_discord_rpc

import club.minnced.discord.rpc.DiscordEventHandlers
import club.minnced.discord.rpc.DiscordEventHandlers.OnReady
import club.minnced.discord.rpc.DiscordRPC
import club.minnced.discord.rpc.DiscordRichPresence
import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.EventConnectServer
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.logger
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.Thread
import su.mandora.event.EventDispatcher
import java.net.InetSocketAddress

class TarasandeDiscordRPC : ClientModInitializer {

    private var showConnectedAddress: ValueBoolean? = null
    private val lib = DiscordRPC.INSTANCE

    override fun onInitializeClient() {
        EventDispatcher.apply {
            add(EventConnectServer::class.java) {
                if (showConnectedAddress!!.value) {
                    updateDetails("Playing on " + (it.connection.address as InetSocketAddress).hostName)
                }
            }
            add(EventDisconnect::class.java) { updateDetails() }
            add(EventSuccessfulLoad::class.java) {
                showConnectedAddress = ValueBoolean(this, "Show connected address", false)

                val handlers = DiscordEventHandlers()

                handlers.ready = OnReady { logger.info("Connected to Discord RPC via " + it.username) }
                lib.Discord_Initialize("1073339087857135757", handlers, true, "")

                updateDetails()

                Thread("Discord-RPC-Callback-Handler") {
                    while (!Thread.currentThread().isInterrupted) {
                        lib.Discord_RunCallbacks()
                        try {
                            Thread.sleep(2000)
                        } catch (_: InterruptedException) {}
                    }
                }.start()

                ValueButtonOwnerValues(ClientValues, "Discord values", this)
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
