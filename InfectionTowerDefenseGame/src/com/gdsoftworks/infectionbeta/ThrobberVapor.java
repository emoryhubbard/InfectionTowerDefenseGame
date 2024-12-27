package com.gdsoftworks.infectionbeta;

import gdsoftworks.kinematics.Emitter;

public class ThrobberVapor extends Emitter {
	double inactiveTime = 0;
	public ThrobberVapor(double x, double y, double width, double height, double frequency) {
		super(x, y, width, height, 10000);
		this.frequency = frequency;
		particleGrowthRate = .4/3;
		particleLifespan = 5;
		particleWidth = 36/28*.15; particleHeight = 36/28*.15;
	}
	public void update(double deltaTime) {
		super.update(deltaTime);
		switch (state) {
		case INACTIVE:
			inactiveTime+=deltaTime;
			break;
		}
	}
}
