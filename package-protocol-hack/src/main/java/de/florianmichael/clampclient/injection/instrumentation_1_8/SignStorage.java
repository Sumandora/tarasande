package de.florianmichael.clampclient.injection.instrumentation_1_8;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.libs.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class SignStorage extends StoredObject {

    private final List<SignModel_1_8> signs = new ArrayList<>();

    public SignStorage(UserConnection user) {
        super(user);
    }

    public List<SignModel_1_8> getSigns() {
        return signs;
    }

    public record SignModel_1_8(JsonElement line1, JsonElement line2, JsonElement line3, JsonElement line4, Position position) {
    }
}
