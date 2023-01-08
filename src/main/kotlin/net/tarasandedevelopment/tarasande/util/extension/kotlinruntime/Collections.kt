package net.tarasandedevelopment.tarasande.util.extension.kotlinruntime

inline fun <T> Iterable<T>.prefer(predicate: (T) -> Boolean): T {
    for (element in this) if (predicate(element)) return element
    return first() // None of them match... sad noises
}