package su.mandora.tarasande.util.render.helper

class DragInfo {
    var dragging = false
        private set
    var xOffset = 0.0
        private set
    var yOffset = 0.0
        private set

    fun setDragInfo(dragging: Boolean, xOffset: Double, yOffset: Double) {
        this.dragging = dragging
        this.xOffset = xOffset
        this.yOffset = yOffset
    }
}