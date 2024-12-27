package com.gdsoftworks.util;

public class Arrays {
	public static char[] insert(char[] text, char character, int index) {
		char current; char previous = text[index];
		text[index] = character;
		int remainingText = text.length-index;
		for (int i=index+1; i<remainingText; i++) {
			current = text[i];
			text[i] = previous;
			previous = current;
		}
		return text;
	}
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	public static void copyTo(byte[] to, byte[] from) { 
		int len = from.length<to.length? from.length:to.length;
		for (int i=0; i<len; i++) to[i] = from[i];
	}
}
