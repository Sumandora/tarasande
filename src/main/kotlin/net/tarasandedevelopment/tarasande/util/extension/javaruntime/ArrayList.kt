package net.tarasandedevelopment.tarasande.util.extension.javaruntime

fun ArrayList<*>.clearAndGC() {
    clear()
    trimToSize()
}
