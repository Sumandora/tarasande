package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.storage;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.Protocol1_8to1_7_6_10;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model.GameProfile;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model.TabListEntry;

import java.util.*;

public class TabListStorage extends StoredObject {

    public final Map<String, TabListEntry> tablist = new HashMap<>();

    public TabListStorage(UserConnection user) {
        super(user);
    }

    public void sendTempEntry(final TabListEntry entry) throws Exception {
        entry.ping = -1;
        this.sendAddEntry(entry); // send tablist entry before spawning player
        Via.getPlatform().runSync(() -> { // wait for the client to load the skin then remove the fake tablist entry
            try {
                sendRemoveEntry(entry);
            } catch (Throwable ignored) {
            }
        }, entry.gameProfile.isOffline() ? 2L : 60L);
    }

    public void sendAddEntry(final TabListEntry entry) throws Exception {
        final List<GameProfile.Property> gameProfileProperties = entry.gameProfile.getAllProperties();

        final PacketWrapper addPlayerListItemPacket = PacketWrapper.create(ClientboundPackets1_8.PLAYER_INFO, this.getUser());
        addPlayerListItemPacket.write(Type.VAR_INT, 0); // action
        addPlayerListItemPacket.write(Type.VAR_INT, 1); // count
        addPlayerListItemPacket.write(Type.UUID, entry.gameProfile.uuid); // uuid
        addPlayerListItemPacket.write(Type.STRING, entry.gameProfile.userName); // name
        addPlayerListItemPacket.write(Type.VAR_INT, gameProfileProperties.size()); // properties count
        { // properties
            for (GameProfile.Property profileEntry : gameProfileProperties) {
                addPlayerListItemPacket.write(Type.STRING, profileEntry.key); // key
                addPlayerListItemPacket.write(Type.STRING, profileEntry.value); // value
                addPlayerListItemPacket.write(Type.OPTIONAL_STRING, profileEntry.signature); // signature
            }
        }
        addPlayerListItemPacket.write(Type.VAR_INT, entry.gameMode); // gamemode
        addPlayerListItemPacket.write(Type.VAR_INT, entry.ping); // ping
        addPlayerListItemPacket.write(Type.OPTIONAL_STRING, null); // display name
        addPlayerListItemPacket.send(Protocol1_8to1_7_6_10.class);
    }

    public void sendRemoveEntry(final TabListEntry entry) throws Exception {
        final PacketWrapper removePlayerListItemPacket = PacketWrapper.create(ClientboundPackets1_8.PLAYER_INFO, this.getUser());
        removePlayerListItemPacket.write(Type.VAR_INT, 4); // action
        removePlayerListItemPacket.write(Type.VAR_INT, 1); // count
        removePlayerListItemPacket.write(Type.UUID, entry.gameProfile.uuid); // uuid
        removePlayerListItemPacket.send(Protocol1_8to1_7_6_10.class);
    }
}
