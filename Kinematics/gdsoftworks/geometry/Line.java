package gdsoftworks.geometry;
/**
 * Trivial immutable platform-independent representation of a line.
 * @author EDH
 * @version 1.0 alpha
 * @see Polygon
 */
public class Line {
	private double[] xs; private double[] ys;
	//*****
	/**
	 * Constructs a line represented by two pairs of points. Their x values
	 * are determined from the specified abscissae, and their y values are determined
	 * from the specified ordinates.
	 * @param abscissae x values
	 * @param ordinates y values
	 * @throws IllegalArgumentException
	 */
	public Line(double[] abscissae, double[] ordinates) {
		if (abscissae.length!=2 || ordinates.length!=2)
			throw new IllegalArgumentException("Construction of a line was attempted"
					+ " from an inappropriate number of points: "
					+ "abscissa count: "+abscissae.length+" ordinate count: "
					+ ordinates.length);
		xs = abscissae; ys = ordinates;
	}
	/**
	 * Returns x values representing this line
	 * @return abscissae
	 */
	public double[] abscissae() {return Math.copyOfArray(xs, xs.length);}
	/**
	 * Returns y values representing this line
	 * @return ordinates
	 */
	public double[] ordinates() {return Math.copyOfArray(ys, ys.length);}
}
