package net.tarasandedevelopment.tarasande.feature.friend.panel

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.feature.friend.Friends
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.PanelElements

class PanelElementsFriends(private val friends: Friends) : PanelElements<ElementPlayer>("Friends", 150.0, 100.0) {

    override fun tick() {
        elementList.removeIf {
            if (MinecraftClient.getInstance().networkHandler?.playerList?.none { p -> p.profile == it.gameProfile }!!) {
                it.onClose()
                return@removeIf true
            }
            false
        }
        for (player in MinecraftClient.getInstance().networkHandler?.playerList!!) {
            if (player != null && player.profile != MinecraftClient.getInstance().player?.gameProfile && elementList.none { it.gameProfile == player.profile } && player.profile.name.isNotEmpty()) {
                val elementPlayer = ElementPlayer(player.profile, 0.0)
                elementPlayer.init()
                elementList.add(elementPlayer)
            }
        }
        elementList.sortBy { !friends.isFriend(it.gameProfile) } // friends to top
        super.tick()
    }
}