package gdsoftworks.kinematics;

import gdsoftworks.geometry.FastRectangle;

public class StaticObject {
	public final gdsoftworks.geometry.Vector position;
	public final FastRectangle bounds;
	public double orientation = 0;
	public double spin = 0;
	public StaticObject(double x, double y, double width, double height) {
		position = new gdsoftworks.geometry.Vector(x,y);
		bounds = new FastRectangle(x-width/2, y-height/2, width, height);
	}
	public void setWidth(double width) {
		position.set(position.x-bounds.width/2+width/2, position.y);
		bounds.width = width;
	}
	public void setHeight(double height) {
		position.set(position.x, position.y-bounds.height/2+height/2);
		bounds.height = height;
	}
}
