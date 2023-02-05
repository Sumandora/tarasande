package de.florianmichael.tarasande_litematica.generator.impl

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import de.florianmichael.tarasande_litematica.generator.Generator
import de.florianmichael.tarasande_litematica.util.LitematicaGenerator

val writer = MultiFormatWriter()

class GeneratorQRCode : Generator("QR Code") {

    private val types = HashMap<String, BarcodeFormat>()

    init {
        for (value in BarcodeFormat.values())
            types[StringUtil.formatEnumTypes(value.name)] = value
    }

    private val type = ValueMode(this, "Type", false, *types.keys.toTypedArray())
    private val input = ValueText(this, "Input", "https://youtu.be/dQw4w9WgXcQ")
    private val dimensionScaleX = ValueNumber(this, "Dimension Scale X", 0.0, 0.0, 100.0, 10.0)
    private val dimensionScaleYorZ = ValueNumber(this, "Dimension Scale Y/Z", 0.0, 0.0, 100.0, 10.0)
    private val side = ValueMode(this, "Side", false, "Vertical", "Horizontal")

    override fun perform() {
        try {
            writer.encode(input.value, types[type.getSelected()], dimensionScaleX.value.toInt(), dimensionScaleYorZ.value.toInt()).apply {
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
            CustomChat.printChatMessage("Failed to parse input, " + e.localizedMessage)
        }
    }
}
