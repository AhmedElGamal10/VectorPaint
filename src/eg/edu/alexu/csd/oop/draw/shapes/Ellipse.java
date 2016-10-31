package eg.edu.alexu.csd.oop.draw.shapes;

import java.awt.Graphics;
import java.util.Hashtable;

import eg.edu.alexu.csd.oop.draw.Shape;

public class Ellipse extends MyShape implements Shape{
	protected int width, height;
	
	public Ellipse() {
		properties = new Hashtable<>();
		properties.put("width", new Double(0));
		properties.put("height", new Double(0));
	}
	
	protected void setDimentions(){
		width = properties.get("width").intValue();
		height = properties.get("height").intValue();
	}
	
	public void draw(Graphics canvas) {
		try {
			setDimentions();
			canvas.setColor(getFillColor());
			int x = position.x - width/2;
			int y = position.y - height/2;
			canvas.fillOval(x, y, width, height);
			canvas.setColor(getColor());
			canvas.drawOval(x, y, width, height);
		} catch (Exception e) {
			throw new RuntimeException("Properties does not contain enough information");
		}
	}
}
