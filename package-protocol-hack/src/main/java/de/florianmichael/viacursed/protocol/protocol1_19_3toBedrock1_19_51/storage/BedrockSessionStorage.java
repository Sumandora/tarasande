package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage;

import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viacursed.netty.BedrockConnection;

import java.net.InetSocketAddress;
import java.security.KeyPair;

public class BedrockSessionStorage extends StoredObject {

    public final KeyPair keyPair = EncryptionUtils.createKeyPair();

    private final InetSocketAddress targetAddress;
    private BedrockConnection bedrockConnection;
    public long xuid;

    public BedrockSessionStorage(final UserConnection user, final InetSocketAddress targetAddress) {
        super(user);
        this.targetAddress = targetAddress;
    }

    public void connectToBedrock(final Runnable onConnected) {
        try {
            this.bedrockConnection = new BedrockConnection(onConnected);
            this.getBedrockConnection().create(this.targetAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BedrockConnection getBedrockConnection() {
        return bedrockConnection;
    }
}
