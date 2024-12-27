package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.ReadOnlyPolygon;

import java.util.LinkedList;
import java.util.List;

abstract public class Button {
	public List<ReadOnlyPolygon> polygons = new LinkedList<ReadOnlyPolygon>();
	public void press(){} public void release(){}
}
