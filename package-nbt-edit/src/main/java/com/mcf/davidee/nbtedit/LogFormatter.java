/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 7/16/22, 12:37 AM
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

package com.mcf.davidee.nbtedit;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LogFormatter extends Formatter
{
	private static final String SEP = System.getProperty("line.separator");
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

	public String format(LogRecord record)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(dateFormat.format(Long.valueOf(record.getMillis())));
		sb.append(" [" + record.getLevel().getLocalizedName() + "] ");

		sb.append(record.getMessage());
		sb.append(SEP);
		Throwable thr = record.getThrown();

		if (thr != null)
		{
			StringWriter thrDump = new StringWriter();
			thr.printStackTrace(new PrintWriter(thrDump));
			sb.append(thrDump.toString());
		}

		return sb.toString();
	}
}
