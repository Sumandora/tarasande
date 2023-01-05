package de.florianmichael.viabeta.protocol.alpha.protocola1_0_17_1_0_17_4toa1_0_16_2.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class TimeLockStorage extends StoredObject {

    private long time;

    public TimeLockStorage(UserConnection user, final long time) {
        super(user);
        this.time = time;
    }

    public void setTime(final long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

}
