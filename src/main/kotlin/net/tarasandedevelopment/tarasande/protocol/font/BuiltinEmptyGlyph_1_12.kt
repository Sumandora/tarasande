package net.tarasandedevelopment.tarasande.protocol.font

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.font.Glyph
import net.minecraft.client.font.GlyphRenderer
import net.minecraft.client.font.RenderableGlyph
import net.minecraft.client.texture.NativeImage
import java.util.function.Function
import java.util.function.Supplier

fun createRectImage(width: Int, height: Int, colorSupplier: BuiltinEmptyGlyph_1_12.ColorSupplier): NativeImage {
    val nativeImage = NativeImage(NativeImage.Format.RGBA, width, height, false)
    for (i in 0 until height) {
        for (j in 0 until width) {
            nativeImage.setColor(j, i, colorSupplier.getColor(j, i))
        }
    }
    nativeImage.untrack()
    return nativeImage
}

@Environment(value = EnvType.CLIENT)
enum class BuiltinEmptyGlyph_1_12(imageSupplier: Supplier<NativeImage>) : Glyph {

    VERY_MISSING(Supplier {
        createRectImage(5, 8) { x: Int, y: Int ->
            val bl = x == 0 || x + 1 == 5 || y == 0 || y + 1 == 8
            if (bl) -1 else 0
        }
    });

    val image = imageSupplier.get()

    override fun getAdvance(): Float {
        return (image.width + 1).toFloat()
    }

    override fun bake(function: Function<RenderableGlyph, GlyphRenderer>): GlyphRenderer {
        return function.apply(object : RenderableGlyph {
            override fun getWidth(): Int {
                return image.width
            }

            override fun getHeight(): Int {
                return image.height
            }

            override fun getOversample(): Float {
                return 1.0f
            }

            override fun upload(x: Int, y: Int) {
                image.upload(0, x, y, false)
            }

            override fun hasColor(): Boolean {
                return true
            }
        })
    }

    @Environment(value = EnvType.CLIENT)
    fun interface ColorSupplier {
        fun getColor(var1: Int, var2: Int): Int
    }
}
