package gdsoftworks.geometry;

import static java.lang.Math.*;

public class Vector {
	public double x, y;
	public Vector(){} public Vector(double x, double y){this.x=x; this.y=y;}
	public Vector(Vector other) {this.x=other.x; this.y=other.y;}
	public Vector set(double x, double y) {this.x=x; this.y=y; return this;}
	public Vector set(Vector other) {
		this.x=other.x; this.y=other.y; return this;
	}
	public Vector add(double x, double y) {this.x+=x; this.y+=y; return this;}
	public Vector add(Vector other) {this.x+=other.x; this.y+=other.y; return this;}
	public Vector scale(double scalar) {this.x*=scalar; this.y*=scalar; return this;}
	public double distance() {return sqrt(x*x+y*y);}
	public Vector normalize() {
		double dist = distance();
		if (dist!=0) {this.x/=dist; this.y/=dist;}
		return this;
	}
	public double angle() {
		double angle = atan2(y, x);
		return angle<0 ? angle+2*PI: angle;
	}
	public Vector rotate(double radians) {
		double cos = cos(radians); double sin = sin(radians);
		this.x = this.x*cos-this.y*sin;
		this.y = this.x*sin+this.y*cos;
		return this;
	}
	public double distance(Vector other) {
		double base = x-other.x; double height = y-other.y;
		return sqrt(base*base+height*height);
	}
	public double distance(double x, double y) {
		double base = this.x-x; double height = this.y-y;
		return sqrt(base*base+height*height);
	}
	public double distanceSquared(Vector other) {
		double base = this.x-other.x; double height = this.y-other.y;
		return base*base+height*height;
	}
	public double distanceSquared(double x, double y) {
		double base = this.x-x; double height = this.y-y;
		return base*base+height*height;
	}
	public boolean equals(Vector other) {
		if (x==other.x && y==other.y) return true;
		else return false;
	}
	public String toString() {return "x: "+x+", y: "+y;}
}
