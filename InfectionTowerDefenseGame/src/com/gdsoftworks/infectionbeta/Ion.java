package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.FastRectangle;
import gdsoftworks.geometry.Vector;
import gdsoftworks.kinematics.DynamicObject;

public class Ion extends DynamicObject {
	boolean alive = true;
	double spin = Math.PI/2;
	double value;
	FastRectangle logicBounds;
	public Ion(double x, double y, double size, Vector velocity, double value) {
		super(x, y, size, size);
		this.velocity.set(velocity); this.value = value;
		logicBounds = new FastRectangle(x-size/4, y-size/4, size/2, size/2);
	}
	public void moveTo(Vector newPos) {
		super.moveTo(newPos);
		logicBounds.lowerLeft.set(position.x-logicBounds.width/2,
				position.y-logicBounds.height/2);
	}
	public void update(double deltaTime) {
		orientation+=spin*deltaTime;
		position.add(velocity.x*deltaTime, velocity.y*deltaTime);
		bounds.lowerLeft.add(velocity.x*deltaTime, velocity.y*deltaTime);
		logicBounds.lowerLeft.add(velocity.x*deltaTime, velocity.y*deltaTime);
	}
}
