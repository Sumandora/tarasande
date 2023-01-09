package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51;

import com.google.gson.Gson;
import com.nimbusds.jwt.SignedJWT;
import com.nukkitx.protocol.bedrock.BedrockClientSession;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.authentication.BedrockAuthenticator;

import javax.crypto.SecretKey;
import java.net.URI;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;

public class IncomingBedrockPacketHandler extends StoredObject implements BedrockPacketHandler {

    private final Gson gson = new Gson();
    private final BedrockClientSession bedrockClientSession;

    public IncomingBedrockPacketHandler(UserConnection user, BedrockClientSession bedrockClientSession) {
        super(user);
        this.bedrockClientSession = bedrockClientSession;
    }

    @Override
    public boolean handle(DisconnectPacket packet) {
        System.out.println(packet.getKickMessage());
        return false;
    }

    @Override
    public boolean handle(ServerToClientHandshakePacket packet) {
        try {
            final SignedJWT saltJwt = SignedJWT.parse(packet.getJwt());
            final URI x5u = saltJwt.getHeader().getX509CertURL();
            final ECPublicKey serverKey = EncryptionUtils.generateKey(x5u.toASCIIString());
            final SecretKey key = EncryptionUtils.getSecretKey(
                    BedrockAuthenticator.INSTANCE.getPrivateKey(),
                    serverKey,
                    Base64.getDecoder().decode(saltJwt.getJWTClaimsSet().getStringClaim("salt"))
            );

            this.bedrockClientSession.enableEncryption(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bedrockClientSession.sendPacketImmediately(new ClientToServerHandshakePacket());
        return true;
    }

    @Override
    public boolean handle(PlayStatusPacket packet) {
        System.out.println("UPDATE STINKT UND IST FETT");
        return false;
    }
}
