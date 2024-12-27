package gdsoftworks.kinematics;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import gdsoftworks.geometry.Polygon;
import gdsoftworks.geometry.ReadOnlyPolygon;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.gdsoftworks.kludgecode.Vector;

/**
 * Kinematic models can translate and rotate attached {@link Polygon polygons} within their
 * translation limits and rotation limits, respectively. Other models
 * can be attached and then manipulated as well with these commands.
 * Limits are prescribed only for
 * directly attached polygons, however--attached models limit their own
 * motion. Conditions where a command to move a model can fail:
 * <ol><li>An attached model fails a test move.
 * <li>Rotating an attached polygon would hit a rotation limit.
 * <li>Translating an attached polygon would hit a translation limit.
 * <li>Any attached polygon would hit a boundary.</ol>
 * @author EDH
 * @version 1.1
 * @see Platform
 */
public class Model implements CompositePolygon{
	private double x, y;
	public double radians = 0;
	public double relativeRadians = 0;
	public double extension = 0;
	public double rotationLimit = -1;
	public double translationLimit = -1;
	private double referenceZ = 0;
	private int tier = 0;
	public final List<Model> models = new LinkedList<Model>();
	public final List<Polygon> polygons = new LinkedList<Polygon>();
	private final List<Polygon> rotationLimits = new LinkedList<Polygon>();
	private final List<Polygon> translationLimits = new LinkedList<Polygon>();
	public final Map<String, Model> modelMap = new HashMap<String, Model>(10);
	/**
	 * Constructs a deep copy of the specified model. Expected equivalence between
	 * the references of the named models in the new model map and
	 * the references of all new associated descendant models is preserved.
	 * @param other the model to be copied
	 * @see Model#modelMap()
	 */
	protected Model(final Model other) {
		this(other.x, other.y);
		polygons.addAll(new LinkedList<Polygon>(){{
			for (Polygon gon: other.polygons) add(gon.copy());
			}});
		models.addAll(new LinkedList<Model>(){{
			for (Model joint: other.models) {
				Model newJoint = joint.copyAndMap(other.modelMap, modelMap);
				for (Entry<String, Model> entry: other.modelMap.entrySet())
					if (joint==entry.getValue())
						modelMap.put(entry.getKey(), newJoint);
				add(newJoint);
			}}});
		rotationLimits.addAll(new LinkedList<Polygon>(){{
			for (Polygon limit: other.rotationLimits) add(limit.copy());
			}});
		translationLimits.addAll(new LinkedList<Polygon>(){{
			for (Polygon limit: other.translationLimits) add(limit.copy());
			}});
		referenceZ = other.referenceZ; tier = other.tier;
		radians = other.radians; relativeRadians = other.relativeRadians;
		extension = other.extension;
		rotationLimit = other.rotationLimit; translationLimit = other.translationLimit;
	}
	/**
	 * Constructs a new model with its joint at the specified coordinate,
	 * with a default angle of zero radians. The angle determines the
	 * direction of {@link Model#translate(double) translation} commands.
	 * @param x the abscissa of the coordinate
	 * @param y the ordinate of the coordinate
	 */
	public Model(double x, double y) {this.x=x; this.y=y;}
	/**
	 * Constructs a model with a joint {@link Model#currentAngle() angle} of theta at
	 * the specified coordinate.
	 * @param x the joint's x-value
	 * @param y the joint's y-value
	 * @param theta starting angle of joint
	 */
	public Model(double x, double y, double theta) {
		this(x,y); radians = theta; relativeRadians = theta;
	}
	/**
	 * Translates the model's attached {@link Model models}, {@link Polygon polygons},
	 * and rotation limits in the direction of the model's current angle by s. Positive s will
	 * push them out, negative s will push them back in.
	 * @param s the amount to translate
	 * @return move success
	 * @see Model#rotate(double)
	 */
	Vector tVector = new Vector(0,0);
	double tX, tY;
	public boolean translate(double s) {
		if (s==0) return true;
		if (translationLimit!=-1 && (extension+s>translationLimit || extension+s<0))
			return false;
		extension+=s;
		tVector.speed = s; tVector.radians = radians;
		tX = tVector.dx(); tY = tVector.dy();
		int len = polygons.size();
		for (int i=0; i<len; i++) {
			Polygon gon = polygons.get(i);
			gon.addX(tX).addY(tY);
		}
		len = models.size();
		for (int i=0; i<len; i++) {
			Model joint = models.get(i);
			joint.deepMoveX(tX); joint.deepMoveY(tY);
		}
		this.x+=tX; this.y+=tY;
		return true;
	}
	/**
	 * Convenience method for resetting the joint's x-position. Typically
	 * paired with {@link Model#setY(double) setY}.
	 * @param x the new x-value of the joint
	 * @return move success
	 */
	double moveX;
	public boolean setX(double x) {
		if (x==0) return true;
		moveX = x-this.x;
		
		int len = polygons.size();
		for (int i=0; i<len; i++) {Polygon gon = polygons.get(i); gon.addX(moveX);}
		len = models.size();
		for (int i=0; i<len; i++) {Model joint = models.get(i); joint.deepMoveX(moveX);}
		this.x+=moveX;
		return true;
		}
	/**
	 * Convenience method for resetting the joint's y-position. Typically
	 * paired with {@link Model#setX(double) setX}.
	 * @param y the new y-value of the joint
	 * @return move success
	 */
	double moveY;
	public boolean setY(double y) {
		if (y==0) return true;
		moveY = y-this.y;

		int len = polygons.size();
		for (int i=0; i<len; i++) {Polygon gon = polygons.get(i); gon.addY(moveY);}
		len = models.size();
		for (int i=0; i<len; i++) {Model joint = models.get(i); joint.deepMoveY(moveY);}
		this.y+=moveY;
		return true;
	}
	protected boolean deepMoveX(double x) {
		this.x+=x;
		int len = polygons.size();
		for (int i=0; i<len; i++) {Polygon gon = polygons.get(i); gon.addX(x);}
		len = models.size();
		for (int i=0; i<len; i++) {
			Model joint = models.get(i);
			if (!joint.deepMoveX(x)) return false;
		}
		return true;
	}
	protected boolean deepMoveY(double y) {
		this.y+=y;
		int len = polygons.size();
		for (int i=0; i<len; i++) {Polygon gon = polygons.get(i); gon.addY(y);}
		len = models.size();
		for (int i=0; i<len; i++) {
			Model joint = models.get(i);
			if (!joint.deepMoveY(y)) return false;
		}
		return true;
	}
	//***********
	/**
	 * Returns current angle of joint
	 * @return angle joint angle in radians
	 * @see Model#translate(double)
	 */
	public double currentAngle() {return radians;}
	/**
	 * Returns joint x
	 * @return x-value of this model's joint
	 */
	public double x() {return x;}
	/**
	 * Returns joint y
	 * @return y-value of this model's joint
	 */
	public double y() {return y;}
	/**
	 * References to attached models of arbitrary generation can be stored
	 * in the model map. The references of all attached models of every generation must be saved
	 * for any model intended to be written to file. Crucially, saving
	 * references allows for their associated models to be animated.
	 * @return map of this model's named attached models
	 * @see Model#Model(Model)
	 */
	public Map<String, Model> modelMap() {
		return modelMap;
	}
	/**
	 * Attaches the specified {@link Model model}. Attempting to attach a model to
	 * itself will throw a {@link SelfReferenceException
	 * self-reference exception}.
	 * @param model the model to attach
	 * @return this 
	 * @throws SelfReferenceException if the reference of this model and
	 * the specified model are equivalent
	 */
	public Model add(Model joint) {
		if (joint==this) throw new SelfReferenceException(joint);
		joint.addTier(tier);
		models.add(joint);
		return this;
	}
	/**
	 * Removes and discards the specified model from this model.
	 * If the model is not found in the first generation, removal of the
	 * specified model is attempted recursively.
	 * @param model the model to be removed
	 * @return this
	 */
	public Model remove(Object model) {
		 if (!models.remove(model)) {
			 int len = models.size();
			 for (int i=0; i<len; i++)
				 models.get(i).remove(model);
		 }
		return this;
	}
	/**
	 * Returns a copy of the list of models directly attached to this model
	 * for direct manipulation. Models can only be attached or removed
	 * by calling {@link Model#add(Model) add(Model)} or {@link Model#remove(Object)
	 * remove(Object)}.
	 * @return list of attached models
	 */
	public List<Model> models() {return new LinkedList<Model>(models);}
	/**
	 * Returns the private list of attached {@link Polygon polygons}.
	 * To add or remove polygons from this model, add or remove them from the returned list.
	 * @return list of attached polygons
	 */
	public List<Polygon> attachedPolygons() {return polygons;}
	/**
	 * Returns the private list of {@link Polygon polygons} composing the
	 * logical rotation limits of this model's joint. If any attached polygons
	 * would overlap a polygon
	 * in this list, the rotate command fails. To add or remove limits, add or remove
	 * them from the returned list.
	 * @return list of rotation limits
	 * @see Model#translationLimits()
	 */
	public List<Polygon> rotationLimits() {return rotationLimits;}
	/**
	 * Returns the private list of {@link Polygon polygons} composing the
	 * logical translation limits of this model's joint. If any attached polygons
	 * would overlap a polygon
	 * in this list, the {@link Model#translate(double) translate} command fails.
	 * To add or remove a limits, add or remove it from this list.
	 * @return list of translation limits
	 * @see Model#rotationLimits()
	 */
	public List<Polygon> translationLimits() {return translationLimits;}
	List<ReadOnlyPolygon> gons = new LinkedList<ReadOnlyPolygon>();
	public List<ReadOnlyPolygon> getPolygons(){
		gons = new LinkedList<ReadOnlyPolygon>();
		gons.addAll(polygons); gons.addAll(rotationLimits);
		gons.addAll(translationLimits);
		for (Model joint: models) gons.addAll(joint.getPolygons());
		return gons;
	}
	/**
	 * Attempts to rotate this model's attached {@link Polygon polygons},
	 * {@link Model joints}, and translation limits about the joint.
	 * Increases the joint's angle by the specified radians. The rotation will fail
	 * if any attached polygons would overlap a rotation limit or boundary.
	 * It will also fail if any attached models would fail their own rotation.
	 * @param radians the amount by which to rotate
	 * @return move success
	 * @see Model#translate(double)
	 */
	public boolean rotate(double radians) {
		if (radians==0) return true;
		if (rotationLimit!=-1 && (relativeRadians+radians>rotationLimit
				|| relativeRadians+radians<0))
			return false;
		relativeRadians+=radians;
		this.radians += radians;
		int len = polygons.size();
		for (int i=0; i<len; i++) {
			Polygon gon=polygons.get(i);
			gon.rotate(this.x, this.y, radians);
		}
		len = models.size();
		for (int i=0; i<len; i++) {
			Model joint = models.get(i);
			joint.deepRotate(this.x, this.y, radians);
		}
		return true;
	}
	protected boolean deepRotate(double aboutX, double aboutY, double radians) {
		this.radians += radians;
		rotateCenter(aboutX, aboutY, radians);
		int len = polygons.size();
		for (int i=0; i<len; i++) {
			Polygon gon = polygons.get(i);
			gon.rotate(aboutX, aboutY, radians);
		}
		len = models.size();
		for (int i=0; i<len; i++) {
			Model joint = models.get(i);
			if (!joint.deepRotate(aboutX, aboutY, radians)) return false;
		}
		return true;
	}
	double distanceFactor, unitX, unitY, theta;
	private void rotateCenter(double aboutX, double aboutY, double radians) {
		distanceFactor = sqrt(square(abs(aboutX-this.x))
				+ square(abs(aboutY-this.y)));
		if (distanceFactor==0) return;
		unitX = (this.x-aboutX)/distanceFactor;
		unitY = -(this.y-aboutY)/distanceFactor;
		
		theta = unitY<0? 2*PI-acos(unitX): acos(unitX);
		this.x = cos(theta+radians)*distanceFactor+aboutX;
		this.y = -sin(theta+radians)*distanceFactor+aboutY;
	}
	private static double square(double base) {return pow(base, 2d);}
	/**
	 * Scales this model and all components in place by the specified factor.
	 * Attempted change will fail if any attached models would
	 * fail their own {@link Model#deepScale(double, double, double) deep-scale}
	 * or any attached polygons would strike a boundary.
	 * @param factor the factor by which to scale
	 * @return scale success
	 * @see Model#boundaries()
	 * @see Polygon#scale(double, double, double)
	 */
	public boolean scale(double factor) {
		if (factor==1) return true;
		double centerX = this.x; double centerY = this.y;
		
		int len = polygons.size();
		for (int i=0; i<len; i++) {
			Polygon gon = polygons.get(i);
			gon.scale(centerX, centerY, factor);
		}
		len = models.size();
		for (int i=0; i<len; i++) {
			Model joint = models.get(i);
			joint.deepScale(centerX, centerY, factor);
		}
		return true;
	}
	protected boolean deepScale(double originX, double originY, double factor) {
		scaleCenter(originX, originY, factor);
		int len = polygons.size();
		for (int i=0; i<len; i++) {
			Polygon gon = polygons.get(i);
			gon.scale(originX, originY, factor);
		}
		len = models.size();
		for (int i=0; i<len; i++) {
			Model joint = models.get(i);
			if (!joint.deepScale(originX, originY, factor))
				return false;
		}
		return true;
	}
	private void scaleCenter(double originX, double originY, double factor) {
		double distanceFactor = sqrt(square(abs(originX-x))
				+ square(abs(originY-y)));
		if (distanceFactor==0) return;
		double unitX = (x-originX)/distanceFactor;
		double unitY = -(y-originY)/distanceFactor;
		
		distanceFactor*=factor;
		double theta = unitY<0? 2*PI-acos(unitX): acos(unitX);
		x = cos(theta)*distanceFactor+originX;
		y = -sin(theta)*distanceFactor+originY;
	}
	/**
	 * Sets this joint's reference {@link Polygon#z() z} along with all component z's,
	 * preserving the relative difference between z's of attached polygons in
	 * each component model.
	 * @param z
	 * @return this
	 */
	public Model setReferenceZ(double z) {
		for (Polygon gon: polygons) gon.z(z+(gon.z()-referenceZ));
		for (Model joint: models) joint.setReferenceZ(z);
		
		referenceZ = z;
		return this;
	}
	protected Model addTier(int tier) {
		this.tier+=(tier+1);
		int len = models.size();
		for (int i=0; i<len; i++) {
			models.get(i).addTier(tier);
		}
		return this;
	}
	/**
	 * Returns the generation of this model. If it has not been attached to any other
	 * model, it is generation zero by default.
	 * @return this model's generation
	 */
	public int tier() {return tier;}
	/**
	 * Modifies the {@link Polygon#z() z} of all polygons descendant from this model
	 * by the specified value. Useful for raising or lowering the displayed model
	 * in its entirety.
	 * @param z the reference z by which to raise this model
	 * @return this
	 */
	public Model addReferenceZ(double z) {
		referenceZ+=z;
		for (Polygon gon: polygons) gon.z(gon.z()+z);
		for (Model joint: models) joint.addReferenceZ(z);
		return this;
	}
	/**
	 * Returns the suggested reference {@link Polygon#z(double) z} for this model's polygons.
	 * @return this model's reference z
	 * @see Model#setReferenceZ(double)
	 * @see Model#addReferenceZ(double)
	 */
	public double referenceZ() {return referenceZ;}
	/**
	 * Tests all polygons of descendant models for
	 * {@link Polygon#isOverlapping(ReadOnlyPolygon) overlap} with the specified polygon
	 * @param gon the polygon with which to test overlap
	 * @return overlap
	 */
	public boolean isOverlapping(ReadOnlyPolygon gon) {
		int len = polygons.size();
		for (int i=0; i<len; i++) {
			if (gon.isOverlapping(polygons.get(i)))
				return true;
		}
		len = models.size();
		for (int i=0; i<len; i++) {
			if (models.get(i).isOverlapping(gon))
				return true;
		}
		return false;
	}
	/**
	 * Returns the model with the highest {@link Model#referenceZ() reference z}
	 * that possesses a polygon overlapping with the specified {@link Polygon polygon}.
	 * @param gon the polygon with which to test for overlap
	 * @return the highest-z overlapping model, or null if none overlap
	 */
	public Model overlappingModel(ReadOnlyPolygon gon) {
		Model highestPart = null;
		int len = polygons.size();
		for (int i=0; i<len; i++) {
			if (gon.isOverlapping(polygons.get(i)) &&
					(highestPart==null || tier>highestPart.tier))
				highestPart = this;
		}
		len = models.size();
		for (int i=0; i<len; i++) {
			Model part = models.get(i).overlappingModel(gon);
			if (part!=null &&
					(highestPart==null || part.tier>highestPart.tier))
				highestPart = part;
		}
		return highestPart;
	}
	public Polygon overlappingPolygon(ReadOnlyPolygon gon) {
		int len = polygons.size();
		for (int i=0; i<len; i++) {
			Polygon attachedGon = polygons.get(i);
			if (gon.isOverlapping(attachedGon))
				return attachedGon;
		}
		len = models.size();
		for (int i=0; i<len; i++) {
			Model joint = models.get(i);
			if (joint.overlappingPolygon(gon)!=null)
				return joint.overlappingPolygon(gon);
		}
		return null;
	}
	/**
	 * Checks for a positive result on a call to any descendant polygon's {@link
	 * Polygon#contains(double, double) contains method} with the specified point.
	 * @param x the x-value of the specified point
	 * @param y the y-value of the specified point
	 * @return true if any of this model's polygons contain specified point, false otherwise
	 */
	public boolean contains(double x, double y) {
		int len = polygons.size();
		for (int i=0; i<len; i++) {
			if (polygons.get(i).contains(x, y))
				return true;
		}
		len = models.size();
		for (int i=0; i<len; i++) {
			if (models.get(i).contains(x, y))
				return true;
		}
		return false;
	}
	public Model containingModel(double x, double y) {
		Model highestPart = null;
		int len = polygons.size();
		for (int i=0; i<len; i++) {
			if (polygons.get(i).contains(x, y) &&
					(highestPart==null || tier>highestPart.tier))
				highestPart = this;
		}
		len = models.size();
		for (int i=0; i<len; i++) {
			Model part = models.get(i).containingModel(x, y);
			if (part!=null &&
					(highestPart==null || part.tier>highestPart.tier))
				highestPart = part;
		}
		return highestPart;
	}
	//************
	public Model reflectYAxis(double x) {
		this.x += 2*(x-this.x);
		for (Polygon gon: new LinkedList<Polygon>(polygons){{
			addAll(rotationLimits); addAll(translationLimits);}})
			gon.reflectYAxis(x);
		for (Model joint: models) joint.reflectYAxis(x);
		return this;
	}
	protected <K> Model copyAndMap(final Map<K, Model> oldMap, final Map<K, Model> newMap) {
		Model copy = new Model(x, y);
		copy.polygons.addAll(new LinkedList<Polygon>(){{
			for (Polygon gon: polygons) add(gon.copy());
			}});
		copy.models.addAll(new LinkedList<Model>(){{
			for (Model joint: models) {
				Model newJoint = joint.copyAndMap(oldMap, newMap);
				for (Entry<K, Model> entry: oldMap.entrySet())
					if (joint==entry.getValue())
						newMap.put(entry.getKey(), newJoint);
				add(newJoint);
			}}});
		copy.rotationLimits.addAll(new LinkedList<Polygon>(){{
			for (Polygon limit: rotationLimits) add(limit.copy());
			}});
		copy.translationLimits.addAll(new LinkedList<Polygon>(){{
			for (Polygon limit: translationLimits) add(limit.copy());
			}});
		copy.referenceZ = referenceZ; copy.tier = tier;
		copy.radians = radians;
		return copy;
	}
	public Model copy() {
		final Model copy = new Model(x, y);
		copy.polygons.addAll(new LinkedList<Polygon>(){{
			for (Polygon gon: polygons) add(gon.copy());
			}});
		copy.models.addAll(new LinkedList<Model>(){{
			for (Model joint: models) {
				Model newJoint = joint.copyAndMap(modelMap, copy.modelMap);
				for (Entry<String, Model> entry: modelMap.entrySet())
					if (joint==entry.getValue())
						copy.modelMap.put(entry.getKey(), newJoint);
				add(newJoint);
			}}});
		copy.rotationLimits.addAll(new LinkedList<Polygon>(){{
			for (Polygon limit: rotationLimits) add(limit.copy());
			}});
		copy.translationLimits.addAll(new LinkedList<Polygon>(){{
			for (Polygon limit: translationLimits) add(limit.copy());
			}});
		copy.referenceZ = referenceZ; copy.radians = radians;
		copy.tier = tier;
		copy.relativeRadians = relativeRadians;
		copy.extension = extension;
		copy.rotationLimit = rotationLimit; copy.translationLimit = translationLimit;
		return copy;
	}
	public String toString() {
		return "x: "+this.x+" y: "+this.y+" radians: "+radians+" tier: "+tier;
	}
}
