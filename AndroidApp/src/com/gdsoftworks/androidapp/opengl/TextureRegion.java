package com.gdsoftworks.androidapp.opengl;

public class TextureRegion {
	public final double u1, v1, u2, v2;
	public final Texture texture;
	public TextureRegion(Texture texture, double x, double y, double width, double height) {
		u1 = x/texture.width; v1 = y/texture.height;
		u2 = u1+width/texture.width; v2 = v1+height/texture.height;
		this.texture = texture;
	}
}
