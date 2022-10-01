/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 12.02.22, 21:12
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package de.florianmichael.viaprotocolhack.util;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class JLoggerToLog4J extends Logger {

    private final org.apache.logging.log4j.Logger base;

    public JLoggerToLog4J(org.apache.logging.log4j.Logger logger) {
        super("logger", null);
        this.base = logger;
    }

    @Override
    public void log(LogRecord record) {
        this.log(record.getLevel(), record.getMessage());
    }

    @Override
    public void log(Level level, String msg) {
        this.typeRemap(level, msg, null);
    }

    @Override
    public void log(Level level, String msg, Object param1) {
        this.typeRemap(level, msg, param1);

    }

    @Override
    public void log(Level level, String msg, Object[] params) {
        log(level, MessageFormat.format(msg, params));
    }

    @Override
    public void log(Level level, String msg, Throwable params) {
        this.typeRemap(level, msg, params);
    }

    public void typeRemap(final Level level, final String message, final Object params) {
        if (level == Level.FINE) {
            this.base.debug(message, params);
        } else if (level == Level.WARNING) {
            this.base.warn(message, params);
        } else if (level == Level.SEVERE) {
            this.base.error(message, params);
        } else if (level == Level.INFO) {
            this.base.info(message, params);
        } else {
            this.base.trace(message, params);
        }
    }
}
