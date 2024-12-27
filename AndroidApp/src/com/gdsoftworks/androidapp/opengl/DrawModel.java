package com.gdsoftworks.androidapp.opengl;

import gdsoftworks.geometry.Polygon;

import javax.microedition.khronos.opengles.GL10;

public class DrawModel implements RenderCommand {
	private int length, vertexCount;
	private float[] vertices = new float[100];
	private Vertices model;
	private GLApp app;
	public DrawModel(GLApp app) {this.app=app;}
	public DrawModel setModel(Vertices model, float[] vertices, Polygon gon, double heightFactor) {
		this.model = model;
		length = vertices.length;
		double[] abscissae = gon.abscissae(); double[] ordinates = gon.ordinates();
		vertexCount = abscissae.length;
		int coordinateSize = 2+(model.color?4:0)+(model.textCoords?2:0);
		for (int i=0, gonI=0; i<length-1 && gonI<vertexCount; i+=coordinateSize, gonI++) {
			this.vertices[i] = (float) abscissae[gonI];
			this.vertices[i+1] = (float) (heightFactor - ordinates[gonI]);
		}
		return this;
	}
	public void render(GL10 gl) {
		model.setVertices(vertices, 0, length);
		model.bind();
		model.draw(GL10.GL_TRIANGLE_FAN, 0, vertexCount);
		model.unbind();
		app.modelPool.free(this);
	}
}
