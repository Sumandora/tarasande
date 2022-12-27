package net.tarasandedevelopment.tarasande.transformation.grabber

import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.transformation.ManagerTransformer
import net.tarasandedevelopment.tarasande.transformation.Transformer
import net.tarasandedevelopment.tarasande.transformation.grabber.impl.TransformerGrabberDefaultFlightSpeed
import net.tarasandedevelopment.tarasande.transformation.grabber.impl.TransformerGrabberReach
import net.tarasandedevelopment.tarasande.transformation.grabber.impl.TransformerGrabberSpeedReduction

class ManagerGrabber(private val transformerSystem: ManagerTransformer) : Manager<TransformerGrabber>() {

    init {
        add(
            TransformerGrabberReach(),
            TransformerGrabberSpeedReduction(),
            TransformerGrabberDefaultFlightSpeed()
        )
    }

    override fun insert(obj: TransformerGrabber, index: Int) {
        super.insert(obj, index)
        transformerSystem.insert(obj, index)
    }

    fun getConstant(transformerGrabber: Class<out TransformerGrabber>): Any {
        return get(transformerGrabber).constant!!
    }

}

abstract class TransformerGrabber(targetedClass: String, private val expected: Any) : Transformer(targetedClass) {
    var constant: Any? = null
        get() {
            if(field == null)
                error(javaClass.simpleName + " wasn't able to read their constant")
            return field
        }
        protected set(value) {
            if(value != expected)
                error(javaClass.simpleName + " read a different value than expected (Expected: $expected, but received $value)")
            field = value
        }
}
