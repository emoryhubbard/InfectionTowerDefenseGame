package com.gdsoftworks.infectionbeta;

import java.util.Random;

import gdsoftworks.geometry.Polygon;
import gdsoftworks.geometry.Vector;
import gdsoftworks.kinematics.Model;

public class Lipolyzer extends Gadget {
	interface Spawner {void spawn(Lipolase lipolase);}
	static final int READY = 0;
	static final int SHRINKING = 1;
	static final int GROWING = 2;
	static final int FIRED = 3;
	static final int COST = 40;
	static final Random random = new Random();
	int state = READY;
	double laseSpeed = 10;
	double growRate = 1.002; double shrinkRate = .9;
	double currentSize = 1.24;
	double growLimit = currentSize; double shrinkLimit = currentSize/3;
	Polygon body, base;
	Model top, bottom;
	Spawner spawner; LungLevel.Player player;
	public Lipolyzer(double x, double y, Spawner spawner, LungLevel.Player player) {
		super(x, y);
		body = new Polygon(new double[]{x-.24, x-.45, x-.26, x+.22, x+.46, x+.26},
				new double[]{y+.39, y+.05, y-.85, y-.85, y+.03, y+.39});
		base =  new Polygon(new double[]{x-.24/3, x-.45/3, x+.46/3, x+.26/3},
				new double[]{y+.39/3, y+.05/3, y+.03/3, y+.39/3});
		polygons.add(body); polygons.add(base);
		attachingPolygons.add(base);
		top = new Model(x, y-.85); bottom = new Model(x, y+.39);
		models.add(top); models.add(bottom);
		cost = COST;
		this.spawner = spawner; this.player = player;
		buttons.add(new Button(){
			{polygons.add(body);}
			public void press() {if (state==READY) state=FIRED;}
		});
	}
	Vector speed = new Vector();
	public void update(double deltaTime) {
		switch(state) {
		case FIRED:
			topPosition.set(top.x(), LungLevel.HEIGHT-top.y());
			bottomPosition.set(bottom.x(), LungLevel.HEIGHT-bottom.y());
			speed.set(topPosition);
			speed.add(-bottomPosition.x, -bottomPosition.y);
			speed.normalize(); speed.scale(laseSpeed);
			spawner.spawn(new Lipolase(topPosition.x, topPosition.y,
					speed, random.nextDouble()*2*Math.PI));
			player.shoot();
			state = SHRINKING;
			break;
		case SHRINKING:
			scale(shrinkRate);
			updateSize();
			if (currentSize<shrinkLimit)
				state = GROWING;
			break;
		case GROWING:
			updateSize();
			scale(growRate);
			if (currentSize>growLimit)
				state = READY;
			break;
		}
	}
	Vector topPosition = new Vector();
	Vector bottomPosition = new Vector();
	private void updateSize() {
		topPosition.set(top.x(), top.y());
		bottomPosition.set(bottom.x(), bottom.y());
		currentSize = topPosition.distance(bottomPosition);
	}
}
