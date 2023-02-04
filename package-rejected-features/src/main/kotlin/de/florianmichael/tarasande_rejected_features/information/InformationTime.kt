package de.florianmichael.tarasande_rejected_features.information

import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InformationDate : Information("Time", "Date") {
    override fun getMessage() = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now())!!
}

class InformationTime : Information("Time", "Time") {
    override fun getMessage() = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now())!!
}
