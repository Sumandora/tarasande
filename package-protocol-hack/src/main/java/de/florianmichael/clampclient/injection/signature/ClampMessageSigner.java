package de.florianmichael.clampclient.injection.signature;

import de.florianmichael.clampclient.injection.signature.model.ClampSignatureUpdatable;

import java.security.*;

public interface ClampMessageSigner {

    byte[] sign(final ClampSignatureUpdatable signer);

    static ClampMessageSigner create(final PrivateKey privateKey, final String algorithm) {
        return signer -> {
            try {
                final Signature signature = Signature.getInstance(algorithm);
                signature.initSign(privateKey);

                signer.update(data -> {
                    try {
                        signature.update(data);
                    } catch (SignatureException e) {
                        throw new RuntimeException(e);
                    }
                });
                return signature.sign();
            } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                throw new IllegalStateException("Failed to sign message", e);
            }
        };
    }
}
