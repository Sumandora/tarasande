package de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.data;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntOpenHashMap;
import de.florianmichael.viabeta.api.data.BlockList1_6;
import de.florianmichael.viabeta.api.model.IdAndData;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.data.ClassicBlocks;

public class ExtendedClassicBlocks {

    public static final int COBBLESTONE_SLAB = 50;
    public static final int ROPE = 51;
    public static final int SANDSTONE = 52;
    public static final int SNOW = 53;
    public static final int FIRE = 54;
    public static final int LIGHT_PINK_WOOL = 55;
    public static final int FOREST_GREEN_WOOL = 56;
    public static final int BROWN_WOOL = 57;
    public static final int DEEP_BLUE_WOOL = 58;
    public static final int TURQUOISE_WOOL = 59;
    public static final int ICE = 60;
    public static final int CERAMIC_TILE = 61;
    public static final int MAGMA = 62;
    public static final int PILLAR = 63;
    public static final int CRATE = 64;
    public static final int STONE_BRICK = 65;

    public static Int2ObjectMap<IdAndData> MAPPING = new Int2ObjectOpenHashMap<>(15, 0.99F);
    public static Object2IntMap<IdAndData> REVERSE_MAPPING = new Object2IntOpenHashMap<>(15, 0.99F);

    static {
        MAPPING.defaultReturnValue(new IdAndData(BlockList1_6.stone.blockID, 0));
        REVERSE_MAPPING.defaultReturnValue(ClassicBlocks.STONE);

        MAPPING.put(COBBLESTONE_SLAB, new IdAndData(BlockList1_6.stoneSingleSlab.blockID, 3));
        MAPPING.put(ROPE, new IdAndData(BlockList1_6.deadBush.blockID, 0));
        MAPPING.put(SANDSTONE, new IdAndData(BlockList1_6.sandStone.blockID, 0));
        MAPPING.put(SNOW, new IdAndData(BlockList1_6.snow.blockID, 0));
        MAPPING.put(FIRE, new IdAndData(BlockList1_6.torchWood.blockID, 5));
        MAPPING.put(LIGHT_PINK_WOOL, new IdAndData(BlockList1_6.cloth.blockID, 6));
        MAPPING.put(FOREST_GREEN_WOOL, new IdAndData(BlockList1_6.cloth.blockID, 13));
        MAPPING.put(BROWN_WOOL, new IdAndData(BlockList1_6.cloth.blockID, 12));
        MAPPING.put(DEEP_BLUE_WOOL, new IdAndData(BlockList1_6.cloth.blockID, 11));
        MAPPING.put(TURQUOISE_WOOL, new IdAndData(BlockList1_6.cloth.blockID, 9));
        MAPPING.put(ICE, new IdAndData(BlockList1_6.ice.blockID, 0));
        MAPPING.put(CERAMIC_TILE, new IdAndData(BlockList1_6.blockNetherQuartz.blockID, 1));
        MAPPING.put(MAGMA, new IdAndData(BlockList1_6.oreNetherQuartz.blockID, 0));
        MAPPING.put(PILLAR, new IdAndData(BlockList1_6.blockNetherQuartz.blockID, 2));
        MAPPING.put(CRATE, new IdAndData(BlockList1_6.jukebox.blockID, 0));
        MAPPING.put(STONE_BRICK, new IdAndData(BlockList1_6.stoneBrick.blockID, 0));

        REVERSE_MAPPING.put(new IdAndData(BlockList1_6.stoneSingleSlab.blockID, 3), COBBLESTONE_SLAB);
        REVERSE_MAPPING.put(new IdAndData(BlockList1_6.deadBush.blockID, 0), ROPE);
        REVERSE_MAPPING.put(new IdAndData(BlockList1_6.sandStone.blockID, 0), SANDSTONE);
        REVERSE_MAPPING.put(new IdAndData(BlockList1_6.snow.blockID, 0), SNOW);
        REVERSE_MAPPING.put(new IdAndData(BlockList1_6.torchWood.blockID, 0), FIRE);
        REVERSE_MAPPING.put(new IdAndData(BlockList1_6.cloth.blockID, 12), BROWN_WOOL);
        REVERSE_MAPPING.put(new IdAndData(BlockList1_6.ice.blockID, 0), ICE);
        REVERSE_MAPPING.put(new IdAndData(BlockList1_6.blockNetherQuartz.blockID, 1), CERAMIC_TILE);
        REVERSE_MAPPING.put(new IdAndData(BlockList1_6.oreNetherQuartz.blockID, 0), MAGMA);
        REVERSE_MAPPING.put(new IdAndData(BlockList1_6.blockNetherQuartz.blockID, 2), PILLAR);
        REVERSE_MAPPING.put(new IdAndData(BlockList1_6.jukebox.blockID, 0), CRATE);
        REVERSE_MAPPING.put(new IdAndData(BlockList1_6.stoneBrick.blockID, 0), STONE_BRICK);
    }
}
