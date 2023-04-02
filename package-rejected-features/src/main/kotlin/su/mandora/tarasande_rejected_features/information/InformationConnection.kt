package su.mandora.tarasande_rejected_features.information

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.util.math.TimeUtil

class InformationNettyLag : Information("Connection", "Netty Lag") {
    private val executionTime = TimeUtil()
    private val processingTime = TimeUtil()

    init {
        EventDispatcher.add(EventTick::class.java) {
            if (it.state != EventTick.State.PRE) return@add

            if (executionTime.hasReached(1000L)) {
                mc.networkHandler?.apply {
                    connection.channel.eventLoop().execute { processingTime.reset() }
                }
                executionTime.reset()
            }
        }
    }

    override fun getMessage(): String? {
        if (mc.networkHandler == null) return null
        if (processingTime.hasReached(1500L)) {
            return (System.currentTimeMillis() - processingTime.time).toString()
        }
        return null
    }
}
