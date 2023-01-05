package de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.task;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.Protocolc0_30toc0_30cpe;
import de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.ServerboundPacketsc0_30cpe;
import de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.data.ClassicProtocolExtension;
import de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.storage.ExtensionProtocolMetadataStorage;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class ClassicPingTask implements Runnable {

    @Override
    public void run() {
        for (UserConnection info : Via.getManager().getConnectionManager().getConnections()) {
            final ExtensionProtocolMetadataStorage protocolMetadata = info.get(ExtensionProtocolMetadataStorage.class);
            if (protocolMetadata == null) continue;
            if (!protocolMetadata.hasServerExtension(ClassicProtocolExtension.TWO_WAY_PING, 1)) continue;
            try {
                final PacketWrapper pingRequest = PacketWrapper.create(ServerboundPacketsc0_30cpe.EXT_TWO_WAY_PING, info);
                pingRequest.write(Type.BYTE, (byte) 0); // direction
                pingRequest.write(Type.SHORT, (short) (ThreadLocalRandom.current().nextInt() % Short.MAX_VALUE)); // data
                pingRequest.sendToServer(Protocolc0_30toc0_30cpe.class);
            } catch (Throwable e) {
                ViaBeta.getPlatform().getLogger().log(Level.WARNING, "Error sending TwoWayPing extension ping packet", e);
            }
        }
    }
}
