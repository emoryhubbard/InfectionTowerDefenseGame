package com.gdsoftworks.util;

public class Strings {
	/**
	 * 
	 * @param builder
	 * @param integer
	 * @return length of integer
	 */
	public static int buildInt(char[] builder, int integer) {
		int length = 0;
		boolean moreDigits = true;
		int intLeft = integer>0 ? integer: -integer;
		int remainder;
		do {
			remainder = intLeft % 10;
			Arrays.insert(builder, (char)(remainder+'0'), 0);
			length++;
			if (intLeft<10) moreDigits=false;
			intLeft /= 10;
		} while (moreDigits);
		if (integer<0) {
			Arrays.insert(builder, '-', 0);
			length++;
		}
		return length;
	}
	public static void main(String... args) {
		char[] text = {'b', 'l', 'a', 'h', 'b', 'l', 'a', 'h'};
		int length = buildInt(text, 1025);
		System.out.print("length: "+length+" text: ");
		for (int i=0; i<length; i++) System.out.print(text[i]);
	}
}
