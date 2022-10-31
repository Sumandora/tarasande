package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.bridge.game.PackType;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.ViaProtocolHack;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.GameVersion;
import net.minecraft.SaveVersion;
import net.minecraft.SharedConstants;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.tarasandedevelopment.tarasande.features.protocol.platform.ProtocolHackValues;
import org.apache.commons.codec.digest.DigestUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

@Mixin(ClientBuiltinResourcePackProvider.class)
public class MixinClientBuiltinResourcePackProvider {

    @Unique
    private File protocolhack_trackedFile;

    @Redirect(method = "getDownloadHeaders", at = @At(value = "INVOKE", target = "Lnet/minecraft/SharedConstants;getGameVersion()Lnet/minecraft/GameVersion;"))
    private static GameVersion editHeaders() {
        if (ProtocolHackValues.INSTANCE.getChangeResourcePackDownloadHeaders().getValue()) {
            return new GameVersion() {
                @Override
                public SaveVersion getSaveVersion() {
                    return null;
                }

                @Override
                public String getId() {
                    return ProtocolHackValues.INSTANCE.getVersionName().getValue();
                }

                @Override
                public String getName() {
                    return ProtocolHackValues.INSTANCE.getVersionName().getValue();
                }

                @Override
                public String getReleaseTarget() {
                    return null;
                }

                @Override
                public int getProtocolVersion() {
                    return ViaProtocolHack.instance().provider().getClientsideVersion();
                }

                @Override
                public Date getBuildTime() {
                    return null;
                }

                @Override
                public boolean isStable() {
                    return false;
                }

                @Override
                public int getPackVersion(PackType packType) {
                    return (int) ProtocolHackValues.INSTANCE.getPackFormat().getValue();
                }
            };
        }
        return SharedConstants.getGameVersion();
    }

    @Inject(method = "getDownloadHeaders", at = @At("TAIL"))
    private static void removeHeaders(CallbackInfoReturnable<Map<String, String>> cir) {
        if (ProtocolHackValues.INSTANCE.getChangeResourcePackDownloadHeaders().getValue()) {
            if (VersionList.isOlderTo(ProtocolVersion.v1_14))
                cir.getReturnValue().remove("X-Minecraft-Version-ID");
            if (VersionList.isOlderTo(ProtocolVersion.v1_13))
                cir.getReturnValue().remove("X-Minecraft-Pack-Format");
        }
    }

    @Inject(method = "verifyFile", at = @At("HEAD"))
    public void keepFile(String expectedSha1, File file, CallbackInfoReturnable<Boolean> cir) {
        protocolhack_trackedFile = file;
    }

    @Redirect(method = "verifyFile", at = @At(value = "INVOKE", target = "Lcom/google/common/hash/HashCode;toString()Ljava/lang/String;", remap = false))
    public String revertHashAlgorithm(HashCode instance) {
        try {
            if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8)) {
                //noinspection UnstableApiUsage,deprecation
                return Hashing.sha1().hashBytes(Files.toByteArray(protocolhack_trackedFile)).toString();
            } else if (VersionList.isOlderTo(ProtocolVersion.v1_18)) {
                return DigestUtils.sha1Hex(new FileInputStream(protocolhack_trackedFile));
            }
        } catch (IOException ignored) {
        }
        return instance.toString();
    }

    @Redirect(method = "verifyFile", at = @At(value = "INVOKE", target = "Ljava/lang/String;toLowerCase(Ljava/util/Locale;)Ljava/lang/String;"))
    public String disableIgnoreCase(String instance, Locale locale) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8)) {
            return instance;
        }
        return instance.toLowerCase(locale);
    }
}
