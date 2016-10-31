package eg.edu.alexu.csd.oop.draw.shapes;

import java.util.Hashtable;

import eg.edu.alexu.csd.oop.draw.Shape;

public class Square extends Rectangle implements Shape {
	
	public Square(){
		properties = new Hashtable<>();
		properties.put("length", new Double(0));
	}

	protected void setDimentions() {
		width = height = properties.get("length").intValue();
	}
	
}
