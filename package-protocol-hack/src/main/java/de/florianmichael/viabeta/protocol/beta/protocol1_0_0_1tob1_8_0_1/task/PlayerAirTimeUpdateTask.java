package de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.task;

import com.google.common.collect.Lists;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.api.data.BlockList1_6;
import de.florianmichael.viabeta.api.model.IdAndData;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.Protocol1_0_0_1tob1_8_0_1;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.storage.PlayerAirTimeStorage;
import de.florianmichael.viabeta.protocol.protocol1_1to1_0_0_1.ClientboundPackets1_0;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.Type1_3_1_2;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.impl.MetaType1_3_1_2;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.ChunkTracker;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.PlayerInfoStorage;

public class PlayerAirTimeUpdateTask implements Runnable {

    @Override
    public void run() {
        for (UserConnection info : Via.getManager().getConnectionManager().getConnections()) {
            final PlayerAirTimeStorage playerAirTimeStorage = info.get(PlayerAirTimeStorage.class);
            if (playerAirTimeStorage != null) {
                final PlayerInfoStorage playerInfoStorage = info.get(PlayerInfoStorage.class);
                if (playerInfoStorage == null) continue;
                final IdAndData headBlock = info.get(ChunkTracker.class).getBlockNotNull(floor(playerInfoStorage.posX), floor(playerInfoStorage.posY + 1.62F), floor(playerInfoStorage.posZ));
                if (headBlock.id == BlockList1_6.waterMoving.blockID || headBlock.id == BlockList1_6.waterStill.blockID) {
                    playerAirTimeStorage.sentPacket = false;
                    playerAirTimeStorage.air--;
                    if (playerAirTimeStorage.air < 0) playerAirTimeStorage.air = 0;
                    this.sendAirTime(playerInfoStorage, playerAirTimeStorage, info);
                } else if (!playerAirTimeStorage.sentPacket) {
                    playerAirTimeStorage.sentPacket = true;
                    playerAirTimeStorage.air = playerAirTimeStorage.MAX_AIR;
                    this.sendAirTime(playerInfoStorage, playerAirTimeStorage, info);
                }
            }
        }
    }

    private void sendAirTime(final PlayerInfoStorage playerInfoStorage, final PlayerAirTimeStorage playerAirTimeStorage, final UserConnection userConnection) {
        try {
            final PacketWrapper updateAirTime = PacketWrapper.create(ClientboundPackets1_0.ENTITY_METADATA, userConnection);
            updateAirTime.write(Type.INT, playerInfoStorage.entityId); // entity id
            updateAirTime.write(Type1_3_1_2.METADATA_LIST, Lists.newArrayList(new Metadata(1, MetaType1_3_1_2.Short, Integer.valueOf(playerAirTimeStorage.air).shortValue()))); // metadata
            updateAirTime.send(Protocol1_0_0_1tob1_8_0_1.class);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static int floor(double f) {
        int i = (int) f;
        return f < (double) i ? i - 1 : i;
    }

}
