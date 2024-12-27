package com.gdsoftworks.androidapp.opengl;

import javax.microedition.khronos.opengles.GL10;

import gdsoftworks.geometry.Vector;

public class Camera2D {
	public final Vector position;
	public double zoom = 1;
	public final double frustumWidth; public final double frustumHeight;
	final GLGraphics glGraphics;
	public Camera2D(GLGraphics glGraphics, double frustumWidth,
			double frustumHeight) {
		this.glGraphics = glGraphics;
		this.frustumWidth = frustumWidth; this.frustumHeight = frustumHeight;
		position = new Vector(frustumWidth/2, frustumHeight/2);
	}
	public void setViewportAndMatrices() {
		GL10 gl = glGraphics.getGL();
		gl.glViewport(0,  0,  glGraphics.getWidth(),  glGraphics.getHeight());
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof((float)(position.x-frustumWidth*zoom/2),
				(float)(position.x+frustumWidth*zoom/2),
				(float)(position.y-frustumHeight*zoom/2),
				(float)(position.y+frustumHeight*zoom/2), 1, -1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	public void normalizeScreenPoint(Vector point) {
		point.x = point.x*frustumWidth*zoom/glGraphics.getWidth();
		point.y = (1-point.y/glGraphics.getHeight())*frustumHeight*zoom;
		point.add(position).add(-frustumWidth*zoom/2, -frustumHeight*zoom/2);
	}
	public void convertToScreenPoint(Vector point) {
		point.add(frustumWidth*zoom/2, frustumHeight*zoom/2).add(-position.x, -position.y);
		point.x = point.x*glGraphics.getWidth()/(frustumWidth*zoom);
		point.y = -(point.y/(frustumHeight*zoom)-1)*glGraphics.getHeight();
	}
}
