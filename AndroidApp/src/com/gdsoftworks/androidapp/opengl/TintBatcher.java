package com.gdsoftworks.androidapp.opengl;

import gdsoftworks.kinematics.StaticObject;

import javax.microedition.khronos.opengles.GL10;

import static java.lang.Math.*;

public class TintBatcher {
	public final float[] buffer;
	public int bufferIndex = 0, sprites = 0;
	public final Vertices vertices;
	public TintBatcher(GLGraphics glGraphics, int maxSprites) {
		buffer = new float[maxSprites*4*8];
		vertices = new Vertices(glGraphics, maxSprites*4, maxSprites*6, true, true);
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
	public TintBatcher beginBatch(Texture texture) {
		vertices.glGraphics.getGL().glBindTexture(GL10.GL_TEXTURE_2D, texture.textureID);
		sprites = 0; bufferIndex = 0; return this;
	}
	public TintBatcher endBatch() {
		vertices.setVertices(buffer, 0, bufferIndex);
		vertices.bind();
		vertices.draw(GL10.GL_TRIANGLES, 0, sprites*6);
		vertices.unbind(); return this;
	}
	public void drawSprite(StaticObject obj, TextureRegion region) {
		drawSprite(obj.position.x, obj.position.y, obj.bounds.height, obj.bounds.width,
				obj.orientation, region, 1,1,1,1);
	}
	public void drawSpriteUpright(StaticObject obj, TextureRegion region) {
		drawSprite(obj.position.x, obj.position.y, obj.bounds.height, obj.bounds.width, region,
				1,1,1,1);
	}
	public void drawSprite(StaticObject obj, TextureRegion region, float red, float green,
			float blue, float alpha) {
		drawSprite(obj.position.x, obj.position.y, obj.bounds.height, obj.bounds.width,
				obj.orientation, region, red, green, blue, alpha);
	}
	public void drawSpriteUpright(StaticObject obj, TextureRegion region, float red, float green,
			float blue, float alpha) {
		drawSprite(obj.position.x, obj.position.y, obj.bounds.height, obj.bounds.width, region,
				red, green, blue, alpha);
	}
	public void drawSprite(double x, double y, double width, double height,
			TextureRegion region, float red, float green, float blue, float alpha) {
		double halfWidth = width/2; double halfHeight = height/2;
		float x1 = (float)(x-halfWidth); float y1 = (float)(y-halfHeight);
		float x2 = (float)(x+halfWidth); float y2 = (float)(y+halfHeight);
		buffer[bufferIndex++] = x1; buffer[bufferIndex++] = y1;
		buffer[bufferIndex++] = red; buffer[bufferIndex++] = green;
		buffer[bufferIndex++] = blue; buffer[bufferIndex++] = alpha;
		buffer[bufferIndex++] = (float)(region.u1); buffer[bufferIndex++] = (float)(region.v2);
		buffer[bufferIndex++] = x2; buffer[bufferIndex++] = y1;
		buffer[bufferIndex++] = red; buffer[bufferIndex++] = green;
		buffer[bufferIndex++] = blue; buffer[bufferIndex++] = alpha;
		buffer[bufferIndex++] = (float)(region.u2); buffer[bufferIndex++] = (float)(region.v2);
		buffer[bufferIndex++] = x2; buffer[bufferIndex++] = y2;
		buffer[bufferIndex++] = red; buffer[bufferIndex++] = green;
		buffer[bufferIndex++] = blue; buffer[bufferIndex++] = alpha;
		buffer[bufferIndex++] = (float)(region.u2); buffer[bufferIndex++] = (float)(region.v1);
		buffer[bufferIndex++] = x1; buffer[bufferIndex++] = y2;
		buffer[bufferIndex++] = red; buffer[bufferIndex++] = green;
		buffer[bufferIndex++] = blue; buffer[bufferIndex++] = alpha;
		buffer[bufferIndex++] = (float)(region.u1); buffer[bufferIndex++] = (float)(region.v1);
		sprites++;
	}
	public void drawSprite(double x, double y, double width, double height,
			TextureRegion region) {
		drawSprite(x,y,width,height,region,1,1,1,1);
	}
	public void drawSprite(double x, double y, double width, double height, double radians,
			TextureRegion region, float red, float green, float blue, float alpha) {
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
		buffer[bufferIndex++] = red; buffer[bufferIndex++] = green;
		buffer[bufferIndex++] = blue; buffer[bufferIndex++] = alpha;
		buffer[bufferIndex++] = (float)region.u1; buffer[bufferIndex++] = (float)region.v2;
		buffer[bufferIndex++] = x2; buffer[bufferIndex++] = y2;
		buffer[bufferIndex++] = red; buffer[bufferIndex++] = green;
		buffer[bufferIndex++] = blue; buffer[bufferIndex++] = alpha;
		buffer[bufferIndex++] = (float)region.u2; buffer[bufferIndex++] = (float)region.v2;
		buffer[bufferIndex++] = x3; buffer[bufferIndex++] = y3;
		buffer[bufferIndex++] = red; buffer[bufferIndex++] = green;
		buffer[bufferIndex++] = blue; buffer[bufferIndex++] = alpha;
		buffer[bufferIndex++] = (float)region.u2; buffer[bufferIndex++] = (float)region.v1;
		buffer[bufferIndex++] = x4; buffer[bufferIndex++] = y4;
		buffer[bufferIndex++] = red; buffer[bufferIndex++] = green;
		buffer[bufferIndex++] = blue; buffer[bufferIndex++] = alpha;
		buffer[bufferIndex++] = (float)region.u1; buffer[bufferIndex++] = (float)region.v1;
		sprites++;
	}
	public void drawSprite(double x, double y, double width, double height, double radians,
			TextureRegion region) {
		drawSprite(x,y,width,height,radians,region,1,1,1,1);
	}
}

