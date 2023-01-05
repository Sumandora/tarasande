package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.IWorldChunkManager;

public class SeedStorage extends StoredObject {

    public long seed;
    public IWorldChunkManager worldChunkManager;

    public SeedStorage(UserConnection user) {
        super(user);
    }

}
