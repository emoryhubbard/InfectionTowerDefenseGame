package gdsoftworks.geometry;

public class Circle {
	public final Vector center; public double radius;
	public Circle(double x, double y, double radius) {
		this.center = new Vector(x, y); this.radius = radius;
	}
}
