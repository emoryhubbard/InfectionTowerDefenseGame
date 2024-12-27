package com.gdsoftworks.infectionbeta;

import java.util.Random;

import com.gdsoftworks.ai.FitnessFunction;

import gdsoftworks.geometry.Polygon;
import gdsoftworks.geometry.Vector;
import gdsoftworks.kinematics.Model;

public class DrexlerSaw extends Gadget {
	double cutTime = .5;
	static final int SEEKING = 0;
	static final int ACTIVATING = 2;
	static final int DEACTIVATING = 3;
	static final int PASSIVE = 4;
	static final int CUTTING = 5;
	static final int CONTACT = 6;
	static final int SEPARATE = 7;
	//***
	static final int CC = 0;
	static final int C = 1;
	static final int STOP = 2;
	//***
	static final int COST = 250;
	int state = PASSIVE;
	//***
	int firstState, secondState, previousFirstState, previousSecondState = STOP;
	//***
	static final int SPINNING = 0;
	static final int STOPPED = 1;
	int firstJointCC, firstJointC, secondJointCC, secondJointC = STOPPED;
	int previousFirstJointCC, previousFirstJointC, previousSecondJointCC,
	previousSecondJointC = STOPPED;
	double seekDuration = 5; double changeTime = seekDuration; 
	double bladeSpin = 20*Math.PI; double previousFitness = 0;
	double armSpin = Math.PI;
	Random random  = new Random();
	Polygon firstSegment, secondSegment, blade;
	Model secondJoint, bladeJoint, bladeEdge;
	FitnessFunction function = null;
	ThrobberDebris vapor;
	public DrexlerSaw(double x, double y) {
		super(x, y);
		firstSegment = new Polygon(new double[]{x-.25, x-.53, x+3.32, x+3.37},
				new double[]{y+.47, y-.37, y-.64, y-.31});
		polygons.add(firstSegment);
		secondSegment = new Polygon(new double[]{x+3.47, x+3.49, x+6.78, x+6.77},
				new double[]{y-.33, y-.83, y-.41, y-.08});
		secondJoint = new Model(x+3.72, y-.55);
		secondJoint.polygons.add(secondSegment);
		add(secondJoint);
		blade = new Polygon(new double[]{x+6.96, x+6.96, x+8.46, x+8.46},
				new double[]{y+.59, y-.89, y-.89, y+.59});
		bladeJoint = new Model(x+7.72, y-.16);
		bladeJoint.polygons.add(blade);
		bladeEdge = new Model(x+7.72, y-.89);
		bladeJoint.add(bladeEdge);
		secondJoint.add(bladeJoint);
		scale(.5);
		Vector vector = new Vector(secondJoint.x()-x, secondJoint.y()-y);
		radians = relativeRadians = vector.angle();
		vector.set(bladeJoint.x()-secondJoint.x(), bladeJoint.y()-secondJoint.y());
		secondJoint.radians = secondJoint.relativeRadians = vector.angle();
		secondJoint.translate(-.2);
		bladeJoint.translate(-.2);
		cost = COST;
		Polygon base = new Polygon(x, y, .1, 24);
		polygons.add(base);
		attachingPolygons.add(base);
		buttons.add(new Button() {
			{polygons.add(firstSegment); polygons.add(secondSegment); polygons.add(blade);}
			public void press() {if (state==PASSIVE) state=ACTIVATING;}
		});
		vapor = new ThrobberDebris(bladeEdge.x(), 10-bladeEdge.y(), .1, .1, Double.MAX_VALUE);
	}
	public void setFitnessFunction(FitnessFunction function) {
		this.function = function;
	}
	public void update(double deltaTime) {
		changeTime+=deltaTime;
		vapor.position.set(bladeEdge.x(), 10-bladeEdge.y());
		vapor.bounds.lowerLeft.set(bladeEdge.x()-.05, 10-bladeEdge.y()-.05);
		switch (state) {
		case ACTIVATING:
			changeTime = 0; state = SEEKING;
			break;
		case SEEKING:
			bladeJoint.rotate(bladeSpin*deltaTime);
			switch (firstState) {
			case CC: rotate(armSpin*deltaTime); break;
			case C: rotate(-armSpin*deltaTime); break;
			}
			switch (secondState) {
			case CC: secondJoint.rotate(armSpin*deltaTime); break;
			case C: secondJoint.rotate(-armSpin*deltaTime); break;
			}
			double currentFitness = function.evaluate();
			if (currentFitness>=previousFitness) {
				previousFirstState = firstState;
				previousSecondState = secondState;
				if (random.nextDouble()>.5)
					firstState = (int) (random.nextDouble()*3);
				else secondState = (int) (random.nextDouble()*3);
				previousFitness = currentFitness;
			}
			else {
				firstState = previousFirstState;
				secondState = previousSecondState;
				if (random.nextDouble()>.5)
					firstState = (int) (random.nextDouble()*3);
				else secondState = (int) (random.nextDouble()*3);
				previousFitness = currentFitness;
			}
			/**double firstJointSpin = (firstJointCC==STOPPED?0:armSpin) +
					(firstJointC==STOPPED?0:-armSpin);
			double secondJointSpin = (secondJointCC==STOPPED?0:armSpin) +
					(secondJointC==STOPPED?0:-armSpin);
			rotate(firstJointSpin*deltaTime);
			secondJoint.rotate(secondJointSpin*deltaTime);
			double currentFitness = function.evaluate();
			if (currentFitness>=previousFitness) {
				previousFirstJointCC = firstJointCC;
				previousFirstJointC = firstJointC;
				previousSecondJointCC = secondJointCC;
				previousSecondJointC = secondJointC;
				int change = (int) (random.nextDouble()*4);
				switch (change) {
				case 0: firstJointCC = (int) (random.nextDouble()*2); break;
				case 1: firstJointC = (int) (random.nextDouble()*2); break;
				case 2: secondJointCC = (int) (random.nextDouble()*2); break;
				case 3: secondJointC = (int) (random.nextDouble()*2); break;
				}
				previousFitness = currentFitness;
			}
			else {
				firstJointCC = previousFirstJointCC;
				firstJointC = previousFirstJointC;
				secondJointCC = previousSecondJointCC;
				secondJointC = previousSecondJointC;
				int change = (int) (random.nextDouble()*4);
				switch (change) {
				case 0: firstJointCC = (int) (random.nextDouble()*2); break;
				case 1: firstJointC = (int) (random.nextDouble()*2); break;
				case 2: secondJointCC = (int) (random.nextDouble()*2); break;
				case 3: secondJointC = (int) (random.nextDouble()*2); break;
				}
				previousFitness = currentFitness;
			}**/
			
			if (changeTime>seekDuration) {
				changeTime = 0; state = DEACTIVATING;
			}
			break;
		case DEACTIVATING:
			changeTime = 0; state = PASSIVE;
			break;
		}
	}
}
