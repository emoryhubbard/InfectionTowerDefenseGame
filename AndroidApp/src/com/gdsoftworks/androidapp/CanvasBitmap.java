package com.gdsoftworks.androidapp;

import android.graphics.Bitmap;

import com.gdsoftworks.app.Graphics.PixMapFormat;
import com.gdsoftworks.app.PixMap;

public class CanvasBitmap implements PixMap{
	Bitmap bitmap;
	PixMapFormat format;
	
	public CanvasBitmap(Bitmap bitmap, PixMapFormat format) {
		this.bitmap = bitmap; this.format = format;
	}
	public int getWidth() {return bitmap.getWidth();}
	public int getHeight() {return bitmap.getHeight();}
	public PixMapFormat getFormat() {return format;}
	public void dispose() {bitmap.recycle();}
}
