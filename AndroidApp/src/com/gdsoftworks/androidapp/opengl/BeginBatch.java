package com.gdsoftworks.androidapp.opengl;

import javax.microedition.khronos.opengles.GL10;

public class BeginBatch implements RenderCommand {
	private Texture texture; private TintBatcher batcher;
	private GLApp app;
	public BeginBatch(GLApp app) {this.app=app;}
	public BeginBatch set(Texture texture, TintBatcher batcher) {
		this.texture = texture; this.batcher = batcher; return this;
	}
	public void render(GL10 gl) {batcher.beginBatch(texture); app.beginPool.free(this);}
}
