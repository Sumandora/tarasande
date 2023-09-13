package su.mandora.tarasande.util.math.time

class TickCounter(private val sequence: Long) {

    var time = System.currentTimeMillis()

    fun getElapsedTime(): Long {
        return System.currentTimeMillis() - time
    }

    fun getTicks(): Long {
        val ticks = getElapsedTime() / sequence
        time += ticks * sequence
        return ticks
    }

    fun reset() {
        time = System.currentTimeMillis()
    }
}