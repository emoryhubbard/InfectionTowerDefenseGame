package com.gdsoftworks.androidapp.opengl;

import gdsoftworks.kinematics.StaticObject;

import javax.microedition.khronos.opengles.GL10;

public class DrawSprite implements RenderCommand {
	private double x, y, width, height, orientation;
	private float red, green, blue, alpha;
	private TextureRegion region;
	private TintBatcher batcher;
	private GLApp app;
	public DrawSprite(GLApp app) {this.app=app;}
	public DrawSprite setBatcher(TintBatcher batcher) {this.batcher = batcher; return this;}
	public DrawSprite setSprite(StaticObject obj, TextureRegion region) {
		x=obj.position.x; y=obj.position.y; height=obj.bounds.height; width=obj.bounds.width;
		orientation=obj.orientation;
		red=green=blue=alpha=1;
		this.region = region;
		return this;
	}
	public DrawSprite setSpriteUpright(StaticObject obj, TextureRegion region) {
		x=obj.position.x; y=obj.position.y; height=obj.bounds.height; width=obj.bounds.width;
		orientation=-1;
		red=green=blue=alpha=1;
		this.region = region;
		return this;
	}
	public DrawSprite setSpriteUpright(double x, double y, double width, double height,
			TextureRegion region, float red, float green, float blue, float alpha) {
		this.x=x; this.y=y; this.width=width; this.height=height; orientation = -1;
		this.red=red; this.green=green; this.blue=blue; this.alpha=alpha;
		this.region = region;
		return this;
	}
	public DrawSprite setSpriteUpright(double x, double y, double width, double height,
			TextureRegion region) {
		this.x=x; this.y=y; this.width=width; this.height=height; orientation = -1;
		red=green=blue=alpha=1;
		this.region = region;
		return this;
	}
	public DrawSprite setSprite(double x, double y, double width, double height, double orientation,
			TextureRegion region, float red, float green, float blue, float alpha) {
		this.x=x; this.y=y; this.width=width; this.height=height; this.orientation=orientation;
		this.red=red; this.green=green; this.blue=blue; this.alpha=alpha;
		this.region = region;
		return this;
	}
	public DrawSprite setSprite(double x, double y, double width, double height, double orientation,
			TextureRegion region) {
		this.x=x; this.y=y; this.width=width; this.height=height; this.orientation=orientation;
		red=green=blue=alpha=1;
		this.region = region;
		return this;
	}
	public DrawSprite setSprite(StaticObject obj, TextureRegion region, float red, float green,
			float blue, float alpha) {
		x=obj.position.x; y=obj.position.y; height=obj.bounds.height; width=obj.bounds.width;
		orientation=obj.orientation;
		this.red=red; this.green=green; this.blue=blue; this.alpha=alpha;
		this.region = region;
		return this;
	}
	public DrawSprite setSpriteUpright(StaticObject obj, TextureRegion region, float red, float green,
			float blue, float alpha) {
		x=obj.position.x; y=obj.position.y; height=obj.bounds.height; width=obj.bounds.width;
		orientation=-1;
		this.red=red; this.green=green; this.blue=blue; this.alpha=alpha;
		this.region = region;
		return this;
	}
	public void render(GL10 gl) {
		if (orientation!=-1)
			batcher.drawSprite(x, y, width, height, orientation, region, red, green, blue, alpha);
		else batcher.drawSprite(x, y, width, height, region, red, green, blue, alpha);
		app.spritePool.free(this);
	}
}
