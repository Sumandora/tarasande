package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage;

import com.nukkitx.protocol.bedrock.BedrockClient;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.v560.Bedrock_v560;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viacursed.ViaCursed;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BedrockSessionStorage extends StoredObject {
    public static final BedrockPacketCodec CODEC = Bedrock_v560.V560_CODEC;
    public final InetSocketAddress targetAddress;
    public final BedrockClient bedrockClient;

    public BedrockSessionStorage(UserConnection user, final InetSocketAddress targetAddress) {
        super(user);
        this.targetAddress = targetAddress;

        bedrockClient = new BedrockClient(new InetSocketAddress("0.0.0.0", ThreadLocalRandom.current().nextInt(20000, 60000)));
        bedrockClient.bind().join();
    }

    public void connect(final Consumer<BedrockClient> connect) {
        bedrockClient.connect(targetAddress).whenComplete((clientSession, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }
            clientSession.addDisconnectHandler((disconnectReason) -> ViaCursed.getPlatform().getLogger().log(Level.INFO, "Fake-Bedrock Client got disconnected: " + disconnectReason));
            clientSession.setBatchHandler((session, compressed, packets) -> ViaCursed.getPlatform().getLogger().log(Level.INFO, "Handling incoming Packets: " + packets.stream().map(p -> p.getClass().getSimpleName()).collect(Collectors.joining(", "))));
            clientSession.setPacketCodec(CODEC);
            connect.accept(bedrockClient);
        }).join();
    }
}
