package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.Polygon;
import gdsoftworks.geometry.Vector;
import gdsoftworks.kinematics.Model;

public class IonCatcher extends Gadget {
	static final int PASSIVE = 0;
	static final int ACTIVE = 1;
	static final int ACTIVATING = 2;
	static final int COST = 40;
	static final int NORMAL_FIELD = 10;
	boolean sparking = false;
	int state = PASSIVE;
	double field = NORMAL_FIELD;
	double sparksTime = 0; double sparkElapsed = 0;
	double sparkFrequency = 2; double sparkDuration = .15; double activeDuration = 3;
	double activeFrequency = 15; double activeElapsed = activeFrequency;
	double sparkAngle = 0;
	Polygon sphere, antenna;
	Model spherePoint;
	Ion closest = null;
	LungLevel.Player player;
	public IonCatcher(double x, double y, LungLevel.Player player) {
		super(x, y);
		this.player = player;
		antenna = new Polygon(new double[]{x-.37, x-.13, x-.08, x-.02, x+.19, x+.15, x+.37},
				new double[]{y+.1, y-.14, y-1.22, y-1.31, y-1.31, y-.11, y+.1});
		sphere = new Polygon(new double[]{x-.04, x-.04, x+.18, x+.18},
				new double[]{y-1.31, y-1.53, y-1.53, y-1.31});
		Polygon base = new Polygon(new double[]{x-.37, x-.13, x+.15, x+.37},
				new double[]{y+.1, y-.14, y-.11, y+.1});
		spherePoint = new Model(sphere.centerX(), sphere.centerY());
		spherePoint.polygons.add(sphere);
		polygons.add(antenna); polygons.add(base);
		add(spherePoint);
		attachingPolygons.add(base);
		cost = COST;
		buttons.add(new Button(){
			{polygons.add(sphere); polygons.add(antenna);}
			public void press() {if (activeElapsed>activeFrequency) state = ACTIVATING;}
		});
	}
	Vector distance = new Vector();
	Vector spherePosition = new Vector();
	public void update(double deltaTime) {
		sparkElapsed+=deltaTime;
		activeElapsed+=deltaTime;
		switch (state) {
		case ACTIVATING:
			field = NORMAL_FIELD*10;
			sparkFrequency = .3;
			activeElapsed = 0;
			state = ACTIVE;
			break;
		case ACTIVE:
			if (activeElapsed>activeDuration) {
				field = NORMAL_FIELD;
				sparkFrequency = 2;
				state = PASSIVE;
			}
			break;
		}
		if (closest!=null) {
			if (closest.alive) {
				spherePosition.set(spherePoint.x(), LungLevel.HEIGHT-spherePoint.y());
				distance.set(closest.position).add(-spherePosition.x, -spherePosition.y);
				sparkAngle = distance.angle();
			}
			else closest = null;
		}
		if (!sparking) {
			if (sparkElapsed>sparkFrequency) {
				sparkElapsed = 0;
				sparking = true;
				player.zap();
			}
		}
		else {
			sparksTime+=deltaTime;
			if (sparkElapsed>sparkDuration) {
				sparkElapsed = 0;
				sparking = false;
				sparksTime = 0;
			}
		}
	}
}
