package su.mandora.tarasande.base.esp

import su.mandora.tarasande.base.Manager

class ManagerESP : Manager<ESPElement>() {

    init {
        add(

        )
    }
}

abstract class ESPElement(val name: String, val forbiddenOrientations: ArrayList<Orientation>, val rotate: Boolean = true) {
    abstract fun draw(sideBegin: Double, sideEnd: Double, orientation: Orientation)
    abstract fun getHeight(): Double
}

enum class Orientation {
    TOP, LEFT, BOTTOM, RIGHT
}