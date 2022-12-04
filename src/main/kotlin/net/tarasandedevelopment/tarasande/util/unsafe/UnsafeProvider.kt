package net.tarasandedevelopment.tarasande.util.unsafe

import sun.misc.Unsafe

object UnsafeProvider {

    val unsafe by lazy { Unsafe::class.java.let { it.getDeclaredField("theUnsafe").let { it.isAccessible = true; it.get(null) as Unsafe } } }

}