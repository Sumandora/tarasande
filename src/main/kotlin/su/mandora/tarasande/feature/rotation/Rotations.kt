package su.mandora.tarasande.feature.rotation

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPollEvents
import su.mandora.tarasande.event.impl.EventRotation
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.feature.rotation.component.*
import su.mandora.tarasande.feature.rotation.component.correctmovement.CorrectMovement
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean

@Suppress("unused")
object Rotations {

    var fakeRotation: Rotation? = null

    val correctMovement = CorrectMovement(this)
    private val tickSkipHandler = TickSkipHandler(this)
    private val rotateToOrigin = RotateToOrigin(this)
    private val correctMovementPacket = CorrectMovementPacket(this)
    private val correctRotationSet = CorrectRotationSet(this)
    private val preventRotationLeak = PreventRotationLeak(this)

    private val visualizeFakeRotation = ValueBoolean(this, "Visualize fake rotation", false)
    val adjustThirdPersonModel = ValueBoolean(this, "Adjust third person model", true)

    init {
        EventDispatcher.add(EventPollEvents::class.java) {
            createRotationEvent()
        }
    }

    fun createRotationEvent() {
        if (mc.player != null && mc.interactionManager != null) {
            val realRotation = Rotation(mc.player!!)
            val eventRotation = EventRotation(realRotation)

            EventDispatcher.call(eventRotation)

            if (eventRotation.dirty) {
                fakeRotation = eventRotation.rotation
            } else if (fakeRotation != null) {
                rotateToOrigin.handleRotateToOrigin(realRotation)
            }

            if (visualizeFakeRotation.value) fakeRotation?.applyOn(mc.player!!)
        }
    }

}