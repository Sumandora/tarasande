package su.mandora.tarasande_rejected_features.information

import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.util.render.RenderUtil

class InformationKeyBinds : Information("KeyBinds", "Modules") {
    private val onlyBound = ValueBoolean(this, "Only bound", true)

    override fun getMessage(): String? {
        val names = ArrayList<String>()
        for (module in ManagerModule.list) {
            if ((module.bind.button != GLFW.GLFW_KEY_UNKNOWN && onlyBound.value) || !onlyBound.value) {
                names.add(module.name + " ["  + RenderUtil.getBindName(module.bind.type, module.bind.button) + "]")
            }
        }
        if (names.isEmpty()) return null
        return "\n" + names.joinToString("\n")
    }
}
