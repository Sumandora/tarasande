package net.tarasandedevelopment.tarasande.screen.cheatmenu.information

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.information.Information
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import java.nio.charset.StandardCharsets

class InformationServerBrand : Information("Server", "Server Brand") {

    private val compiledRegex = Regex("\\(.*?\\) ")
    private val regex = ValueBoolean(this, "Regex", true)

    override fun getMessage(): String? {
        if (!MinecraftClient.getInstance().isInSingleplayer) {
            var brand: String? = MinecraftClient.getInstance().player!!.serverBrand ?: return null

            if (regex.value)
                brand = brand!!.replace(compiledRegex, "")
            return brand
        }
        return null
    }
}

class InformationOpenChannels : Information("Server", "Open Channels") {

    private val openChannels = ArrayList<String>()

    init {
        TarasandeMain.get().eventDispatcher.also {
            it.add(EventPacket::class.java) {
                if (it.type == EventPacket.Type.RECEIVE && it.packet is CustomPayloadS2CPacket) {
                    if (it.packet.channel.toString() == "minecraft:register") {
                        it.packet.data.toString(StandardCharsets.UTF_8).split("\u0000").forEach { data ->
                            if (!openChannels.contains(data))
                                openChannels.add(data)
                        }
                    } else if (it.packet.channel.toString() == "minecraft:unregister") {
                        it.packet.data.toString(StandardCharsets.UTF_8).split("\u0000").forEach { data ->
                            if (openChannels.contains(data))
                                openChannels.remove(data)
                        }
                    }
                }
            }
            it.add(EventDisconnect::class.java) {
                openChannels.clear()
            }
        }
    }

    override fun getMessage(): String? {
        if (openChannels.isEmpty()) return null

        return "\n" + openChannels.joinToString("\n")
    }
}

class InformationVanishedPlayers : Information("Server", "Vanished players") {

    private val vanishedPlayers = ArrayList<String>()

    init {
        TarasandeMain.get().eventDispatcher.also {
            it.add(EventPacket::class.java) {
                if (it.type == EventPacket.Type.RECEIVE) {
                    when (it.packet) {
                        is PlayerListS2CPacket -> {
                            if (it.packet.action != PlayerListS2CPacket.Action.ADD_PLAYER && it.packet.action != PlayerListS2CPacket.Action.REMOVE_PLAYER)
                                for (packetEntry in it.packet.entries)
                                    if (MinecraftClient.getInstance().networkHandler?.playerList?.any { entry -> entry.profile?.id == packetEntry.profile.id } != true)
                                        when (it.packet.action) {
                                            PlayerListS2CPacket.Action.UPDATE_GAME_MODE -> {
                                                vanishedPlayers.add("Gamemode: " + packetEntry.profile.id + " -> " + StringUtil.formatEnumTypes(packetEntry.gameMode.name))
                                            }

                                            PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME -> {
                                                vanishedPlayers.add("Name: " + packetEntry.profile.id + " -> " + packetEntry.displayName)
                                            }

                                            PlayerListS2CPacket.Action.UPDATE_LATENCY -> {
                                                vanishedPlayers.add("Latency: " + packetEntry.profile.id + " -> " + packetEntry.latency)
                                            }

                                            else -> {
                                                vanishedPlayers.add("Invalid: $packetEntry")
                                            }
                                        }
                        }

                        is PlayerRespawnS2CPacket -> {
                            vanishedPlayers.clear()
                        }
                    }
                }
            }
        }
    }

    override fun getMessage(): String? {
        if (vanishedPlayers.isEmpty()) return null

        return "\n" + vanishedPlayers.joinToString("\n")
    }
}