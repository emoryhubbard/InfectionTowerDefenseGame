package com.gdsoftworks.kludgecode;

import static java.lang.Math.*;
import gdsoftworks.geometry.Line;
/**
 * A few convenience methods for manipulating vectors bundled here:
 * <ul><li>Delta-x and delta-y derived from a given speed and angle
 * <li>Vector reflection against a hard surface represented by a line</ul>
 * @author EDH
 * @version 1.0 alpha
 */
public class Vector {
	public double speed, radians;
	/**
	 * Constructs a vector with specified speed and angle parameters
	 * @param speed vector speed
	 * @param radians vector angle in radians
	 */
	public Vector(double speed, double radians) {
		this.speed = speed; this.radians = radians;
	}
	public double speed() {return speed;}
	public double radians() {return radians;}
	/**
	 * Returns delta-x of this vector
	 * @return dx
	 */
	public double dx() {return cos(radians)*speed;}
	/**
	 * Returns delta-y of this vector
	 * @return dy
	 */
	public double dy() {return -sin(radians)*speed;}
	/**
	 * Reflects vector against specified line
	 * @param line line representing contacting surface
	 * @return this
	 */
	public Vector reflect(Line line) {
		double slope = (line.ordinates()[0]-line.ordinates()[1])/
				(line.abscissae()[0]-line.abscissae()[1]);
		if (slope!=Double.POSITIVE_INFINITY && slope!=Double.NEGATIVE_INFINITY
				&& slope!=0) {
			double distanceFactor = sqrt(1 + square(slope));
			double unitX = 1/distanceFactor;
			double unitY = -slope/distanceFactor;
			
			double lineRadians = unitY<0? 2*PI-acos(unitX): acos(unitX);
			radians = 2*PI - (radians-lineRadians) + lineRadians;
		}
		else if (slope!=Double.POSITIVE_INFINITY && slope!=Double.NEGATIVE_INFINITY)
			radians = 2*PI-radians;
		else
			radians = 3*PI-radians;
		return this;
	}
	private static double square(double base) {return pow(base, 2d);}
}
