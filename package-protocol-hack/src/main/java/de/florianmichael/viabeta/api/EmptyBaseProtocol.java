package de.florianmichael.viabeta.api;

import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;

public class EmptyBaseProtocol extends AbstractSimpleProtocol {

    @Override
    public boolean isBaseProtocol() {
        return true;
    }
}
