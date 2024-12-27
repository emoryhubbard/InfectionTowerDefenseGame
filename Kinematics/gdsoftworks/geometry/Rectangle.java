package gdsoftworks.geometry;

import static java.lang.Math.*;

import java.util.Random;
/**
 * Trivial platform-independent representation of a rectangle.
 * Convenience methods are available for creating a new {@link Polygon polygon}
 * reference from a rectangle and calculating vertices.
 * @author EDH
 * @version 1.0 alpha
 */
public class Rectangle implements ReadOnlyRectangle {
	//***********************
	private double width = 10;
	private double height = 10;
	private double x = 0;
	private double y = 0;
	//*************************
	/**
	 * Constructs a new rectangle with the specified dimensions at a corner
	 * specified by the least x-value and least y-value vertex of the rectangle
	 * @param x corner x-value
	 * @param y corner y-value
	 * @param width rectangle width
	 * @param height rectangle height
	 */
	public Rectangle(double x, double y, double width, double height) {
		this.x = x; this.y = y; this.width = width; this.height = height;
	}
	//*******************************************************************
	public double x() {return x;}
	public Rectangle setX(double x) {this.x = x; return this;}
	public Rectangle moveX(double x) {
		this.x += x; 
		return this;
	}
	public double y() {return y;}
	public Rectangle setY(double y) {this.y = y; return this;}
	public Rectangle moveY(double y) {
		this.y += y;
		return this;
	}
	//*******************************************************************
	public double width() {return width;}
	public Rectangle setWidth(double width) { this.width = width;return this;}
	public Rectangle addWidth(double width) {
		x = x-width/2;
		this.width += width;
		return this;
	}
	public double height() {return height;}
	public Rectangle setHeight(double height) { this.height = height;return this;}
	public Rectangle addHeight(double height) {
		y = y-height/2;
		this.height += height;
		return this;
	}
	/**
	 * Convenience method for simultaneously modifying height and width
	 * @param size amount by which to change both dimensions
	 * @return this
	 */
	public Rectangle addSize(double size) {
		addWidth(size); addHeight(size); return this;
	}
	//*******************************************************************
	public double getMinX() {return x;}
	public double getMaxX() {return x + width;}
	public double getMinY() {return y;}
	public double getMaxY() {return y + height;}
	public double centerX() {return x+width/2;}
	public double centerY() {return y+height/2;}
	//***************************************************************************
	/**
	 * Returns a random x-value between the least and greatest x-values
	 * with a continuous uniform distribution
	 * @return random x-value
	 */
	public double getRandomX() {
		return random()*(getMaxX()-getMinX());
	}
	/**
	 * Equivalent to {@link Rectangle#getRandomX() <code>getRandomX()</code>}
	 * with specified seed
	 * @param seed seed from which to generate random result
	 * @return random x-value
	 */
	public double getRandomX(int seed) {
		return new Random(seed).nextDouble()*(getMaxX()-getMinX());
	}
	/**
	 * Returns a random y-value between the least and greatest y-values
	 * with a continuous uniform distribution
	 * @return random y-value
	 */
	public double getRandomY() {
		return random()*(getMaxY()-getMinY());
	}
	/**
	 * Equivalent to {@link Rectangle#getRandomY() <code>getRandomY()</code>}
	 * with specified seed
	 * @param seed seed from which to generate random result
	 * @return random y-value
	 */
	public double getRandomY(int seed) {
		return new Random(seed).nextDouble()*(getMaxY()-getMinY());
	}
	//*****************
	public double[] abscissae() {return new double[]{x, x+width, x+width, x};}
	public double[] ordinates() {return new double[]{y, y, y+height, y+height};}
	public int[] intAbscissae() {
		return new int[]{(int)round(x),(int)round(x+width),(int)round(x+width),(int)x};
	}
	public int[] intOrdinates() {
		return new int[]{(int)round(y),(int)round(y),
				(int)round(y+height),(int)round(y+height)};
	}
	/**
	 * Returns a new polygon comprising this rectangle's vertices
	 * @return the new polygon
	 */
	public Polygon toPolygon() {
		return new Polygon(new double[]{x, x+width, x+width, x},
				new double[]{y, y, y+height, y+height});
	}
	public String toString() {
		return "width: " + width + " height: " + height + " x: " + x + " y: " + y;
	}

}
