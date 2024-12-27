package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.Vector;
import gdsoftworks.kinematics.DynamicObject;

import com.gdsoftworks.app.Sound;

public class Widget extends DynamicObject {
	static final int EXPANDED = 0;
	static final int COLLAPSED = 1;
	static final int MOVING = 2;
	static final int RECALLED = 3;
	int state;
	double changeTime = 0;
	Sound sound;
	public Widget(double x, double y, double width, double height) {
		super(x, y, width, height);
	}
	public void press(Vector point) {
		
	}
	public void update(double deltaTime) {
		changeTime+=deltaTime;
		velocity.add(acceleration.x*deltaTime, acceleration.y*deltaTime);
		position.add(velocity.x*deltaTime, velocity.y*deltaTime);
		bounds.lowerLeft.add(velocity.x*deltaTime, velocity.y*deltaTime);
	}
	public void recall() {
		
	}
}
