package su.mandora.tarasande.util.extension.kotlinruntime

fun Double.roundTo(factor: Double): Double {
    val tmp = this * factor
    val roundedTmp = Math.round(tmp)
    return roundedTmp / factor
}