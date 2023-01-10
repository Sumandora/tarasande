package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.Protocol1_19_3toBedrock1_19_51;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.util.JoseStuff;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.util.XboxRequests;

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

public class LoginData {

	private final KeyPairGenerator KEY_PAIR_GEN;

	private ECPublicKey publicKey;
	private ECPrivateKey privateKey;

	public LoginData() {
		try {
			KEY_PAIR_GEN = KeyPairGenerator.getInstance("EC");
			KEY_PAIR_GEN.initialize(new ECGenParameterSpec("secp256r1"));//use P-256
		} catch (Exception e) {
			throw new AssertionError("Unable to initialize required encryption", e);
		}
	}

	public String getOnlineChainData(final String authToken) throws Exception {
		final KeyPair ecdsa256KeyPair = createKeyPair();
		this.publicKey = (ECPublicKey) ecdsa256KeyPair.getPublic();
		this.privateKey = (ECPrivateKey) ecdsa256KeyPair.getPrivate();

		final XboxRequests xbox = new XboxRequests(authToken);

		final String userToken = xbox.getUserToken(this.publicKey, this.privateKey);
		final String deviceToken = xbox.getDeviceToken(this.publicKey, this.privateKey);
		final String titleToken = xbox.getTitleToken(this.publicKey, this.privateKey, deviceToken);
		final String xstsToken = xbox.getXstsToken(userToken, deviceToken, titleToken, this.publicKey, this.privateKey);

		final KeyPair ecdsa384KeyPair = EncryptionUtils.createKeyPair();
		this.publicKey = (ECPublicKey) ecdsa384KeyPair.getPublic();
		this.privateKey = (ECPrivateKey) ecdsa384KeyPair.getPrivate();

		final String chainData = xbox.requestMinecraftChain(xstsToken, this.publicKey);
		final JsonObject chainDataObject = Protocol1_19_3toBedrock1_19_51.BEDROCKED_GSON.fromJson(chainData, JsonObject.class).getAsJsonObject();
		final JsonArray minecraftNetChain = chainDataObject.get("chain").getAsJsonArray();

		String firstChainHeader = minecraftNetChain.get(0).getAsString();
		firstChainHeader = firstChainHeader.split("\\.")[0];
		firstChainHeader = new String(Base64.getDecoder().decode(firstChainHeader.getBytes()));

		String firstKeyx5u = Protocol1_19_3toBedrock1_19_51.BEDROCKED_GSON.fromJson(firstChainHeader, JsonObject.class).get("x5u").getAsString();

		final JsonObject newFirstChain = new JsonObject();
		newFirstChain.addProperty("certificateAuthority", true);
		newFirstChain.addProperty("exp", Instant.now().getEpochSecond() + TimeUnit.HOURS.toSeconds(6));
		newFirstChain.addProperty("identityPublicKey", firstKeyx5u);
		newFirstChain.addProperty("nbf", Instant.now().getEpochSecond() - TimeUnit.HOURS.toSeconds(6));

		{
			final String publicKeyBase64 = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
			final JsonObject jwtHeader = new JsonObject();
			jwtHeader.addProperty("alg", "ES384");
			jwtHeader.addProperty("x5u", publicKeyBase64);

			final String header = Base64.getUrlEncoder().withoutPadding().encodeToString(Protocol1_19_3toBedrock1_19_51.BEDROCKED_GSON.toJson(jwtHeader).getBytes());
			final String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(Protocol1_19_3toBedrock1_19_51.BEDROCKED_GSON.toJson(newFirstChain).getBytes());

			byte[] dataToSign = (header + "." + payload).getBytes();
			String signatureString = this.signBytes(dataToSign);

			String jwt = header + "." + payload + "." + signatureString;

			chainDataObject.add("chain", this.addChainToBeginning(jwt, minecraftNetChain));
		}

		return Protocol1_19_3toBedrock1_19_51.BEDROCKED_GSON.toJson(chainDataObject);
	}

	public String getOfflineChainData(String username) throws Exception {
		UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));

		KeyPair ecdsa256KeyPair = createKeyPair();
		this.publicKey = (ECPublicKey) ecdsa256KeyPair.getPublic();
		this.privateKey = (ECPrivateKey) ecdsa256KeyPair.getPrivate();

		String publicKeyBase64 = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());

		final JsonObject chain = new JsonObject();

		chain.addProperty("exp", Instant.now().getEpochSecond() + TimeUnit.HOURS.toSeconds(6));
		chain.addProperty("identityPublicKey", publicKeyBase64);
		chain.addProperty("nbf", Instant.now().getEpochSecond() - TimeUnit.HOURS.toSeconds(6));

		JsonObject extraData = new JsonObject();
		extraData.addProperty("identity", offlineUUID.toString());
		extraData.addProperty("displayName", username);
		chain.add("extraData", extraData);

		JsonObject jwtHeader = new JsonObject();
		jwtHeader.addProperty("alg", "ES384");
		jwtHeader.addProperty("x5u", publicKeyBase64);

		String header = Base64.getUrlEncoder().withoutPadding().encodeToString(Protocol1_19_3toBedrock1_19_51.BEDROCKED_GSON.toJson(jwtHeader).getBytes());
		String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(Protocol1_19_3toBedrock1_19_51.BEDROCKED_GSON.toJson(chain).getBytes());

		byte[] dataToSign = (header + "." + payload).getBytes();
		String signatureString = this.signBytes(dataToSign);

		String jwt = header + "." + payload + "." + signatureString;

		JsonArray chainDataJsonArray = new JsonArray();
		chainDataJsonArray.add(jwt);

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("chain", chainDataJsonArray);

		return Protocol1_19_3toBedrock1_19_51.BEDROCKED_GSON.toJson(jsonObject);
	}

	public String signBytes(byte[] dataToSign) throws Exception {
		Signature signature = Signature.getInstance("SHA384withECDSA");
		signature.initSign(this.privateKey);
		signature.update(dataToSign);
		byte[] signatureBytes = JoseStuff.DERToJOSE(signature.sign(), JoseStuff.AlgorithmType.ECDSA384);

		return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
	}

	private JsonArray addChainToBeginning(String chain, JsonArray chainArray) {
		JsonArray newArray = new JsonArray();
		newArray.add(chain);

		for (JsonElement jsonElement : chainArray) {
			newArray.add(jsonElement);
		}
		return newArray;
	}

	public KeyPair createKeyPair() {
		return KEY_PAIR_GEN.generateKeyPair();
	}

	public ECPublicKey getPublicKey() {
		return this.publicKey;
	}

	public ECPrivateKey getPrivateKey() {
		return this.privateKey;
	}
}
