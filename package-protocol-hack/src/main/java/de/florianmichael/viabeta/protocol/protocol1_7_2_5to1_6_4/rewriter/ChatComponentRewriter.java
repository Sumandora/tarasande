package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.rewriter;

import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.rewriter.ComponentRewriter;

public class ChatComponentRewriter {

    private static final ComponentRewriter FIX_COMPONENT = new ComponentRewriter() {
        @Override
        protected void handleTranslate(JsonObject object, String translate) {
            final JsonElement args = object.remove("using");
            if (args != null) {
                object.add("with", args);
            }
        }
    };

    public static String toClient(final String text) {
        return FIX_COMPONENT.processText(text).toString();
    }

}
