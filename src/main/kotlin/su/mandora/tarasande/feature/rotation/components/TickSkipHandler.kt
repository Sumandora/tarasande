package su.mandora.tarasande.feature.rotation.components

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPollEvents
import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.util.INFINITE_FPS_VALUE
import su.mandora.tarasande.util.render.RenderUtil
import kotlin.math.roundToInt

class TickSkipHandler(rotations: Rotations) {

    private val updateRotationsWhenTickSkipping = ValueBoolean(rotations, "Update rotations when tick skipping", false)
    private val updateRotationsAccurately = ValueBoolean(rotations, "Update rotations accurately", true, isEnabled = { updateRotationsWhenTickSkipping.value })

    private var rotated = false
    private var lastUpdate: Long? = null

    init {
        EventDispatcher.apply {
            add(EventPollEvents::class.java) {
                rotated = true
                lastUpdate = System.currentTimeMillis()
            }
            add(EventTick::class.java) { event ->
                if (event.state != EventTick.State.PRE) return@add

                if (!rotated) {
                    // There was no frame in between the last tick and the current one
                    if (updateRotationsWhenTickSkipping.value) if (updateRotationsAccurately.value && lastUpdate != null) {
                        val timeSinceLastUpdate = System.currentTimeMillis() - lastUpdate!!
                        var imaginaryFrames = (timeSinceLastUpdate / RenderUtil.deltaTime).roundToInt()

                        val maxFps = mc.options.maxFps.value
                        if (maxFps < INFINITE_FPS_VALUE && imaginaryFrames > maxFps) imaginaryFrames = maxFps

                        if (imaginaryFrames == Int.MAX_VALUE) // conversion from Float.INFINITY, we probably don't want this
                            imaginaryFrames = maxFps


                        repeat(imaginaryFrames) {
                            rotations.createRotationEvent()
                        }
                    } else {
                        rotations.createRotationEvent()
                    }
                }

                rotated = false
            }
        }
    }
}