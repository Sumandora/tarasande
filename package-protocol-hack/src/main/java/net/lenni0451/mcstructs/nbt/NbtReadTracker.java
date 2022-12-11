package net.lenni0451.mcstructs.nbt;

public class NbtReadTracker {

    public static NbtReadTracker unlimited() {
        return new NbtReadTracker(0) {
            @Override
            public void read(int bits) {
            }
        };
    }


    private final int maxDepth;
    private final int maxBytes;
    private int depth;
    private int size;

    public NbtReadTracker() {
        this(2_097_152);
    }

    public NbtReadTracker(final int maxBytes) {
        this(512, maxBytes);
    }

    public NbtReadTracker(final int maxDepth, final int maxBytes) {
        this.maxDepth = maxDepth;
        this.maxBytes = maxBytes;
    }

    public void pushDepth() {
        this.depth++;
        if (this.depth > 512) throw new IllegalStateException("Tried to read NBT with more depth than allowed (" + this.maxDepth + ")");
    }

    public void popDepth() {
        this.depth--;
    }

    public void read(final int bits) {
        this.size += bits / 8;
        if (this.size > this.maxBytes) {
            throw new IllegalStateException("Tried to read larger NBT than allowed. Needed bytes " + this.size + "bytes but max is " + this.maxBytes + "bytes");
        }
    }

}
