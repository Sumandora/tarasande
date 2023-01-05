package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model.map.MapData;

public class MapStorage extends StoredObject {

    private final Int2ObjectMap<MapData> maps = new Int2ObjectOpenHashMap<>();

    public MapStorage(UserConnection user) {
        super(user);
    }

    public MapData getMapData(final int id) {
        return this.maps.get(id);
    }

    public void putMapData(final int id, final MapData mapData) {
        this.maps.put(id, mapData);
    }
}
