package su.mandora.tarasande.util.render.animation

class TimeAnimator(private val animationLength: Long) {

    var reversed = false
    private var baseTime = 0L

    @JvmName("setReversedAndUpdate")
    fun setReversed(reversed: Boolean) {
        val remainingTime = (animationLength - (System.currentTimeMillis() - baseTime)).coerceAtLeast(0)
        baseTime = System.currentTimeMillis() - remainingTime
        this.reversed = reversed
    }

    fun getProgress(): Double {
        val delta = System.currentTimeMillis() - baseTime
        var animation = (delta / animationLength.toDouble()).coerceAtLeast(0.0).coerceAtMost(1.0)
        if (reversed) animation = 1.0 - animation
        return animation
    }

    fun setProgress(progress: Double) {
        baseTime = System.currentTimeMillis() - (animationLength * progress).toLong()
    }

    fun isCompleted() = System.currentTimeMillis() - baseTime > animationLength

}