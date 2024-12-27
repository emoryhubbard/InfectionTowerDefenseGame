package gdsoftworks.kinematics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;
abstract public class Emitter extends DynamicObject {
	public static final int ACTIVE = 0;
	public static final int INACTIVE = 1;
	public int state = ACTIVE;
	public double frequency;
	public double time = 0;
	public double particleSpin = 0;
	public double orientationVariance = PI*2;
	public double spinVariance = 0;
	public double particleSpeed = .2;
	public double speedVariance = .05;
	public double arcLimit = -1;
	public double particleWidth = .15; public double particleHeight = .15;
	public double sizeVariance = 0;
	public double particleLifespan = 5;
	public double particleAgeVariance = 1;
	public double particleGrowthRate = .1/5;
	public double particleGrowthVariance = particleGrowthRate/5;
	public double red, green, blue, alpha = 1;
	public int max;
	public List<Particle> particles;
	public Random random = new Random();
	public InstancePool<Particle> pool;
	public Emitter(double x, double y, double width, double height, int max) {
		super(x,y,width,height);
		pool = new InstancePool<Particle>(
				new InstancePool.InstanceFactory<Particle>(){
					public Particle newInstance(){
						return new Particle(0, 0, particleWidth, particleHeight);
					}
				}, max);
		this.max = max;
		particles = new ArrayList<Particle>(max);
		removeParticles = new ArrayList<Particle>(max);
	}
	List<Particle> removeParticles;
	public void update(double deltaTime) {
		position.add(velocity.x*deltaTime, velocity.y*deltaTime);
		bounds.lowerLeft.add(velocity.x*deltaTime, velocity.y*deltaTime);
		int len = particles.size();
		for (int i=0; i<len; i++) {
			Particle particle = particles.get(i);
			particle.update(deltaTime);
			if (particle.age>particle.lifespan) {
				pool.free(particle);
				removeParticles.add(particle);
			}
		}
		len = removeParticles.size();
		for (int i=0; i<len; i++) {
			Particle particle = removeParticles.get(i);
			particles.remove(particle);
		}
		removeParticles.clear();
		switch (state) {
		case ACTIVE: 
			time+=deltaTime;
			double times = time / frequency;
			int newParticles = (int) times;
			for (int i=0; i<newParticles; i++) if (particles.size()<max) emit();
			time -= newParticles*frequency;
			break;
		}
	}
	public void emit() {
		Particle particle = pool.newObject();
		particle.bounds.width = particleWidth-sizeVariance/2+random.nextDouble()*sizeVariance;
		particle.bounds.height = particleHeight-sizeVariance/2+random.nextDouble()*sizeVariance;
		particle.position.set(bounds.lowerLeft.x+random.nextDouble()*bounds.width,
				bounds.lowerLeft.y+random.nextDouble()*bounds.height);
		particle.bounds.lowerLeft.set(particle.position.x-particle.bounds.width/2,
				particle.position.y-particle.bounds.height/2);
		particle.age = 0;
		particle.lifespan = particleLifespan
				+ (-particleAgeVariance/2+random.nextDouble()*particleAgeVariance);
		particle.alpha = alpha;
		particle.fadeRate = particle.alpha/particle.lifespan;
		particle.growthRate = particleGrowthRate
				+ (-particleGrowthVariance/2+random.nextDouble()*particleGrowthVariance);
		particle.red = red; particle.green = green; particle.blue = blue;
		
		particle.orientation = orientation-orientationVariance/2
				+random.nextDouble()*orientationVariance;
		particle.spin = particleSpin-spinVariance/2+random.nextDouble()*spinVariance;
		if (arcLimit==-1) {
			particle.velocity.set(0, particleSpeed
					+(-speedVariance/2+random.nextDouble()*speedVariance)).rotate(
					2*PI*random.nextDouble());
		}
		particles.add(particle);
	}
}
