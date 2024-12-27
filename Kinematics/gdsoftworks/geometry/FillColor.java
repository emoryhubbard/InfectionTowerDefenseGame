package gdsoftworks.geometry;

/**
 * Trivial platform-independent representation of a color.
 * @author EDH
 * @see ReadOnlyColor
 * @see Polygon
 */
public class FillColor implements ReadOnlyColor {
	public double red;
	public double green;
	public double blue;
	public double alpha = 1;
	//*******
	/**
	 * Creates a new color with the specified red, green, and blue components
	 * each ranging from 0.0 (weakest) to 1 (strongest)
	 * @param red
	 * @param green
	 * @param blue
	 */
	public FillColor(double red, double green, double blue) {
		this.red = red; this.green = green; this.blue = blue;
	}
	public FillColor(double red, double green, double blue, double alpha) {
		if (alpha>1 || alpha<0)
			throw new IllegalArgumentException("Color parameters are out of "
					+ "range: alpha: "+alpha);
		this.red = red; this.green = green; this.blue = blue; this.alpha = alpha;
	}
	//******
	public FillColor setRed(double red) {
		if (red>1 || red<0)
			throw new IllegalArgumentException("Red is out of range: "+red);
		this.red=red; return this;
	}
	public FillColor setGreen(double green) {
		if (green>1 || green<0)
			throw new IllegalArgumentException("Green is out of range: "+green);
		this.green = green; return this;
	}
	public FillColor setBlue(double blue) {
		if (blue>1 || blue<0)
			throw new IllegalArgumentException("Blue is out of range: "+blue);
		this.blue = blue; return this;
		}
	public FillColor setAlpha(double alpha) {
		if (alpha>1 || alpha<0)
			throw new IllegalArgumentException("Alpha is out of range: "+alpha);
		this.alpha = alpha; return this;
	}
	public double red() {return red;} public double green() {return green;}
	public double blue() {return blue;}
	public double alpha() {return alpha;}
	public double sanitizedRed() {if (red>1) return 1; else return red;}
	public double sanitizedBlue() {if (blue>1) return 1; else return blue;}
	public double sanitizedGreen() {if (green>1) return 1; else return green;}
	public int toInt() {
		int color = 0x00000000;
		color |= (int)(255*sanitizedBlue());
		color |= (int)(255*sanitizedGreen()) << 8;
		color |= (int)(255*sanitizedRed()) << 16;
		color |= (int)(255*alpha) << 24;
		return color;
	}
	//********
	public FillColor copy() {return new FillColor(red, green, blue, alpha);}
	public String toString() {return "red: " + red + " green: " + green + " blue: " + blue;}
}
