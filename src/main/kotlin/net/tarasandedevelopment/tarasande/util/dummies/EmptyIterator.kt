package net.tarasandedevelopment.tarasande.util.dummies

class EmptyIterator<T> : Iterator<T?> {

    override fun hasNext(): Boolean {
        return false
    }

    override fun next(): T? {
        return null
    }
}