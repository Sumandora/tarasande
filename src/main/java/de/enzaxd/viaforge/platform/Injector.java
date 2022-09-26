package de.enzaxd.viaforge.platform;

import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.libs.gson.JsonObject;
import de.enzaxd.viaforge.ViaForge;
import de.enzaxd.viaforge.handler.CommonTransformer;

public class Injector implements ViaInjector {

    private final ViaForge viaForge;

    public Injector(ViaForge viaForge) {
        this.viaForge = viaForge;
    }

    @Override
    public void inject() {
    }

    @Override
    public void uninject() {
    }

    @Override
    public int getServerProtocolVersion() {
        return viaForge.getVersion();
    }

    @Override
    public String getEncoderName() {
        return CommonTransformer.HANDLER_ENCODER_NAME;
    }

    @Override
    public String getDecoderName() {
        return CommonTransformer.HANDLER_DECODER_NAME;
    }

    @Override
    public JsonObject getDump() {
        return new JsonObject();
    }
}
