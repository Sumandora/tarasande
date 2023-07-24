package su.mandora.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.entity.player.PlayerEntity
import su.mandora.tarasande.event.impl.EventIsEntityAttackable
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleTeams : Module("Teams", "Prevents targeting of teammates", ModuleCategory.COMBAT) {

    private val mode = ValueMode(this, "Mode", true, "Vanilla team", "Display name")
    private val displayNameMode = ValueMode(this, "Display name mode", true, "Sibling styles", "Paragraph symbols", isEnabled = { mode.isSelected(1) })

    fun isTeammate(entity: PlayerEntity): Boolean {
        if (mode.isSelected(0)) {
            if (entity.isTeammate(mc.player)) {
                return true
            }
        }

        if (mode.isSelected(1)) {
            while (displayNameMode.isSelected(0)) {
                val selfName = mc.inGameHud.playerListHud.getPlayerName(mc.networkHandler?.playerList?.firstOrNull { it.profile == mc.player?.gameProfile } ?: break)
                val otherName = mc.inGameHud.playerListHud.getPlayerName(mc.networkHandler?.playerList?.firstOrNull { it.profile == entity.gameProfile } ?: break)

                if ((selfName.style ?: break) == (otherName.style ?: break) || (selfName.siblings.firstOrNull { it.style.color != null }?.style ?: break) == (otherName.siblings.firstOrNull { it.style.color != null }?.style ?: break)) {
                    return true
                }
                break
            }

            while (displayNameMode.isSelected(1)) {
                var selfTeam = mc.inGameHud.playerListHud.getPlayerName(mc.networkHandler?.playerList?.firstOrNull { it.profile == mc.player?.gameProfile } ?: break).string ?: break
                var otherTeam = mc.inGameHud.playerListHud.getPlayerName(mc.networkHandler?.playerList?.firstOrNull { it.profile == entity.gameProfile } ?: break).string ?: break

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
                    return true
                }
                break
            }
        }

        return false
    }

    init {
        registerEvent(EventIsEntityAttackable::class.java) { event ->
            if (!event.attackable) return@registerEvent
            if (event.entity !is PlayerEntity) return@registerEvent

            if (isTeammate(event.entity))
                event.attackable = false
        }
    }
}