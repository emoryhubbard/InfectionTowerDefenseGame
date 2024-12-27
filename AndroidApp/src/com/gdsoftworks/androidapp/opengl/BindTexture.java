package com.gdsoftworks.androidapp.opengl;

import javax.microedition.khronos.opengles.GL10;

public class BindTexture implements RenderCommand {
	private Texture texture;
	private GLApp app;
	public BindTexture(GLApp app) {this.app=app;}
	public BindTexture setTexture(Texture texture) {this.texture=texture; return this;}
	public void render(GL10 gl) {texture.bind(); app.bindPool.free(this);}
}
