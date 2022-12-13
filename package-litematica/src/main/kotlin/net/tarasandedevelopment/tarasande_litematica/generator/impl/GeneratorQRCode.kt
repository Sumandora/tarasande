package net.tarasandedevelopment.tarasande_litematica.generator.impl

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import net.tarasandedevelopment.tarasande_litematica.generator.Generator
import net.tarasandedevelopment.tarasande_litematica.util.LitematicaGenerator

val writer = MultiFormatWriter()

class GeneratorQRCode(parent: Any) : Generator(parent, "QR Code") {

    private val types = HashMap<String, BarcodeFormat>()

    init {
        for (value in BarcodeFormat.values())
            types[StringUtil.formatEnumTypes(value.name)] = value
    }

    private val type = ValueMode(parent, "Type", false, *types.keys.toTypedArray())
    private val input = ValueText(parent, "Input", "https://youtu.be/dQw4w9WgXcQ")
    private val dimensionScaleX = ValueNumber(parent, "Dimension Scale X", 0.0, 0.0, 100.0, 10.0)
    private val dimensionScaleYorZ = ValueNumber(parent, "Dimension Scale Y/Z", 0.0, 0.0, 100.0, 10.0)
    private val side = ValueMode(parent, "Side", false, "Vertical", "Horizontal")

    override fun perform() {
        try {
            writer.encode(input.value, if (type.anySelected()) types[type.selected[0]] else BarcodeFormat.QR_CODE, dimensionScaleX.value.toInt(), dimensionScaleYorZ.value.toInt()).apply {
                if (!side.anySelected() || side.isSelected(0)) {
                    LitematicaGenerator.create(name, BlockPos(width, height, 0)) {
                        for (x in 0 until width) {
                            for (z in 0 until height) {
                                if (get(x, z)) {
                                    it.set(x, z, 0, Blocks.WHITE_WOOL.defaultState)
                                } else {
                                    it.set(x, z, 0, Blocks.BLACK_WOOL.defaultState)
                                }
                            }
                        }

                        finish()
                    }
                } else {
                    LitematicaGenerator.create(name, BlockPos(width, 0, height)) {
                        for (x in 0 until width) {
                            for (z in 0 until height) {
                                if (get(x, z)) {
                                    it.set(x, 0, z, Blocks.WHITE_WOOL.defaultState)
                                } else {
                                    it.set(x, 0, z, Blocks.BLACK_WOOL.defaultState)
                                }
                            }
                        }

                        finish()
                    }
                }
            }
        } catch (e: Exception) {
            TarasandeMain.notifications().notify("Failed to parse input, " + e.localizedMessage)
        }
    }
}
