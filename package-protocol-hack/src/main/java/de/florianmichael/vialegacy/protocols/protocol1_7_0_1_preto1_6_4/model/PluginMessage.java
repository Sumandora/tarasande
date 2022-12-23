package de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.model;

public class PluginMessage {

    public final String channel;
    public final byte[] message;

    public PluginMessage(String channel, byte[] message) {
        this.channel = channel;
        this.message = message;
    }
}
