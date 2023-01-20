package de.florianmichael.clampclient.injection.signature.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import de.florianmichael.clampclient.injection.signature.ClampMessageSigner;

import java.security.PrivateKey;

public abstract class AbstractChatSession extends StoredObject {
    private final ProfileKey profileKey;
    private final PrivateKey privateKey;

    private final ClampMessageSigner signer;

    public AbstractChatSession(UserConnection user, final ProfileKey profileKey, final PrivateKey privateKey) {
        super(user);
        this.profileKey = profileKey;
        this.privateKey = privateKey;

        this.signer = ClampMessageSigner.create(privateKey, "SHA256withRSA");
    }

    public ProfileKey getProfileKey() {
        return profileKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public ClampMessageSigner getSigner() {
        return signer;
    }
}
