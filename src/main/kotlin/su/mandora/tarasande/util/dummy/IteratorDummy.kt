package su.mandora.tarasande.util.dummy

class IteratorDummy<T> : Iterator<T?> {

    override fun hasNext(): Boolean {
        return false
    }

    override fun next(): T? {
        return null
    }
}