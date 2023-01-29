package de.florianmichael.viabedrock.protocol.storage;

import com.nukkitx.protocol.bedrock.BedrockClient;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viabedrock.ViaBedrock;
import de.florianmichael.viabedrock.api.BedrockProtocols;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class BedrockSessionStorage extends StoredObject {

    private final InetSocketAddress targetAddress;
    private final BedrockClient bedrockClient;

    public BedrockSessionStorage(UserConnection user, final InetSocketAddress targetAddress) {
        super(user);
        this.targetAddress = targetAddress;

        this.bedrockClient = new BedrockClient(new InetSocketAddress("0.0.0.0", ThreadLocalRandom.current().nextInt(20000, 60000)));
        this.bedrockClient.bind().join();
    }

    public void connect(final Consumer<BedrockClient> connection) {
        bedrockClient.connect(this.getTargetAddress()).whenComplete((bedrockClientSession, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            bedrockClientSession.addDisconnectHandler((disconnectReason) -> ViaBedrock.getPlatform().getLogger().info("Fake-Bedrock Client got disconnected: " + disconnectReason));
            bedrockClientSession.setPacketCodec(BedrockProtocols.CODEC);

            connection.accept(bedrockClient);
        }).join();
    }

    public InetSocketAddress getTargetAddress() {
        return targetAddress;
    }

    public BedrockClient getBedrockClient() {
        return bedrockClient;
    }
}
