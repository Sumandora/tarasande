package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.util;

import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.NibbleArray;

public class ExtendedBlockStorage_1_7_6_10 {

    private final byte[] blockLSBArray;
    private NibbleArray blockMSBArray;
    private final NibbleArray blockMetadataArray;
    private final NibbleArray blockLightArray;
    private NibbleArray skyLightArray;

    public ExtendedBlockStorage_1_7_6_10(final boolean skylight) {
        this.blockLSBArray = new byte[4096];
        this.blockMetadataArray = new NibbleArray(this.blockLSBArray.length);
        this.blockLightArray = new NibbleArray(this.blockLSBArray.length);

        if (skylight) {
            this.skyLightArray = new NibbleArray(this.blockLSBArray.length);
        }
    }

    public int getBlockId(final int x, final int y, final int z) {
        int value = this.blockLSBArray[ChunkSection.index(x, y, z)] & 255;
        if (this.blockMSBArray != null) {
            value |= this.blockMSBArray.get(x, y, z) << 8;
        }
        return value;
    }

    public void setBlockId(final int x, final int y, final int z, final int value) {
        this.blockLSBArray[ChunkSection.index(x, y, z)] = (byte) (value & 255);
        if (value > 255) {
            this.getOrCreateBlockMSBArray().set(x, y, z, (value & 0xF00) >> 8);
        } else if (this.blockMSBArray != null) {
            this.blockMSBArray.set(x, y, z, 0);
        }
    }

    public int getBlockMetadata(final int x, final int y, final int z) {
        return this.blockMetadataArray.get(x, y, z);
    }

    public void setBlockMetadata(final int x, final int y, final int z, final int value) {
        this.blockMetadataArray.set(x, y, z, value);
    }

    public int getBlockLight(final int x, final int y, final int z) {
        return this.blockLightArray.get(x, y, z);
    }

    public void setBlockLight(final int x, final int y, int z, final int value) {
        this.blockLightArray.set(x, y, z, value);
    }

    public int getSkyLight(final int x, final int y, final int z) {
        return this.skyLightArray.get(x, y, z);
    }

    public void setSkyLight(final int x, final int y, final int z, final int value) {
        this.skyLightArray.set(x, y, z, value);
    }

    public boolean hasBlockMSBArray() {
        return this.blockMSBArray != null;
    }

    public byte[] getBlockLSBArray() {
        return this.blockLSBArray;
    }

    public NibbleArray getOrCreateBlockMSBArray() {
        if (this.blockMSBArray == null) {
            return this.blockMSBArray = new NibbleArray(this.blockLSBArray.length);
        }
        return this.blockMSBArray;
    }

    public NibbleArray getBlockMetadataArray() {
        return this.blockMetadataArray;
    }

    public NibbleArray getBlockLightArray() {
        return this.blockLightArray;
    }

    public NibbleArray getSkyLightArray() {
        return this.skyLightArray;
    }

}
