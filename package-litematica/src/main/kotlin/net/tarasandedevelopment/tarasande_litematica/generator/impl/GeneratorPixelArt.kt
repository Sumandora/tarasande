package net.tarasandedevelopment.tarasande_litematica.generator.impl

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.tarasandedevelopment.tarasande_litematica.generator.Generator

// Thank you EvilCodeZ, for generating this palette for RK_01 and me ^^
private val colorPalette = HashMap<Block, Int>().apply {
    this[Blocks.WHITE_WOOL]         = 0xDDDDDD
    this[Blocks.ORANGE_WOOL]        = 0xDB7D3E
    this[Blocks.MAGENTA_WOOL]       = 0xB350BC
    this[Blocks.LIGHT_BLUE_WOOL]    = 0x6B8AC9
    this[Blocks.YELLOW_WOOL]        = 0xB1A627
    this[Blocks.LIME_WOOL]          = 0x41AE38
    this[Blocks.PINK_WOOL]          = 0xD08499
    this[Blocks.GRAY_WOOL]          = 0x404040
    this[Blocks.LIGHT_GRAY_WOOL]    = 0x9AA1A1
    this[Blocks.CYAN_WOOL]          = 0x2E6E89
    this[Blocks.PURPLE_WOOL]        = 0x7E3DB5
    this[Blocks.BLUE_WOOL]          = 0x2E388D
    this[Blocks.BROWN_WOOL]         = 0x4F321F
    this[Blocks.GREEN_WOOL]         = 0x35461B
    this[Blocks.RED_WOOL]           = 0x963430
    this[Blocks.BLACK_WOOL]         = 0x191616
    this[Blocks.GRASS]              = 0x73b349
    this[Blocks.IRON_BLOCK]         = 0xc3c3c3
    this[Blocks.COAL_BLOCK]         = 0x0d0d0d
    this[Blocks.BRICKS]             = 0x9f5845
    this[Blocks.NETHER_BRICKS]      = 0x30181c
    this[Blocks.END_STONE]          = 0xd9dc9e
    this[Blocks.PRISMARINE]         = 0x64a89d
    this[Blocks.DARK_PRISMARINE]    = 0x365649
    this[Blocks.OBSIDIAN]           = 0x0f0f18
    this[Blocks.SANDSTONE]          = 0xdbd2a1
    this[Blocks.RED_SANDSTONE]      = 0xa2521d
    this[Blocks.DIAMOND_BLOCK]      = 0x73e1dc
    this[Blocks.LAPIS_BLOCK]        = 0x254585
    this[Blocks.GOLD_BLOCK]         = 0xfff849
    this[Blocks.EMERALD_BLOCK]      = 0x42d86d
    this[Blocks.STONE]              = 0x747474
    this[Blocks.OAK_PLANKS]         = 0xb4905a
    this[Blocks.SPRUCE_PLANKS]      = 0x785836
    this[Blocks.BIRCH_PLANKS]       = 0xd7c185
    this[Blocks.JUNGLE_PLANKS]      = 0xb1805c
    this[Blocks.ACACIA_PLANKS]      = 0xba6337
    this[Blocks.DARK_OAK_PLANKS]    = 0x462d15
}

class GeneratorPixelArt(parent: Any) : Generator(parent, "Pixel art") {

    override fun perform() {
    }
}
