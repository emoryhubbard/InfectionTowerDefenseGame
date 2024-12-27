package com.gdsoftworks.infectionbeta;

import java.util.LinkedList;
import java.util.List;

import gdsoftworks.geometry.FastRectangle;
import gdsoftworks.geometry.Vector;
import gdsoftworks.kinematics.DynamicObject;

public class Lipolase extends DynamicObject {
	static final double size = 1;
	double orientation; double mass = 1;
	boolean alive = true;
	boolean hit = false;
	FastRectangle logicBounds;
	List<Throbber> throbbers = new LinkedList<Throbber>();
	List<Lipolase> lipolaseProjectiles = new LinkedList<Lipolase>();
	public Lipolase(double x, double y, Vector velocity, double orientation) {
		super(x, y, size, size);
		this.velocity.set(velocity);
		this.orientation = orientation;
		logicBounds = new FastRectangle(x-size/4, y-size/4, size/2, size/2);
	}
	public void update(double deltaTime) {
		position.add(velocity.x*deltaTime, velocity.y*deltaTime);
		bounds.lowerLeft.add(velocity.x*deltaTime, velocity.y*deltaTime);
		logicBounds.lowerLeft.add(velocity.x*deltaTime, velocity.y*deltaTime);
		for (Lipolase lipolase: lipolaseProjectiles) {
			lipolase.velocity.set(velocity);
			lipolase.update(deltaTime);
		}
		for (Throbber throbber: throbbers) {
			throbber.velocity.set(velocity);
			if (!throbber.disintegrating) throbber.update(deltaTime);
		}
	}
}
