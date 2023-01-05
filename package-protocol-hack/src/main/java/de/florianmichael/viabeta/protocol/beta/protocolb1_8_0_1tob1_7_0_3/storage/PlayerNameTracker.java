package de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectArrayMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;

public class PlayerNameTracker extends StoredObject {

    public final Int2ObjectMap<String> names = new Int2ObjectArrayMap<>();

    public PlayerNameTracker(UserConnection user) {
        super(user);
    }

}
