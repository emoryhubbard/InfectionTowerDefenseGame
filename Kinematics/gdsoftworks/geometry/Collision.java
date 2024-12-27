package gdsoftworks.geometry;

public class Collision {
	public static boolean areOverlapping(Circle c1, Circle c2) {
		double radiusSum = c1.radius+c2.radius;
		return c1.center.distanceSquared(c2.center) <= radiusSum*radiusSum;
	}
	public static boolean areOverlapping(FastRectangle r1, FastRectangle r2) {
		if (r1.lowerLeft.x<r2.lowerLeft.x+r2.width
				&& r1.lowerLeft.x+r1.width>r2.lowerLeft.x
				&& r1.lowerLeft.y<r2.lowerLeft.y+r2.height
				&& r1.lowerLeft.y+r1.height>r2.lowerLeft.y) return true;
		else return false;
	}
	public static boolean areOverlapping(Circle c, FastRectangle r) {
		double closestX = c.center.x; double closestY = c.center.y;
		if (c.center.x<r.lowerLeft.x) closestX = r.lowerLeft.x;
		else if (c.center.x>r.lowerLeft.x+r.width)
			closestX = r.lowerLeft.x+r.width;
		if (c.center.y<r.lowerLeft.y) closestY = r.lowerLeft.y;
		else if (c.center.y>r.lowerLeft.y+r.height)
			closestY = r.lowerLeft.y+r.height;
		return c.center.distanceSquared(closestX, closestY)<c.radius*c.radius;
	}
	public static boolean circleContains(Circle c, Vector p) {
		return c.center.distanceSquared(p)<c.radius*c.radius;
	}
	public static boolean circleContains(Circle c, double x, double y) {
		return c.center.distanceSquared(x,y) < c.radius*c.radius;
	}
	public static boolean rectContains(FastRectangle r, Vector p) {
		return r.lowerLeft.x<=p.x && r.lowerLeft.x+r.width>=p.x
				&& r.lowerLeft.y<=p.y && r.lowerLeft.y+r.height>=p.y;
	}
	public static boolean rectContains(FastRectangle r, double x, double y) {
		return r.lowerLeft.x<=x && r.lowerLeft.x+r.width>=x
				&& r.lowerLeft.y<=y && r.lowerLeft.y+r.height>=y;
	}
}
