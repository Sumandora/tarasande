package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.api.model.IdAndData;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.Protocol1_2_1_3to1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.model.PendingBlockEntry;
import de.florianmichael.viabeta.protocol.protocol1_2_4_5to1_2_1_3.ClientboundPackets1_2_1;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class PendingBlocksTracker extends StoredObject {

    private final List<PendingBlockEntry> pendingBlockEntries = new LinkedList<>();

    public PendingBlocksTracker(UserConnection user) {
        super(user);
    }

    public void clear() {
        this.pendingBlockEntries.clear();
    }

    public void addPending(final Position position, final IdAndData block) {
        this.pendingBlockEntries.add(new PendingBlockEntry(position, block));
    }

    public void markReceived(final Position position) {
        this.markReceived(position, position);
    }

    public void markReceived(final Position startPos, final Position endPos) {
        final Iterator<PendingBlockEntry> it = this.pendingBlockEntries.iterator();
        while (it.hasNext()) {
            final Position pendingBlockPos = it.next().getPosition();
            if (pendingBlockPos.x() >= startPos.x() && pendingBlockPos.y() >= startPos.y() && pendingBlockPos.z() >= startPos.z() && pendingBlockPos.x() <= endPos.x() && pendingBlockPos.y() <= endPos.y() && pendingBlockPos.z() <= endPos.z()) {
                it.remove();
            }
        }
    }

    public void tick() {
        final Iterator<PendingBlockEntry> it = this.pendingBlockEntries.iterator();
        while (it.hasNext()) {
            final PendingBlockEntry pendingBlockEntry = it.next();
            if (pendingBlockEntry.decrementAndCheckIsExpired()) {
                it.remove();
                try {
                    final PacketWrapper blockChange = PacketWrapper.create(ClientboundPackets1_2_1.BLOCK_CHANGE, this.getUser());
                    blockChange.write(Type1_7_6_10.POSITION_UBYTE, pendingBlockEntry.getPosition()); // position
                    blockChange.write(Type.UNSIGNED_BYTE, (short) pendingBlockEntry.getBlock().id); // block id
                    blockChange.write(Type.UNSIGNED_BYTE, (short) pendingBlockEntry.getBlock().data); // block data
                    blockChange.send(Protocol1_2_1_3to1_1.class);
                } catch (Throwable e) {
                    ViaBeta.getPlatform().getLogger().log(Level.WARNING, "Could not send block update for expired pending block", e);
                }
            }
        }
    }

}
