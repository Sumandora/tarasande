package de.florianmichael.viaprotocolhack.platform.viaversion;

import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.IntLinkedOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.libs.gson.JsonObject;
import de.florianmichael.viaprotocolhack.netty.NettyConstants;
import de.florianmichael.viaprotocolhack.util.VersionList;

public class CustomViaInjector implements ViaInjector {

    @Override
    public void inject() throws Exception {
        // Implemented by Mixins
    }

    @Override
    public void uninject() throws Exception {
        // ICM
    }

    @Override
    public String getEncoderName() {
        return NettyConstants.HANDLER_ENCODER_NAME;
    }

    @Override
    public String getDecoderName() {
        return NettyConstants.HANDLER_DECODER_NAME;
    }

    @Override
    public IntSortedSet getServerProtocolVersions() throws Exception {
        final IntSortedSet versions = new IntLinkedOpenHashSet();

        for (ProtocolVersion value : VersionList.getProtocols())
            versions.add(value.getOriginalVersion());

        return versions;
    }

    @Override
    public int getServerProtocolVersion() throws Exception {
        return this.getServerProtocolVersions().firstInt();
    }

    @Override
    public JsonObject getDump() {
        return new JsonObject();
    }
}
