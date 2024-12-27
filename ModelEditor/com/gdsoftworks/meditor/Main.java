package com.gdsoftworks.meditor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import gdsoftworks.geometry.FillColor;
import gdsoftworks.geometry.Polygon;
import gdsoftworks.geometry.ReadOnlyColor;
import gdsoftworks.geometry.ReadOnlyPolygon;
import gdsoftworks.geometry.ZComparator;
import gdsoftworks.kinematics.Model;
import gdsoftworks.kinematics.Reader;

public class Main extends JFrame {
	private Model currentModel = null;
	private Model selectedModel = null;
	private Polygon selectedPolygon = null;
	
	private boolean modelSelected = true;
	private boolean creatingNewPolygon = false;
	private List<Double> xs;
	private List<Double> ys;
	
	private ExecutorService pool = Executors.newCachedThreadPool();
	
	private double cursorX; private double cursorY;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	
	public Main(Model model) {
		this();
		currentModel = model;
		currentModel.setX(WIDTH/2); currentModel.setY(HEIGHT/2);
	}
	public Main() {
		super("Model Editor");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		add(new JPanel(){
			{
				addMouseListener(new MouseAdapter(){
					public void mousePressed(MouseEvent me) {
						if (modelSelected && !creatingNewPolygon) {
							Model highestModel = null;
							if (currentModel!=null)
								for (Model model: new LinkedList<Model>(
										currentModel.modelMap().values()){{add(currentModel);}})
									if (model.contains(me.getX(), me.getY())
											&& (highestModel==null ||
											model.tier()>highestModel.tier()))
											highestModel = model;
							selectedModel=highestModel;
						}
						else if (!creatingNewPolygon) {
							Polygon highestPolygon = selectedPolygon;
							Model highestModel = selectedModel;
							if (currentModel!=null)
								for (final Model model: new LinkedList<Model>(
										currentModel.modelMap().values()){{add(currentModel);}})
									for (Polygon gon: new LinkedList<Polygon>(
											model.attachedPolygons()){{addAll(
											model.rotationLimits());
											addAll(model.translationLimits());}})
										if (gon.contains(me.getX(), me.getY())
												&& (highestPolygon==null ||
												gon.z()>highestPolygon.z())) {
												highestPolygon = gon;
												highestModel = model;
										}
							selectedPolygon = highestPolygon;
							selectedModel = highestModel;
						}
						else {xs.add((double)me.getX()); ys.add((double)me.getY());}
					}
				});
				addMouseMotionListener(new MouseMotionAdapter(){
					public void mouseDragged(MouseEvent me) {
						if (modelSelected) {
							if (selectedModel!=null) {
								selectedModel.setX(me.getX()); selectedModel.setY(me.getY());
							}}
						else
							if (selectedModel!=null && selectedPolygon!=null) {
								selectedPolygon.addX(me.getX()-selectedPolygon.centerX());
								selectedPolygon.addY(me.getY()-selectedPolygon.centerY());
							}
						cursorX = me.getX(); cursorY = me.getY();
					}
				});
				addMouseMotionListener(new MouseMotionAdapter(){
					public void mouseMoved(MouseEvent me) {
						cursorX = me.getX(); cursorY = me.getY();
					}
				});
			}
			public Dimension getPreferredSize(){return new Dimension(800,600);}
			public void paintComponent(Graphics g) {
				LinkedList<ReadOnlyPolygon> polygons = new LinkedList<ReadOnlyPolygon>();
				if (currentModel!=null) polygons.addAll(currentModel.getPolygons());
				Collections.sort(polygons, new ZComparator());
				
				for (ReadOnlyPolygon gon: polygons) paintGon((Graphics2D) g, gon);
				paintGon((Graphics2D) g, selectedPolygon);
				paintGon((Graphics2D) g,
						new Polygon(cursorX, cursorY, 3, 12).setColor(
						new FillColor(0,0,1,.5)));
			}
		});
		
		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ModelChooser chooser = new ModelChooser();
				if (chooser.showOpenDialog(Main.this)==chooser.APPROVE_OPTION) {
					try {
					currentModel = new Reader(new FileInputStream(chooser.getSelectedFile())).getModel(
							chooser.getSelectedFile().getName().substring(0,
							chooser.getSelectedFile().getName().length()-4));
					currentModel.setX(WIDTH/2); currentModel.setY(HEIGHT/2);
					repaint();
					}
					catch(IOException ioe) {
						JOptionPane.showMessageDialog(Main.this,
								"IO Exception: "+ioe.getMessage());
					}
				}
			}
		});
		
		final JMenu file = new JMenu("File"); file.add(open);

		final JMenuItem editColor = new JMenuItem("Edit Color");
		editColor.setEnabled(false);
		editColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (selectedPolygon!=null) {
					Color newColor = JColorChooser.showDialog(Main.this, "Edit Polygon Color",
							new Color(1f,1f,1f));
					selectedPolygon.setColor(new FillColor(
							newColor.getRed()/256d, newColor.getGreen()/256d,
							newColor.getBlue()/256d, selectedPolygon.color().alpha()));
				}
			}
		});
		JMenuItem model = new JMenuItem("Select Model");
		model.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				modelSelected = true; editColor.setEnabled(false);
			}
		});
		JMenuItem polygon = new JMenuItem("Select Polygon");
		polygon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				modelSelected = false; editColor.setEnabled(true);
			}
		});
		final JMenuItem newPolygon = new JMenuItem("New Polygon");
		newPolygon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (currentModel!=null)
					if (!creatingNewPolygon) {
						xs = new ArrayList<Double>(); ys = new ArrayList<Double>();
						selectedPolygon = null;
						creatingNewPolygon=true;
						newPolygon.setText("Create Polygon");
					}
					else {
						double[] primitiveXs = new double[xs.size()];
						double[] primitiveYs = new double[primitiveXs.length];
						for (int i=0; i<primitiveXs.length; i++) {
							primitiveXs[i] = xs.get(i);
							primitiveYs[i] = ys.get(i);
						}
						selectedPolygon = new Polygon(primitiveXs, primitiveYs);
						modelSelected = false;
						selectedModel = currentModel;
						creatingNewPolygon = false;
						newPolygon.setText("New Polygon");
						editColor.setEnabled(true);
					}
			}
		});
		JMenuItem delete = new JMenuItem("Delete Selection");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (modelSelected && selectedModel!=null && selectedModel!=currentModel )
					currentModel.remove(selectedModel);
				else if (selectedPolygon!=null) {
					selectedModel.attachedPolygons().remove(selectedPolygon);
					selectedModel.rotationLimits().remove(selectedPolygon);
					selectedModel.translationLimits().remove(selectedPolygon);
					selectedPolygon = null;
				}
			}
		});
		JMenuItem copyGon = new JMenuItem("Copy Selected Polygon");
		copyGon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (!modelSelected && currentModel!=null && selectedPolygon!=null) {
					selectedPolygon = selectedPolygon.copy();
					selectedPolygon.addX(WIDTH/2-selectedPolygon.centerX());
					selectedPolygon.addY(HEIGHT/2-selectedPolygon.centerY());
				}
			}
		});
		JMenuItem attachGon = new JMenuItem("Attach Polygon");
		attachGon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (currentModel!=null && selectedPolygon!=null
						&& !selectedModel.attachedPolygons().contains(selectedPolygon)) {
					selectedModel.attachedPolygons().add(selectedPolygon.copy());
					selectedPolygon = null;
				}
			}
		});
		JMenuItem addTransLimit = new JMenuItem("Add Translation Limit");
		addTransLimit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (currentModel!=null && selectedPolygon!=null
						&& !selectedModel.attachedPolygons().contains(selectedPolygon)) {
					selectedModel.translationLimits().add(selectedPolygon.copy());
					selectedPolygon = null;
				}
			}
		});
		JMenuItem addRotLimit = new JMenuItem("Add Rotation Limit");
		addRotLimit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (currentModel!=null && selectedPolygon!=null
						&& !selectedModel.attachedPolygons().contains(selectedPolygon)) {
					selectedModel.rotationLimits().add(selectedPolygon.copy());
					selectedPolygon = null;
				}
			}
		});
		final JMenu edit = new JMenu("Edit"); edit.add(model); edit.add(polygon);
		edit.add(delete); edit.add(copyGon); edit.add(attachGon);
		edit.add(addTransLimit); edit.add(addRotLimit); edit.add(newPolygon);
		edit.add(editColor);
		
		setJMenuBar(new JMenuBar(){{add(file); add(edit);}});
		
		pack();
		pool.submit(new Runnable() { private boolean interrupted = false;
			public void run() {
				while (!interrupted) {
					Main.this.repaint();
					try {Thread.sleep(10);} catch(InterruptedException ie) {
						JOptionPane.showMessageDialog(Main.this, "Window refresh thread "
								+"was terminated. Save work and restart application.");
						interrupted=true;
						Thread.currentThread().interrupt();
					}
				}
				
			}
		});
	}
	public static void paintGon(Graphics2D g2, ReadOnlyPolygon gon) {
		if (gon==null) return;
		ReadOnlyColor gonColor = gon.getColor();
		
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				(float)gonColor.alpha()));
		g2.setColor(new Color((float)gonColor.sanitizedRed(), (float)gonColor.sanitizedGreen(),
				(float)gonColor.sanitizedBlue()));
		int[] xs = gon.intAbscissae(); int[] ys = gon.intOrdinates();
		g2.fillPolygon(xs, ys, gon.points());
	}
	public static void main(final String... args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (final Exception e) {
			EventQueue.invokeLater(new Runnable(){
				public void run() {
					JOptionPane.showMessageDialog(new JFrame(),
							"UI Look&Feel Exception: "+e.getMessage());
				}
			});
		}
		
		if (args.length==0)
			EventQueue.invokeLater(new Runnable(){
				public void run() {
					new Main().setVisible(true);
				}
			});
		else {
			Model model = null;
			try {
				Reader reader = new Reader();
				model = reader.getModel(reader.loadModel(new FileInputStream(new File(args[0]))));
			}
			catch (final IOException ioe) {
				EventQueue.invokeLater(new Runnable(){
					public void run() {
						JOptionPane.showMessageDialog(new JFrame(),
								"IO Exception: "+ioe.getMessage());
					}
				});
			}
			final Model finalModel = model;
			EventQueue.invokeLater(new Runnable() {
				public void run() {new Main(finalModel).setVisible(true);}
			});
		}
	}
}