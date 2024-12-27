package com.gdsoftworks.androidapp.opengl;

import javax.microedition.khronos.opengles.GL10;

public class ChangeColor implements RenderCommand {
	private float red, green, blue, alpha;
	private GLApp app;
	public ChangeColor(GLApp app) {this.app=app;}
	public ChangeColor setColor(float red, float green, float blue, float alpha) {
		this.red=red; this.green=green; this.blue=blue; this.alpha=alpha;
		return this;
	}
	public void render(GL10 gl) {gl.glColor4f(red, green, blue, alpha); app.colorPool.free(this);}
}
