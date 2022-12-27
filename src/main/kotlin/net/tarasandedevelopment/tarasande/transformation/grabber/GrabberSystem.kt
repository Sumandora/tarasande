package net.tarasandedevelopment.tarasande.transformation.grabber

import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.transformation.ManagerTransformer
import net.tarasandedevelopment.tarasande.transformation.Transformer
import net.tarasandedevelopment.tarasande.transformation.grabber.impl.GrabberReach

class ManagerGrabber(private val transformerSystem: ManagerTransformer) : Manager<Grabber>() {

    init {
        add(
            GrabberReach()
        )
    }

    override fun insert(obj: Grabber, index: Int) {
        super.insert(obj, index)
        transformerSystem.insert(obj, index)
    }

    fun getConstant(grabber: Class<out Grabber>): Any {
        return get(grabber).constant!!
    }

}

abstract class Grabber(targetedClass: String) : Transformer(targetedClass) {
    var constant: Any? = null
        get() {
            if(field == null)
                error(javaClass.simpleName + " wasn't able to read their constant")
            return field
        }
        protected set
}
