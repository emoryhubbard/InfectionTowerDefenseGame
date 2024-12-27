package com.gdsoftworks.infectionbeta;

import static java.lang.Math.PI;

import com.gdsoftworks.app.InstancePool;

import gdsoftworks.geometry.Collision;
import gdsoftworks.geometry.FastRectangle;
import gdsoftworks.geometry.Vector;
import gdsoftworks.kinematics.DynamicObject;
import gdsoftworks.kinematics.Particle;


public class Throbber extends DynamicObject {
	static InstancePool<ThrobberVapor> vaporPool = new InstancePool<ThrobberVapor>(
			new InstancePool.InstanceFactory<ThrobberVapor>(){
				public ThrobberVapor newInstance() {
					return new ThrobberVapor(0, 0, .1, .1, 1/(1*25));
				}
			}, 20);
	double orientation = 0;
	double spin = 0;
	double throbFrequency; double changeTime = 0; double throbDuration = .85;
	double density = 5; double bumpFrequency = throbFrequency; double bumpTime = 0;
	boolean alive = true; boolean throbbing = false;
	boolean hit = false; boolean bumping = false;
	boolean infecting = false; boolean disintegrating = false;
	double size;
	FastRectangle logicBounds;
	ThrobberVapor vapor;
	public Throbber(double x, double y, Vector velocity, double size) {
		super(x, y, size, size);
		this.size = size;
		this.velocity.set(velocity);
		logicBounds = new FastRectangle(x-size/4, y-size/4, size/2, size/2);
		spin = PI*100/((120*size)*(120*size));
		throbFrequency = .5/velocity.distance();
		mass = PI*(size/2*(size/2))*density;
		/**else if(size>1.25) {
			vapor = new ThrobberVapor(x, y, .1, .1, 
					size<.75?1/(size*2.5):1/(size*5));
			vapor.particleSpeed = .15/2;
			vapor.particleGrowthRate = .2/8;
			vapor.alpha = .3;
		}**/
	}
	public void smoke() {
		vapor = vaporPool.newObject();
		vapor.moveTo(position);
		vapor.frequency = size<.75?1/(size*25):1/(size*50);
		vapor.inactiveTime = 0;
		vapor.particleSpeed = size<.75?.25/3:.15;
		vapor.alpha = 1;
	}
	public void moveTo(Vector newPos) {
		super.moveTo(newPos);
		logicBounds.lowerLeft.set(position.x-logicBounds.width/2,
				position.y-logicBounds.height/2);
		if (vapor!=null) vapor.moveTo(newPos);
	}
	public void update(double deltaTime) {
		changeTime+=deltaTime;
		orientation+=spin;
		position.add(velocity.x*deltaTime, velocity.y*deltaTime);
		bounds.lowerLeft.add(velocity.x*deltaTime, velocity.y*deltaTime);
		if (vapor!=null) {
			vapor.update(deltaTime);
			vapor.position.add(velocity.x*deltaTime, velocity.y*deltaTime);
			vapor.bounds.lowerLeft.add(velocity.x*deltaTime, velocity.y*deltaTime);
			if (vapor.inactiveTime>vapor.particleLifespan) {
				vaporPool.free(vapor);
				vapor = null;
			}
		}
		logicBounds.lowerLeft.add(velocity.x*deltaTime, velocity.y*deltaTime);
		if (!disintegrating) {
			if (throbbing) {
				if (changeTime>throbDuration) {throbbing = false; changeTime = 0;}
			}
			else {
				if (changeTime>throbFrequency) {changeTime = 0; throbbing = true;}
			}
		}
		if (bumping) {
			bumpTime+=deltaTime;
			if (bumpTime>bumpFrequency) {bumping=false; bumpTime=0;}
		}
	}
	public void extinguish() {
		if (vapor!=null) {
			int len = vapor.particles.size();
			for (int i=0; i<len; i++) {
				Particle particle = vapor.particles.get(i);
				if (Collision.areOverlapping(logicBounds, particle.bounds)
					&& particle.age<particle.lifespan/3)
					particle.age = particle.lifespan;
			}
			vapor.state = ThrobberVapor.INACTIVE;
		}
	}
}
