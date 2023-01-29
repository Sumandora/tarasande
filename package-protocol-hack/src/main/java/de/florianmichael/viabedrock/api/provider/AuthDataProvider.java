package de.florianmichael.viabedrock.api.provider;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

import java.security.KeyPair;

public class AuthDataProvider extends StoredObject {

    private final String username;
    private final String chainData;
    private final String bedrockXuid;
    private final KeyPair keyPair;
    private final Signer signer;

    public AuthDataProvider(UserConnection user, String username, String chainData, String bedrockXuid, KeyPair keyPair, Signer signer) {
        super(user);
        this.username = username;
        this.chainData = chainData;
        this.bedrockXuid = bedrockXuid;
        this.keyPair = keyPair;
        this.signer = signer;
    }

    public String getUsername() {
        return username;
    }

    public String getChainData() {
        return chainData;
    }

    public String getBedrockXuid() {
        return bedrockXuid;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public Signer getSigner() {
        return signer;
    }

    public interface Signer {
        String signBytes(final byte[] input) throws Exception;
    }
}
