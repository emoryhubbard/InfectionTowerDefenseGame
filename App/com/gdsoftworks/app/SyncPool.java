package com.gdsoftworks.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Beat the garbage collector
 * @author EDH
 *
 * @param <T>
 */
public class SyncPool<T> {
	public interface InstanceFactory<T> {public T newInstance();}
	
	public final List<T> freeObjects;
	public final InstanceFactory<T> factory;
	public final int maxSize;
	
	public SyncPool(InstanceFactory<T> factory, int maxSize) {
		this.factory = factory; this.maxSize = maxSize;
		this.freeObjects = new ArrayList<T>(maxSize);
	}
	synchronized public T newObject() {
		return freeObjects.isEmpty() ? factory.newInstance():
			freeObjects.remove(freeObjects.size()-1);
	}
	synchronized public void free(T object) {
		if (freeObjects.size()<maxSize) freeObjects.add(object);
	}
	synchronized public void freeAll(List<T> objects) {
		int len = objects.size();
		for (int i=0; i<len; i++)
			if (freeObjects.size()<maxSize) freeObjects.add(objects.get(i));
	}
}
