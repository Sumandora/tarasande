package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.genlayer;

import de.florianmichael.viabeta.api.LegacyVersionEnum;

public abstract class GenLayer {
    private long worldGenSeed;
    protected GenLayer parent;
    private long chunkSeed;
    private long baseSeed;

    public static GenLayer[] func_35497_a(final LegacyVersionEnum version, final long seed) {
        GenLayer obj = new LayerIsland(1L);
        obj = new GenLayerZoomFuzzy(2000L, obj);
        if (version.isNewerThanOrEqualTo(LegacyVersionEnum.r1_0_0tor1_0_1)) {
            obj = new GenLayerIsland_r1_0(1L, obj);
        } else {
            obj = new GenLayerIsland_b1_8(1L, obj);
        }
        obj = new GenLayerZoom(2001L, obj);
        if (version.isNewerThanOrEqualTo(LegacyVersionEnum.r1_0_0tor1_0_1)) {
            obj = new GenLayerIsland_r1_0(2L, obj);
        } else {
            obj = new GenLayerIsland_b1_8(2L, obj);
        }
        if (version.isNewerThanOrEqualTo(LegacyVersionEnum.r1_0_0tor1_0_1)) {
            obj = new GenLayerSnow(2L, obj);
        }
        obj = new GenLayerZoom(2002L, obj);
        if (version.isNewerThanOrEqualTo(LegacyVersionEnum.r1_0_0tor1_0_1)) {
            obj = new GenLayerIsland_r1_0(3L, obj);
        } else {
            obj = new GenLayerIsland_b1_8(3L, obj);
        }
        obj = new GenLayerZoom(2003L, obj);
        if (version.isNewerThanOrEqualTo(LegacyVersionEnum.r1_0_0tor1_0_1)) {
            obj = new GenLayerIsland_r1_0(4L, obj);
            obj = new GenLayerMushroomIsland(5L, obj);
        } else {
            obj = new GenLayerIsland_b1_8(3L, obj);
            obj = new GenLayerZoom(2004L, obj);
            obj = new GenLayerIsland_b1_8(3L, obj);
        }
        byte byte0 = 4;
        GenLayer obj1 = obj;
        obj1 = GenLayerZoom.func_35515_a(1000L, obj1, 0);
        obj1 = new GenLayerRiverInit(100L, obj1);
        obj1 = GenLayerZoom.func_35515_a(1000L, obj1, byte0 + 2);
        obj1 = new GenLayerRiver(1L, obj1);
        obj1 = new GenLayerSmooth(1000L, obj1);
        GenLayer obj2 = obj;
        obj2 = GenLayerZoom.func_35515_a(1000L, obj2, 0);
        if (version.isNewerThanOrEqualTo(LegacyVersionEnum.r1_0_0tor1_0_1)) {
            obj2 = new GenLayerVillageLandscape_r1_0(200L, obj2);
        } else {
            obj2 = new GenLayerVillageLandscape_b1_8(200L, obj2);
        }
        obj2 = GenLayerZoom.func_35515_a(1000L, obj2, 2);
        if (version.isNewerThanOrEqualTo(LegacyVersionEnum.r1_1)) {
            obj2 = new GenLayerHills(1000L, obj2);
        }
        GenLayer obj3 = new GenLayerTemperature(obj2);
        GenLayer obj4 = new GenLayerDownfall(obj2);
        for (int i = 0; i < byte0; i++) {
            obj2 = new GenLayerZoom(1000 + i, obj2);
            if (version.isNewerThanOrEqualTo(LegacyVersionEnum.r1_1)) {
                if (i == 0) {
                    obj2 = new GenLayerIsland_r1_0(3L, obj2);
                }
                if (i == 1) {
                    obj2 = new GenLayerShore_r1_1(1000L, obj2);
                }
                if (i == 1) {
                    obj2 = new GenLayerSwampRivers(1000L, obj2);
                }
            } else {
                if (i == 0) {
                    if (version.isNewerThanOrEqualTo(LegacyVersionEnum.r1_0_0tor1_0_1)) {
                        obj2 = new GenLayerIsland_r1_0(3L, obj2);
                    } else {
                        obj2 = new GenLayerIsland_b1_8(3L, obj2);
                    }
                }
                if (version.isNewerThanOrEqualTo(LegacyVersionEnum.r1_0_0tor1_0_1)) {
                    if (i == 0) {
                        obj2 = new GenLayerShore_r1_0(1000L, obj2);
                    }
                }
            }
            obj3 = new GenLayerSmoothZoom(1000 + i, obj3);
            obj3 = new GenLayerTemperatureMix(obj3, obj2, i);
            obj4 = new GenLayerSmoothZoom(1000 + i, obj4);
            obj4 = new GenLayerDownfallMix(obj4, obj2, i);
        }

        obj2 = new GenLayerSmooth(1000L, obj2);
        obj2 = new GenLayerRiverMix(100L, obj2, obj1);
        GenLayerRiverMix genlayerrivermix = ((GenLayerRiverMix) (obj2));
        obj3 = GenLayerSmoothZoom.func_35517_a(1000L, obj3, 2);
        obj4 = GenLayerSmoothZoom.func_35517_a(1000L, obj4, 2);
        GenLayerZoomVoronoi genlayerzoomvoronoi = new GenLayerZoomVoronoi(10L, obj2);
        obj2.initWorldGenSeed(seed);
        obj3.initWorldGenSeed(seed);
        obj4.initWorldGenSeed(seed);
        genlayerzoomvoronoi.initWorldGenSeed(seed);
        return (new GenLayer[]
                {
                        obj2, genlayerzoomvoronoi, obj3, obj4, genlayerrivermix
                });
    }

