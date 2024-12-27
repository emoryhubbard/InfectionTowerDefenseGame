package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.Vector;
import gdsoftworks.kinematics.DynamicObject;

public class PointsPhantom extends DynamicObject {
	static final double FONT_HEIGHT = 9;
	static final double FONT_WIDTH = 8;
	static final double LIFESPAN = 5;
	int length = 0;
	double age = 0;
	double alpha = 1;
	double fadeRate = alpha/LIFESPAN;
	char[] text = new char[10];
	Vector floatVelocity = new Vector(0, 4);
	public PointsPhantom() {
		super(0, 0, 8, 9);
		setText();
		velocity.set(floatVelocity);
	}
	public void setText() {
		double length = this.length*FONT_WIDTH;
		bounds.width = length;
		bounds.lowerLeft.set(position.x-bounds.width/2, position.y-bounds.height/2);
	}
	public void update(double deltaTime) {
		age+=deltaTime;
		alpha-=fadeRate*deltaTime;
		super.update(deltaTime);
	}
}
