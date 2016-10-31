package eg.edu.alexu.csd.oop.draw.shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Hashtable;
import java.util.Map;

import eg.edu.alexu.csd.oop.draw.Shape;

public abstract class MyShape implements Shape {

	protected Point position;
	protected Map<String, Double> properties;
	protected Color color, fillColor;
	
	public MyShape(){
		properties = new Hashtable<>();
	}
	
	public void setPosition(Point position) {
		this.position = position;
	}

	public Point getPosition() {
		return position;
	}

	public void setProperties(Map<String, Double> properties) {
		this.properties = properties;
	}

	public Map<String, Double> getProperties() {
		return properties;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public Color getColor() {
		return color;
	}

	abstract public void draw(Graphics canvas);

	public Object clone() throws CloneNotSupportedException {
		MyShape newShape = (MyShape) basic_Clone();
		return newShape;
	}

	protected Shape basic_Clone() {
		Class<?> c = this.getClass();
		MyShape newShape;
		try {
			newShape = (MyShape) c.newInstance();
			if (this.position != null)
				newShape.position = (Point) this.position.clone();
			//System.out.println(c);
			newShape.color = this.color;
			newShape.fillColor = this.fillColor;
			if (properties != null)
				newShape.properties.putAll(this.properties);
			return newShape;
		} catch (Exception e) {
			return null;
		}
	}
}
