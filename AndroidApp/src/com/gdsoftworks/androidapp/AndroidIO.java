package com.gdsoftworks.androidapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import com.gdsoftworks.app.IO;

public class AndroidIO implements IO{
	Context context;
	AssetManager assets;
	String externalStoragePath;
	
	public AndroidIO(Context context) {
		this.context=context; this.assets=context.getAssets();
		this.externalStoragePath =
				Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator;
	}
	public InputStream readResource(String fileName) throws IOException {
		return assets.open(fileName);
	}
	public InputStream readFile(String fileName) throws IOException {
		return new FileInputStream(externalStoragePath+fileName);
	}
	public OutputStream writeFile(String fileName) throws IOException {
		return new FileOutputStream(externalStoragePath+fileName);
	}
}
