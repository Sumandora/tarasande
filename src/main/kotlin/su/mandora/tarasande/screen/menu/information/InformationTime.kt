package su.mandora.tarasande.screen.menu.information

import su.mandora.tarasande.base.screen.menu.information.Information
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InformationDate : Information("Time", "Date") {
	override fun getMessage() = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now())!!
}

class InformationTime : Information("Time", "Time") {
	override fun getMessage() = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now())!!
}