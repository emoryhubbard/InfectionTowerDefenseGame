package com.gdsoftworks.androidapp;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class Bytes {
	static {System.loadLibrary("gdsoftworksutils");}
	public static native void copyTo(ByteBuffer dst, float[] src, int offset, int length);
	public static native void copyTo(ShortBuffer dst, short[] src, int offset, int length);
}
