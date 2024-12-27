package gdsoftworks.kinematics;

import gdsoftworks.geometry.Vector;

public class DynamicObject extends StaticObject{
	public final gdsoftworks.geometry.Vector velocity;
	public final gdsoftworks.geometry.Vector acceleration;
	public double mass = 0;
	public DynamicObject(double x, double y, double width, double height) {
		super(x, y, width, height);
		velocity = new gdsoftworks.geometry.Vector();
		acceleration = new gdsoftworks.geometry.Vector();
	}
	public void moveTo(Vector newPos) {
		position.set(newPos);
		bounds.lowerLeft.set(position.x-bounds.width/2, position.y-bounds.height/2);
	}
	public void update(double deltaTime) {
		velocity.add(acceleration.x*deltaTime, acceleration.y*deltaTime);
		position.add(velocity.x*deltaTime, velocity.y*deltaTime);
		bounds.lowerLeft.add(velocity.x*deltaTime, velocity.y*deltaTime);
	}
}
