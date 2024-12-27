package com.gdsoftworks.app;

public interface Graphics {
	public static enum PixMapFormat {ARGB8888, ARGB4444, RGB565}
	public PixMap newPixMap(String fileName, PixMapFormat format);
	
	public void clear(int color);
	public void drawPixel(int x, int y, int color);
	public void drawLine(int x, int y, int x2, int y2, int color);
	public void drawPolygon(int[] xs, int[] ys, int color);
	public void drawFilledPolygon(int[] xs, int[] ys, int color);
	public void drawRect(int x, int y, int width, int height, int color);
	public void drawPixMap(PixMap pixMap, int x, int y, int srcX, int srcY,
			int srcWidth, int srcHeight);
	public void drawPixMap(PixMap pixMap, int x, int y);
	
	public int getWidth(); public int getHeight();
}
