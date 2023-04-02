package su.mandora.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import org.apache.commons.lang3.RandomStringUtils
import su.mandora.tarasande.event.impl.EventChat
import su.mandora.tarasande.event.impl.EventPollEvents
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.ValueText
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.javaruntime.clearAndGC
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.player.PlayerUtil
import kotlin.math.round
import kotlin.math.sqrt

class ModuleSpammer : Module("Spammer", "Spams something into the chat", ModuleCategory.MISC) {

    private val delay = ValueNumber(this, "Delay", 0.0, 2000.0, 10000.0, 500.0)
    private val noArbitraryTexts = ValueBoolean(this, "No arbitrary texts", false)
    private val garbage = ValueBoolean(this, "Garbage", false, isEnabled = { !noArbitraryTexts.value })
    private val amount = ValueNumber(this, "Amount", 0.0, 5.0, 10.0, 1.0, isEnabled = { !noArbitraryTexts.value && garbage.value })
    private val case = ValueMode(this, "Case", false, "Uppercase", "Random", "Lowercase", isEnabled = { !noArbitraryTexts.value && garbage.value })
    private val position = ValueMode(this, "Position", true, "Before", "After", isEnabled = { !noArbitraryTexts.value && garbage.value })
    private val mode = ValueMode(this, "Mode", false, "Custom message", "Position broadcast", isEnabled = { !noArbitraryTexts.value })
    private val message = ValueText(this, "Message", "", isEnabled = { !noArbitraryTexts.value && mode.isSelected(0) })
    private val target = ValueText(this, "Target", "", isEnabled = { !noArbitraryTexts.value && mode.isSelected(1) })

    private val timeUtil = TimeUtil()
    private val priorityMessages = ArrayList<String>()

    init {
        autoDisable.select(1) // Disable on disconnect
    }

    override fun onDisable() {
        priorityMessages.clearAndGC()
    }

    init {
        registerEvent(EventPollEvents::class.java) {
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

        registerEvent(EventChat::class.java, 9999) { event ->
            if(!event.cancelled)
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
