package su.mandora.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.SplashPotionItem
import net.minecraft.potion.PotionUtil
import su.mandora.tarasande.event.impl.EventAttack
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventKeyBindingIsPressed
import su.mandora.tarasande.event.impl.EventRotation
import su.mandora.tarasande.injection.accessor.ILivingEntity
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.util.player.container.ContainerUtil

class ModuleHealingBot : Module("Healing bot", "Automates healing using items", ModuleCategory.COMBAT) {

    private val items = ValueMode(this, "Items", true, "Soup", "Pot")
    private val stews = ValueMode(this, "Stews", true, "Mushroom Stew", "Rabbit stew", "Suspicious stew", isEnabled = { items.isSelected(0) })
    private val foodCheck = ValueBoolean(this, "Food check", false, isEnabled = { items.isSelected(0) })
    private val health = ValueNumber(this, "Health", 0.0, 0.4, 1.0, 0.01, isEnabled = { items.anySelected() })
    private val delay = ValueNumber(this, "Delay", 0.0, 300.0, 500.0, 50.0)

    private var prevSlot: Int? = null
    var state = State.IDLE
    private var intendedSlot: Int? = null
    private var intendedItem: ItemStack? = null
    private var targetRotation: Rotation? = null

    private val timer = TimeUtil()

    private fun findItem(block: (ItemStack) -> Boolean): Int? {
        if (block(mc.player?.offHandStack!!))
            return -1
        return ContainerUtil.findSlot { block(it.value) }
    }

    override fun onEnable() {
        state = State.IDLE
        prevSlot = null
        intendedSlot = null
        intendedItem = null
    }

    init {
        registerEvent(EventRotation::class.java, 9999) { event ->
            if (event.dirty) {
                onEnable()
                return@registerEvent
            }
            when (state) {
                State.THROW -> {
                    if (intendedItem?.item is SplashPotionItem) {
                        event.rotation = Rotation(mc.player!!).withPitch(90F).correctSensitivity()
                        targetRotation = event.rotation
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

            if (mc.player?.isUsingItem == true)
                return@registerEvent

            var bestItem: Int? = null

            if (items.isSelected(0)) {
                if (mc.player?.let { it.health / it.maxHealth }!! <= health.value)
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

            if (bestItem == null && items.isSelected(1) && mc.player?.isOnGround == true && !event.dirty) {
                bestItem = findItem {
                    if (it.item !is SplashPotionItem)
                        return@findItem false

                    val effects = PotionUtil.getPotionEffects(it)
                    if (effects.all { effect -> effect.effectType.isBeneficial && !(mc.player as ILivingEntity).tarasande_forceHasStatusEffect(effect.effectType) }) {
                        if (effects.any { type -> type.effectType == StatusEffects.REGENERATION || type.effectType == StatusEffects.INSTANT_HEALTH })
                            return@findItem mc.player?.let { player -> player.health / player.maxHealth }!! <= health.value
                        return@findItem true
                    }
                    return@findItem false
                }
            }

            if (bestItem != null) {
                state = State.THROW
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
                    if (timer.hasReached(delay.value.toLong())) {
                        event.pressed = true
                        state = State.SWITCH_BACK
                        timer.reset()
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
            if (event.connection == mc.player?.networkHandler?.connection) {
                state = State.IDLE // Abort
            }
        }
    }

    enum class State {
        IDLE, THROW, SWITCH_BACK
    }
}
