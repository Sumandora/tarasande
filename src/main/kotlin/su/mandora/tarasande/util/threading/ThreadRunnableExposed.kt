package su.mandora.tarasande.util.threading

class ThreadRunnableExposed : Thread {

    var runnable: Runnable? = null

    constructor() : super()
    constructor(target: Runnable?) : super(target) {
        runnable = target
    }

    constructor(group: ThreadGroup?, target: Runnable?) : super(group, target) {
        runnable = target
    }

    constructor(name: String) : super(name)
    constructor(group: ThreadGroup?, name: String) : super(group, name)
    constructor(target: Runnable?, name: String?) : super(target, name) {
        runnable = target
    }

    constructor(group: ThreadGroup?, target: Runnable?, name: String) : super(group, target, name) {
        runnable = target
    }

    constructor(group: ThreadGroup?, target: Runnable?, name: String, stackSize: Long) : super(group, target, name, stackSize) {
        runnable = target
    }

    constructor(group: ThreadGroup?, target: Runnable?, name: String?, stackSize: Long, inheritThreadLocals: Boolean) : super(group, target, name, stackSize, inheritThreadLocals) {
        runnable = target
    }
}