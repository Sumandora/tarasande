package de.florianmichael.clampclient.injection.signature.provider;

import com.viaversion.viaversion.api.platform.providers.Provider;
import net.minecraft.util.Pair;

import java.util.List;

public class CommandArgumentsProvider implements Provider {

    public List<Pair<String, String>> getSignedArguments(final String command) {
        return null;
    }
}
