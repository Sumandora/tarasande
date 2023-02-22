package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.event.impl.EventKey
import net.tarasandedevelopment.tarasande.event.impl.EventMouse
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.focusable.impl.ElementWidthValueComponentFocusableBind
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import org.lwjgl.glfw.GLFW
import net.tarasandedevelopment.tarasande.event.EventDispatcher

open class ValueBind(
    owner: Any,
    name: String,
    type: Type,
    button: Int,
    val mouse: Boolean = true,
    visible: Boolean = true,
    isEnabled: () -> Boolean = { true },
    manage: Boolean = true
) : Value(owner, name, visible, isEnabled, ElementWidthValueComponentFocusableBind::class.java, manage) {

    var type = type
        set(value) {
            val prevValue = field
            field = value
            onChange(prevValue, value)
        }
    var button = button
        set(value) {
            val prevValue = field
            field = value
            onChange(prevValue, value)
        }

    init {
        EventDispatcher.apply {
            add(EventMouse::class.java) {
                if (this@ValueBind.type == Type.MOUSE)
                    if (mc.currentScreen == null)
                        if (this@ValueBind.button == it.button)
                            if (it.action == GLFW.GLFW_PRESS) {
                                if (owner !is Module || this@ValueBind == owner.bind || owner.enabled.value)
                                    presses++
                            }
            }
            add(EventKey::class.java) {
                if (this@ValueBind.type == Type.KEY)
                    if (mc.currentScreen == null)
                        if (it.key == this@ValueBind.button)
                            if (it.action == GLFW.GLFW_PRESS) {
                                if (owner !is Module || this@ValueBind == owner.bind || owner.enabled.value)
                                    presses++
                            }
            }
        }
    }

    private var presses = 0

    open fun filter(type: Type, bind: Int): Boolean {
        return true
    }

    fun wasPressed(): Int {
        val prev = presses
        presses = 0
        return prev
    }

    fun isPressed(ignoreScreen: Boolean = false): Boolean {
        if (button == GLFW.GLFW_KEY_UNKNOWN) return false
        if (mc.currentScreen != null && !ignoreScreen) return false

        return when (type) {
            Type.KEY -> GLFW.glfwGetKey(mc.window.handle, button) == GLFW.GLFW_PRESS
            Type.MOUSE -> GLFW.glfwGetMouseButton(mc.window.handle, button) == GLFW.GLFW_PRESS
        }
    }

    open fun onChange(oldType: Type?, newType: Type) {}
    open fun onChange(oldButton: Int?, newButton: Int) {}

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        jsonArray.add(type.ordinal)
        jsonArray.add(button)
        return jsonArray
    }

    override fun load(jsonElement: JsonElement) {
        val jsonArray = jsonElement.asJsonArray
        type = Type.values()[jsonArray.get(0).asInt]
        button = jsonArray.get(1).asInt
    }

    enum class Type {
        KEY, MOUSE
    }
}