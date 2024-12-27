package gdsoftworks.geometry;
/**
 * Platform-independent representation of a {@link Rectangle rectangle} with
 * no mutability. Whenever access to a rectangle's properties is needed but modification
 * could wreak havoc, a read-only rectangle can be safely used as a method parameter.
 * @author EDH
 * @version 1.0 alpha
 * @see ReadOnlyPolygon
 */
public interface ReadOnlyRectangle{
	/**
	 * Returns least x of this rectangle's vertices
	 * @return least x
	 */
	double x();
	/**
	 * Returns least y of this rectangle's vertices
	 * @return least y
	 */
	double y();
	/**
	 * Returns the halfway value between the least x and the
	 * greatest x within the rectangle
	 * @return center x
	 */
	double centerX();
	/**
	 * Returns the halfway value between the least y and the
	 * greatest y within the rectangle
	 * @return center y
	 */
	double centerY();
	double width(); double height();
	/**
	 * Returns an ordered double array of x values represented by this rectangle's vertices
	 * @return array of x values
	 */
	double[] abscissae();
	/**
	 * Returns an ordered double array of y values represented by this rectangle's vertices
	 * @return array of y values
	 */
	double[] ordinates();
	/**
	 * Convenience method for rounded x values as integers
	 * @return array of x values
	 */
	int[] intAbscissae();
	/**
	 * Convenience method for rounded y values as integers
	 * @return array of y values
	 */
	int[] intOrdinates();
}
