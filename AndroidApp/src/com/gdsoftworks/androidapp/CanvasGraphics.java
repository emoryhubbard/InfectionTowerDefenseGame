package com.gdsoftworks.androidapp;

import java.io.InputStream;

import com.gdsoftworks.app.Graphics;
import com.gdsoftworks.app.PixMap;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;

public class CanvasGraphics implements Graphics {
	AssetManager assets;
	Bitmap virtualBuffer;
	Canvas canvas; Paint paint;
	Rect srcRect = new Rect(); Rect dstRect = new Rect();
	
	public CanvasGraphics(AssetManager assets, Bitmap virtualBuffer) {
		this.assets = assets; this.virtualBuffer = virtualBuffer;
		this.canvas = new Canvas(virtualBuffer); this.paint = new Paint();
	}
	public PixMap newPixMap(String fileName, PixMapFormat format) {
		Config config = null;
		if (format==PixMapFormat.RGB565) config = Config.RGB_565;
		else if (format==PixMapFormat.ARGB4444) config = Config.ARGB_4444;
		else config = Config.ARGB_8888;
		
		Options options = new Options(); options.inPreferredConfig = config;
		InputStream in = null; Bitmap bitmap = null;
		try {
			in = assets.open(fileName);
			bitmap = BitmapFactory.decodeStream(in);
			if (bitmap==null)
				throw new RuntimeException("Couldn't load bitmap "+fileName);
		} catch(Exception e) {
			throw new RuntimeException("Couldn't load bitmap: "+e.getMessage());
		} finally {
			if (in!=null) {
				try {in.close();} catch(Exception e){}
			}
		}
		if (bitmap.getConfig()==Config.RGB_565) format = PixMapFormat.RGB565;
		else if (bitmap.getConfig()==Config.ARGB_4444) format=PixMapFormat.ARGB4444;
		else format = PixMapFormat.ARGB8888;
		
		return new CanvasBitmap(bitmap, format);
	}
	public void clear(int color) {
		canvas.drawRGB((color & 0xff0000)>>16, (color & 0xff00)>>8,(color & 0xff));
	}
	public void drawPixel(int x, int y, int color) {
		paint.setColor(color); canvas.drawPoint(x,y,paint);
	}
	public void drawLine(int x, int y, int x2, int y2, int color) {
		paint.setColor(color); canvas.drawLine(x, y, x2, y2, paint);
	}
	public void drawPolygon(int[] xs, int[] ys, int color) {
		paint.setColor(color);
		int highestIndex = xs.length-1; int nextI;
		for (int i=0; i<highestIndex; i++) {
			nextI = i+1;
			canvas.drawLine(xs[i], ys[i], xs[nextI], ys[nextI], paint);
		}
		canvas.drawLine(xs[highestIndex], ys[highestIndex], xs[0], ys[0], paint);
	}
	public void drawFilledPolygon(int[] xs, int[] ys, int color) {
		paint.setColor(color); paint.setStyle(Style.FILL);
		int length = xs.length;
		Path path = new Path();
		path.moveTo(xs[0], ys[0]);
		for (int i=1; i<length; i++) path.lineTo(xs[i], ys[i]);
		path.close();
		canvas.drawPath(path, paint);
	}
	public void drawRect(int x, int y, int width, int height, int color) {
		paint.setColor(color); paint.setStyle(Style.FILL);
		canvas.drawRect(x,  y, x+width-1, y+-1, paint);
	}
	public void drawPixMap(PixMap pixMap, int x, int y, int srcX, int srcY,
			int srcWidth, int srcHeight) {
		srcRect.left = srcX; srcRect.top = srcY;
		srcRect.right = srcX+srcWidth-1; srcRect.bottom = srcY+srcHeight-1;
		dstRect.left = x; dstRect.top = y;
		dstRect.right = x+srcWidth-1; dstRect.bottom = y+srcHeight-1;
		
		canvas.drawBitmap(((CanvasBitmap) pixMap).bitmap, srcRect, dstRect, null);
	}
	public void drawPixMap(PixMap pixMap, int x, int y) {
		canvas.drawBitmap(((CanvasBitmap) pixMap).bitmap, x, y, null);
	}
	public int getWidth() {return virtualBuffer.getWidth();}
	public int getHeight() {return virtualBuffer.getHeight();}
}
