package de.florianmichael.clampclient.injection.signature.storage;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import de.florianmichael.clampclient.injection.signature.ClampMessageMetadata;
import de.florianmichael.clampclient.injection.signature.model.chain.v1_19_2.ClampMessageBody;
import de.florianmichael.clampclient.injection.signature.model.chain.v1_19_2.ClampMessageHeader;

import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.UUID;

public class ChatSession1_19_2 extends AbstractChatSession {
    public final static SecureRandom SECURE_RANDOM = new SecureRandom();

    private byte[] precedingSignature = null;

    public ChatSession1_19_2(UserConnection user, ProfileKey profileKey, PrivateKey privateKey) {
        super(user, profileKey, privateKey);
    }

    public byte[] sign(final UUID sender, final ClampMessageMetadata messageMetadata, final PlayerMessageSignature[] lastSeenMessages) {
        final ClampMessageHeader header = new ClampMessageHeader(sender, precedingSignature);
        final ClampMessageBody body = new ClampMessageBody(messageMetadata, lastSeenMessages);

        precedingSignature = getSigner().sign(updater -> header.updater(body.digestBytes(), updater));

        return precedingSignature;
    }
}
