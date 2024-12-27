package com.gdsoftworks.androidapp.opengl;

import javax.microedition.khronos.opengles.GL10;

public class Clear implements RenderCommand {
	private int mask;
	private GLApp app;
	public Clear(GLApp app) {this.app=app;}
	public Clear setMask(int mask) {this.mask=mask; return this;}
	public void render(GL10 gl) {gl.glClear(mask); app.clearPool.free(this);}
}
