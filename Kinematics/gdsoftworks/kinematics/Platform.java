package gdsoftworks.kinematics;

import gdsoftworks.geometry.Polygon;
import gdsoftworks.geometry.ReadOnlyPolygon;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link Model Models} can be wrapped with a platform to allow
 * independent movement in both dimensions and limitless rotation.
 * A command to move a platform can fail only if its associated model
 * would fail the move.
 * @author EDH
 * @version 1.1
 */
public class Platform implements CompositePolygon {
	private final Model model;
	//***********
	/**
	 * Constructs a deep copy of the specified platform
	 * @param other the platform to be copied
	 */
	protected Platform(final Platform other) {
		this(other.model.copy());
	}
	/**
	 * Constructs a new platform wrapper for the specified model
	 * @param model the model to be wrapped
	 */
	public Platform(Model model) {this.model = model;}
	/**
	 * Returns the model associated with this platform
	 * @return the model
	 */
	public Model getModel() {return model;}
	//***********
	/**
	 * Convenience method for setting this platform's x-position. Typically
	 * paired with {@link Platform#setY(double) setY}.
	 * @param x the new x-value of this platform's center
	 * @return move success
	 */
	public boolean setX(double x) {return moveX(x-centerX());}
	/**
	 * Convenience method for setting this platform's y-position. Typically
	 * paired with {@link Platform#setX(double) setX}.
	 * @param y the new y-value of this platform's center
	 * @return move success
	 */
	public boolean setY(double y) {return moveY(y-centerY());}
	/**
	 * Attempts to move the associated {@link Model model} along with its
	 * {@link Polygon polygons}
	 *  in the x dimension. Move will fail if the model would fail the move.
	 * @param x the amount by which to move x
	 * @return move success
	 * @see Platform#moveY(double)
	 */
	public boolean moveX(double x) {
		if (x==0) return true;
		return model.deepMoveX(x);
	}
	/**
	 * Attempts to move the associated {@link Model model} along with its
	 *  {@link Polygon polygons}
	 * in the y dimension. Will fail if the model fails the move.
	 * @param y the amount by which to move y
	 * @return move success
	 * @see Platform#moveX(double)
	 */
	public boolean moveY(double y) {
		return model.deepMoveY(y);
	}
	//***********
	public double centerX() {return model.x();}
	public double centerY() {return model.y();}
	public List<ReadOnlyPolygon> getPolygons(){
		return new LinkedList<ReadOnlyPolygon>(model.getPolygons());
	}
	//**********
	/**
	 * Attempts to rotate the associated {@link Model model} 
	 * and its attached {@link Polygon polygons}.
	 * Rotation will fail if the model would fail the rotation.
	 * @param radians the amount by which to rotate
	 * @return move success
	 */
	public boolean rotate(double radians) {
		return model.deepRotate(centerX(), centerY(), radians);
	}
	/**
	 * Tests all descendant polygons of this platform's model for
	 * {@link Polygon#isOverlapping(ReadOnlyPolygon) overlap} with the specified polygon
	 * @param gon the polygon against which to test overlap
	 * @return overlap
	 */
	public boolean isOverlapping(ReadOnlyPolygon gon) {
		if (model.isOverlapping(gon))
			return true;
		return false;
	}
	/**
	 * Convenience method for calling {@link Model#overlappingModel(ReadOnlyPolygon)
	 * overlappingPart(ReadOnlyPolygon)} on this platform's model
	 * @param gon the polygon with which to test for overlap
	 * @return the highest-z overlapping model visible to this platform's model, or null if none
	 */
	public Model overlappingModel(ReadOnlyPolygon gon) {
		return model.overlappingModel(gon);
	}
	//************
	public Platform copy() {
		return new Platform(model.copy());
	}
	public String toString() {return "x: "+centerX()+" y: "+centerY();}
}

