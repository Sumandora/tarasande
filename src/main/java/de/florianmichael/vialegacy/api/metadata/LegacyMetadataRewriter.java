package de.florianmichael.vialegacy.api.metadata;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import de.florianmichael.vialegacy.api.EnZaProtocol;

import java.util.List;

public abstract class LegacyMetadataRewriter<T extends EnZaProtocol<?, ?, ?, ?>> extends RewriterBase<T> {

    public LegacyMetadataRewriter(T protocol) {
        super(protocol);
    }

    public abstract void rewrite(final Entity1_10Types.EntityType entityType, final boolean isObject, final List<Metadata> metadata);
}
