package gdsoftworks.kinematics;

import gdsoftworks.geometry.Vector;

public class Particle extends DynamicObject {
	public int color = 0x00000000;
	public double age = 0;
	public double lifespan = 5;
	public double size = 1;
	public double initialSpeed = 1;
	public double red, green, blue, alpha = 1;
	double growthRate = .1/5; double fadeRate = .2;
	public Vector oldPosition = new Vector();
	public Particle(double x, double y, double width, double height) {
		super(x, y, width, height);
		oldPosition.set(position);
	}
	public void update(double deltaTime) {
		orientation+=spin*deltaTime;
		oldPosition.set(position);
		position.add(velocity.x*deltaTime, velocity.y*deltaTime);
		bounds.lowerLeft.add(velocity.x*deltaTime, velocity.y*deltaTime);
		age+=deltaTime;
		alpha-=fadeRate*deltaTime;
		bounds.width+=growthRate*deltaTime;
		bounds.height+=growthRate*deltaTime;
		double halfWidth = bounds.width/2;
		double halfHeight = bounds.height/2;
		bounds.lowerLeft.set(position.x-halfWidth,
				position.y-halfHeight);
	}
}
