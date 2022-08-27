package su.mandora.tarasande.module.combat

import net.minecraft.entity.player.PlayerEntity
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventIsEntityAttackable
import su.mandora.tarasande.value.ValueMode
import java.util.function.Consumer

class ModuleTeams : Module("Teams", "Prevents targeting teammates", ModuleCategory.COMBAT) {

    private val mode = ValueMode(this, "Mode", true, "Vanilla team", "Display name")
    private val displayNameMode = object : ValueMode(this, "Display name mode", true, "Sibling styles", "Paragraph symbols") {
        override fun isEnabled() = mode.isSelected(1)
    }

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventIsEntityAttackable) {
            if (!event.attackable) return@Consumer
            if (event.entity !is PlayerEntity) return@Consumer

            if (mode.isSelected(0)) {
                if (event.entity.isTeammate(mc.player)) {
                    event.attackable = false
                }
            }

            if (mode.isSelected(1)) {
                while (displayNameMode.isSelected(0)) {
                    val selfName = mc.inGameHud.playerListHud.getPlayerName(mc.networkHandler?.playerList?.firstOrNull { it.profile == mc.player?.gameProfile } ?: break)
                    val otherName = mc.inGameHud.playerListHud.getPlayerName(mc.networkHandler?.playerList?.firstOrNull { it.profile == event.entity.gameProfile } ?: break)

                    if ((selfName.style ?: break) == (otherName.style ?: break) || (selfName.siblings.firstOrNull { it.style.color != null }?.style ?: break) == (otherName.siblings.firstOrNull { it.style.color != null }?.style ?: break)) {
                        event.attackable = false
                    }
                    break
                }

                while (displayNameMode.isSelected(1)) {
                    var selfTeam = mc.inGameHud.playerListHud.getPlayerName(mc.networkHandler?.playerList?.firstOrNull { it.profile == mc.player?.gameProfile } ?: break).string ?: break
                    var otherTeam = mc.inGameHud.playerListHud.getPlayerName(mc.networkHandler?.playerList?.firstOrNull { it.profile == event.entity.gameProfile } ?: break).string ?: break

                    if (selfTeam.length <= 2 || !selfTeam.startsWith("§")) break

                    if (otherTeam.length <= 2 || !otherTeam.startsWith("§")) break

                    while (selfTeam.length > 2 && selfTeam.startsWith("§")) {
                        selfTeam = selfTeam.substring(1, selfTeam.length)
                        selfTeam = if (selfTeam.first() in 'a'..'f') {
                            selfTeam.first().toString()
                        } else {
                            selfTeam.substring(1, selfTeam.length)
                        }
                    }

                    while (otherTeam.length > 2 && otherTeam.startsWith("§")) {
                        otherTeam = otherTeam.substring(1, otherTeam.length)
                        otherTeam = if (otherTeam.first() in 'a'..'f') {
                            otherTeam.first().toString()
                        } else {
                            otherTeam.substring(1, otherTeam.length)
                        }
                    }

                    if (selfTeam == otherTeam) {
                        event.attackable = false
                    }
                    break
                }
            }
        }
    }
}