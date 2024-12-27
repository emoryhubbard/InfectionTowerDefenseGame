package gdsoftworks.geometry;
/**
 * Read-only colors allow {@link Polygon polygons} to hide the sensitive
 * mutator methods of their {@link FillColor color} objects.
 * @author EDH
 * @version 1.0 alpha
 * @see ReadOnlyPolygon
 */
public interface ReadOnlyColor {
	/**
	 * Returns the red component of this color, as a double from 0.0 to 1
	 * @return red component
	 */
	double red();
	/**
	 * Returns the green component of this color, as a double from 0.0 to 1
	 * @return green component
	 */
	double green();
	/**
	 * Returns the blue component of this color, as a double from 0.0 to 1
	 * @return blue component
	 */
	double blue();
	double sanitizedRed(); double sanitizedGreen(); double sanitizedBlue();
	/**
	 * Returns the alpha component of this color, as a double from 0.0 to 1.
	 * Assumes the source-over Porter-Duff rule.
	 */
	double alpha();
	int toInt();
}
