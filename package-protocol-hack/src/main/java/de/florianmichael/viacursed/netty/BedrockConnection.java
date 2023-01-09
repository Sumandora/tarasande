package de.florianmichael.viacursed.netty;

import com.nukkitx.protocol.bedrock.*;
import com.nukkitx.protocol.bedrock.handler.BatchHandler;
import com.nukkitx.protocol.bedrock.v560.Bedrock_v560;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.ClientboundPacketHandler;
import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class BedrockConnection {
    public final static BedrockPacketCodec CODEC = Bedrock_v560.V560_CODEC;
    private final Runnable onConnected;
    private BedrockClientSession bedrockClientSession;

    public BedrockConnection(final Runnable onConnected) {
        this.onConnected = onConnected;
    }

    public void create(final InetSocketAddress targetAddress) {
        final BedrockClient bedrockClient = new BedrockClient(new InetSocketAddress("0.0.0.0", ThreadLocalRandom.current().nextInt(20000, 60000)));
        bedrockClient.bind().join();

        bedrockClient.connect(targetAddress).whenComplete((clientSession, throwable) -> {
            if (throwable != null) {
                // Servers disconnects
                throwable.printStackTrace();
                return;
            }
            System.out.println("UWU");
            clientSession.addDisconnectHandler((disconnectReason) -> {
                System.out.println("Disconnected Via Handler: " + disconnectReason.name());
            });
            clientSession.setBatchHandler(new BatchHandler() {
                @Override
                public void handle(BedrockSession session, ByteBuf compressed, Collection<BedrockPacket> packets) {
                    System.out.println(session.getAddress() + " " + packets.stream().map(c -> c.getClass().getSimpleName()).collect(Collectors.joining()));
                }
            });
            clientSession.setPacketCodec(CODEC);
            clientSession.setPacketHandler(new ClientboundPacketHandler());
            this.bedrockClientSession = clientSession;
            if (this.onConnected != null) {
                this.onConnected.run();
            }
        }).join();
    }

    public BedrockClientSession getSession() {
        return this.bedrockClientSession;
    }
}
