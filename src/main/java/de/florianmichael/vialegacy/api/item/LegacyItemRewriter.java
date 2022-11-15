package de.florianmichael.vialegacy.api.item;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class LegacyItemRewriter<T extends EnZaProtocol<?, ?, ?, ?>> extends RewriterBase<T> implements ItemRewriter<T> {

    public LegacyItemRewriter(T protocol) {
        super(protocol);
    }

    @Override
    public @Nullable Item handleItemToServer(@Nullable Item item) {
        return null;
    }

    @Override
    public @Nullable Item handleItemToClient(@Nullable Item item) {
        return null;
    }
}
