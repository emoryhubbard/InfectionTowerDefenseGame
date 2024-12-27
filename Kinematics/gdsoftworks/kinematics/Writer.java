package gdsoftworks.kinematics;

import gdsoftworks.geometry.ReadOnlyColor;
import gdsoftworks.geometry.ReadOnlyPolygon;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 * {@link Model Models} can be written to file using this class.
 * For a model to write properly, all attached models and descendant models
 * must have their references stored in the {@link Model#modelMap() model map}
 * of the model to be written.
 * @author EDH
 * @version 1.1
 * @see Reader
 */
public class Writer {
	private final DataOutputStream dos;
	/**
	 * Constructs a writer ready to write to the specified file.
	 * A {@link DataOutputStream data-output stream} remains open until closed with a call to
	 * {@link Writer#close() close()}.
	 * @param out the file to which this model will write
	 */
	public Writer(OutputStream out) {dos = new DataOutputStream(out);}
	/**
	 * Overwrites this file with the specified model, and assigns it the
	 * specified name. This name is the key used by {@link Reader#getModel(String)
	 * getModel(String)}
	 * to retrieve the model. File names should generally be identical to these keys.
	 * @param model the model to be written
	 * @param name the name encoded for the specified model
	 * @throws IOException
	 * @see {@link Writer#close()}
	 */
	public void writeModel(Model model, String name) throws IOException {
		dos.writeShort(model.modelMap().size()+1);
		
		Model highestTier; Model mappedJoint; String jointName = null;
		List<Model> writtenJoints = new LinkedList<Model>();
		for (int i=0, joints = model.modelMap().size(); i<joints; i++) {
			highestTier = null;
			for (Entry<String, Model> entry: model.modelMap().entrySet()) {
				mappedJoint = entry.getValue();
				if (!writtenJoints.contains(mappedJoint)
						&& (highestTier==null || mappedJoint.tier()>highestTier.tier())) {
					highestTier = mappedJoint;
					jointName = entry.getKey();
				}
			}
			writeString(jointName);
			writeSingleJoint(highestTier, model.modelMap());
			writtenJoints.add(highestTier);
		}
		
		writeString(name); writeSingleJoint(model, model.modelMap());
	}
	private void writeString(String string) throws IOException {
		dos.writeShort(string.length());
		for (int i=0, totalChars=string.length(); i<totalChars; i++) {
			dos.writeChar(string.charAt(i));
		}
	}
	private void writeSingleJoint(Model joint, Map<String, Model> jointMap) throws IOException {
		dos.writeDouble(joint.x());
		dos.writeDouble(joint.y());
		
		dos.writeShort(joint.attachedPolygons().size());
		for (int i=0, totalGons=joint.attachedPolygons().size(); i<totalGons; i++)
			writePolygon(joint.attachedPolygons().get(i));
		dos.writeShort(joint.translationLimits().size());
		for (int i=0, totalGons=joint.translationLimits().size(); i<totalGons; i++)
			writePolygon(joint.translationLimits().get(i));
		dos.writeShort(joint.rotationLimits().size());
		for (int i=0, totalGons=joint.rotationLimits().size(); i<totalGons; i++)
			writePolygon(joint.rotationLimits().get(i));
		dos.writeShort(joint.models().size());
		for (Model j: joint.models())
			for (Entry<String, Model> entry: jointMap.entrySet())
				if (j==entry.getValue())
					writeString(entry.getKey());
	}
	private void writePolygon(ReadOnlyPolygon gon) throws IOException {
		dos.writeShort(gon.points());
		for (double d: gon.abscissae()) dos.writeDouble(d);
		for (double d: gon.ordinates()) dos.writeDouble(d);
	
		ReadOnlyColor gonColor = gon.getColor();
		dos.writeDouble(gonColor.red()); dos.writeDouble(gonColor.green());
		dos.writeDouble(gonColor.blue()); dos.writeDouble(gonColor.alpha());
		
		dos.writeDouble(gon.z()); dos.writeBoolean(gon.isGraphics());
	}
	/**
	 * Releases the {@link DataOutputStream data output stream} used by this writer.
	 * Should always be called after the writer is finished.
	 * @throws IOException
	 */
	public void close() throws IOException {dos.close();}
}
