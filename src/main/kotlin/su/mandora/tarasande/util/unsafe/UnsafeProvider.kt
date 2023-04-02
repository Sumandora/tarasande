package su.mandora.tarasande.util.unsafe

import sun.misc.Unsafe

object UnsafeProvider {

    val unsafe by lazy { Unsafe::class.java.let { it.getDeclaredField("theUnsafe").apply { isAccessible = true }.get(null) as Unsafe } }

}