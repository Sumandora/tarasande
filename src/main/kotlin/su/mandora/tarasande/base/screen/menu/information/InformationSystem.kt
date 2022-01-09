package su.mandora.tarasande.base.screen.menu.information

import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.screen.menu.information.*

class ManagerInformation : Manager<Information>() {

	init {
		add(
			InformationXYZ(),
			InformationNetherXYZ(),
			InformationRotation(),
			InformationFakeRotation(),
			InformationDate(),
			InformationTime(),
			InformationHandlers(),
			InformationEntities(),
			InformationWorldTime(),
			InformationTimeShifted()
		)
	}

	fun getAllOwners(): ArrayList<String> {
		val list = ArrayList<String>()
		for (information in this.list) {
			if (information.isVisible())
				if (!list.contains(information.owner))
					list.add(information.owner)
		}
		return list
	}

	fun getAllInformation(owner: String): ArrayList<Information> {
		val list = ArrayList<Information>()
		for (information in this.list) {
			if (information.isVisible())
				if (information.owner == owner)
					list.add(information)
		}
		return list
	}

}

abstract class Information(val owner: String, val information: String) {
	fun isVisible() = true
	abstract fun getMessage(): String?
}
