package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.Vector;

public class Card extends Widget {
	interface Caller {
		public void collapseCards();
		public void createGadget(Vector point, int type);
	}
	static final int NULL = 0;
	static final int SCAFFOLD = 1;
	static final int ION_CATCHER = 2;
	static final int LIPOLYZER = 3;
	static final int DREXLER_SAW = 4;
	static final int PICKED = RECALLED+1;
	int type = NULL;
	int cost = 0;
	double moveTime = .25;
	double pickedTime = moveTime;
	double spin = -Math.PI*4;
	double shrinkRate = 75*(1/pickedTime);
	static final double width = 75; static final double height = 75;
	Vector collapsedPoint = new Vector();
	Vector expandedPoint = new Vector();
	Vector moveVelocity = new Vector();
	Caller caller;
	public Card(Vector collapsed, Vector expanded, Caller caller) {
		super(collapsed.x, collapsed.y, width, height);
		this.caller = caller;
		collapsedPoint.set(collapsed);
		expandedPoint.set(expanded);
		state = COLLAPSED;
		moveVelocity.set(collapsedPoint);
		moveVelocity.add(-expandedPoint.x, -expandedPoint.y);
		moveVelocity.scale(1/moveTime);
	}
	public void press(Vector point) {
		if (state==EXPANDED && type!=NULL) {
			changeTime = 0;
			state = PICKED;
			caller.createGadget(point, type);
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
		case PICKED:
			caller.collapseCards();
			orientation+=spin*deltaTime;
			bounds.width-=shrinkRate*deltaTime;
			bounds.height-=shrinkRate*deltaTime;
			bounds.lowerLeft.set(position.x-bounds.width/2, position.y-bounds.height/2);
			if (changeTime>pickedTime) {
				changeTime = 0;
				velocity.set(0, 0);
				position.set(collapsedPoint);
				orientation = 0;
				bounds.width = width; bounds.height = height;
				bounds.lowerLeft.set(collapsedPoint.x-bounds.width/2,
						collapsedPoint.y-bounds.height/2);
				state = COLLAPSED;
			}
			break;
		}
	}
}
