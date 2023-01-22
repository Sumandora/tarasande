package de.florianmichael.clampclient.injection.instrumentation_1_19_0;

import de.florianmichael.clampclient.injection.instrumentation_1_19_0.model.SignatureUpdatableModel;

import java.security.*;

public interface ViaMessageSigner {

    byte[] sign(final SignatureUpdatableModel signer);

    static ViaMessageSigner create(final PrivateKey privateKey, final String algorithm) {
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
