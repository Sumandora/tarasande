package de.florianmichael.vialegacy.protocols.protocol1_6_2to1_6_1.util;

public enum EnumFace {

    DOWN((byte) 0, (byte) -1, (byte) 0),
    UP((byte) 0, (byte) 1, (byte) 0),
    NORTH((byte) 0, (byte) 0, (byte) -1),
    SOUTH((byte) 0, (byte) 0, (byte) 1),
    WEST((byte) -1, (byte) 0, (byte) 0),
    EAST((byte) 1, (byte) 0, (byte) 0);

    private final byte modX;
    private final byte modY;
    private final byte modZ;

    EnumFace(byte modX, byte modY, byte modZ) {
        this.modX = modX;
        this.modY = modY;
        this.modZ = modZ;
    }

    public byte getModX() {
        return this.modX;
    }

    public byte getModY() {
        return this.modY;
    }

    public byte getModZ() {
        return this.modZ;
    }
}
