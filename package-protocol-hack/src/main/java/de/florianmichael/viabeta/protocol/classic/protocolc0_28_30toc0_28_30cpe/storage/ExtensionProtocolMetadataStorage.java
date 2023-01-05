package de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.data.ClassicProtocolExtension;

import java.util.EnumMap;

public class ExtensionProtocolMetadataStorage extends StoredObject {

    private String serverSoftwareName = "classic";
    private short extensionCount = -1;
    private short receivedExtensions = 0;

    private final EnumMap<ClassicProtocolExtension, Integer> serverExtensions = new EnumMap<>(ClassicProtocolExtension.class);

    public ExtensionProtocolMetadataStorage(final UserConnection user) {
        super(user);
    }

    public void setServerSoftwareName(final String serverSoftwareName) {
        if (serverSoftwareName.isEmpty()) return;
        this.serverSoftwareName = serverSoftwareName;
    }

    public String getServerSoftwareName() {
        return this.serverSoftwareName;
    }

    public void setExtensionCount(final short extensionCount) {
        this.extensionCount = extensionCount;
    }

    public short getExtensionCount() {
        return this.extensionCount;
    }

    public void incrementReceivedExtensions() {
        this.receivedExtensions++;
    }

    public short getReceivedExtensions() {
        return this.receivedExtensions;
    }

    public void addServerExtension(final ClassicProtocolExtension extension, final int version) {
        this.serverExtensions.put(extension, version);
    }

    public boolean hasServerExtension(final ClassicProtocolExtension extension, final int... versions) {
        final Integer extensionVersion = this.serverExtensions.get(extension);
        if (extensionVersion == null) return false;
        if (versions.length == 0) return true;

        for (int version : versions) {
            if (version == extensionVersion) return true;
        }
        return false;
    }
}
