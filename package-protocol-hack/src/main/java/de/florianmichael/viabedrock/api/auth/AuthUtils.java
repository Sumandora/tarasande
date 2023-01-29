package de.florianmichael.viabedrock.api.auth;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AuthUtils {

    private static final KeyPairGenerator KEY_PAIR_GEN;

    static {
        try {
            KEY_PAIR_GEN = KeyPairGenerator.getInstance("EC");
            KEY_PAIR_GEN.initialize(new ECGenParameterSpec("secp256r1"));//use P-256
        } catch (Exception e) {
            throw new AssertionError("Unable to initialize required encryption", e);
        }
    }

    public static KeyPair createKeyPair() {
        return KEY_PAIR_GEN.generateKeyPair();
    }

    public static String signBytes(ECPrivateKey privateKey, byte[] data) throws Exception {
        Signature signature = Signature.getInstance("SHA384withECDSA");
        signature.initSign(privateKey);
        signature.update(data);
        byte[] signatureBytes = JoseStuff.DERToJOSE(signature.sign(), JoseStuff.AlgorithmType.ECDSA384);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }

    public static String getOfflineChainData(String username, ECPrivateKey privateKey, ECPublicKey publicKey) throws Exception {
        // So we need to assign the uuid from a username, or else everytime we join a server with the same name, we will get reset (as if we are a new player)
        // Java does it this way, I'm not sure if bedrock does, but it gets our goal accomplished, PlayerEntity.getOfflinePlayerUuid()
        UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
        String xuid = Long.toString(offlineUUID.getLeastSignificantBits());

        Gson gson = new Gson();

        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        JsonObject chain = new JsonObject();//jwtPayload
//        chain.addProperty("certificateAuthority", true);
        chain.addProperty("exp", Instant.now().getEpochSecond() + TimeUnit.HOURS.toSeconds(6));
        chain.addProperty("identityPublicKey", publicKeyBase64);
        chain.addProperty("nbf", Instant.now().getEpochSecond() - TimeUnit.HOURS.toSeconds(6));

        JsonObject extraData = new JsonObject();
        extraData.addProperty("titleId", "896928775"); // Use the Title ID for Windows 10, as some servers ban for an incorrect Title ID.
        extraData.addProperty("identity", offlineUUID.toString());
        extraData.addProperty("displayName", username);
        extraData.addProperty("XUID", xuid);
        chain.add("extraData", extraData);

        JsonObject jwtHeader = new JsonObject();
        jwtHeader.addProperty("alg", "ES384");
        jwtHeader.addProperty("x5u", publicKeyBase64);

        String header = Base64.getUrlEncoder().withoutPadding().encodeToString(gson.toJson(jwtHeader).getBytes());
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(gson.toJson(chain).getBytes());

        byte[] dataToSign = (header + "." + payload).getBytes();
        String signatureString = signBytes(privateKey, dataToSign);

        String jwt = header + "." + payload + "." + signatureString;

        //create a json object with our 1 chain array
        JsonArray chainDataJsonArray = new JsonArray();
        chainDataJsonArray.add(jwt);

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("chain", chainDataJsonArray);

        return gson.toJson(jsonObject);
    }
}