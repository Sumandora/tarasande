package de.enzaxd.viaforge.platform;

import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.libs.gson.JsonObject;
import de.enzaxd.viaforge.handler.CommonTransformer;
import su.mandora.tarasande.TarasandeMain;

public class Injector implements ViaInjector {

    @Override
    public void inject() {
    }

    @Override
    public void uninject() {
    }

    @Override
    public int getServerProtocolVersion() {
        return TarasandeMain.Companion.get().getProtocolHack().getVersion();
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
        JsonObject obj = new JsonObject();
        return obj;
    }
}
