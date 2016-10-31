package eg.edu.alexu.csd.oop.draw.shapes;
import java.awt.Graphics;
import java.util.Hashtable;

import eg.edu.alexu.csd.oop.draw.Shape;

public class Rectangle extends MyShape implements Shape {
	protected int width, height;
	
	public Rectangle(){
		properties = new Hashtable<>();
		properties.put("width", new Double(0));
		properties.put("height", new Double(0));
	}
	
	protected  void setDimentions(){
		width = properties.get("width").intValue();
		height = properties.get("height").intValue();
	}
	
	public void draw(Graphics canvas) {
		if (properties == null)
			throw new NullPointerException("Properties of Shape is not set");
		try {
			setDimentions();
			canvas.setColor(getFillColor());
			canvas.fillRect(position.x-width/2, position.y-height/2, width, height);
			canvas.setColor(getColor());
			canvas.drawRect(position.x-width/2, position.y-height/2, width, height);
		} catch (Exception e) {
			throw new RuntimeException("Properties does not contain enough information");
		}
	}

}
