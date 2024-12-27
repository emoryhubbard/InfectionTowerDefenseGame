package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.Vector;

public class NanogramsBar extends MigratingWidget {
	static double WIDTH = 312/2; static double HEIGHT = 61/2d;
	static double OFFSET = 261/2-WIDTH/2;
	static Vector[] destinations = new Vector[]{new Vector(240, 320-6-61/4d),
		new Vector(9+312/4d, 6+61/4d),
		new Vector(480-9-312/4d, 6+61/4d)};
	public NanogramsBar(int point) {
		super(point, WIDTH, HEIGHT, .3, destinations);
	}
	public void press(Vector v) {
		advance();
	}
}
/**public class NanogramsBar extends Widget {
	static final Vector collapsedPoint = new Vector(9+312/4, 6+61/4);
	static final Vector expandedPoint = new Vector(240, 320-6-61/4);
	double moveTime = .3;
	Vector moveVelocity = new Vector();
	public NanogramsBar() {
		super(expandedPoint.x, expandedPoint.y, 312/2, 61/2);
		state = EXPANDED;
		moveVelocity.set(collapsedPoint);
		moveVelocity.add(-expandedPoint.x, -expandedPoint.y);
		moveVelocity.scale(1/moveTime);
	}
	public void press(Vector v) {
		switch (state) {
		case COLLAPSED:
			recall();
			break;
		case EXPANDED:
			collapse();
			break;
		}
	}
	public void recall() {
		if (state==COLLAPSED) {
			changeTime = 0;
			state = RECALLED;
		}
	}
	public void collapse() {
		if (state==EXPANDED) {
			changeTime = 0;
			velocity.set(moveVelocity);
			state = MOVING;
		}
	}
	public void update(double deltaTime) {
		super.update(deltaTime);
		switch (state) {
		case RECALLED:
			changeTime = 0;
			velocity.set(moveVelocity).rotate(Math.PI);
			state = MOVING;
			break;
		case MOVING:
			if (velocity.equals(moveVelocity)) {
				if (changeTime>moveTime) {
					changeTime = 0;
					velocity.set(0, 0);
					position.set(collapsedPoint);
					bounds.lowerLeft.set(collapsedPoint.x-bounds.width/2,
							collapsedPoint.y-bounds.height/2);
					state = COLLAPSED;
				}
			}
			else {
				if (changeTime>moveTime) {
					changeTime = 0;
					velocity.set(0, 0);
					position.set(expandedPoint);
					bounds.lowerLeft.set(expandedPoint.x-bounds.width/2,
							expandedPoint.y-bounds.height/2);
					state = EXPANDED;
					
				}
			}
			break;
		}
	}
}**/
