package eg.edu.alexu.csd.oop.draw;

import java.awt.Graphics;
import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MyEngine extends Observable implements DrawingEngine {

	private List<Shape> shapes;
	private Stack<List<Shape>> redo, undo;

	public MyEngine() {
		reset();
	}

	private void reset() {
		shapes = new LinkedList<>();
		redo = new Stack<>();
		undo = new Stack<>();
	}

	public void refresh(Graphics canvas) {
		for (Shape shape : shapes)
			try {
				shape.draw(canvas);
			} catch (Exception e) {
			}
	}

	private List<Shape> listClone(List<Shape> l) {
		List<Shape> newl = new LinkedList<>();
		for (Shape s : l) {
			try {
				newl.add((Shape) s.clone());
			} catch (Exception e) {
				newl.add(null);
			}
		}
		return newl;
	}

	public void addShape(Shape shape) {
		undo.add(listClone(shapes));
		shapes.add(shape);
		histroyStacksCheck();
		setChanged();
		notifyObservers();
	}

	public void removeShape(Shape shape) {
		if (shapes.contains(shape)) {
			undo.add(listClone(shapes));
			shapes.remove(shape);
			histroyStacksCheck();
			setChanged();
			notifyObservers();
		} else {
			throw new RuntimeException("this shape does not exist");
		}
	}

	public void updateShape(Shape oldShape, Shape newShape) {
		int i = shapes.indexOf(oldShape);
		if (i != -1) {
			undo.add(listClone(shapes));
			shapes.set(i, newShape);
			histroyStacksCheck();
			setChanged();
			notifyObservers();
		} else {
			throw new RuntimeException("this shape does not exist");
		}
	}

	public Shape[] getShapes() {
		Shape[] arr = shapes.toArray(new Shape[shapes.size()]);
		return arr;
	}

	public void undo() {
		if (undo.isEmpty())
			throw new RuntimeException();
		redo.add(shapes);
		shapes = undo.pop();
		setChanged();
		notifyObservers();
	}

	public void redo() {
		if (redo.isEmpty())
			throw new RuntimeException();
		undo.add(shapes);
		shapes = redo.pop();
		setChanged();
		notifyObservers();
	}

	private void histroyStacksCheck() {
		redo.clear();
	}

	public void save(String path) {
		try {
			if (XMLFileOperations.correctExtension(path)) {
				XMLFileOperations.write(path, getShapes());
			} else if (JSONFileOperations.correctExtension(path)) {
				JSONFileOperations.write(path, getShapes());
			} else {
				// throw new RuntimeException("invalid file format/path");
			}
		} catch (Exception e) {
			System.out.println("Error while saving: " + e.getMessage());
		}
	}

	public void load(String path) {
		List<Shape> loadedShapeList = null;
		try {
			if (XMLFileOperations.correctExtension(path)) {
				loadedShapeList = XMLFileOperations.read(path);
			} else if (JSONFileOperations.correctExtension(path)) {
				loadedShapeList = JSONFileOperations.read(path);
			}
			reset();
			shapes = loadedShapeList;
		} catch (Exception e) {
			System.out.println("Error while loading: " + e.getMessage());
		}
		setChanged();
		notifyObservers();
	}

	/*
	 * getting class names in class path(non-Javadoc)
	 * 
	 * @see eg.edu.alexu.csd.oop.draw.DrawingEngine#getSupportedShapes()
	 */
	@SuppressWarnings("unchecked")
	public List<Class<? extends Shape>> getSupportedShapes() {
		ClassesGetter classesGetter = new ClassesGetter();
		Set<Class<?>> classesSet = classesGetter.getClasses();
		List<Class<? extends Shape>> supportedShapesList = new LinkedList<>();
		for (Class<?> c : classesSet) {
			if (!Modifier.isAbstract(c.getModifiers()) && isShape(c))
				supportedShapesList.add((Class<? extends Shape>) c);
		}
		return supportedShapesList;
	}

	private boolean isShape(Class<?> c) {
		Class<?>[] interfaces = c.getInterfaces();
		for (Class<?> d : interfaces) {
			if (d.getSimpleName().equals("Shape"))
				return true;
		}
		return false;
	}

	private class ClassesGetter {
		private Set<Class<?>> classes;

		public Set<Class<?>> getClasses() {
			classes = new LinkedHashSet<>(2000);
			// tokenize classpath
			String classpath = System.getProperty("java.class.path");
			String pathSeparator = System.getProperty("path.separator");
			StringTokenizer st = new StringTokenizer(classpath, pathSeparator);

			// for each element in the classpath
			while (st.hasMoreTokens()) {
				File currentDirectory = new File(st.nextToken());
				processFile(currentDirectory.getAbsolutePath(), "");
			}
			return this.classes;
		}

		private void addClassName(String className) {
			try {
				Class<?> c = Class.forName(className);
				addClassName(c);
			} catch (Exception ex) {
			}
		}

		private void addClassName(Class<?> c) {
			this.classes.add(c);
		}

		private void processFile(String base, String current) {
			File currentDirectory = new File(base + File.separatorChar + current);
			// Handle special for archives
			if (checkFileType(currentDirectory, "zip") || checkFileType(currentDirectory, "jar")) {
				try {
					processZip(new ZipFile(currentDirectory));
					return;
				} catch (Exception ex) {
				}
			}
			try {
				if (checkFileType(currentDirectory, "jar")) {
					processJar(new JarFile(currentDirectory));
				} else if (!currentDirectory.isDirectory() && checkFileType(currentDirectory, "class")) {
					String className = getClassName(current);
					addClassName(className);
				} else {
					File[] children = currentDirectory.listFiles();
					for (File child : children)
						processFile(base, current + ((current == "") ? "" : File.separator) + child.getName());
				}
			} catch (Exception ex) {
			}

		}

		private void processJar(JarFile jarFile) {
			Enumeration<?> e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry je = (JarEntry) e.nextElement();
				if (je.isDirectory() || !checkFileType(je.getName(), "class"))
					continue;
				// -6 because of .class
				String className = je.getName().substring(0, je.getName().length() - 6);
				addClassName(getClassName(className));
			}
		}

		/*
		 * Returns the Fully Qualified Class name of a class from it's path
		 */
		private String getClassName(String fileName) {
			String newName = fileName.replace(File.separatorChar, '.');
			// Because zipfiles don't have platform specific seperators
			newName = newName.replace('/', '.');
			return newName.substring(0, fileName.length() - 6);
		}

		/*
		 * This is not recursive as zip's in zip's are not searched by the
		 * classloader.
		 */
		private void processZip(ZipFile file) {
			Enumeration<?> files = file.entries();
			while (files.hasMoreElements()) {
				Object tfile = files.nextElement();
				ZipEntry child = (ZipEntry) tfile;
				if (checkFileType(child.getName(),"class")) {
					addClassName(getClassName(child.getName()));
				}
			}
		}

		private boolean checkFileType(File file, String type) {
			return checkFileType(file.getName(), type);
		}

		private boolean checkFileType(String file, String type) {
			return file.toLowerCase().endsWith('.' + type.toLowerCase());
		}
	}

}
