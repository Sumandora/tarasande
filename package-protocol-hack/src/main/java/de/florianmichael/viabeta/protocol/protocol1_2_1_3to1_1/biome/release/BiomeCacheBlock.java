package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release;

public class BiomeCacheBlock {

    public NewBiomeGenBase[] biomes;
    public int xPosition;
    public int zPosition;
    public long lastAccessTime;
    final BiomeCache biomeCache;

    public BiomeCacheBlock(BiomeCache biomecache, int i, int j) {
        biomeCache = biomecache;

        biomes = new NewBiomeGenBase[256];
        xPosition = i;
        zPosition = j;
        BiomeCache.getWorldChunkManager(biomecache).getBiomeGenAt(biomes, i << 4, j << 4, 16, 16, false);
    }

    public NewBiomeGenBase getBiomeGenAt(int i, int j) {
        return biomes[i & 0xf | (j & 0xf) << 4];
    }

}
