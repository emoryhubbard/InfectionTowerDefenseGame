package com.gdsoftworks.androidapp.opengl;

import gdsoftworks.kinematics.StaticObject;

import javax.microedition.khronos.opengles.GL10;

import static java.lang.Math.*;

public class SpriteBatcher {
	public final float[] buffer;
	public int bufferIndex = 0, sprites = 0;
	public final Vertices vertices;
	public SpriteBatcher(GLGraphics glGraphics, int maxSprites) {
		buffer = new float[maxSprites*4*4];
		vertices = new Vertices(glGraphics, maxSprites*4, maxSprites*6, false, true);
		short[] indices = new short[maxSprites*6];
		int length = indices.length; short j = 0;
		for (int i=0; i<length; i+=6, j+=4) {
			indices[i] = (short)(j);
			indices[i+1] = (short)(j+1);
			indices[i+2] = (short)(j+2);
			indices[i+3] = (short)(j+2);
			indices[i+4] = (short)(j+3);
			indices[i+5] = (short)(j);
		}
		vertices.setIndices(indices, 0, indices.length);
	}
	public SpriteBatcher beginBatch(Texture texture) {
		texture.bind(); sprites = 0; bufferIndex = 0; return this;
	}
	public SpriteBatcher endBatch() {
		vertices.setVertices(buffer, 0, bufferIndex);
		vertices.bind();
		vertices.draw(GL10.GL_TRIANGLES, 0, sprites*6);
		vertices.unbind(); return this;
	}
	public void drawSprite(StaticObject obj, TextureRegion region) {
		drawSprite(obj.position.x, obj.position.y, obj.bounds.height, obj.bounds.width,
				obj.orientation, region);
	}
	public void drawSpriteUpright(StaticObject obj, TextureRegion region) {
		drawSprite(obj.position.x, obj.position.y, obj.bounds.height, obj.bounds.width, region);
	}
	public void drawSprite(double x, double y, double width, double height,
			TextureRegion region) {
		double halfWidth = width/2; double halfHeight = height/2;
		float x1 = (float)(x-halfWidth); float y1 = (float)(y-halfHeight);
		float x2 = (float)(x+halfWidth); float y2 = (float)(y+halfHeight);
		buffer[bufferIndex++] = x1; buffer[bufferIndex++] = y1;
		buffer[bufferIndex++] = (float)(region.u1); buffer[bufferIndex++] = (float)(region.v2);
		buffer[bufferIndex++] = x2; buffer[bufferIndex++] = y1;
		buffer[bufferIndex++] = (float)(region.u2); buffer[bufferIndex++] = (float)(region.v2);
		buffer[bufferIndex++] = x2; buffer[bufferIndex++] = y2;
		buffer[bufferIndex++] = (float)(region.u2); buffer[bufferIndex++] = (float)(region.v1);
		buffer[bufferIndex++] = x1; buffer[bufferIndex++] = y2;
		buffer[bufferIndex++] = (float)(region.u1); buffer[bufferIndex++] = (float)(region.v1);
		sprites++;
	}
	public void drawSprite(double x, double y, double width, double height, double radians,
			TextureRegion region) {
		double halfWidth = width/2; double halfHeight = height/2;
		double cos = cos(radians); double sin = sin(radians);
		float x1 = (float)(-halfWidth*cos-(-halfHeight)*sin);
		float y1 = (float)(-halfWidth*sin+(-halfHeight)*cos);
		float x2 = (float)(halfWidth*cos-(-halfHeight)*sin);
		float y2 = (float)(halfWidth*sin+(-halfHeight)*cos);
		float x3 = (float)(halfWidth*cos-halfHeight*sin);
		float y3 = (float)(halfWidth*sin+halfHeight*cos);
		float x4 = (float)(-halfWidth*cos-halfHeight*sin);
		float y4 = (float)(-halfWidth*sin+halfHeight*cos);
		x1+=x; x2+=x; x3+=x; x4+=x; y1+=y; y2+=y; y3+=y; y4+=y;
		buffer[bufferIndex++] = x1; buffer[bufferIndex++] = y1;
		buffer[bufferIndex++] = (float)region.u1; buffer[bufferIndex++] = (float)region.v2;
		buffer[bufferIndex++] = x2; buffer[bufferIndex++] = y2;
		buffer[bufferIndex++] = (float)region.u2; buffer[bufferIndex++] = (float)region.v2;
		buffer[bufferIndex++] = x3; buffer[bufferIndex++] = y3;
		buffer[bufferIndex++] = (float)region.u2; buffer[bufferIndex++] = (float)region.v1;
		buffer[bufferIndex++] = x4; buffer[bufferIndex++] = y4;
		buffer[bufferIndex++] = (float)region.u1; buffer[bufferIndex++] = (float)region.v1;
		sprites++;
	}
}
