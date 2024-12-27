package com.gdsoftworks.androidapp.opengl;

import gdsoftworks.geometry.Polygon;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.gdsoftworks.androidapp.Bytes;

public class Vertices {
	final GLGraphics glGraphics;
	final boolean color; final boolean textCoords;
	final int vertexSize;
	final ByteBuffer vertices; final ShortBuffer indices;
	public Vertices(GLGraphics glGraphics, int maxVertices, int maxIndices,
			boolean hasColor, boolean hasTexCoords) {
		this.glGraphics = glGraphics;
		color = hasColor; textCoords = hasTexCoords;
		vertexSize = (2 + (color?4:0) + (textCoords?2:0)) * 4;
		vertices = ByteBuffer.allocateDirect(maxVertices*vertexSize);
		vertices.order(ByteOrder.nativeOrder());
		if (maxIndices>0) {
			ByteBuffer buffer = ByteBuffer.allocateDirect(maxIndices*Short.SIZE/8);
			buffer.order(ByteOrder.nativeOrder());
			indices = buffer.asShortBuffer();
		} else {indices = null;}
	}
	public Vertices setVertices(float[] vertices, int offset, int length) {
		this.vertices.clear();
		Bytes.copyTo(this.vertices,  vertices,  offset, length);
		this.vertices.position(length*4);
		return this;
	}
	public Vertices setIndices(short[] indices, int offset, int length) {
		this.indices.clear();
		Bytes.copyTo(this.indices, indices, offset, length);
		this.indices.position(length);
		return this;
	}
	public Vertices bind() {
		GL10 gl = glGraphics.getGL();
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		vertices.position(0); gl.glVertexPointer(2, GL10.GL_FLOAT, vertexSize, vertices);
		if (color) {
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			vertices.position(8);
			gl.glColorPointer(4, GL10.GL_FLOAT, vertexSize, vertices);
		}
		if (textCoords) {
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			vertices.position(color?24:8);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, vertexSize, vertices);
		}
		return this;
	}
	public Vertices draw(int type, int offset, int numVertices) {
		GL10 gl = glGraphics.getGL();
		if (indices!=null) {
			indices.position(offset);
			gl.glDrawElements(type,  numVertices,  GL10.GL_UNSIGNED_SHORT,  indices);
		} else {gl.glDrawArrays(type, offset, numVertices);}
		return this;
	}
	public Vertices unbind() {
		GL10 gl = glGraphics.getGL();
		if (textCoords) gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		if (color) gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		return this;
	}
	public Vertices drawModel(float[] vertices, Polygon gon, double heightFactor) {
		int length = vertices.length;
		double[] abscissae = gon.abscissae(); double[] ordinates = gon.ordinates();
		int vertexCount = abscissae.length;
		int coordinateSize = 2+(color?4:0)+(textCoords?2:0);
		for (int i=0, gonI=0; i<length-1 && gonI<vertexCount; i+=coordinateSize, gonI++) {
			vertices[i] = (float) abscissae[gonI];
			vertices[i+1] = (float) (heightFactor - ordinates[gonI]);
		}
		setVertices(vertices, 0, length);
		bind();
		draw(GL10.GL_TRIANGLE_FAN, 0, vertexCount);
		unbind();
		return this;
	}
}
