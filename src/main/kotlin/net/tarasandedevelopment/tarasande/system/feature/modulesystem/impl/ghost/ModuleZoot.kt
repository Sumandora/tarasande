package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.ghost

import net.minecraft.item.Items
import net.tarasandedevelopment.tarasande.event.impl.EventAttack
import net.tarasandedevelopment.tarasande.event.impl.EventDisconnect
import net.tarasandedevelopment.tarasande.event.impl.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.impl.EventRotation
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.player.container.ContainerUtil

class ModuleZoot : Module("Zoot", "Removes fire status", ModuleCategory.GHOST) {
    private val delay = ValueNumber(this, "Delay", 0.0, 300.0, 500.0, 50.0)

    /*
     * This code is garbage....
     * I might delete life later, cya
     */

    private var prevSlot: Int? = null
    var state = State.IDLE
    private var intendedSlot: Int? = null
    private var targetRotation: Rotation? = null

    private val timer = TimeUtil()

    override fun onEnable() {
        onEnable(true)
    }

    fun onEnable(resetTimer: Boolean = true) {
        state = State.IDLE
        prevSlot = null
        intendedSlot = null
        if (resetTimer)
            timer.reset()
    }

    init {
        registerEvent(EventRotation::class.java, 1002) { event ->
            if (mc.player?.isOnFire != true) {
                onEnable(false)
                return@registerEvent
            }

            when (state) {
                State.EXTINGUISH, State.RETRIEVE_WATER -> {
                    if (event.dirty) {
                        onEnable()
                        return@registerEvent
                    }
                    event.rotation = Rotation(mc.player!!).withPitch(90.0F).correctSensitivity()
                    targetRotation = event.rotation
                    if (intendedSlot != null && intendedSlot != -1)
                        mc.player?.inventory?.selectedSlot = intendedSlot
                }

                State.SWITCH_BACK -> {
                    if (intendedSlot != null && intendedSlot != -1)
                        mc.player?.inventory?.selectedSlot = prevSlot
                    onEnable()
                    return@registerEvent
                }

                else -> {}
            }
            if (state != State.IDLE)
                return@registerEvent

            if (!timer.hasReached(delay.value.toLong()))
                return@registerEvent

            var waterBucket = ContainerUtil.findSlot { it.value.isOf(Items.WATER_BUCKET) }

            if (waterBucket == null)
                if (mc.player?.offHandStack?.isOf(Items.WATER_BUCKET) == true)
                    waterBucket = -1

            if (waterBucket != null) {
                state = State.EXTINGUISH
                if (waterBucket != -1) {
                    prevSlot = mc.player?.inventory?.selectedSlot!!
                    mc.player?.inventory?.selectedSlot = waterBucket
                } else {
                    intendedSlot = waterBucket
                }
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.useKey) {
                if (state == State.EXTINGUISH || state == State.RETRIEVE_WATER) {
                    if (mc.player?.lastPitch != targetRotation?.pitch)
                        return@registerEvent
                    event.keyBinding.timesPressed++
                    if (state == State.EXTINGUISH)
                        state = State.RETRIEVE_WATER
                    else if (state == State.RETRIEVE_WATER) {
                        state = State.SWITCH_BACK
                    }
                }
            }
        }
        registerEvent(EventAttack::class.java, 1) { event ->
            if (state != State.IDLE) {
                event.dirty = true
            }
        }
        registerEvent(EventDisconnect::class.java) { event ->
            if(event.connection == mc.player?.networkHandler?.connection) {
                state = State.IDLE // Abort
            }
        }
    }

    enum class State {
        IDLE, EXTINGUISH, RETRIEVE_WATER, SWITCH_BACK
    }
}