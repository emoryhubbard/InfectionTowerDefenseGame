package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.ReadOnlyPolygon;
import gdsoftworks.kinematics.Model;

import java.util.LinkedList;
import java.util.List;

abstract public class Gadget extends Model {
	public boolean enabled = false;
	public double cost;
	public Gadget(double x, double y) {super(x, y);}
	public List<ReadOnlyPolygon> attachingPolygons = new LinkedList<ReadOnlyPolygon>();
	public List<Button> buttons = new LinkedList<Button>();
}
