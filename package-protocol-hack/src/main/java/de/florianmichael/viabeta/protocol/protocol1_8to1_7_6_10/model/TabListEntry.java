package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model;

import com.google.common.base.Charsets;

import java.util.UUID;

public class TabListEntry {

    public GameProfile gameProfile;
    public int ping;
    public int gameMode;

    public boolean resolved;

    public TabListEntry(final String name, final UUID uuid) {
        this.gameProfile = new GameProfile(name, uuid);
        this.resolved = true;
    }

    public TabListEntry(final String name, final short ping) {
        this.gameProfile = new GameProfile(name, UUID.nameUUIDFromBytes(("LegacyPlayer:" + name).getBytes(Charsets.UTF_8)));
        this.ping = ping;
    }

}
