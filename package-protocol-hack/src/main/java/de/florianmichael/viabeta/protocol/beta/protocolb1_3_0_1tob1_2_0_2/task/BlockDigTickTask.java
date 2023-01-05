package de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.task;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.storage.BlockDigStorage;

public class BlockDigTickTask implements Runnable {

    @Override
    public void run() {
        for (UserConnection info : Via.getManager().getConnectionManager().getConnections()) {
            final BlockDigStorage blockDigStorage = info.get(BlockDigStorage.class);
            if (blockDigStorage != null) blockDigStorage.tick();
        }
    }

}
