package com.gdsoftworks.infectionbeta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.util.Log;

import com.gdsoftworks.app.IO;

public class Settings {
	public static boolean soundEnabled = true;
	public final static String fileName = ".infection";
	public static void load(IO io) {
		DataInputStream in = null;
		try {
			try {
				in = new DataInputStream(io.readFile(fileName));
				soundEnabled = in.readBoolean();
			} finally {if (in!=null) in.close();}
		} catch (IOException ioe) {Log.e("infection", "IOException: "+ioe);}
	}
	public static void save(IO io) {
		DataOutputStream out = null;
		try {
			try {
				out = new DataOutputStream(io.writeFile(fileName));
				out.writeBoolean(soundEnabled);
			}
			finally {if (out!=null) out.close();}
		} catch (IOException ioe) {Log.e("infection", "IOException: "+ioe);}
	}
}
