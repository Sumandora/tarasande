package net.tarasandedevelopment.tarasande.features.module.combat

import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.SplashPotionItem
import net.minecraft.potion.PotionUtil
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventAttack
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientPlayerEntity
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.value.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.value.impl.ValueMode
import net.tarasandedevelopment.tarasande.value.impl.ValueNumber

class ModuleHealingBot : Module("Healing bot", "Automates healing using items", ModuleCategory.COMBAT) {

    private val items = ValueMode(this, "Items", true, "Soup", "Pot")
    private val stews = object : ValueMode(this, "Stews", true, "Mushroom Stew", "Rabbit stew", "Suspicious stew") {
        override fun isEnabled() = items.isSelected(0)
    }
    private val foodCheck = object : ValueBoolean(this, "Food check", false) {
        override fun isEnabled() = items.isSelected(0)
    }
    private val health = object : ValueNumber(this, "Health", 0.0, 4.0, 10.0, 0.1) {
        override fun isEnabled() = items.anySelected()
    }
    private val delay = ValueNumber(this, "Delay", 0.0, 300.0, 500.0, 50.0)

    /*
     * This code is garbage....
     * I might delete life later, cya
     */

    private var prevSlot: Int? = null
    internal var state = State.IDLE
    private var intendedSlot: Int? = null
    private var intendedItem: ItemStack? = null
    private var targetRotation: Rotation? = null

    private val timer = TimeUtil()

    private fun findItem(block: (ItemStack) -> Boolean): Int? {
        if (block.invoke(mc.player?.offHandStack!!))
            return -1
        for (slot in 0..8) {
            val stack = mc.player?.inventory?.main?.get(slot)
            if (stack != null && block.invoke(stack)) {
                return slot
            }
        }
        return null
    }

    override fun onEnable() {
        state = State.IDLE
        prevSlot = null
        intendedSlot = null
        intendedItem = null
    }

    init {
        registerEvent(EventPollEvents::class.java, 1) { event ->
            if (event.dirty)
                return@registerEvent

            when (state) {
                State.THROW -> {
                    if (intendedItem?.item is SplashPotionItem) {
                        event.rotation = Rotation(mc.player!!).also { it.pitch = 90.0f }.correctSensitivity()
                        targetRotation = event.rotation
                        event.minRotateToOriginSpeed = 1.0
                        event.maxRotateToOriginSpeed = 1.0
                    }
                    mc.player?.inventory?.selectedSlot = intendedSlot
                }

                State.SWITCH_BACK -> {
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

            var bestItem: Int? = null

            if (items.isSelected(0)) {
                if (mc.player?.health?.div(2.0)!! <= health.value)
                    if (!foodCheck.value || mc.player?.hungerManager?.isNotFull == true)
                        bestItem = findItem {
                            if (stews.isSelected(0) && it.item == Items.MUSHROOM_STEW)
                                true
                            else if (stews.isSelected(1) && it.item == Items.RABBIT_STEW)
                                true
                            else
                                stews.isSelected(2) && it.item == Items.SUSPICIOUS_STEW
                        }
            }

            if (bestItem == null && items.isSelected(1)) {
                bestItem = findItem {
                    if (it.item !is SplashPotionItem)
                        return@findItem false

                    val effects = PotionUtil.getPotionEffects(it)
                    if (effects.size == 1 && effects[0].effectType.let { it == StatusEffects.REGENERATION || it == StatusEffects.INSTANT_HEALTH })
                        return@findItem mc.player?.health?.div(2.0)!! <= health.value
                    return@findItem effects.all { it.effectType.isBeneficial && !(mc.player as IClientPlayerEntity).tarasande_forceHasStatusEffect(it.effectType) }
                }
            }

            if (bestItem != null) {
                state = State.THROW
                timer.reset()
                intendedItem = mc.player?.offHandStack
                intendedSlot = bestItem
                if (bestItem != -1) {
                    prevSlot = mc.player?.inventory?.selectedSlot!!
                    mc.player?.inventory?.selectedSlot = bestItem
                    intendedItem = mc.player?.inventory?.main?.get(bestItem)
                }
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.useKey) {
                if (state == State.THROW) {
                    if (intendedItem?.item is SplashPotionItem && mc.player?.lastPitch != targetRotation?.pitch)
                        return@registerEvent
                    event.pressed = true
                    if ((intendedSlot == -1 && mc.player?.offHandStack?.item != intendedItem?.item) || mc.player?.mainHandStack?.item != intendedItem?.item) {
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
    }

    enum class State {
        IDLE, THROW, SWITCH_BACK
    }
}
