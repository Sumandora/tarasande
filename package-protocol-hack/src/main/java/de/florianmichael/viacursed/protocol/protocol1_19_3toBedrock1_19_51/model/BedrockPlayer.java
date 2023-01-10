package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.model;

public class BedrockPlayer {
    private final long runtimeEntityId;
    private final long uniqueEntityId;

    public BedrockPlayer(long runtimeEntityId, long uniqueEntityId) {
        this.runtimeEntityId = runtimeEntityId;
        this.uniqueEntityId = uniqueEntityId;
    }

    public long getRuntimeEntityId() {
        return runtimeEntityId;
    }

    public long getUniqueEntityId() {
        return uniqueEntityId;
    }
}
