package net.tarasandedevelopment.tarasande.feature.friend.panel

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.feature.friend.Friends
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.api.PanelElements

class PanelElementsFriends(private val friends: Friends) : PanelElements<ElementWidthPlayer>("Friends", 150.0, 100.0) {

    override fun tick() {
        elementList.removeIf {
            if (MinecraftClient.getInstance().networkHandler?.playerList?.none { p -> p.profile == it.gameProfile }!!) {
                it.onClose()
                TarasandeMain.friends().apply {
                    if (isFriend(it.gameProfile))
                        changeFriendState(it.gameProfile)
                }
                return@removeIf true
            }
            false
        }
        for (player in MinecraftClient.getInstance().networkHandler?.playerList!!) {
            if (player != null && player.profile != MinecraftClient.getInstance().player?.gameProfile && elementList.none { it.gameProfile == player.profile } && player.profile.name.isNotEmpty()) {
                val elementWidthPlayer = ElementWidthPlayer(player.profile, 0.0)
                elementWidthPlayer.init()
                elementList.add(elementWidthPlayer)
            }
        }
        elementList.sortBy { !friends.isFriend(it.gameProfile) } // friends to top
        super.tick()
    }
}