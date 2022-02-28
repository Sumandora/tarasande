package su.mandora.tarasande.util.reflection

import java.lang.reflect.Modifier

object ReflectionUtil {
    fun createReflectorClass(className: String): ReflectorClass? {
        val clazz = Class.forName(className) ?: return null
        return ReflectorClass(clazz)
    }
}

class ReflectorClass(private val clazz: Class<*>) {

    var instance: Any? = null

    constructor(clazz: Class<*>, instance: Any) : this(clazz) {
        this.instance = instance
    }

    fun newInstance(vararg arguments: Any?): ReflectorAny? {
        val constructor = clazz.getConstructor(*arguments.map { it?.javaClass }.toTypedArray()) ?: return null
        return ReflectorAny(constructor.newInstance(*arguments))
    }

    fun invokeMethod(methodName: String, vararg arguments: Any?): ReflectorAny? {
        val method = clazz.getMethod(methodName) ?: return null
        val static = Modifier.isStatic(method.modifiers)
        if (!static && instance == null)
            error("instance is null")
        return ReflectorAny(method.invoke(if (static) null else instance, *arguments))
    }

    fun getField(fieldName: String): ReflectorAny? {
        val field = clazz.getField(fieldName) ?: return null
        val static = Modifier.isStatic(field.modifiers)
        if (!static && instance == null)
            error("instance is null")
        return ReflectorAny(field.get(if (static) null else instance))
    }

}

class ReflectorAny(private val any: Any) {

    fun asReflectorClass(): ReflectorClass {
        return ReflectorClass(any.javaClass, any)
    }

    fun <T> interpretAs(clazz: Class<T>): T {
        return clazz.cast(any)
    }

}