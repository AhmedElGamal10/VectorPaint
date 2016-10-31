package eg.edu.alexu.csd.oop.draw.shapes;

import java.util.Hashtable;
import java.util.Map;

import eg.edu.alexu.csd.oop.draw.Shape;

public class Triangle extends Polygon implements Shape {
	
	public Triangle() {
		properties = new Hashtable<>();
		numberOfSides = 3;
		xPoints = new int[numberOfSides];
		yPoints = new int[numberOfSides];
	}

	public void setProperties(Map<String, Double> properties) {
		this.properties = properties;
		if (properties!=null && properties.containsKey("_x0")) {
			for (ind = 0; ind < numberOfSides; ++ind) {
				try {
					xPoints[ind] = properties.get("_x" + Integer.toString(ind)).intValue();
					yPoints[ind] = properties.get("_y" + Integer.toString(ind)).intValue();
				} catch (Exception ex) {
					break;
				}
			}
		}
	}

}