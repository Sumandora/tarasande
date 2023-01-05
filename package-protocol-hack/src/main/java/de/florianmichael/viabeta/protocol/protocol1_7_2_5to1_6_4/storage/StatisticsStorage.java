package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;

public class StatisticsStorage extends StoredObject {

    public final Int2IntMap values = new Int2IntOpenHashMap();

    public StatisticsStorage(final UserConnection userConnection) {
        super(userConnection);
    }
}
