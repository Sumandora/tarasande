package su.mandora.tarasande.util.math.time

class TimeUtil {

    var time = System.currentTimeMillis()

    fun getElapsedTime(): Long {
        return System.currentTimeMillis() - time
    }

    fun getTimeLeft(delay: Long): Long {
        return delay - getElapsedTime()
    }

    fun hasReached(delay: Long) = getElapsedTime() >= delay

    fun reset() {
        time = System.currentTimeMillis()
    }

}