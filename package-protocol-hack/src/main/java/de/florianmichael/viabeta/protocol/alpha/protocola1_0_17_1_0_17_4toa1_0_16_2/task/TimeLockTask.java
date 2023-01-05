package de.florianmichael.viabeta.protocol.alpha.protocola1_0_17_1_0_17_4toa1_0_16_2.task;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.protocol.alpha.protocola1_0_17_1_0_17_4toa1_0_16_2.Protocola1_0_17_1_0_17_4toa1_0_16_2;
import de.florianmichael.viabeta.protocol.alpha.protocola1_0_17_1_0_17_4toa1_0_16_2.storage.TimeLockStorage;
import de.florianmichael.viabeta.protocol.alpha.protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4.ClientboundPacketsa1_0_17;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.PlayerInfoStorage;

public class TimeLockTask implements Runnable {

    @Override
    public void run() {
        for (UserConnection info : Via.getManager().getConnectionManager().getConnections()) {
            final TimeLockStorage timeLockStorage = info.get(TimeLockStorage.class);
            final PlayerInfoStorage playerInfoStorage = info.get(PlayerInfoStorage.class);
            if (timeLockStorage != null && playerInfoStorage != null && playerInfoStorage.entityId != -1) {
                try {
                    final PacketWrapper updateTime = PacketWrapper.create(ClientboundPacketsa1_0_17.TIME_UPDATE, info);
                    updateTime.write(Type.LONG, timeLockStorage.getTime() % 24_000L);
                    updateTime.send(Protocola1_0_17_1_0_17_4toa1_0_16_2.class);
                } catch (Throwable ignored) {
                }
            }
        }
    }

}
