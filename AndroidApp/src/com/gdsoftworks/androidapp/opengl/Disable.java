package com.gdsoftworks.androidapp.opengl;

import javax.microedition.khronos.opengles.GL10;

public class Disable implements RenderCommand {
	private int capacity;
	private GLApp app;
	public Disable(GLApp app) {this.app=app;}
	public Disable setCapacity(int capacity) {this.capacity=capacity; return this;}
	public void render(GL10 gl) {gl.glDisable(capacity); app.disablePool.free(this);}
}
