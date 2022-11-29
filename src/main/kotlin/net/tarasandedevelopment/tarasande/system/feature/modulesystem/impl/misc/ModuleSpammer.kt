package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.tarasandedevelopment.tarasande.event.EventChat
import net.tarasandedevelopment.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import org.apache.commons.lang3.RandomStringUtils
import kotlin.math.round
import kotlin.math.sqrt

class ModuleSpammer : Module("Spammer", "Spams something into the chat", ModuleCategory.MISC) {

    private val delay = ValueNumber(this, "Delay", 0.0, 2000.0, 10000.0, 500.0)
    private val noArbitraryTexts = ValueBoolean(this, "No arbitrary texts", false)
    private val garbage = object : ValueBoolean(this, "Garbage", false) {
        override fun isEnabled() = !noArbitraryTexts.value
    }
    private val amount = object : ValueNumber(this, "Amount", 0.0, 5.0, 10.0, 1.0) {
        override fun isEnabled() = !noArbitraryTexts.value && garbage.value
    }
    private val case = object : ValueMode(this, "Case", false, "Uppercase", "Random", "Lowercase") {
        override fun isEnabled() = !noArbitraryTexts.value && garbage.value
    }
    private val position = object : ValueMode(this, "Position", true, "Before", "After") {
        override fun isEnabled() = !noArbitraryTexts.value && garbage.value
    }
    private val mode = object : ValueMode(this, "Mode", false, "Custom message", "Position broadcast") {
        override fun isEnabled() = !noArbitraryTexts.value
    }
    private val message = object : ValueText(this, "Message", "") {
        override fun isEnabled() = !noArbitraryTexts.value && mode.isSelected(0)
    }
    private val target = object : ValueText(this, "Target", "") {
        override fun isEnabled() = !noArbitraryTexts.value && mode.isSelected(1)
    }

    private val timeUtil = TimeUtil()
    private val priorityMessages = ArrayList<String>()

    override fun onDisable() {
        priorityMessages.clear()
    }

    init {
        registerEvent(EventPollEvents::class.java) { event ->
            if (event.fake) return@registerEvent

            if (timeUtil.hasReached(delay.value.toLong())) {
                if (priorityMessages.isNotEmpty()) {
                    PlayerUtil.sendChatMessage(priorityMessages.removeFirst(), true)
                    timeUtil.reset()
                    return@registerEvent
                }
                if (noArbitraryTexts.value) return@registerEvent
                var text = when {
                    mode.isSelected(0) -> message.value
                    mode.isSelected(1) -> {
                        var target: Entity? = null
                        for (entity in mc.world?.entities!!) {
                            if (entity is PlayerEntity && entity.gameProfile.name.equals(this.target.value, true)) {
                                target = entity
                                break
                            }
                        }

                        if (target != null) {
                            var closest: PlayerEntity? = null
                            var dist = 0.0
                            for (entity in mc.world?.entities!!) {
                                if (entity is PlayerEntity && target != entity) {
                                    val dist2 = target.squaredDistanceTo(entity)
                                    if (closest == null || dist2 < dist) {
                                        closest = entity
                                        dist = dist2
                                    }
                                }
                            }

                            var string = "X: " + (round(target.x * 10) / 10.0) + " Y: " + (round(target.y * 10) / 10.0) + " Z: " + (round(target.z * 10) / 10.0)
                            if (closest != null) {
                                string += " " + closest.gameProfile.name + " (" + (round(sqrt(dist) * 10) / 10) + "m)"
                            }
                            string
                        } else {
                            "Target is not in render distance"
                        }
                    }

                    else -> null
                }
                if (text != null) {
                    if (garbage.value) {
                        if (position.isSelected(0))
                            text = formatGarbage(RandomStringUtils.randomAlphanumeric(amount.value.toInt())) + " " + text
                        if (position.isSelected(1))
                            text = text + " " + formatGarbage(RandomStringUtils.randomAlphanumeric(amount.value.toInt()))
                    }
                    PlayerUtil.sendChatMessage(text, true)
                }
                timeUtil.reset()
            }
        }

        registerEvent(EventChat::class.java) { event ->
            priorityMessages.add(event.chatMessage)
            event.cancelled = true
        }
    }

    private fun formatGarbage(string: String): String {
        return when {
            case.isSelected(0) -> string.uppercase()
            case.isSelected(1) -> string
            case.isSelected(2) -> string.lowercase()
            else -> string
        }
    }

}
