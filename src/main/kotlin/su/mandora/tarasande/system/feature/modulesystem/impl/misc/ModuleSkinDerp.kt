package su.mandora.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.client.render.entity.PlayerModelPart
import net.minecraft.text.TranslatableTextContent
import net.minecraft.util.Arm
import su.mandora.tarasande.event.impl.EventScreenInput
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.string.StringUtil
import java.util.concurrent.ThreadLocalRandom

class ModuleSkinDerp : Module("Skin derp", "Randomly toggles skin layers on and off", ModuleCategory.MISC) {

    private val modelParts = ValueMode(this, "Model parts", true, *PlayerModelPart.entries.map {
        val content = it.optionName.content
        if (content is TranslatableTextContent)
            StringUtil.uncoverTranslation(content.key)
        else
            it.optionName.string
    }.toTypedArray())

    private val affectHand = ValueBoolean(this, "Affect hand", true)

    // TODO Group packets

    private val previousStates = HashMap<PlayerModelPart, Boolean>()
    private var mainArm: Arm? = null

    override fun onDisable() {
        previousStates.forEach { (modelPart, enabled) ->
            mc.options.togglePlayerModelPart(modelPart, enabled)
        }
        previousStates.clear()

        if(mainArm != null)
            mc.options.mainArm.value = mainArm
        mainArm = null
    }

    init {
        registerEvent(EventScreenInput::class.java) { _ ->
            for (modelPart in PlayerModelPart.entries) {
                val enabled = mc.options.isPlayerModelPartEnabled(modelPart)
                if(!previousStates.contains(modelPart))
                    previousStates[modelPart] = enabled
                mc.options.togglePlayerModelPart(modelPart, ThreadLocalRandom.current().nextBoolean())
            }
            if(affectHand.value) {
                if(mainArm == null)
                    mainArm = mc.options.mainArm.value
                mc.options.mainArm.value = Arm.entries.random()
            }
        }
    }

}