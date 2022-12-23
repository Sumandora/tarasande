package de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.ClientboundPackets1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.Protocol1_7_0_1_preto1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.model.PluginMessage;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.type.Types1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Types1_7_6_10;

import java.util.ArrayList;
import java.util.List;

public class PluginMessageStorage extends StoredObject {

    private final List<PluginMessage> pluginMessages = new ArrayList<>();

    public PluginMessageStorage(UserConnection user) {
        super(user);
    }

    public void reSyncPluginMessages(final UserConnection connection) throws Exception {
        for (PluginMessage pluginMessageModel : getPluginMessages()) {
            final PacketWrapper pluginMessage = PacketWrapper.create(ClientboundPackets1_6_4.PLUGIN_MESSAGE, connection);
            pluginMessage.write(Types1_6_4.STRING, pluginMessageModel.channel);
            pluginMessage.write(Types1_7_6_10.BYTEARRAY, pluginMessageModel.message);

            pluginMessage.send(Protocol1_7_0_1_preto1_6_4.class);
        }
        getPluginMessages().clear();
    }

    public List<PluginMessage> getPluginMessages() {
        return pluginMessages;
    }
}
