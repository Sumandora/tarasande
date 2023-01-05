package de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * This is not needed for the internal protocol translation, it's just an API for users who want to implement this information provided by the MessageTypes Extension
 */
public class ExtMessageTypesStorage extends StoredObject {

    public String status1;
    public String status2;
    public String status3;

    public String bottomRight1;
    public String bottomRight2;
    public String bottomRight3;

    public String announcement;

    public ExtMessageTypesStorage(UserConnection user) {
        super(user);
    }

    public List<String> getAsDisplayList() {
        final List<String> displayList = new ArrayList<>();
        if (status1 != null) displayList.add("[Status 1] " + status1);
        if (status2 != null) displayList.add("[Status 2] " + status2);
        if (status3 != null) displayList.add("[Status 3] " + status3);

        if (bottomRight1 != null) displayList.add("[Bottom right 1] " + bottomRight1);
        if (bottomRight2 != null) displayList.add("[Bottom right 2] " + bottomRight2);
        if (bottomRight2 != null) displayList.add("[Bottom right 3] " + bottomRight3);

        if (announcement != null) displayList.add("[Announcement] " + announcement);
        return displayList;
    }
}
