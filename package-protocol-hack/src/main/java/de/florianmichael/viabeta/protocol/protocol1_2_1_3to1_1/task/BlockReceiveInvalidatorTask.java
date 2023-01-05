package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.task;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.storage.PendingBlocksTracker;

public class BlockReceiveInvalidatorTask implements Runnable {

    @Override
    public void run() {
        for (UserConnection info : Via.getManager().getConnectionManager().getConnections()) {
            final PendingBlocksTracker pendingBlocksTracker = info.get(PendingBlocksTracker.class);
            if (pendingBlocksTracker != null) {
                info.getChannel().eventLoop().submit(pendingBlocksTracker::tick);
            }
        }
    }

}
