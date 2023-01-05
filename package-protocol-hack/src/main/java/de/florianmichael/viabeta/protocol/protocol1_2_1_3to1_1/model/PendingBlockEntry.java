package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.model;

import com.viaversion.viaversion.api.minecraft.Position;
import de.florianmichael.viabeta.api.model.IdAndData;

public class PendingBlockEntry {

    private final Position position;
    private final IdAndData block;
    private int countdown = 80;

    public PendingBlockEntry(final Position position, final IdAndData block) {
        this.position = position;
        this.block = block;
    }

    public Position getPosition() {
        return this.position;
    }

    public IdAndData getBlock() {
        return this.block;
    }

    public boolean decrementAndCheckIsExpired() {
        return --this.countdown <= 0;
    }

}
