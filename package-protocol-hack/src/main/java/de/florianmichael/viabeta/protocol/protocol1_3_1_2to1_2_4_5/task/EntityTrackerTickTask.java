package de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.task;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.storage.EntityTracker_1_2_4_5;

public class EntityTrackerTickTask implements Runnable {

    @Override
    public void run() {
        for (UserConnection info : Via.getManager().getConnectionManager().getConnections()) {
            final EntityTracker_1_2_4_5 entityTracker = info.get(EntityTracker_1_2_4_5.class);
            if (entityTracker != null) entityTracker.tick();
        }
    }

}
