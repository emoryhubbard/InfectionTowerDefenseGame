package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.Polygon;
import gdsoftworks.kinematics.Model;

public class Scaffold extends Gadget {
	static final int OPEN = 0;
	static final int CLOSED = 1;
	static final int OPENING = 2;
	static final int CLOSING = 3;
	int state = OPEN;
	double speed = 1;
	Polygon base, extension, slider;
	Model extensor;
	static final int COST = 80;
	class Extensor extends Model {public Extensor(double x, double y){super(x, y);}}
	public Scaffold(double x, double y) {
		super(x, y);
		base = new Polygon(new double[]{x-.72, x-.72, x+.72, x+.72},
				new double[]{y+.73, y-.73, y-.73, y+.73});
		extension = new Polygon(new double[]{x-1.57*2, x-1.57*2, x-.72, x-.72},
				new double[]{y+.16, y-.16, y-.16, y+.16});
		attachingPolygons.add(base);
		polygons.add(base); polygons.add(extension);
		slider = new Polygon(new double[]{x-4.17*1.75, x-4.17*1.75, x-1.57*2, x-1.57*2},
				new double[]{y+.73, y-.73, y-.73, y+.73});
		extensor = new Extensor(x, y);
		extensor.translationLimit = (1.57*2-.72)*.5;
		extensor.polygons.add(slider);
		extensor.addReferenceZ(1);
		add(extensor);
		scale(.5);
		buttons.add(new Button(){
			{polygons.add(base);}
			public void press() {
				switch (state) {
				case OPEN: state = CLOSING; break;
				case CLOSED: state = OPENING; break;
				case OPENING: state = CLOSING; break;
				case CLOSING: state = OPENING; break;
				}
			}
		});
		cost = COST;
	}
	public void update(double deltaTime) {
		switch (state) {
		case OPENING:
			if (!extensor.translate(-speed*deltaTime))
				state = OPEN; break;
		case CLOSING:
			if (!extensor.translate(speed*deltaTime))
				state = CLOSED; break;
		}
	}
}
