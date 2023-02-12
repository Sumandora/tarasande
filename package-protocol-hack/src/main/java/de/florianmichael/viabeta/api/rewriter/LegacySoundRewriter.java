package de.florianmichael.viabeta.api.rewriter;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.rewriter.Rewriter;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectOpenHashMap;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;

@SuppressWarnings("unchecked")
public abstract class LegacySoundRewriter<P extends Protocol> extends RewriterBase<P> implements Rewriter<P> {

    protected final Object2ObjectMap<String, String> soundMappings;
    protected final String protocolName;

    public LegacySoundRewriter(P protocol, final String protocolName, final int expected) {
        super(protocol);
        this.protocolName = protocolName;
        this.soundMappings = new Object2ObjectOpenHashMap<>(expected, 0.99F);
    }

    public void addSoundMapping(final String oldName, final String newName) {
        soundMappings.put(oldName, newName);
    }

    public void registerNamedSound(final ClientboundPacketType packetType) {
        protocol.registerClientbound(packetType, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    final String newSound = handleSoundRewrite(wrapper, wrapper.read(Type1_6_4.STRING));
                    if (newSound != null) {
                        wrapper.write(Type.STRING, newSound);
                    }
                });
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.FLOAT); // volume
                map(Type.UNSIGNED_BYTE); // pitch
            }
        });
    }


    public void registerNamedSound_1_6_4(final ClientboundPacketType packetType) {
        protocol.registerClientbound(packetType, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    final String newSound = handleSoundRewrite(wrapper, wrapper.read(Type1_6_4.STRING));
                    if (newSound != null) {
                        wrapper.write(Type1_6_4.STRING, newSound);
                    }
                });
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.FLOAT); // volume
                map(Type.UNSIGNED_BYTE); // pitch
            }
        });
    }

    private String handleSoundRewrite(final PacketWrapper wrapper, final String oldSound) {
        String newSound = soundMappings.get(oldSound);
        if (oldSound.isEmpty()) newSound = "";
        if (newSound == null) {
            ViaBeta.getPlatform().getLogger().warning("Unable to map " + protocolName + " sound '" + oldSound + "'");
            newSound = "";
        }
        if (newSound.isEmpty()) {
            wrapper.cancel();
            return null;
        }
        return newSound;
    }
}
