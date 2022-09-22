package su.mandora.tarasande.util.math

class TimeUtil {

    var time = System.currentTimeMillis()

    fun hasReached(delay: Long) = System.currentTimeMillis() - time >= delay
    fun reset() {
        time = System.currentTimeMillis()
    }

}