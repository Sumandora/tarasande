package de.florianmichael.vialegacy.protocols.protocol1_2_5;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;

public class Protocol1_2_5 extends AbstractProtocol {

    public static final Protocol1_2_5 INSTANCE = new Protocol1_2_5();

    @Override
    protected void registerPackets() {
        super.registerPackets();


    }

    @Override
    public boolean isBaseProtocol() {
        return true;
    }
}
