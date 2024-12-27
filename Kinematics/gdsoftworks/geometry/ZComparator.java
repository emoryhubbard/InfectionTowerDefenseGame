package gdsoftworks.geometry;

import java.util.Comparator;
/**
 * Used to sort lists of {@link ReadOnlyPolygon polygons} by their z values, which determine
 * their suggested rendering order.
 * @author EDH
 * @version 1.0 alpha
 * @see ReadOnlyPolygon#z()
 */
public class ZComparator implements Comparator<ReadOnlyPolygon> {
	public int compare(ReadOnlyPolygon first, ReadOnlyPolygon second) {
		if (first.z()>second.z()) return 1;
		if (first.z()<second.z()) return -1;
		return 0;
	}
}
