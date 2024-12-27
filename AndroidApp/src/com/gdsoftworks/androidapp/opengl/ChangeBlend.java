package com.gdsoftworks.androidapp.opengl;

import javax.microedition.khronos.opengles.GL10;

public class ChangeBlend implements RenderCommand {
	private int srcFactor, dstFactor;
	private GLApp app;
	public ChangeBlend(GLApp app) {this.app=app;}
	public ChangeBlend setFunction(int srcFactor, int dstFactor) {
		this.srcFactor=srcFactor; this.dstFactor=dstFactor; return this;
	}
	public void render(GL10 gl) {gl.glBlendFunc(srcFactor, dstFactor); app.blendPool.free(this);}
}
