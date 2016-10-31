package eg.edu.alexu.csd.oop.draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import eg.edu.alexu.csd.oop.draw.shapes.XPoint;

public class GUIController extends Observable implements Observer {
	private MyEngine engine;
	private Shape currentShape;
	private Mode mode;
	private Color fillColor, borderColor;
	private List<Class<? extends Shape>> importedShapesList;

	private static enum Mode {
		DRAW, MOVE, REMOVE, RESIZE;
	}

	public static enum ColorType {
		FILL, BORDER;
	}

	public GUIController() {
		engine = new MyEngine();
		engine.addObserver(this);
		mode = Mode.DRAW;
		fillColor = Color.RED;
		borderColor = Color.BLACK;
		importedShapesList = new LinkedList<>();
	}

	public void update(Observable arg0, Object arg1) {
		setChanged();
		notifyObservers();
	}

	public void refresh(Graphics canvas) {
		engine.refresh(canvas);
	}

	public List<Class<? extends Shape>> getSupportedShapes() {
		List<Class<? extends Shape>> l = engine.getSupportedShapes();
		l.addAll(importedShapesList);
		return l;
	}

	/* GUI Buttons ActionListener */

	private class ShapeBtnListener implements ActionListener {
		private Class<? extends Shape> shapeClass;

		public ShapeBtnListener(Class<? extends Shape> shapeClass) {
			this.shapeClass = shapeClass;
		}

		public void actionPerformed(ActionEvent e) {
			mode = Mode.DRAW;
			try {
				Shape shape = shapeClass.newInstance();
				PropertiesDialog dialog = new PropertiesDialog(shape);
				dialog.showDialog();
				currentShape = shape;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	// Controllers

	// Shape Modification Buttons
	private class MoveBtnListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			mode = Mode.MOVE;
		}

	}