    public GenLayer(long l) {
        baseSeed = l;
        baseSeed *= baseSeed * 0x5851f42d4c957f2dL + 0x14057b7ef767814fL;
        baseSeed += l;
        baseSeed *= baseSeed * 0x5851f42d4c957f2dL + 0x14057b7ef767814fL;
        baseSeed += l;
        baseSeed *= baseSeed * 0x5851f42d4c957f2dL + 0x14057b7ef767814fL;
        baseSeed += l;
    }

    public void initWorldGenSeed(long l) {
        worldGenSeed = l;
        if (parent != null) {
            parent.initWorldGenSeed(l);
        }
        worldGenSeed *= worldGenSeed * 0x5851f42d4c957f2dL + 0x14057b7ef767814fL;
        worldGenSeed += baseSeed;
        worldGenSeed *= worldGenSeed * 0x5851f42d4c957f2dL + 0x14057b7ef767814fL;
        worldGenSeed += baseSeed;
        worldGenSeed *= worldGenSeed * 0x5851f42d4c957f2dL + 0x14057b7ef767814fL;
        worldGenSeed += baseSeed;
    }

    public void initChunkSeed(long l, long l1) {
        chunkSeed = worldGenSeed;
        chunkSeed *= chunkSeed * 0x5851f42d4c957f2dL + 0x14057b7ef767814fL;
        chunkSeed += l;
        chunkSeed *= chunkSeed * 0x5851f42d4c957f2dL + 0x14057b7ef767814fL;
        chunkSeed += l1;
        chunkSeed *= chunkSeed * 0x5851f42d4c957f2dL + 0x14057b7ef767814fL;
        chunkSeed += l;
        chunkSeed *= chunkSeed * 0x5851f42d4c957f2dL + 0x14057b7ef767814fL;
        chunkSeed += l1;
    }

    protected int nextInt(int i) {
        int j = (int) ((chunkSeed >> 24) % (long) i);
        if (j < 0) {
            j += i;
        }
        chunkSeed *= chunkSeed * 0x5851f42d4c957f2dL + 0x14057b7ef767814fL;
        chunkSeed += worldGenSeed;
        return j;
    }

    public abstract int[] getInts(int i, int j, int k, int l);

}
