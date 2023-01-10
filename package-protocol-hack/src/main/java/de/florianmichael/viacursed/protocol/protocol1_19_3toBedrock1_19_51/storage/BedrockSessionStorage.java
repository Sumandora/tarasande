package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage;

import com.nukkitx.protocol.bedrock.BedrockClient;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.v560.Bedrock_v560;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viacursed.ViaCursed;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.data.LoginData;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class BedrockSessionStorage extends StoredObject {
    public static final BedrockPacketCodec CODEC = Bedrock_v560.V560_CODEC;
    public final InetSocketAddress targetAddress;
    public final LoginData loginData;
    public final BedrockClient bedrockClient;

    public BedrockSessionStorage(UserConnection user, final InetSocketAddress targetAddress) {
        super(user);

        this.targetAddress = targetAddress;
        this.loginData = new LoginData();
        this.bedrockClient = new BedrockClient(new InetSocketAddress("0.0.0.0", ThreadLocalRandom.current().nextInt(20000, 60000)));

        this.bedrockClient.bind().join();
    }

    public void connect(final BedrockClientConsumer connect) throws Exception {
        bedrockClient.connect(targetAddress).whenComplete((clientSession, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }
            clientSession.addDisconnectHandler((disconnectReason) -> ViaCursed.getPlatform().getLogger().log(Level.INFO, "Fake-Bedrock Client got disconnected: " + disconnectReason));
            clientSession.setPacketCodec(CODEC);
            try {
                connect.connect(bedrockClient);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).join();
    }

    public interface BedrockClientConsumer {

        void connect(final BedrockClient bedrockClient) throws Exception;
    }
}