	private class DeleteBtnListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			mode = Mode.REMOVE;
		}

	}

	private class ResizeBtnListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			mode = Mode.RESIZE;
		}

	}

	// redo and undo
	private class UndoBtnListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			try {
				engine.undo();
			} catch (Exception ex) {
				// able or disable button
			}
		}
	}

	private class RedoBtnListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			try {
				engine.redo();
			} catch (Exception ex) {
				// able or disable button
			}
		}

	}

	// file manipulation (Save and load)
	private String getPath() {
		JFileChooser fc = new JFileChooser();
		File file = null;
		int retValue = fc.showOpenDialog(new JPanel());
		if (retValue == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			return file.getAbsolutePath();
		} else {
			System.out.println("Next time select a file.");
			return null;
		}
	}

	private class LoadBtnListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			String path = getPath();
			if (path != null) {
				engine.load(path);
			}
		}

	}

	private class SaveBtnListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			String path = getPath();
			if (path != null) {
				engine.save(path);
			}
		}

	}

	// Import Classes from given path
	private class ImportBtnListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			String pathToJar = getPath();
			try {
				loadClassFromJar(pathToJar);
				setChanged();
				notifyObservers("shapesImported");
			} catch (Exception e1) {
				System.out.println("jar not found");
			}
		}

		@SuppressWarnings("unchecked")
		private void loadClassFromJar(String pathToJar) throws MalformedURLException {
			JarFile jarFile = null;
			try {
				jarFile = new JarFile(pathToJar);
				Enumeration<?> e = jarFile.entries();
				URL[] urls = { new URL("jar:file:" + pathToJar + "!/") };
				URLClassLoader cl = URLClassLoader.newInstance(urls);

				while (e.hasMoreElements()) {
					JarEntry je = (JarEntry) e.nextElement();
					if (je.isDirectory() || !je.getName().endsWith(".class"))
						continue;

					// -6 because of .class
					String className = je.getName().substring(0, je.getName().length() - 6);
					className = className.replace('/', '.');
					try {
						Class<? extends Shape> c = (Class<? extends Shape>) cl.loadClass(className);
						importedShapesList.add(c);
					} catch (Exception ex) {
					}
				}
			} catch (IOException e1) {
			}
		}

	}

	// Color Chooser
	private class ColorActionListener extends MouseAdapter {
		private ColorType colorType;

		public ColorActionListener(ColorType colorType) {
			this.colorType = colorType;
		}

		public void mouseClicked(MouseEvent e) {
			JComponent comp = (JComponent) e.getSource();
			Color c = JColorChooser.showDialog(null, "Colors", borderColor);
			comp.setBackground(c);
			if (colorType == ColorType.FILL)
				fillColor = c;
			else
				borderColor = c;
		}
	}

	/* Draw Area MouseListeners */

	// return shape whose position point nearest to the given mouse position
	private Shape nearestShape(Point mousePosition) {
		Shape[] shapeList = engine.getShapes();
		double dist = Double.MAX_VALUE;
		Shape nearShape = null;
		for (Shape s : shapeList) {
			double tmp = mousePosition.distance(s.getPosition());
			if (tmp < dist) {
				dist = tmp;
				nearShape = s;
			}
		}
		return nearShape;
	}

	private class DrawAreaMouseListners extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			JComponent drawView = (JComponent) e.getSource();
			if (mode == Mode.DRAW) {
				try {
					currentShape.setPosition(e.getPoint());
					currentShape.setColor(borderColor);
					currentShape.setFillColor(fillColor);
					
					Graphics canvas = drawView.getGraphics();
					currentShape.draw(canvas);
					engine.addShape(currentShape);
					currentShape = null;
				} catch (Exception ex) {
					drawView.repaint();
				}
			} else if (mode == Mode.REMOVE) {
				Shape selectedShape = nearestShape(e.getPoint());
				engine.removeShape(selectedShape);
			} else if (mode == Mode.RESIZE) {
				try {
					Shape selectedShape = nearestShape(e.getPoint());
					Shape resizedShape = (Shape) selectedShape.clone();
					PropertiesDialog dialog = new PropertiesDialog(resizedShape);
					dialog.showDialog();
					engine.updateShape(selectedShape, resizedShape);
				} catch (CloneNotSupportedException e1) {
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			mode = Mode.DRAW;
		}

	}

	private class DrawAreaMouseMotionListener extends MouseMotionAdapter {

		// Draw cross point on the nearest shape to indicate what shape will be
		// selected
		public void mouseMoved(MouseEvent e) {
			if (mode == Mode.DRAW)
				return;
			JComponent drawView = (JComponent) e.getSource();
			try {
				Point mousePosition = new Point(e.getPoint());
				Shape nearShape = nearestShape(mousePosition);
				Graphics canvas = drawView.getGraphics();
				setChanged();
				notifyObservers();
				XPoint centerPoint = new XPoint(nearShape.getPosition());
				centerPoint.draw(canvas);
				currentShape.draw(canvas);
				drawView.repaint();
			} catch (Exception ex) {
				drawView.repaint();
			}
		}

		// Drag the shape to its new position
		public void mouseDragged(MouseEvent e) {
			if (mode != Mode.MOVE)
				return;
			Point mousePosition = new Point(e.getPoint());
			Shape selectedShape = nearestShape(mousePosition);
			try {
				Shape movedShape = (Shape) selectedShape.clone();
				movedShape.setPosition(mousePosition);
				engine.updateShape(selectedShape, movedShape);
			} catch (Exception ex) {
			}
		}
	}

	// Listeners Getters
	public ActionListener getShapeBtnListener(Class<? extends Shape> shapeClass) {
		return new ShapeBtnListener(shapeClass);
	}

	public ActionListener getMoveBtnListener() {
		return new MoveBtnListener();
	}

	public ActionListener getDeleteBtnListener() {
		return new DeleteBtnListener();
	}

	public ActionListener getResizeBtnListener() {
		return new ResizeBtnListener();
	}

	public ActionListener getUndoBtnListener() {
		return new UndoBtnListener();
	}

	public ActionListener getRedoBtnListener() {
		return new RedoBtnListener();
	}

	public ActionListener getLoadBtnListener() {
		return new LoadBtnListener();
	}

	public ActionListener getSaveBtnListener() {
		return new SaveBtnListener();
	}

	public ActionListener getImportBtnListener() {
		return new ImportBtnListener();
	}

	public MouseAdapter getColorActionListener(ColorType type) {
		return new ColorActionListener(type);
	}

	public MouseAdapter getDrawAreaMouseListners() {
		return new DrawAreaMouseListners();
	}

	public MouseMotionAdapter getDrawAreaMouseMotionListener() {
		return new DrawAreaMouseMotionListener();
	}
}