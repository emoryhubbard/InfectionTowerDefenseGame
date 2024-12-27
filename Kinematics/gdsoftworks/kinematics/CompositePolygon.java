package gdsoftworks.kinematics;

import gdsoftworks.geometry.ReadOnlyPolygon;

import java.util.List;
/**
 * Rendering data can be extracted from any object whose class implements
 * this interface.
 * @author EDH
 * @version 1.1
 * @see Model
 * @see Platform
 */
public interface CompositePolygon {
	/**
	 * Returns a list of polygons destined for a view module. The polygons
	 * are not guaranteed to be sorted by their {@link ReadOnlyPolygon#z()
	 * suggested rendering order}.
	 * @return list of read-only polygons
	 */
	List<ReadOnlyPolygon> getPolygons();
}
