package gdsoftworks.kinematics;

import java.util.ArrayList;
import java.util.List;

public class SpatialHashGrid {
	int cellsPerRow, cellsPerCol;
	double cellSize;
	int[] IDs = new int[4];
	List<StaticObject> foundObjects;
	List<StaticObject>[] dynamicCells;
	List<StaticObject>[] staticCells;
	@SuppressWarnings("unchecked")
	public SpatialHashGrid(double worldWidth, double worldHeight, double cellSize) {
		this.cellSize = cellSize;
		this.cellsPerRow = (int)Math.ceil(worldWidth/cellSize);
		this.cellsPerCol = (int)Math.ceil(worldHeight/cellSize);
		int cells = cellsPerRow*cellsPerCol;
		dynamicCells = new List[cells]; staticCells = new List[cells];
		for (int i=0; i<cells; i++) {
			dynamicCells[i] = new ArrayList<StaticObject>(100);
			staticCells[i] = new ArrayList<StaticObject>(100);
		}
		foundObjects = new ArrayList<StaticObject>(100);
	}
	public void insertStaticObject(StaticObject obj) {
		int[] IDs = getCellIDs(obj);
		int i = 0; int ID = -1;
		while (i<=3 && (ID=IDs[i++])!=-1) staticCells[ID].add(obj);
	}
	public void insertDynamicObject(StaticObject obj) {
		int[] IDs = getCellIDs(obj);
		int i=0; int ID = -1;
		while (i<=3 && (ID=IDs[i++])!=-1) dynamicCells[ID].add(obj);
	}
	public void removeObject(StaticObject obj) {
		int[] IDs = getCellIDs(obj);
		int i=0; int ID = -1;
		while (i<=3 && (ID=IDs[i++])!=-1) {
			dynamicCells[ID].remove(obj);
			staticCells[ID].remove(obj);
		}
	}
	public void clearDynamicCells(StaticObject obj) {
		int len = dynamicCells.length;
		for (int i=0; i<len; i++) dynamicCells[i].clear();
	}
	public List<StaticObject> getPotentialColliders(StaticObject obj) {
		foundObjects.clear(); int[] IDs = getCellIDs(obj);
		int i=0; int ID = -1;
		while (i<=3 && (ID=IDs[i++])!=-1) {
			int len = dynamicCells[ID].size();
			for (int j=0; j<len; j++) {
				StaticObject collider = dynamicCells[ID].get(j);
				if (!foundObjects.contains(collider))
					foundObjects.add(collider);
			}
			len = staticCells[ID].size();
			for (int j=0; j<len; j++) {
				StaticObject collider = staticCells[ID].get(j);
				if (!foundObjects.contains(collider)) foundObjects.add(collider);
			}
		}
		return foundObjects;
	}
	public int[] getCellIDs(StaticObject obj) {
		int x1 = (int)Math.floor(obj.bounds.lowerLeft.x/cellSize);
		int y1 = (int)Math.floor(obj.bounds.lowerLeft.y/cellSize);
		int x2 = (int)Math.floor((obj.bounds.lowerLeft.x+obj.bounds.width)/cellSize);
		int y2 = (int)Math.floor((obj.bounds.lowerLeft.y+obj.bounds.height)/cellSize);
		
		if (x1==x2 && y1==y2) {
			if (x1>=0 && x1<cellsPerRow && y1>=0 && y1<cellsPerCol)
				IDs[0] = x1+y1*cellsPerRow;
			else IDs[0] = -1;
			IDs[1] = -1;
			IDs[2] = -1;
			IDs[3] = -1;
		}
		else if (x1==x2) {
			int i=0;
			if (x1>=0 && x1<cellsPerRow) {
				if (y1>=0 && y1<cellsPerCol) IDs[i++] = x1+y1*cellsPerRow;
				if (y2>=0 && y2<cellsPerCol) IDs[i++] = x1+y2*cellsPerRow;
			}
			while(i<=3) IDs[i++] = -1;
		}
		else if (y1==y2) {
			int i=0;
			if (y1>=0 && y1<cellsPerCol) {
				if (x1>0 && x1<cellsPerRow)
					IDs[i++] = x1+y1*cellsPerRow;
				if (x2>=0 && x2<cellsPerRow)
					IDs[i++] = x2+y1*cellsPerRow;
			}
			while (i<=3) IDs[i++] = -1;
		}
		else {
			int i=0;
			int y1CellsPerRow = y1*cellsPerRow;
			int y2CellsPerRow = y2*cellsPerRow;
			if (x1>=0 && x1<cellsPerRow && y1>=0 && y1<cellsPerCol)
				IDs[i++] = x1+y1CellsPerRow;
			if (x2>=0 && x2<cellsPerRow && y1>=0 && y1<cellsPerCol)
				IDs[i++] = x2+y1CellsPerRow;
			if (x2>=0 && x2<cellsPerRow && y2>=0 && y2<cellsPerCol)
				IDs[i++] = x2+y2CellsPerRow;
			if (x1>=0 && x1<cellsPerRow && y2>=0 && y2<cellsPerCol)
				IDs[i++] = x1+y2CellsPerRow;
			while (i<=3) IDs[i++] = -1;
			
		}
		return IDs;
	}
}
