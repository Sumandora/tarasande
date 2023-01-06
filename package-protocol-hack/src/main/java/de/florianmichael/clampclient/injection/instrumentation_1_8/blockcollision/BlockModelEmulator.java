package de.florianmichael.clampclient.injection.instrumentation_1_8.blockcollision;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class BlockModelEmulator {

    public static final Map<Block, BlockModel> blockModelTransformer = new HashMap<>();

    static {
        // Base
        blockModelTransformer.put(Blocks.STONE, new StoneBlockModel());
        blockModelTransformer.put(Blocks.AIR, new AirBlockModel());

        // Carpet
        blockModelTransformer.put(Blocks.WHITE_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.ORANGE_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.MAGENTA_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.LIGHT_BLUE_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.YELLOW_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.LIME_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.PINK_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.GRAY_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.LIGHT_GRAY_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.CYAN_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.PURPLE_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.BLUE_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.BROWN_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.GREEN_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.RED_CARPET, new CarpetBlockModel());
        blockModelTransformer.put(Blocks.BLACK_CARPET, new CarpetBlockModel());

        // Misc
        blockModelTransformer.put(Blocks.COBWEB, new CobwebBlockModel());
        blockModelTransformer.put(Blocks.SLIME_BLOCK, new SlimeBlockModel());
    }

    public static BlockModel getTransformerByBlock(final Block block) {
        if (!blockModelTransformer.containsKey(block)) return blockModelTransformer.get(Blocks.STONE);
        return blockModelTransformer.get(block);
    }
}
