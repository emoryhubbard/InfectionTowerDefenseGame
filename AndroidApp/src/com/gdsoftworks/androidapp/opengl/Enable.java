package com.gdsoftworks.androidapp.opengl;

import javax.microedition.khronos.opengles.GL10;

public class Enable implements RenderCommand {
	private int capacity;
	private GLApp app;
	public Enable(GLApp app) {this.app=app;}
	public Enable setCapacity(int capacity) {this.capacity=capacity; return this;}
	public void render(GL10 gl) {gl.glEnable(capacity); app.enablePool.free(this);}
}
