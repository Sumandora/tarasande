package net.tarasandedevelopment.tarasande_rejected_features.information

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import org.lwjgl.glfw.GLFW

class InformationKeyBinds : Information("KeyBinds", "Modules") {
    private val onlyBound = ValueBoolean(this, "Only bound", true)

    override fun getMessage(): String? {
        val names = ArrayList<String>()
        for (module in TarasandeMain.managerModule().list) {
            if ((module.bind.button != GLFW.GLFW_KEY_UNKNOWN && onlyBound.value) || !onlyBound.value) {
                names.add(module.name + " ["  + RenderUtil.getBindName(module.bind.type, module.bind.button) + "]")
            }
        }
        if (names.isEmpty()) return null
        return "\n" + names.subList(0, names.size - 1).joinToString("\n")
    }
}
