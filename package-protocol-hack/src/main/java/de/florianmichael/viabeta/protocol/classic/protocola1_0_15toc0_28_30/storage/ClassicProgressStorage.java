package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class ClassicProgressStorage extends StoredObject {

    public int upperBound = 100;
    public int progress; // 0% - upperBound
    public String status = "Waiting...";

    public ClassicProgressStorage(UserConnection user) {
        super(user);
    }

}
