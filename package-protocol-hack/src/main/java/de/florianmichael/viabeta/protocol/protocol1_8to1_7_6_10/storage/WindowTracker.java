package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

import java.util.HashMap;
import java.util.Map;

public class WindowTracker extends StoredObject {

    public final Map<Short, Short> types = new HashMap<>();

    public WindowTracker(UserConnection user) {
        super(user);
    }

    public short get(short windowId) {
        return types.getOrDefault(windowId, (short) -1);
    }
}
