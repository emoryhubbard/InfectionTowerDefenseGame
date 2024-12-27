package com.gdsoftworks.infectionbeta;

import gdsoftworks.geometry.Polygon;
import gdsoftworks.geometry.Vector;
import gdsoftworks.kinematics.Model;

public class Platform extends Model {
	public static final double START_X = 7.5;
	public static final double START_Y = 8.45;
	public Vector position = new Vector(START_X, START_Y);
	public Polygon base = new Polygon(new double[]{START_X-1, START_X, START_X+1,
			START_X}, new double[]{START_Y, START_Y-1, START_Y, START_Y+1});
	public Platform() {
		super(START_X, START_Y);
		polygons.add(base);
	}
}
