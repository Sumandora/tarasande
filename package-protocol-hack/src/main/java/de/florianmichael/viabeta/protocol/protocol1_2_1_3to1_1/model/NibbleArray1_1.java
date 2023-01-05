package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.model;

import com.viaversion.viaversion.api.minecraft.chunks.NibbleArray;

public class NibbleArray1_1 extends NibbleArray {

    private final int depthBits;
    private final int depthBitsPlusFour;

    public NibbleArray1_1(final int length) {
        this(length, 7);
    }

    public NibbleArray1_1(final byte[] handle) {
        this(handle, 7);
    }

    public NibbleArray1_1(final int length, final int depthBits) {
        super(length);
        this.depthBits = depthBits;
        this.depthBitsPlusFour = depthBits + 4;
    }

    public NibbleArray1_1(final byte[] handle, final int depthBits) {
        super(handle);
        this.depthBits = depthBits;
        this.depthBitsPlusFour = depthBits + 4;
    }

    @Override
    public byte get(int x, int y, int z) {
        return this.get(this.index(x, y, z));
    }

    @Override
    public void set(int x, int y, int z, int value) {
        this.set(this.index(x, y, z), value);
    }

    public int index(final int x, final int y, final int z) {
        return x << this.depthBitsPlusFour | z << this.depthBits | y;
    }
}
