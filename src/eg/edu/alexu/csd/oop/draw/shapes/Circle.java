package eg.edu.alexu.csd.oop.draw.shapes;

import java.util.Hashtable;

import eg.edu.alexu.csd.oop.draw.Shape;

public class Circle extends Ellipse implements Shape {

	public Circle() {
		properties = new Hashtable<>();
		properties.put("radius", new Double(0));
	}

	protected void setDimentions() {
		width = height = properties.get("radius").intValue();
	}

}
