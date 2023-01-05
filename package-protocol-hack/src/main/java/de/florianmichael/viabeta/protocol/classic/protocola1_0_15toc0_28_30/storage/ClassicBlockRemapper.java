package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectFunction;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntFunction;
import de.florianmichael.viabeta.api.model.IdAndData;

public class ClassicBlockRemapper extends StoredObject {

    private final Int2ObjectFunction<IdAndData> mapper;
    private final Object2IntFunction<IdAndData> reverseMapper;

    public ClassicBlockRemapper(UserConnection user, Int2ObjectFunction<IdAndData> mapper, Object2IntFunction<IdAndData> reverseMapper) {
        super(user);
        this.mapper = mapper;
        this.reverseMapper = reverseMapper;
    }

    public Int2ObjectFunction<IdAndData> getMapper() {
        return this.mapper;
    }

    public Object2IntFunction<IdAndData> getReverseMapper() {
        return this.reverseMapper;
    }

}
