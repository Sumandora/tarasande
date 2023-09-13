package su.mandora.tarasande.util.extension.minecraft

import net.minecraft.client.input.Input

fun Input.setMovementForward(movementForward: Float) {
    this.movementForward = movementForward
    this.pressingForward = movementForward > 0.0
    this.pressingBack = movementForward < 0.0
}

fun Input.setMovementSideways(movementSideways: Float) {
    this.movementSideways = movementSideways
    this.pressingLeft = movementSideways > 0.0
    this.pressingRight = movementSideways < 0.0
}