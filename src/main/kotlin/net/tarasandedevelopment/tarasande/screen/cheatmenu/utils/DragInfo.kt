package net.tarasandedevelopment.tarasande.screen.cheatmenu.utils

class DragInfo {
    var dragging: Boolean = false
    var xOffset: Double = 0.0
    var yOffset: Double = 0.0

    fun setDragInfo(dragging: Boolean, xOffset: Double, yOffset: Double) {
        this.dragging = dragging
        this.xOffset = xOffset
        this.yOffset = yOffset
    }
}