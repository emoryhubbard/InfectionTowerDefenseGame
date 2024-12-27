package com.gdsoftworks.meditor;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ModelChooser extends JFileChooser {
	public static final String SAVE_DIRECTORY =
			"C:/Java Stuff/Applications/Desktop/Model Writer/Save";
	public ModelChooser() {
        super(SAVE_DIRECTORY);
        setAcceptAllFileFilterUsed(false);
        addChoosableFileFilter(
          new FileNameExtensionFilter("Model Files (.mdl)", "mdl"));
	}
}
