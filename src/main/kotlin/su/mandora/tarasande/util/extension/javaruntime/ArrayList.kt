package su.mandora.tarasande.util.extension.javaruntime

fun ArrayList<*>.clearAndGC() {
    clear()
    trimToSize()
}
