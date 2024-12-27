package gdsoftworks.kinematics;

import gdsoftworks.geometry.FillColor;
import gdsoftworks.geometry.Polygon;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
/**
 * Applications can create and edit {@link Model model}-encoding files readable by
 * this class.
 * 
 * An instance of reader will cache models as they are loaded. Calls
 * to {@link Reader#getModel(String) getModel(String)} will return a copy
 * of the cached model.
 * @author EDH
 * @version 1.1
 * @see Writer
 */
public class Reader {
	private final Map<String, Model> modelMap = new HashMap<String, Model>(16);
	public Reader(){}
	public Reader(InputStream... ins) throws IOException {
		loadModels(ins);
	}
	public void loadModels(InputStream... ins) throws IOException {
		for (InputStream in: ins) loadModel(in);
	}
	/**
	 * Uploads a model from the specified file to this reader's map. The key
	 * is the name of the model written to file, recommended to be
	 * the same as the file name. A new copy of the model can be retrieved
	 * with the call {@link Reader#getModel(String) getModel(String)}.
	 * @param in the file from which to upload a model
	 */
	public String loadModel(InputStream in) throws IOException {
		short totalModels; int currentModels = 0;
		String currentName = ""; Model currentModel = null;
		Map<String, Model> newModelMap = new HashMap<String, Model>(16);
		DataInputStream stream = new DataInputStream(in);
		
		try {
			for (totalModels=stream.readShort(); currentModels<totalModels; currentModels++) {
				currentName = readString(stream);
				currentModel = new Model(stream.readDouble(), stream.readDouble());
				
				for (int i=0, totalPolygons=stream.readShort(); i<totalPolygons; i++)
					currentModel.attachedPolygons().add(readPolygon(stream));
				for (int i=0, totalPolygons=stream.readShort(); i<totalPolygons; i++)
					currentModel.translationLimits().add(readPolygon(stream));
				for (int i=0, totalPolygons=stream.readShort(); i<totalPolygons; i++)
					currentModel.rotationLimits().add(readPolygon(stream));
				for (int i=0, localTotalModels=stream.readShort(); i<localTotalModels; i++)
					currentModel.add(newModelMap.get(readString(stream)));
				
				newModelMap.put(currentName, currentModel);
			}
			for (Entry<String, Model> entry: newModelMap.entrySet())
				currentModel.modelMap().put(entry.getKey(), entry.getValue());
			modelMap.put(currentName, currentModel);
		}
		finally {stream.close();} return currentName;
	}
	private static String readString(DataInputStream stream) throws IOException {
		String string = "";
		for (int i=0, chars = stream.readShort(); i<chars; i++)
			string+=stream.readChar();
		return string;
	}
	private static Polygon readPolygon(DataInputStream stream) throws IOException {
		double[] polygonXs; double[] polygonYs;
		
		polygonXs = new double[stream.readShort()];
		polygonYs = new double[polygonXs.length];
		for (int i=0; i<polygonXs.length; i++) 
			polygonXs[i] = stream.readDouble();
		for (int i=0; i<polygonYs.length; i++)
			polygonYs[i] = stream.readDouble();
		
		Polygon gon = new Polygon(polygonXs, polygonYs);
		gon.setColor(new FillColor(gon.red=stream.readDouble(), gon.green=stream.readDouble(),
				gon.blue=stream.readDouble(), gon.alpha=stream.readDouble())).z(
				stream.readDouble()).setGraphics(stream.readBoolean());
		return gon;
	}
	/**
	 * Retrieves and returns a new instance of the model associated with the
	 * specified key. The model must have been previously uploaded to this reader with
	 * a call to {@link Reader#loadModel(InputStream) loadModel(File)}.
	 * @param key the specified key
	 * @return a new model corresponding to the specified key
	 */
	public Model getModel(String key) {return modelMap.get(key).copy();}
}