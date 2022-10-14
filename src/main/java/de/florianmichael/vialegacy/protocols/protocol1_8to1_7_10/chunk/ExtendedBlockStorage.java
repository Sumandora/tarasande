package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.chunk;

import com.viaversion.viaversion.api.minecraft.chunks.NibbleArray;

public class ExtendedBlockStorage {

    private final byte[] blockLSBArray;
    private NibbleArray blockMSBArray;
    private final NibbleArray blockMetadataArray;
    private final NibbleArray blocklightArray;
    private NibbleArray skylightArray;

    public ExtendedBlockStorage(boolean paramBoolean) {
        this.blockLSBArray = new byte[4096];
        this.blockMetadataArray = new NibbleArray(this.blockLSBArray.length);
        this.blocklightArray = new NibbleArray(this.blockLSBArray.length);

        if (paramBoolean)
            this.skylightArray = new NibbleArray(this.blockLSBArray.length);

    }

    public byte[] getBlockLSBArray() {
        return this.blockLSBArray;
    }

    public boolean isEmpty() {
        return this.blockMSBArray==null;
    }

    public void clearMSBArray() {
        this.blockMSBArray = null;
    }

    public NibbleArray getBlockMSBArray() {
        return this.blockMSBArray;
    }

    public NibbleArray getMetadataArray() {
        return this.blockMetadataArray;
    }

    public NibbleArray getBlocklightArray() {
        return this.blocklightArray;
    }

    public NibbleArray getSkylightArray() {
        return this.skylightArray;
    }

    public NibbleArray createBlockMSBArray() {
        this.blockMSBArray = new NibbleArray(this.blockLSBArray.length);
        return this.blockMSBArray;
    }
}
