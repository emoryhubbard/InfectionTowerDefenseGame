package com.gdsoftworks.androidapp.opengl;

import javax.microedition.khronos.opengles.GL10;

public class EndBatch implements RenderCommand {
	private TintBatcher batcher;
	private GLApp app;
	public EndBatch(GLApp app) {this.app=app;}
	public EndBatch setBatcher(TintBatcher batcher) {this.batcher=batcher; return this;}
	public void render(GL10 gl) {batcher.endBatch(); app.endPool.free(this);}
}
