package gdsoftworks.geometry;

/**
 * Platform-independent representation of a {@link Polygon polygon} with no
 * mutability. Whenever access to a polygon's properties is needed but modification
 * could wreak havoc, a read-only polygon can be safely used as a method parameter.
 * @author EDH
 * @version 1.0 alpha
 * @see ReadOnlyRectangle
 */
public interface ReadOnlyPolygon {
	/**
	 * Returns all x values of this polygon's vertices as a double array
	 * @return ordered array of x values
	 */
	double[] abscissae();
	/**
	 * Returns all y values of this polygon's vertices as a double array
	 * @return ordered array of y values
	 */
	double[] ordinates();
	/**
	 * Returns an array of all x values rounded to the closest integer
	 * @return array of rounded x values
	 */
	int[] intAbscissae();
	/**
	 * Returns an array of all y values rounded to the closest integer
	 * @return array of rounded y values
	 */
	int[] intOrdinates();
	/**
	 * Returns the number of vertices in the polygon
	 * @return vertex count
	 */
	int points();
	/**
	 * Safely retrieves the read-only {@link FillColor color} associated with this polygon
	 * @return color
	 */
	ReadOnlyColor getColor();
	/**
	 * Tests this polygon for spatial overlap with another polygon
	 * @param other the polygon to be tested against
	 * @return overlap
	 */
	boolean isOverlapping(ReadOnlyPolygon other);
	Line overlappingLine(ReadOnlyPolygon other);
	/**
	 * Tests whether this polygon contains the specified point
	 * @param x the x-value of the point
	 * @param y the y-value of the point
	 * @return true if this polygon contains the specified point
	 */
	boolean contains(double x, double y);
	/**
	 * If true, this polygon should be rendered by the view module of the application.
	 * Useful for debugging logical polygons.
	 * @return rendered at all
	 */
	boolean isGraphics();
	/**
	 * Returns the halfway value between the greatest x and the least x
	 * @return rough center x
	 */
	double centerX();
	/**
	 * Returns the halfway value between the greatest y and the least y
	 * @return rough center y
	 */
	double centerY();
	/**
	 * Returns the minimum x-value of all vertices
	 * @return min x
	 */
	double minX();
	/**
	 * Returns the maximum x-value of all vertices
	 * @return max x
	 */
	double maxX();
	/**
	 * Returns the minimum y-value of all vertices
	 * @return min y
	 */
	double minY();
	/**
	 * Returns the maximum y-value of all vertices
	 * @return max y
	 */
	double maxY();
	/**
	 * Returns the maximum distance from the center of this polygon to one of its vertices
	 * @return max distance from center
	 */
	double maxDistance();
	/**
	 * Returns the intended position of this polygon in the view module's rendering
	 * sequence. Essential for controlling layering or emulating three dimensions.
	 * A list of polygons can be sorted with a {@link ZComparator z-comparator}
	 * before being passed to the view.
	 * @return rendering position
	 */
	double z();
}
