package com.gdsoftworks.androidapp.opengl;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.gdsoftworks.app.IO;

public class Texture {
	GLGraphics glGraphics; IO io; String fileName;
	volatile int textureID;
	int minFilter = GL10.GL_LINEAR;
	int magFilter = GL10.GL_LINEAR;
	int width; int height;
	public Texture(GLApp app, String fileName) {
		glGraphics = app.getGLGraphics();
		io = app.getIO(); this.fileName = fileName; load();
	}
	public Texture(GLApp app, String fileName, int filter) {
		minFilter = magFilter = filter;
		glGraphics = app.getGLGraphics();
		io = app.getIO(); this.fileName = fileName; load();
	}
	private void load() {
		GL10 gl = glGraphics.getGL();
		int[] textureIDs = new int[1];
		gl.glGenTextures(1, textureIDs, 0);
		textureID = textureIDs[0];
		InputStream in = null;
		try {
			try {
				in = io.readResource(fileName);
				Bitmap bitmap = BitmapFactory.decodeStream(in);
				width = bitmap.getWidth(); height = bitmap.getHeight();
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
				setFilters(minFilter, magFilter);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
			} finally {in.close();}
		} catch (IOException ioe) {
			RuntimeException re =
					new RuntimeException("Unable to load texture '"+fileName+"'", ioe);
			re.initCause(ioe); throw re;
		} 
	}
	public void reload() {
		load(); bind(); setFilters(minFilter, magFilter);
		glGraphics.getGL().glBindTexture(GL10.GL_TEXTURE_2D, 0);
	}
	public void setFilters(int minFilter, int magFilter) {
		this.minFilter = minFilter; this.magFilter = magFilter;
		GL10 gl = glGraphics.getGL();
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, minFilter);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, magFilter);
	}
	public void bind() {glGraphics.getGL().glBindTexture(GL10.GL_TEXTURE_2D, textureID);}
	public void dispose() {
		GL10 gl = glGraphics.getGL();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);
		int[] textureIDs = {textureID}; gl.glDeleteTextures(1, textureIDs, 0);
	}
	
}
