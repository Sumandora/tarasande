package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class ClassicServerTitleStorage extends StoredObject {

    private final String title;
    private final String motd;

    public ClassicServerTitleStorage(UserConnection user, String title, String motd) {
        super(user);
        this.title = title;
        this.motd = motd;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMotd() {
        return this.motd;
    }

}
