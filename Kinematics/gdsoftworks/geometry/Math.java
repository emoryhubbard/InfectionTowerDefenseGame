package gdsoftworks.geometry;

/**
 * {@link Polygon} makes use of a {@link Math#solveLinear(double, double, double, double)
 * linear equation solver} in this class.
 * @author EDH
 * @version 1.1
 */
public class Math {
	/**
	 * Returns the solution to a single-variable linear equation represented by
	 * a coefficient and constant on each side. For example, the solution to
	 * <code>3x+5=20</code> would be returned by <code>solveLinear(3, 5, 0, 20)</code>. 
	 * @param cf1 coefficient of the unknown variable on one side
	 * @param double1 constant modifying the cf1 term
	 * @param cf2 coefficient of the unknown variable on the other side
	 * @param double2 constant modifying the cf2 term
	 * @return value of the variable, i.e. solution to the linear equation
	 */
	public static double solveLinear(double cf1, double double1,
			double cf2, double double2) {
		if (cf1>cf2) {
			cf1-=cf2; double2-=double1;
			return double2/cf1;
		}
		else if (cf2>cf1){
			cf2-=cf1; double1-=double2;
			return double1/cf2;
		}
		else return Double.NaN;
	}
	public static double[] copyOfArray(double[] array, int newLength) {
		double[] newArray = new double[newLength];
		for (int i=0; i<newLength && i<array.length; i++)
			newArray[i] = array[i];
		return newArray;
	}
	//*********
	public static void main(String... args) {
		double cf1; double cf2; double double1; double double2;
		System.out.println("sovleLinear test:");
		for (int i=0; i<4; i++) {
			cf1 = java.lang.Math.random()*20-10;
			cf2 = java.lang.Math.random()*20-10;
			double1 = java.lang.Math.random()*200-100;
			double2 = java.lang.Math.random()*200-100;
			
			System.out.println("equation is cf1="+cf1+", double1="+double1+", cf2="+cf2
					+" double2="+double2);
			System.out.println("solution is "+ solveLinear(cf1, double1, cf2, double2));
		}
	}
}
