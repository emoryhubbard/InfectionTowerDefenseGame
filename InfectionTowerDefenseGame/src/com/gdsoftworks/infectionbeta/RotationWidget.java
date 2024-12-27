package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.Collision;
import gdsoftworks.geometry.Vector;

public class RotationWidget extends MigratingWidget {
	public interface Caller {
		void setSpin(int spin);
	}
	static final int NONE = 0;
	static final int CC = 1;
	static final int C = 2;
	static final double SIZE = 290/2d;
	static final double R = SIZE/2;
	static final double CORNER_DIST = Math.sqrt(R*R+R*R);
	static double moveTime = .3;
	static Vector[] destinations = new Vector[]{
		new Vector(480-9-SIZE/2, 290-29-6-SIZE/2),
		new Vector(480-9-SIZE/2+480, 290-29-6-SIZE/2-320),
		new Vector(9+SIZE/2, 6+61/2d+6+SIZE/2)
	};
	Caller caller;
	public RotationWidget(Caller caller) {
		super(1, SIZE, SIZE, moveTime, destinations);
		this.caller = caller;
	}
	
	public void press(Vector point) {
		if (Collision.rectContains(bounds, point)) {
			if (point.x<position.x) caller.setSpin(C);
			else caller.setSpin(CC);
		}
	}
	public void release() {caller.setSpin(NONE);}
	public void bump() {
		if (state!=MOVING) {
			if (currentPoint==0) advance(2);
			else advance(0);
		}
	}
}
