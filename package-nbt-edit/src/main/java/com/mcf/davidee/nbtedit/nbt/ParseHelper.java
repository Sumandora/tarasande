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

package com.mcf.davidee.nbtedit.nbt;

public class ParseHelper {

	public static byte parseByte(String s) throws NumberFormatException{
		try {
			return Byte.parseByte(s);
		}
		catch(NumberFormatException e){
			throw new NumberFormatException("Not a valid byte");
		}
	}
	public static short parseShort(String s) throws NumberFormatException{
		try {
			return Short.parseShort(s);
		}
		catch(NumberFormatException e){
			throw new NumberFormatException("Not a valid short");
		}
	}

	public static int parseInt(String s) throws NumberFormatException{
		try {
			return Integer.parseInt(s);
		}
		catch(NumberFormatException e){
			throw new NumberFormatException("Not a valid int");
		}
	}
	public static long parseLong(String s) throws NumberFormatException{
		try {
			return Long.parseLong(s);
		}
		catch(NumberFormatException e){
			throw new NumberFormatException("Not a valid long");
		}
	}

	public static float parseFloat(String s) throws NumberFormatException{
		try {
			return Float.parseFloat(s);
		}
		catch(NumberFormatException e){
			throw new NumberFormatException("Not a valid float");
		}
	}
	public static double parseDouble(String s) throws NumberFormatException{
		try {
			return Double.parseDouble(s);
		}
		catch(NumberFormatException e){
			throw new NumberFormatException("Not a valid double");
		}
	}

	public static byte[] parseByteArray(String s) throws NumberFormatException{
		try {
			String[] input = s.split(" ");
			byte[] arr = new byte[input.length];
			for (int i =0; i < input.length; ++i)
				arr[i] = parseByte(input[i]);
			return arr;
		}
		catch(NumberFormatException e){
			throw new NumberFormatException("Not a valid byte array");
		}
	}
	public static int[] parseIntArray(String s) throws NumberFormatException{
		try {
			String[] input = s.split(" ");
			int[] arr = new int[input.length];
			for (int i =0; i < input.length; ++i)
				arr[i] = parseInt(input[i]);
			return arr;
		}
		catch(NumberFormatException e){
			throw new NumberFormatException("Not a valid int array");
		}
	}
	
	
}
