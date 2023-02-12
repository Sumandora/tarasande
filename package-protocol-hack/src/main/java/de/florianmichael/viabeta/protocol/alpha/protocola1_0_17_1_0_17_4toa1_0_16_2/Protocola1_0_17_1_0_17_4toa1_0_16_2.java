package de.florianmichael.viabeta.protocol.alpha.protocola1_0_17_1_0_17_4toa1_0_16_2;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.alpha.protocola1_0_17_1_0_17_4toa1_0_16_2.storage.TimeLockStorage;
import de.florianmichael.viabeta.protocol.alpha.protocola1_0_17_1_0_17_4toa1_0_16_2.task.TimeLockTask;
import de.florianmichael.viabeta.protocol.alpha.protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4.ClientboundPacketsa1_0_17;
import de.florianmichael.viabeta.protocol.alpha.protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4.ServerboundPacketsa1_0_17;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

public class Protocola1_0_17_1_0_17_4toa1_0_16_2 extends AbstractProtocol<ClientboundPacketsa1_0_16, ClientboundPacketsa1_0_17, ServerboundPacketsa1_0_17, ServerboundPacketsa1_0_17> {

    public Protocola1_0_17_1_0_17_4toa1_0_16_2() {
        super(ClientboundPacketsa1_0_16.class, ClientboundPacketsa1_0_17.class, ServerboundPacketsa1_0_17.class, ServerboundPacketsa1_0_17.class);
    }

    @Override
    protected void registerPackets() {
        this.registerServerbound(ServerboundPacketsa1_0_17.PLAYER_BLOCK_PLACEMENT, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.SHORT); // item id
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // direction
                handler(wrapper -> {
                    if (wrapper.get(Type.SHORT, 0) < 0) {
                        wrapper.cancel();
                    }
                });
            }
        });
    }

    @Override
    public void register(ViaProviders providers) {
        super.register(providers);

        Via.getPlatform().runRepeatingSync(new TimeLockTask(), 20L);
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocola1_0_17_1_0_17_4toa1_0_16_2.class, ClientboundPacketsa1_0_16::getPacket));

        userConnection.put(new TimeLockStorage(userConnection, 0));
    }
}
