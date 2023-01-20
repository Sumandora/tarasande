package de.florianmichael.clampclient.injection.signature.model.chain.v1_19_2;

import de.florianmichael.clampclient.injection.signature.model.ClampSignatureUpdater;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class ClampMessageHeader {
    private final UUID sender;
    private final byte[] precedingSignature;

    public ClampMessageHeader(UUID sender, byte[] precedingSignature) {
        this.sender = sender;
        this.precedingSignature = precedingSignature;
    }

    public byte[] toByteArray(final UUID uuid) {
        final byte[] data = new byte[16];
        final ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);

        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());

        return data;
    }

    public void updater(final byte[] bodyDigest, final ClampSignatureUpdater updater) {
        if (precedingSignature != null) {
            updater.update(precedingSignature);
        }

        updater.update(toByteArray(getSender()));
        updater.update(bodyDigest);
    }

    public UUID getSender() {
        return sender;
    }

    public byte[] getPrecedingSignature() {
        return precedingSignature;
    }
}
