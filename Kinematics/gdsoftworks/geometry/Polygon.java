package gdsoftworks.geometry;

import static gdsoftworks.geometry.Math.*;
import static java.lang.Math.*;
/**
 * Platform-independent representation of a polygon as vertices comprising an array
 * of x and y values. There are several convenience methods for polygon logic:
 * <ul><li> Trivially moving the polygon up or down in Cartesian space
 * <li> Trivially moving the polygon left or right in Cartesian space
 * <li> Rotating the polygon about a point by a specific degree
 * <li> Testing for overlapping lines with another polygon
 * <li> Retrieving the first line found to overlap with another polygon
 * <li> Reflection about a y or x axis
 * <li> Scale and deep-copy methods</ul>
 * If no mutability is required in a method, consider passing it a
 * {@link ReadOnlyPolygon read-only polygon} for security.
 * @author EDH
 * @version 1.0 alpha
 * @see Rectangle
 */
public class Polygon implements ReadOnlyPolygon{
	public double[] xs; public double[] ys;
	public volatile double red = 0; public volatile double green = 0;
	public volatile double blue = 0;
	public volatile double alpha = 1;
	public FillColor color = new FillColor(0,0,0);
	private boolean graphics = true;
	private double z = 0;
	//*********** 
	/**
	 * Constructs a polygon with vertices represented by the specified x-values
	 * and y-values
	 * @param xs array of x values
	 * @param ys array of y values
	 */
	public Polygon(double[] xs, double[] ys) {this.xs = xs; this.ys = ys;}
	/**
	 * Convenience constructor copies x values and y values from specified rectangle
	 * @param rect the rectangle
	 */
	public Polygon(ReadOnlyRectangle rect) {xs = rect.abscissae(); ys = rect.ordinates();}
	public Polygon(double x, double y, double distance, int sides) {
		double arc = 2*PI/sides; double radians = 0;
		xs = new double[sides]; ys = new double[sides];
		
		for (int i=0; sides>0; sides--, radians+=arc, i++) {
			xs[i] = distance*cos(radians)+x;
			ys[i] = distance*-sin(radians)+y;
		}
	}
	//***********
	/**
	 * Shifts all vertices by the specified x-value
	 * @param x the amount by which to shift in the x dimension
	 * @return this
	 */
	public Polygon addX(double x) {
		if (x==0) return this;
		for (int i=xs.length; i>0; i--) xs[i-1] += x;
		return this;
	}
	/**
	 * Shifts all vertices by the specified y-value
	 * @param y the amount by which to shift in the y dimension
	 * @return this
	 */
	public Polygon addY(double y){
		if (y==0) return this;
		for (int i=ys.length; i>0; i--) ys[i-1] += y;
		return this;
	}
	//***********
	public double[] abscissae() {return xs;}
	public double[] ordinates() {return ys;}
	public int[] intAbscissae() {return toInts(abscissae());}
	private static int[] toInts(double[] array) {
		int[] ints = new int[array.length];
		for (int i=0; i<array.length; i++)
			ints[i] = (int)round(array[i]);
		return ints;
	}
	public int[] intOrdinates() {return toInts(ordinates());}
	public int points() {return xs.length;}
	public double z() {return z;}
	public Polygon z(double z) {this.z = z; return this;}
	public double minX() {return getMin(xs);}
	private static double getMin(double[] array) {
		double min = Double.MAX_VALUE;
		int len = array.length;
		for (int i=0; i<len; i++) {double d=array[i]; if (d<min) min=d;}
		return min;
	}
	public double maxX() {return getMax(xs);}
	private static double getMax(double[] array) {
		double max = -Double.MAX_VALUE;
		int len = array.length;
		for (int i=0; i<len; i++) {double d=array[i]; if (d>max) max=d;}
		return max;
	}
	public double minY() {return getMin(ys);}
	public double maxY() {return getMax(ys);}
	public double maxDistance() {
		double centerX = centerX(); double centerY = centerY();
		double distance; double maxDistance = 0;
		for (int i=0; i<xs.length; i++) {
			distance = sqrt(square(abs(centerX-xs[i])) + square(abs(centerY-ys[i])));
			if (distance>maxDistance) maxDistance=distance;
		}
		return maxDistance;
	}
	public double centerX() {return minX()+(maxX()-minX())/2;}
	public double centerY() {return minY()+(maxY()-minY())/2;}
	//************
	/**
	 * Rotates this polygon by the specified amount of radians, about
	 * the point represented by the specified x and y-value
	 * @param aboutX the x value of the point
	 * @param aboutY the y value of the point
	 * @param radians the amount of radians by which to rotate
	 * @return this
	 */
	double distanceFactor, unitX, unitY, theta;
	public Polygon rotate(double aboutX, double aboutY, double radians) {
		if (radians==0) return this;
		
		for (int i=0; i<xs.length; i++) {
			distanceFactor = sqrt(square(abs(aboutX-xs[i])) + square(abs(aboutY-ys[i])));
			if (distanceFactor==0) continue;
			unitX = (xs[i]-aboutX)/distanceFactor;
			unitY = -(ys[i]-aboutY)/distanceFactor;
			
			theta = unitY<0? 2*PI-acos(unitX): acos(unitX);
			xs[i] = cos(theta+radians)*distanceFactor+aboutX;
			ys[i] = -sin(theta+radians)*distanceFactor+aboutY;
		}
		return this;
	}
	private static double square(double base) {return pow(base, 2d);}
	//************
	public boolean isGraphics() {return graphics;}
	public Polygon setGraphics(boolean graphics) {this.graphics = graphics; return this;}
	public boolean isOverlapping(ReadOnlyPolygon other) {
		if ((square(abs(centerX()-other.centerX()))
				+ square(abs(centerY()-other.centerY())))
				>((maxDistance()+other.maxDistance())*(maxDistance()+other.maxDistance())))
			return false;
		double[] xs = abscissae(); double[] ys = ordinates();
		double[] otherXs = other.abscissae(); double[] otherYs = other.ordinates();
		double slope; double b; double intersectionX; double intersectionY;
		double minX; double maxX; double minY; double maxY;
		double otherMinX; double otherMaxX; double otherMinY;
		double otherMaxY; double otherSlope; double otherB;
		double effectiveInfinity = pow(10, 10);
		
		for (int i=0; i<xs.length; i++) {
			if (i<xs.length-1) {
				slope = (ys[i]-ys[i+1])/(xs[i]-xs[i+1]);
				minX = min(xs[i], xs[i+1]); maxX = max(xs[i], xs[i+1]);
				minY = min(ys[i], ys[i+1]); maxY = max(ys[i], ys[i+1]);
			}
			else {
				slope = (ys[i]-ys[0])/(xs[i]-xs[0]);
				minX = min(xs[i], xs[0]); maxX = max(xs[i], xs[0]);
				minY = min(ys[i], ys[0]); maxY = max(ys[i], ys[0]);
			}
			b = solveLinear(0, ys[i], 1, slope*xs[i]);
			
			for (int j=0; j<otherXs.length; j++) {
				if (j<otherXs.length-1) {
					otherSlope = (otherYs[j]-otherYs[j+1])/(otherXs[j]-otherXs[j+1]);
					otherMinX = min(otherXs[j], otherXs[j+1]);
					otherMaxX = max(otherXs[j], otherXs[j+1]);
					otherMinY = min(otherYs[j], otherYs[j+1]);
					otherMaxY = max(otherYs[j], otherYs[j+1]);
				}
				else {
					otherSlope = (otherYs[j]-otherYs[0])/(otherXs[j]-otherXs[0]);
					otherMinX = min(otherXs[j], otherXs[0]);
					otherMaxX = max(otherXs[j], otherXs[0]);
					otherMinY = min(otherYs[j], otherYs[0]);
					otherMaxY = max(otherYs[j], otherYs[0]);
				}
				
				if ((Double.isInfinite(slope) && Double.isInfinite(otherSlope))
						|| (slope==0 && otherSlope==0)
						|| (slope>effectiveInfinity && otherSlope>effectiveInfinity)
						|| slope==otherSlope)
					continue;
				
				otherB = solveLinear(0, otherYs[j], 1, otherSlope*otherXs[j]);
				if (Double.isInfinite(slope) || slope>effectiveInfinity) {
					intersectionX = xs[i]; intersectionY = otherSlope*xs[i]+otherB;
				}
				else if (Double.isInfinite(otherSlope) || otherSlope>effectiveInfinity) {
					intersectionX = otherXs[j]; intersectionY = slope*otherXs[j]+b;
				}
				else {
					intersectionX = solveLinear(slope, b, otherSlope, otherB);
					intersectionY = slope*intersectionX+b;
				}
				
				if (intersectionX>=minX && intersectionX<=maxX && intersectionX>=otherMinX
						&& intersectionX<=otherMaxX && intersectionY>=minY
						&& intersectionY<=maxY && intersectionY>=otherMinY
								&& intersectionY<=otherMaxY) 
					return true;
			}
		}
		return false;
	}
	/**
	 * Retrieves the first {@link Line line} found to be overlapping this polygon. If none can
	 * be found, returns null.
	 * @param other the polygon against which to test
	 * @return overlapping line or, if none found, null
	 */
	public Line overlappingLine(ReadOnlyPolygon other) {
		if ((square(abs(centerX()-other.centerX()))
				+ square(abs(centerY()-other.centerY())))
				>((maxDistance()+other.maxDistance())*(maxDistance()+other.maxDistance())))
			return null;
		double[] xs = abscissae(); double[] ys = ordinates();
		double[] otherXs = other.abscissae(); double[] otherYs = other.ordinates();
		double slope; double b; double intersectionX; double intersectionY;
		double minX; double maxX; double minY; double maxY;
		double otherMinX; double otherMaxX; double otherMinY;
		double otherMaxY; double otherSlope; double otherB;
		double effectiveInfinity = pow(10, 10);
		
		for (int i=0; i<xs.length; i++) {
			if (i<xs.length-1) {
				slope = (ys[i]-ys[i+1])/(xs[i]-xs[i+1]);
				minX = min(xs[i], xs[i+1]); maxX = max(xs[i], xs[i+1]);
				minY = min(ys[i], ys[i+1]); maxY = max(ys[i], ys[i+1]);
			}
			else {
				slope = (ys[i]-ys[0])/(xs[i]-xs[0]);
				minX = min(xs[i], xs[0]); maxX = max(xs[i], xs[0]);
				minY = min(ys[i], ys[0]); maxY = max(ys[i], ys[0]);
			}
			b = solveLinear(0, ys[i], 1, slope*xs[i]);
			
			for (int j=0; j<otherXs.length; j++) {
				if (j<otherXs.length-1) {
					otherSlope = (otherYs[j]-otherYs[j+1])/(otherXs[j]-otherXs[j+1]);
					otherMinX = min(otherXs[j], otherXs[j+1]);
					otherMaxX = max(otherXs[j], otherXs[j+1]);
					otherMinY = min(otherYs[j], otherYs[j+1]);
					otherMaxY = max(otherYs[j], otherYs[j+1]);
				}
				else {
					otherSlope = (otherYs[j]-otherYs[0])/(otherXs[j]-otherXs[0]);
					otherMinX = min(otherXs[j], otherXs[0]);
					otherMaxX = max(otherXs[j], otherXs[0]);
					otherMinY = min(otherYs[j], otherYs[0]);
					otherMaxY = max(otherYs[j], otherYs[0]);
				}
				
				if ((Double.isInfinite(slope) && Double.isInfinite(otherSlope))
						|| (slope==0 && otherSlope==0)
						|| (slope>effectiveInfinity && otherSlope>effectiveInfinity)
						|| slope==otherSlope)
					continue;
				
				otherB = solveLinear(0, otherYs[j], 1, otherSlope*otherXs[j]);
				if (Double.isInfinite(slope) || slope>effectiveInfinity) {
					intersectionX = xs[i]; intersectionY = otherSlope*xs[i]+otherB;
				}
				else if (Double.isInfinite(otherSlope) || otherSlope>effectiveInfinity) {
					intersectionX = otherXs[j]; intersectionY = slope*otherXs[j]+b;
				}
				else {
					intersectionX = solveLinear(slope, b, otherSlope, otherB);
					intersectionY = slope*intersectionX+b;
				}
				
				if (intersectionX>=minX && intersectionX<=maxX && intersectionX>=otherMinX
						&& intersectionX<=otherMaxX && intersectionY>=minY
						&& intersectionY<=maxY && intersectionY>=otherMinY
								&& intersectionY<=otherMaxY)
					if (j<otherXs.length-1)
						return new Line(new double[]{otherXs[j],otherXs[j+1]},
								new double[]{otherYs[j],otherYs[j+1]});
					else return new Line(new double[]{otherXs[j],otherXs[0]},
							new double[]{otherYs[j],otherYs[0]});
			}
		}
		return null;
	}
	static Polygon testGon = new Polygon(new double[]{0, 0}, new double[]{0, 0});
	static final int STRICTNESS = 3;
	static final double TEST_ARC = 2*PI/STRICTNESS;
	public boolean contains(double x, double y) {
		double maxDistance = 0; double currentDistance = 0;
		
		for (int i=0; i<xs.length; i++) {
			currentDistance = 2 * (sqrt(square(abs(centerX()-xs[i]))
					+ square(abs(centerY()-ys[i]))));
			if (currentDistance>maxDistance) maxDistance = currentDistance;
		}
		double currentAngle = 0;
		for (int i=0; i<STRICTNESS; i++, currentAngle+=TEST_ARC) {
			testGon.xs[0] = x; testGon.xs[1] = maxDistance*cos(currentAngle)+x;
			testGon.ys[0] = y; testGon.ys[1] = maxDistance*-sin(currentAngle)+y;
			if (!testGon.isOverlapping(this)) return false;
		}
		
		return true;
	}
	public Polygon setColor(FillColor color) {this.color = color; return this;}
	public ReadOnlyColor getColor() {return color;}
	/**
	 * Returns the {@link FillColor color} object associated with this polygon
	 * @return color
	 */
	public FillColor color() {return color;}
	//************
	/**
	 * Reflects this polygon about the line y=<b>x</b>
	 * @param x the axis about which to reflect
	 * @return this
	 */
	public Polygon reflectYAxis(double x) {
		for (int i=0; i<xs.length; i++)
			xs[i] += 2*(x-xs[i]);
		return this;
	}
	/**
	 * Stretches or shrinks this polygon from the specified origin by the
	 * specified factor
	 * @param originX the origin x-value
	 * @param originY the origin y-value
	 * @param factor the factor by which to scale
	 * @return this
	 */
	public Polygon scale(double originX, double originY, double factor) {
		double distanceFactor; double unitX; double unitY; double theta;
		
		for (int i=0; i<xs.length; i++) {
			distanceFactor = sqrt(square(abs(originX-xs[i]))
					+ square(abs(originY-ys[i])));
			if (distanceFactor==0) continue;
			unitX = (xs[i]-originX)/distanceFactor;
			unitY = -(ys[i]-originY)/distanceFactor;
			
			distanceFactor*=factor;
			theta = unitY<0? 2*PI-acos(unitX): acos(unitX);
			xs[i] = cos(theta)*distanceFactor+originX;
			ys[i] = -sin(theta)*distanceFactor+originY;
		}
		return this;
	}
	public Polygon copy() {
		Polygon copy = new Polygon(Math.copyOfArray(xs, xs.length),
				Math.copyOfArray(ys, ys.length)){{
					setColor(color.copy());
					setGraphics(graphics); z(z);
					}};
		copy.red = red; copy.green = green; copy.blue = blue; copy.alpha = alpha;
		return copy;
	}
	public String toString() {
		String xValues = "abscissae: ";
		for (double x: xs) {xValues+=("" + x + " ");}
		String yValues = "ordinates: ";
		for (double y: ys) {yValues +=("" + y + " ");}
		return xValues + "\n" + yValues + "\ncolor: " + color +"\nz: "+z;
	}
}
