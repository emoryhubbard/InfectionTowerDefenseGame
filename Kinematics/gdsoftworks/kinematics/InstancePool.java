package gdsoftworks.kinematics;

import java.util.ArrayList;
import java.util.List;

/**
 * Beat the garbage collector
 * @author EDH
 *
 * @param <T>
 */
public class InstancePool<T> {
	public interface InstanceFactory<T> {public T newInstance();}
	
	public final List<T> freeObjects;
	public final InstanceFactory<T> factory;
	public final int maxSize;
	
	public InstancePool(InstanceFactory<T> factory, int maxSize) {
		this.factory = factory; this.maxSize = maxSize;
		this.freeObjects = new ArrayList<T>(maxSize);
	}
	public T newObject() {
		return freeObjects.isEmpty() ? factory.newInstance():
			freeObjects.remove(freeObjects.size()-1);
	}
	public void free(T object) {
		if (freeObjects.size()<maxSize) freeObjects.add(object);
	}
}
