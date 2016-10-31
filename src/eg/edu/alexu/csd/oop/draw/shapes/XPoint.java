package eg.edu.alexu.csd.oop.draw.shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import eg.edu.alexu.csd.oop.draw.Shape;

public class XPoint extends MyShape implements Shape {

	final static int DELTA = 5;
	
	public XPoint() {
		super();
	}
	
	public XPoint(Point position) {
		this();
		setPosition(position);
	}

	public void draw(Graphics canvas) {
		int x = position.x;
		int y = position.y;
		canvas.setColor(Color.BLACK);
		canvas.drawLine(x, y - DELTA, x, y + DELTA);
		canvas.setColor(Color.GRAY);
		canvas.drawLine(x - DELTA, y, x + DELTA, y);
	}

}
