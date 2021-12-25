package su.mandora.tarasande.module.combat

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventIsEntityAttackable
import su.mandora.tarasande.value.ValueMode
import java.util.function.Consumer

class ModuleTeams : Module("Teams", "Prevents targeting teammates", ModuleCategory.COMBAT) {

	val mode = ValueMode(this, "Mode", true, "Vanilla team", "Display name")

	val eventConsumer = Consumer<Event> { event ->
		if(event is EventIsEntityAttackable) {
			if(!event.attackable)
				return@Consumer

			if(mode.isSelected(0)) {
				if(event.entity?.isTeammate(mc.player)!!) {
					event.attackable = false
				}
			}

			if(mode.isSelected(1)) {
				val selfName = mc.player?.displayName?.string!!
				val otherName = event.entity?.displayName?.string!!

				val selfHasTeam = selfName.startsWith("§")
				val otherHasTeam = otherName.startsWith("§")

				if(selfHasTeam != otherHasTeam)
					event.attackable = false
				else if(selfHasTeam && otherHasTeam) {
					val selfTeam = selfName.subSequence(0, 2)
					val otherTeam = otherName.subSequence(0, 2)

					if(selfTeam == otherTeam)
						event.attackable = false
				}
			}
		}
	}
}