package eg.edu.alexu.csd.oop.draw;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.draw.json.*;

public class JSONFileOperations {
	
	private static final String EXTENTSION_REGEX = ".*\\w+\\.json";
	private static final Pattern EXTENTSION_PATTERN = Pattern.compile(EXTENTSION_REGEX,
			Pattern.CASE_INSENSITIVE);

	public static boolean correctExtension(String path) {
		Matcher matcher = EXTENTSION_PATTERN.matcher(path);
		return matcher.matches();
	}
	
	public static List<Shape> read(String path) {
		List<Shape> shapes = new LinkedList<>();
		JsonBuilder builder = null;
		try {
			File inputFile = new File(path);
			builder = new JsonBuilder();
			builder.parse(inputFile);
			System.out.println(builder.toString());
			JsonArray shapesList = (JsonArray) builder.get("shapes");
			if (shapesList == null)
				shapesList = new JsonArray();
			for (Object o : shapesList) {
				JsonObject shapeObject = (JsonObject) o;

				// create object by reflection
				String shapeType = shapeObject.get("type").getContent();
				Shape shape;
				if (shapeType.equals("nullShape")) {
					shape = null;
				} else {
					try {
						Class<?> c = Class.forName(shapeType);
						shape = (Shape) c.newInstance();
					} catch (Exception ex) {
						shape = null;
					}
				}

				// Basic Properties
				// Position
				try {
					JsonObject position = (JsonObject) shapeObject.get("position");
					int x = (int) Double.parseDouble(position.get("x").getContent());
					int y = (int) Double.parseDouble(position.get("y").getContent());
					shape.setPosition(new Point(x, y));
				} catch (Exception e) {
				}
				// Color
				try {
					JsonObject color = (JsonObject) shapeObject.get("color");
					int r = (int) Double.parseDouble(color.get("r").getContent());
					int g = (int) Double.parseDouble(color.get("g").getContent());
					int b = (int) Double.parseDouble(color.get("b").getContent());
					int a = (int) Double.parseDouble(color.get("a").getContent());
					shape.setColor(new Color(r, g, b, a));
				} catch (Exception e) {
					System.out.println("error in color" + e.getMessage());
				}
				// fill Color
				try {
					JsonObject fillColor = (JsonObject) shapeObject.get("fillColor");
					int r = (int) Double.parseDouble(fillColor.get("r").getContent());
					int g = (int) Double.parseDouble(fillColor.get("g").getContent());
					int b = (int) Double.parseDouble(fillColor.get("b").getContent());
					int a = (int) Double.parseDouble(fillColor.get("a").getContent());
					shape.setFillColor(new Color(r, g, b, a));
				} catch (Exception e) {
				}
				// Properties Map
				try {
					Map<String, Double> properties = new Hashtable<>();
					JsonObject propertiesList = (JsonObject) shapeObject.get("properties");
					if (propertiesList == null)
						shape.setProperties(null);
					for (Object o1 : propertiesList) {
						JsonNumber property = (JsonNumber) o1;
						String propertyName = property.getName();
						Double val = Double.parseDouble(property.getContent());
						properties.put(propertyName, val);
					}
					shape.setProperties(properties);
				} catch (Exception e) {
				}
				shapes.add(shape);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return shapes;
	}

	public static void write(String path, Shape[] shapes) {
		JsonBuilder builder = new JsonBuilder();
		try {
			JsonArray shapesList = new JsonArray("shapes");
			for (Shape shape : shapes) {
				JsonObject jsonShape = new JsonObject("shape");

				// setting Class type attribute
				JsonString type = new JsonString("type");
				if (shape == null) {
					type.setContent("nullShape");
				} else {
					type.setContent(shape.getClass().getName());
				}

				jsonShape.add(type);

				// Basic Properties
				// Position
				try {
					JsonObject position = new JsonObject("position");
					position.add(new JsonNumber("x", new Integer(shape.getPosition().x)));
					position.add(new JsonNumber("y", new Integer(shape.getPosition().y)));
					jsonShape.add(position);
				} catch (Exception e) {
				}
				// Color
				try {
					JsonObject color = new JsonObject("color");
					Color c = shape.getColor();
					color.add(new JsonNumber("r", new Integer(c.getRed())));
					color.add(new JsonNumber("b", new Integer(c.getBlue())));
					color.add(new JsonNumber("g", new Integer(c.getGreen())));
					color.add(new JsonNumber("a", new Integer(c.getAlpha())));
					jsonShape.add(color);
				} catch (Exception e) {
				}
				// fill Color
				try {
					JsonObject fillColor = new JsonObject("fillColor");
					Color c = shape.getFillColor();
					fillColor.add(new JsonNumber("r", new Integer(c.getRed())));
					fillColor.add(new JsonNumber("b", new Integer(c.getBlue())));
					fillColor.add(new JsonNumber("g", new Integer(c.getGreen())));
					fillColor.add(new JsonNumber("a", new Integer(c.getAlpha())));
					jsonShape.add(fillColor);
				} catch (Exception e) {
				}
				// Properties Map
				try {
					Map<String, Double> properties = shape.getProperties();
					JsonObject propertiesList = new JsonObject("properties");
					for (Map.Entry<String, Double> p : properties.entrySet()) {
						JsonNumber property = new JsonNumber(p.getKey(), p.getValue());
						;
						propertiesList.add(property);
					}
					jsonShape.add(propertiesList);
				} catch (Exception e) {
				}
				// add shape to shapesList
				shapesList.add(jsonShape);
			}
			builder.add(shapesList);
			// write the content into json file
			builder.buildTo(new FileOutputStream(path));
			// Output to console for testing
			// builder.buildTo(System.out);
			System.out.println(builder.toString());
			System.out.println("json write");

		} catch (Exception e) {
			// e.printStackTrace();
		}
	}
}
