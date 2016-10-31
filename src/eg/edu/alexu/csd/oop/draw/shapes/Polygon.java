package eg.edu.alexu.csd.oop.draw.shapes;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Hashtable;
import java.util.Map;

import eg.edu.alexu.csd.oop.draw.Shape;

public class Polygon extends MyShape implements Shape {

	protected int[] xPoints, yPoints;
	protected int numberOfSides = 0, ind = 0;

	public Polygon() {
		properties = new Hashtable<>();
		properties.put("number of sides", new Double(0));
	}

	public void setProperties(Map<String, Double> properties) {
		this.properties = properties;
		try {
			numberOfSides = properties.get("number of sides").intValue();
			if (numberOfSides > 0) {
				xPoints = new int[numberOfSides];
				yPoints = new int[numberOfSides];
				if (properties.containsKey("_x0")) {
					System.out.println("loaded");
					for (ind = 0; ind < numberOfSides; ++ind) {
						try {
							xPoints[ind] = properties.get("_x" + Integer.toString(ind)).intValue();
							yPoints[ind] = properties.get("_y" + Integer.toString(ind)).intValue();
						} catch (Exception ex) {
							break;
						}
					}
				}
				// System.out.println("Properties set : " + numberOfSides);
			}
		} catch (Exception e) {
		}
	}

	public void setPosition(Point position) {
		if (xPoints != null) {
			if (ind < numberOfSides) {
				if (ind == 0) {
					this.position = position;
				}
				xPoints[ind] = new Integer(position.x);
				yPoints[ind] = new Integer(position.y);
				properties.put("_x" + Integer.toString(ind), new Double(xPoints[ind]));
				properties.put("_y" + Integer.toString(ind), new Double(yPoints[ind]));
				++ind;
			} else {
				// Move the shape
				if (getPosition() != null) {
					int deltaX = position.x - xPoints[0];
					int deltaY = position.y - yPoints[0];
					for (int i = 0; i < numberOfSides; ++i) {
						xPoints[i] += deltaX;
						yPoints[i] += deltaY;
						properties.put("_x" + Integer.toString(i), new Double(xPoints[i]));
						properties.put("_y" + Integer.toString(i), new Double(yPoints[i]));
					}
				}
				this.position = position;
			}
		}
	}

	public void draw(Graphics canvas) {
		try {
			if (ind < numberOfSides) {
				canvas.setColor(getColor());
				for (int i = 0; i < ind - 1; ++i) {
					canvas.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
				}
				throw new RuntimeException("Properties does not contain enough information");
			}
			canvas.setColor(getFillColor());
			canvas.fillPolygon(xPoints, yPoints, numberOfSides);
			canvas.setColor(getColor());
			canvas.drawPolygon(xPoints, yPoints, numberOfSides);
		} catch (Exception ex) {
			throw new RuntimeException("Properties does not contain enough information");
		}

	}

	public Object clone() throws CloneNotSupportedException {
		Polygon newShape = (Polygon) basic_Clone();
		if (newShape != null) {
			try {
				newShape.xPoints = xPoints.clone();
				newShape.yPoints = yPoints.clone();
			} catch (NullPointerException ex) {
			}
			// System.out.println(newShape);
			newShape.numberOfSides = numberOfSides;
			newShape.ind = ind;
		}
		return newShape;
	}

}