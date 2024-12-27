package com.gdsoftworks.androidapp.opengl;

import javax.microedition.khronos.opengles.GL10;

public class ChangeCamera implements RenderCommand {
	private double x,y,frustumWidth,frustumHeight,zoom;
	private GLGraphics glGraphics;
	private GLApp app;
	public ChangeCamera(GLApp app) {this.app=app;}
	public ChangeCamera setCamera(Camera2D camera) {
		x=camera.position.x; y=camera.position.y; frustumWidth=camera.frustumWidth;
		frustumHeight=camera.frustumHeight; zoom=camera.zoom;
		glGraphics = camera.glGraphics;
		return this;
	}
	public void render(GL10 gl) {
		gl.glViewport(0,  0,  glGraphics.getWidth(),  glGraphics.getHeight());
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof((float)(x-frustumWidth*zoom/2),
				(float)(x+frustumWidth*zoom/2),
				(float)(y-frustumHeight*zoom/2),
				(float)(y+frustumHeight*zoom/2), 1, -1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		app.cameraPool.free(this);
	}
}
