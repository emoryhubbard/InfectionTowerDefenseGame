package com.gdsoftworks.infectionbeta;

import static java.lang.Math.PI;
import gdsoftworks.geometry.Collision;
import gdsoftworks.geometry.FastRectangle;
import gdsoftworks.geometry.Polygon;
import gdsoftworks.geometry.ReadOnlyPolygon;
import gdsoftworks.geometry.Vector;
import gdsoftworks.kinematics.Model;

import com.gdsoftworks.app.InstancePool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gdsoftworks.ai.FitnessFunction;

public class LungLevel {
	interface Player {
		void zap(); void shoot(); void stick(); void cut();
		void gain(int amount, Vector pos); void spend(int amount, Vector pos);
		void addTemplate(); void removeTemplate();
	}
	static final int NONE = 0;
	static final int C = 1;
	static final int CC = 2;
	static final int HEIGHT = 10;
	boolean templateAttachable = false;
	int templateZ = 1; int spin = NONE; double speed = Math.PI/2;
	int nanograms = 1000; double elapsedTime = 0; double ionSpawnFrequency = 4;
	double orientation = 0;
	Random random = new Random();
	Polygon finger = new Polygon(0, 0, .25, 24);
	public Gadget template = null;
	public Model containingModel = null;
	public Platform platform = new Platform();
	public List<Button> buttons = new ArrayList<Button>();
	public List<Scaffold> scaffolds = new ArrayList<Scaffold>();
	public List<IonCatcher> ionCatchers = new ArrayList<IonCatcher>();
	public List<Lipolyzer> lipolyzers = new ArrayList<Lipolyzer>();
	List<DrexlerSaw> drexlerSaws = new ArrayList<DrexlerSaw>();
	List<Ion> ions = new ArrayList<Ion>(200);
	InstancePool<Ion> ionPool = new InstancePool<Ion>(new InstancePool.InstanceFactory<Ion>(){
		public Ion newInstance() {return new Ion(0, 0, 0, new Vector(), 0);}
	}, 1000);
	{for (int i=0; i<200; i++) ionPool.free(new Ion(0, 0, 0, new Vector(), 0));}
	List<Lipolase> lipolaseProjectiles = new ArrayList<Lipolase>();
	InstancePool<Throbber> throbberPool =
			new InstancePool<Throbber>( new InstancePool.InstanceFactory<Throbber>(){
				public Throbber newInstance() {
					return new Throbber(0, 0, new Vector(), 0);
				}
	}, 1000);
	{for (int i=0; i<500; i++) throbberPool.free(new Throbber(0, 0, new Vector(), 0));}
	List<Throbber> throbbers = new ArrayList<Throbber>(500);
	List<ThrobberDebris> throbberDebris = new ArrayList<ThrobberDebris>(20);
	Player player;
	public LungLevel(Player player) {
		this.player = player;
		/**for (int i=0; i<1; i++) {
			throbberVapors.add(new ThrobberVapor(platform.x(),
					10-platform.y(), 2, .1, .006));
		}**/
	}
	public void create(String name, double x, double y) {
		y = 10-y;
		if (name.equalsIgnoreCase("scaffold")) {
			Scaffold scaffold = new Scaffold(x, y);
			scaffold.setReferenceZ(templateZ++);
			scaffolds.add(scaffold);
			template = scaffold;
		}
		else if (name.equalsIgnoreCase("ionCatcher")) {
			IonCatcher ionCatcher = new IonCatcher(x, y, player);
			ionCatcher.setReferenceZ(templateZ++);
			ionCatchers.add(ionCatcher);
			template = ionCatcher;
		}
		else if (name.equalsIgnoreCase("lipolyzer")) {
			Lipolyzer lipolyzer = new Lipolyzer(x, y, new Lipolyzer.Spawner(){
				public void spawn(Lipolase lipolase) {lipolaseProjectiles.add(lipolase);}
			}, player);
			lipolyzer.setReferenceZ(templateZ++);
			lipolyzers.add(lipolyzer);
			template = lipolyzer;
		}
		else if (name.equalsIgnoreCase("drexlerSaw")) {
			final DrexlerSaw drexlerSaw = new DrexlerSaw(x, y);
			drexlerSaw.setFitnessFunction(new FitnessFunction(){
				DrexlerSaw saw = drexlerSaw;
				Vector positions = new Vector();
				Throbber closest = null;
				public double evaluate() {
					double fitness = 0; double leastDistance = Double.MAX_VALUE;
					int len = throbbers.size();
					for (int i=0; i<len; i++) {
						Throbber throbber = throbbers.get(i);
						if (throbber.changeTime>.5 || throbber.hit) continue;
						positions.set(throbber.position);
						double distance = positions.distanceSquared(
								saw.x(), 10-saw.y());
						if (distance<leastDistance) {
							closest = throbber; leastDistance = distance;
						}
					}
					if (closest!=null)
						leastDistance = positions.set(closest.position).distanceSquared(
								saw.bladeJoint.x(), 10-saw.bladeJoint.y());
					if (leastDistance>15*15) fitness = 0;
					else if (leastDistance<.25) fitness = 1;
					else fitness = 1-(leastDistance/(15*15));
					return fitness;
				}
			});
			drexlerSaw.setReferenceZ(templateZ++);
			drexlerSaws.add(drexlerSaw);
			template = drexlerSaw;
			throbberDebris.add(drexlerSaw.vapor);
		}
		if (template!=null) {
			template.rotate(orientation);
			player.addTemplate();
		}
	}
	public void move(double x, double y) {
		template.setX(x); template.setY(10-y);
	}
	private void updateAttachable() {
		if (template!=null) {
			templateAttachable = false;
			int len = template.attachingPolygons.size();
			for (int i=0; i<len; i++) {
				ReadOnlyPolygon gon = template.attachingPolygons.get(i);
				containingModel = platform.containingModel(gon.centerX(), gon.centerY());
				/**for (Throbber throbber: throbbers) {
					if (template.isOverlapping(rectToPolygon(throbber.logicBounds)))
						return;
				}**/
				if (containingModel!=null && (containingModel instanceof Scaffold
						|| containingModel instanceof Platform
						|| containingModel instanceof Scaffold.Extensor)
						&& nanograms>=template.cost) {
					templateAttachable = true; break;
				}
			}
		}
	}
	Vector attachPos = new Vector();
	public void attach() {
		if (template!=null && templateAttachable) {
			containingModel.add(template);
			buttons.addAll(template.buttons);
			nanograms-=template.cost;
			attachPos.set(template.attachingPolygons.get(0).centerX(),
					10-template.attachingPolygons.get(0).centerY());
			player.spend((int)template.cost, attachPos);
			template.enabled = true;
			if (template instanceof IonCatcher) {
				double ionSpawnRate = 1/ionSpawnFrequency;
				ionSpawnRate+=.125;
				ionSpawnFrequency = 1/ionSpawnRate;
			}
			template = null;
		}
	}
	public void destroy() {
		scaffolds.remove(template);
		ionCatchers.remove(template);
		lipolyzers.remove(template);
		drexlerSaws.remove(template);
		template = null; containingModel = null;
		player.removeTemplate();
	}
	public void press(double x, double y) {
		finger.addX(x-finger.centerX()); finger.addY((10-y)-finger.centerY());
		int len = buttons.size();
		for (int i=0; i<len; i++) {
			Button button = buttons.get(i);
			int len2 = button.polygons.size();
			for (int j=0; j<len2; j++) {
				ReadOnlyPolygon gon = button.polygons.get(j);
				if (gon.isOverlapping(finger) ||
						gon.contains(finger.centerX(), finger.centerY()))
					button.press();
			}
		}
	}
	public void release(double x, double y) {
		finger.addX(x-finger.centerX()); finger.addY((10-y)-finger.centerY());
		int len = buttons.size();
		for (int i=0; i<len; i++) {
			Button button = buttons.get(i);
			int len2 = button.polygons.size();
			for (int j=0; j<len2; j++) {
				ReadOnlyPolygon gon = button.polygons.get(j);
				if (gon.isOverlapping(finger) ||
						gon.contains(finger.centerX(), finger.centerY()))
					button.release();
			}
		}
	}
	public void update(double deltaTime) {
		updateAttachable();
		updateIons(deltaTime);
		updateLipolaseProjectiles(deltaTime);
		updateThrobbers(deltaTime);
		updateThrobberDebris(deltaTime);
		updateScaffolds(deltaTime);
		updateDrexlerSaws(deltaTime);
		updateIonCatchers(deltaTime);
		updateLipolyzers(deltaTime);
		if (template!=null) {
			switch (spin) {
			case C:
				orientation-=speed*deltaTime;
				template.rotate(-speed*deltaTime);
				break;
			case CC:
				orientation+=speed*deltaTime;
				template.rotate(speed*deltaTime);
				break;
			}
		}
	}
	private void updateThrobberDebris(double deltaTime) {
		int len = throbberDebris.size();
		for (int i=0; i<len; i++) throbberDebris.get(i).update(deltaTime);
	}
	private void updateIonCatchers(double deltaTime) {
		int len = ionCatchers.size();
		for (int i=0; i<len; i++) ionCatchers.get(i).update(deltaTime);
	}
	private void updateLipolyzers(double deltaTime) {
		int len = lipolyzers.size();
		for (int i=0; i<len; i++) lipolyzers.get(i).update(deltaTime);
	}
	Vector spawnIonPosition = new Vector(); Vector spawnIonVelocity = new Vector();
	List<Ion> removeIons = new ArrayList<Ion>();
	Vector field = new Vector(); Vector spherePosition = new Vector();
	Vector previousField = new Vector();
	private void updateIons(double deltaTime) {
		int len = ions.size();
		for (int i=0; i<len; i++) {
			Ion ion = ions.get(i);
			ion.update(deltaTime);
			int len2 = ionCatchers.size();
			boolean captured = false;
			for (int j=0; j<len2; j++) {
				IonCatcher ionCatcher = ionCatchers.get(j);
				if (ionCatcher.enabled) {
					spherePosition.set(ionCatcher.spherePoint.x(),
							HEIGHT-ionCatcher.spherePoint.y());
					double intensity = 1 / spherePosition.distanceSquared(ion.position);
					field.set(spherePosition); field.add(-ion.position.x, -ion.position.y);
					if (ionCatcher.closest==null)
						ionCatcher.closest = ion;
					else {
						previousField.set(spherePosition).add(-ionCatcher.closest.position.x,
								-ionCatcher.closest.position.y);
						if (field.distance()<previousField.distance())
							ionCatcher.closest = ion;
					}
					field.normalize(); field.scale(intensity*ionCatcher.field);
					ion.velocity.add(field.x*deltaTime, field.y*deltaTime);
					if (ionCatcher.isOverlapping(rectToPolygon(ion.logicBounds)))
						captured = true;
				}
			}
			if (!captured && Collision.areOverlapping(ion.logicBounds, platformBounds))
				captured = true;
			if (captured) {
				removeIons.add(ion); ion.alive = false;
				nanograms+=ion.value;
				player.gain((int)ion.value, ion.position);
			}
			if (ion.position.x>18 || ion.position.x<-3 || ion.position.y>15
					|| ion.position.y<-5) {
				removeIons.add(ion); ion.alive = false;
			}
		}
		len = removeIons.size();
		for (int i=0; i<len; i++) ions.remove(removeIons.get(i));
		ionPool.freeAll(removeIons); removeIons.clear();
		if ((elapsedTime+=deltaTime)>ionSpawnFrequency) {
			elapsedTime = 0;
			boolean leftSide = random.nextDouble()>.5;
			spawnIonPosition.x = leftSide ? -2: 17;
			spawnIonPosition.y = random.nextDouble()*25-5;
			spawnIonVelocity.x = leftSide ? 1: -1;
			spawnIonVelocity.x*= random.nextDouble()*2 + (leftSide ? .25: -.25);
			spawnIonVelocity.y = random.nextDouble()*2*(random.nextDouble()>.5?-1:1);
			double size, value;
			int type = random.nextInt() % 11;
			if (type==0) {size=1.5; value=40;}
			else if (type>0 && type<3) {size=1; value=20;}
			else {size=.5; value=5;}
			Ion newIon = ionPool.newObject();
			newIon.bounds.width = newIon.bounds.height = size;
			newIon.logicBounds.width = newIon.logicBounds.height = size/2;
			newIon.moveTo(spawnIonPosition);
			newIon.velocity.set(spawnIonVelocity);
			newIon.value = value;
			newIon.alive = true;
			ions.add(newIon);
		}
		
	}
	private static Polygon testGon = new Polygon(new double[]{0,0,0,0}, new double[]{0,0,0,0});
	private static ReadOnlyPolygon rectToPolygon(FastRectangle rect) {
		/**return new Polygon(rect.lowerLeft.x+rect.width/2,
		 10-(rect.lowerLeft.y+rect.height/2), rect.width/2, 24);**/
		testGon.xs[0] = rect.lowerLeft.x;
		testGon.xs[1] = rect.lowerLeft.x;
		testGon.xs[2] = rect.lowerLeft.x+rect.width;
		testGon.xs[3] = rect.lowerLeft.x+rect.width;
		testGon.ys[0] = 10-rect.lowerLeft.y;
		testGon.ys[1] = 10-(rect.lowerLeft.y+rect.height);
		testGon.ys[2] = 10-(rect.lowerLeft.y+rect.height);
		testGon.ys[3] = 10-rect.lowerLeft.y;
		return testGon;
	}
	List<Lipolase> removeLipolase = new ArrayList<Lipolase>();
	private void updateLipolaseProjectiles(double deltaTime) {
		int len = lipolaseProjectiles.size();
		for (int i=0; i<len; i++) {
			Lipolase lipolase = lipolaseProjectiles.get(i);
			if (!lipolase.hit) {
				lipolase.update(deltaTime);
				for (int j=0; j<len; j++) {
					Lipolase otherLipolase = lipolaseProjectiles.get(j);
					if (!otherLipolase.hit && otherLipolase!=lipolase
							&& Collision.areOverlapping(otherLipolase.logicBounds,
							lipolase.logicBounds)) {
						player.stick();
						newVelocity.set(0, 0);
						double mass = lipolase.mass+otherLipolase.mass;
						double forceX = lipolase.mass*lipolase.velocity.x;
						double forceY = lipolase.mass*lipolase.velocity.y;
						newVelocity.add(forceX/mass, forceY/mass);
						forceX = otherLipolase.mass*otherLipolase.velocity.x;
						forceY = otherLipolase.mass*otherLipolase.velocity.y;
						newVelocity.add(forceX/mass, forceY/mass);
						otherLipolase.mass = mass;
						otherLipolase.lipolaseProjectiles.add(lipolase);
						otherLipolase.velocity.set(newVelocity);
						lipolase.hit = true;
						break;
					}
				}
			}
			boolean allDead = true;
			int len2 = lipolase.throbbers.size();
			for (int j=0; j<len2; j++) {
				Throbber throbber = lipolase.throbbers.get(j);
				if (throbber.vapor!=null) {allDead = false; break;}
			}
			if (lipolase.position.x>18 || lipolase.position.x<-3 || lipolase.position.y>15
					|| lipolase.position.y<-5 && allDead) {
				removeLipolase.add(lipolase); lipolase.alive = false;
			}
		}
		len = removeLipolase.size();
		for (int i=0; i<len; i++) lipolaseProjectiles.remove(removeLipolase.get(i));
		removeLipolase.clear();
	}
	/**static final int ATTACK = 0;
	static final int PAUSE = 1;
	int state = PAUSE;
	double throbberBatch = 1/1.3;
	double throbberBatchElapsed = 0;
	double throbberIntermission = 15;**/
	double throbberSpawnElapsed = 0; double throbberSpawnFrequency = 5;
	double throbberSpeed = .1;
	double throbberBoost1 = .15;
	double throbberBoost2 = .7;
	double fastChance = 1/4d;
	double throbberArc = Math.PI/4;
	double minSize = .5; double maxSize = 2;
	/**double maxVelocity = .3; double minVelocity = .1;
	double minSize = .5; double maxSize = 2;**/
	Vector throbberPosition = new Vector();
	Vector throbberVelocity = new Vector(); Vector newVelocity = new Vector();
	List<Throbber> removeThrobbers = new ArrayList<Throbber>();
	FastRectangle tissueBounds = new FastRectangle(0, -2, 15, 2+1/3d);
	FastRectangle platformBounds = new FastRectangle(7.5-.8, 10-8.45-.8, 1.6, 1.6);
	private void updateThrobbers(double deltaTime) {
		throbberSpawnElapsed+=deltaTime;
		if (throbberSpawnElapsed>throbberSpawnFrequency) {
			throbberVelocity.set(0,
				-throbberSpeed-throbberBoost1*random.nextDouble()
				-((random.nextDouble()<fastChance)?throbberBoost2:0));
			throbberVelocity.rotate(-throbberArc/2+throbberArc*random.nextDouble());
			/**throbberVelocity.set(random.nextDouble()*.2-.1,
					-minVelocity-random.nextDouble()*(maxVelocity-minVelocity));**/
			throbberPosition.set(random.nextDouble()*15, 12);
			double size = random.nextDouble()*(maxSize-minSize)+minSize;
			Throbber newThrobber = throbberPool.newObject();
			newThrobber.bounds.width = newThrobber.bounds.height = size;
			newThrobber.logicBounds.width = newThrobber.logicBounds.height = size/2;
			newThrobber.moveTo(throbberPosition);
			newThrobber.velocity.set(throbberVelocity);
			newThrobber.alive = true;
			newThrobber.infecting = false;
			newThrobber.hit = false;
			newThrobber.disintegrating = false;
			newThrobber.changeTime = 0;
			boolean fast = throbberVelocity.distance()>.4;
			if (fast) {
				newThrobber.smoke();
				newThrobber.vapor.frequency = size<.75?1/(size*25):1/(size*50);
				newThrobber.vapor.particleSpeed = size<.75?.25/3:.15;
				newThrobber.vapor.state = ThrobberVapor.ACTIVE;
			}
			newThrobber.spin = PI*100/((120*size)*(120*size));
			newThrobber.throbFrequency = .5/throbberVelocity.distance();
			newThrobber.mass = PI*(size/2*(size/2))*newThrobber.density;
			throbberSpawnElapsed = 0;
			throbbers.add(newThrobber);
		}
		int length = throbbers.size();
		for (int i=0; i<length; i++) {
			Throbber throbber = throbbers.get(i);
			if (!throbber.hit && !throbber.disintegrating) {
				throbber.update(deltaTime);
				if (!throbber.infecting && throbber.position.y<10-7.45
						&& Collision.areOverlapping(throbber.logicBounds, tissueBounds)
						&& (throbber.position.x<6.5 || throbber.position.x>8.5
						|| throbber.position.y<10-9.45)) {
					throbber.velocity.set(0, 0);
					throbber.infecting = true;
					throbber.spin = 0;
					throbber.extinguish();
					continue;
				}
				int lLength = lipolaseProjectiles.size();
				for (int j=0; j<lLength; j++) {
					Lipolase lipolase = lipolaseProjectiles.get(j);
					if (!lipolase.hit
							&& Collision.areOverlapping(lipolase.logicBounds,
							throbber.logicBounds)) {
						player.stick();
						newVelocity.set(0, 0);
						double mass = throbber.mass+lipolase.mass;
						double forceX = throbber.mass*throbber.velocity.x;
						double forceY = throbber.mass*throbber.velocity.y;
						newVelocity.add(forceX/mass, forceY/mass);
						forceX = lipolase.mass*lipolase.velocity.x;
						forceY = lipolase.mass*lipolase.velocity.y;
						newVelocity.add(forceX/mass, forceY/mass);
						lipolase.mass = mass;
						lipolase.throbbers.add(throbber);
						lipolase.velocity.set(newVelocity);
						throbber.hit = true;
						throbber.extinguish();
						break;
					}
				}
			}
			if (throbber.disintegrating) throbber.update(deltaTime);
			if (throbber.disintegrating && throbber.changeTime>.5 && throbber.vapor==null) {
				removeThrobbers.add(throbber); throbber.alive = false;
			}
			if (!throbber.disintegrating && (throbber.position.x>16 || throbber.position.x<-1
					|| throbber.position.y>12 || throbber.position.y<-1)) {
				throbber.disintegrating = true;
				throbber.changeTime = 0;
				throbber.extinguish();
			}
		}
		int len = removeThrobbers.size();
		for (int i=0; i<len; i++) throbbers.remove(removeThrobbers.get(i));
		throbberPool.freeAll(removeThrobbers); removeThrobbers.clear();
		/**switch (state) {
		case PAUSE:
			throbberBatchElapsed+=deltaTime;
			if (throbberBatchElapsed>throbberIntermission) {
				throbberBatch*=1.3;
				throbberBatchElapsed = 0;
				maxVelocity+=.05;
				state = ATTACK;
				for (int i=0; i<(int)throbberBatch; i++) {
					throbberVelocity.set(random.nextDouble()*.2-.1,
							-minVelocity-random.nextDouble()*(maxVelocity-minVelocity));
					Throbber newThrobber = new Throbber(random.nextDouble()*10, 12,
							throbberVelocity, random.nextDouble()*(maxSize-minSize)+minSize);
					throbbers.add(newThrobber); activeThrobbers.add(newThrobber);
				}
			}
			break;
		case ATTACK:
			if (activeThrobbers.size()==0) state=PAUSE;
		}**/
	}
	com.gdsoftworks.kludgecode.Vector reflectVector =
			new com.gdsoftworks.kludgecode.Vector(0, 0);
	Vector tempVector = new Vector();
	Polygon tempGon = null; ReadOnlyPolygon throbberGon;
	private void updateScaffolds(double deltaTime) {
		int len = scaffolds.size();
		for (int i=0; i<len; i++) {
			Scaffold scaffold = scaffolds.get(i);
			if (scaffold.enabled) {
				scaffold.update(deltaTime);
				/**for (Throbber throbber: throbbers) {
					throbberGon = rectToPolygon(throbber.logicBounds);
					if (!throbber.bumping
							&& !((tempGon=scaffold.overlappingPolygon(throbberGon))==null)) {
						/**tempVector.set(throbber.velocity.x, 10-throbber.velocity.y);
						reflectVector.speed = tempVector.distance();
						reflectVector.radians = tempVector.angle();
						reflectVector.reflect(tempGon.overlappingLine(throbberGon));
						throbber.velocity.rotate(
								-reflectVector.radians-throbber.velocity.angle());
						throbber.bumping = true;
					}
				}**/
			}
		}
	}
	private void updateDrexlerSaws(double deltaTime) {
		int len = drexlerSaws.size();
		for (int i=0; i<len; i++) {
			DrexlerSaw saw = drexlerSaws.get(i);
			if (saw.enabled) {
				saw.update(deltaTime);
				boolean cutting = false;
				if (saw.state==DrexlerSaw.SEEKING) {
					int len2 = throbbers.size();
					for (int j=0; j<len2; j++) {
						Throbber throbber = throbbers.get(j);
						if (!throbber.hit && !throbber.disintegrating
								&& Collision.rectContains(throbber.bounds,
								saw.bladeJoint.x(), 10-saw.bladeJoint.y())) {
							cutting = true;
							player.cut();
							throbber.velocity.set(0, 0);
							throbber.disintegrating = true;
							throbber.changeTime = 0;
							throbber.extinguish();
						}
						else if (!throbber.hit && throbber.changeTime<.5
								&& Collision.rectContains(throbber.bounds,
								saw.bladeJoint.x(), 10-saw.bladeJoint.y()))
							cutting = true;
					}
				}
				if (cutting && saw.vapor.frequency>100)
					saw.vapor.time = 0;
				if (cutting) saw.vapor.frequency = .0025;
				else saw.vapor.frequency = Double.MAX_VALUE;
			}
		}
	}
}
