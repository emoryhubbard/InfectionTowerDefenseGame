package com.gdsoftworks.infectionbeta;

import gdsoftworks.kinematics.Emitter;

public class ThrobberDebris extends Emitter {
	public ThrobberDebris(double x, double y, double width, double height, double frequency) {
		super(x, y, width, height, 1000);
		this.frequency = frequency;
		particleGrowthRate = 0; particleLifespan = 1.5;
		particleWidth = .18; particleHeight = .14;
		particleSpeed = 1; speedVariance = .3;
		sizeVariance = .1*(1/.75);
		particleSpin = Math.PI*4;
	}
}
