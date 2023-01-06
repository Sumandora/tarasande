package de.florianmichael.clampclient.injection.mixininterface;

public interface IEntity_Protocol {

    boolean protocolhack_isOutsideBorder();

    void protocolhack_setOutsideBorder(boolean outsideBorder);

    boolean protocolhack_isInWeb();

    void protocolhack_setInWeb(boolean inWeb);
}
