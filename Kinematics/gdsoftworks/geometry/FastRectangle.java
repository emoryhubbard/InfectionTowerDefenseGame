package gdsoftworks.geometry;

public class FastRectangle {
	public final Vector lowerLeft; public double width, height;
	public FastRectangle(FastRectangle other) {
		lowerLeft = new Vector(other.lowerLeft);
		width = other.width; height = other.height;
	}
	public FastRectangle(double x, double y, double width, double height) {
		lowerLeft = new Vector(x,y); this.width=width; this.height=height;
	}
	public Polygon toPolygon() {
		return new Polygon(new double[]{lowerLeft.x, lowerLeft.x+width,
				lowerLeft.x+width, lowerLeft.x},
				new double[] {-lowerLeft.y, -lowerLeft.y,
				-lowerLeft.y-height, -lowerLeft.y-height});
	}
	public String toString() {
		return "vector: "+lowerLeft+", width: "+width+", height: "+height;
	}
}
