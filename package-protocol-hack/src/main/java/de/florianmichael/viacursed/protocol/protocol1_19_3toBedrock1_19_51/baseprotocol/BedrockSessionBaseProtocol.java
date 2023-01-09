package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.baseprotocol;

import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import com.viaversion.viaversion.protocols.base.ClientboundStatusPackets;
import com.viaversion.viaversion.protocols.base.ServerboundHandshakePackets;
import com.viaversion.viaversion.protocols.base.ServerboundStatusPackets;
import de.florianmichael.viacursed.ViaCursed;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage.BedrockSessionStorage;
import io.netty.buffer.Unpooled;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class BedrockSessionBaseProtocol extends AbstractSimpleProtocol {

    public static final BedrockSessionBaseProtocol INSTANCE = new BedrockSessionBaseProtocol();

    public BedrockSessionBaseProtocol() {
        this.initialize();
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();

        this.registerServerbound(State.HANDSHAKE, ServerboundHandshakePackets.CLIENT_INTENTION.getId(), ServerboundHandshakePackets.CLIENT_INTENTION.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    wrapper.read(Type.VAR_INT); // protocol version

                    final String hostname = wrapper.read(Type.STRING);
                    final int port = wrapper.read(Type.UNSIGNED_SHORT);

                    wrapper.user().put(new BedrockSessionStorage(wrapper.user(), new InetSocketAddress(hostname, port)));
                    ViaCursed.getPlatform().getLogger().log(Level.INFO, "Created BedrockSessionStorage");
                });
            }
        });

        this.registerServerbound(State.STATUS, ServerboundStatusPackets.STATUS_REQUEST.getId(), ServerboundStatusPackets.STATUS_REQUEST.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    final BedrockSessionStorage bedrockSessionStorage = wrapper.user().get(BedrockSessionStorage.class);
                    if (bedrockSessionStorage == null) throw new IllegalStateException("BedrockSessionStorage is null?");

                    CompletableFuture.runAsync(() -> {
                        final long startTime = System.currentTimeMillis();

                        bedrockSessionStorage.bedrockClient.ping(bedrockSessionStorage.targetAddress).whenComplete((bedrockPong, throwable) -> {
                            if (throwable != null) {
                                throwable.printStackTrace();
                                return;
                            }

                            final PacketWrapper statusResponse = new PacketWrapperImpl(ClientboundStatusPackets.STATUS_RESPONSE.getId(), Unpooled.buffer(), wrapper.user());
                            final JsonObject rootObject = new JsonObject();
                            final JsonObject descriptionObject = new JsonObject();
                            final JsonObject playersObject = new JsonObject();
                            final JsonObject versionObject = new JsonObject();

                            descriptionObject.addProperty("text", bedrockPong.getMotd());
                            playersObject.addProperty("max", bedrockPong.getMaximumPlayerCount());
                            playersObject.addProperty("online", bedrockPong.getPlayerCount());
                            versionObject.addProperty("name", bedrockPong.getVersion());
                            versionObject.addProperty("protocol", bedrockPong.getProtocolVersion());
                            rootObject.add("description", descriptionObject);
                            rootObject.add("players", playersObject);
                            rootObject.add("version", versionObject);
                            statusResponse.write(Type.STRING, rootObject.toString());

                            final PacketWrapper pongResponse = new PacketWrapperImpl(ClientboundStatusPackets.PONG_RESPONSE, Unpooled.buffer(), wrapper.user());
                            pongResponse.write(Type.LONG, System.currentTimeMillis() - startTime);

                            try {
                                statusResponse.send(BedrockSessionBaseProtocol.class);
                                pongResponse.send(BedrockSessionBaseProtocol.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).join();
                    }).whenComplete((unused, throwable) -> {
                        if (throwable != null) throwable.printStackTrace();
                    });
                });
            }
        });

        this.cancelServerbound(State.STATUS, ServerboundStatusPackets.PING_REQUEST.getId());
    }

    @Override
    public boolean isBaseProtocol() {
        return true;
    }
}
