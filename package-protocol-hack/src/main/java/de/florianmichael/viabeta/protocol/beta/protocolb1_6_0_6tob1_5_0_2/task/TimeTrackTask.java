package de.florianmichael.viabeta.protocol.beta.protocolb1_6_0_6tob1_5_0_2.task;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viabeta.protocol.beta.protocolb1_6_0_6tob1_5_0_2.storage.WorldTimeStorage;

public class TimeTrackTask implements Runnable {

    @Override
    public void run() {
        for (UserConnection info : Via.getManager().getConnectionManager().getConnections()) {
            final WorldTimeStorage worldTimeStorage = info.get(WorldTimeStorage.class);
            if (worldTimeStorage != null) worldTimeStorage.time++;
        }
    }

}
