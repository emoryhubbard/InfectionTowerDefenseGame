package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.Vector;
import gdsoftworks.kinematics.DynamicObject;

import com.gdsoftworks.app.Sound;

abstract public class MigratingWidget extends DynamicObject {
	static final int STILL = 0;
	static final int MOVING = 1;
	int currentPoint;
	int state = STILL;
	double moveTime = .3;
	double changeTime = 0;
	Vector moveVelocity = new Vector();
	Vector[] points;
	Sound sound;
	public MigratingWidget(int point, double width, double height,
			double moveTime, Vector... points) {
		super(points[point].x, points[point].y, width, height);
		currentPoint = point;
		this.points = points;
		this.moveTime = moveTime;
	}
	abstract public void press(Vector point);
	public void update(double deltaTime) {
		changeTime+=deltaTime;
		velocity.add(acceleration.x*deltaTime, acceleration.y*deltaTime);
		position.add(velocity.x*deltaTime, velocity.y*deltaTime);
		bounds.lowerLeft.add(velocity.x*deltaTime, velocity.y*deltaTime);
		switch (state) {
		case MOVING:
			if (changeTime>moveTime) {
				changeTime = 0;
				velocity.set(0, 0);
				position.set(points[currentPoint]);
				bounds.lowerLeft.set(points[currentPoint].x-bounds.width/2,
						points[currentPoint].y-bounds.height/2);
				state = STILL;
			}
			break;
		}
	}
	public void advance() {
		int nextPoint;
		changeTime = 0;
		if (currentPoint<points.length-1) nextPoint = currentPoint+1;
		else nextPoint = 0;
		moveVelocity.set(points[nextPoint]);
		moveVelocity.add(-position.x, -position.y);
		moveVelocity.scale(1/moveTime);
		velocity.set(moveVelocity);
		currentPoint = nextPoint;
		state = MOVING;
	}
	public void advance(int point) {
		changeTime = 0;
		moveVelocity.set(points[point]);
		moveVelocity.add(-position.x, -position.y);
		moveVelocity.scale(1/moveTime);
		velocity.set(moveVelocity);
		currentPoint = point;
		state = MOVING;
	}
}