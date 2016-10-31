package eg.edu.alexu.csd.oop.draw;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class XMLFileOperations {

	private static final String EXTENTSION_REGEX = ".*\\w+\\.xml";
	private static final Pattern EXTENTSION_PATTERN = Pattern.compile(EXTENTSION_REGEX,
			Pattern.CASE_INSENSITIVE);

	public static boolean correctExtension(String path) {
		Matcher matcher = EXTENTSION_PATTERN.matcher(path);
		return matcher.matches();
	}

	public static List<Shape> read(String path) {
		List<Shape> shapes = new LinkedList<>();
		try {
			File inputFile = new File(path);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = factory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			System.out.println(doc.getTextContent());
			NodeList nList = doc.getElementsByTagName("shape");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				// nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					// create object by reflection
					String shapeType = eElement.getAttribute("type");
					Shape shape;
					try {
						Class<?> c = Class.forName(shapeType);
						shape = (Shape) c.newInstance();
					} catch (Exception ex) {
						shape = null;
					}
					// Basic Properties
					// Position
					try {
						Element positionTag = (Element) eElement.getElementsByTagName("position").item(0);
						int x = (int) Double.parseDouble(positionTag.getAttribute("x"));
						int y = (int) Double.parseDouble(positionTag.getAttribute("y"));
						shape.setPosition(new Point(x, y));
					} catch (Exception e) {
					}
					// Color
					try {
						Element colorTag = (Element) eElement.getElementsByTagName("color").item(0);
						int r = (int) Double.parseDouble(colorTag.getAttribute("r"));
						int g = (int) Double.parseDouble(colorTag.getAttribute("g"));
						int b = (int) Double.parseDouble(colorTag.getAttribute("b"));
						int a = (int) Double.parseDouble(colorTag.getAttribute("a"));
						shape.setColor(new Color(r, g, b, a));
					} catch (Exception e) {
					}
					// fill Color
					try {
						Element fillColorTag = (Element) eElement.getElementsByTagName("fillColor").item(0);
						int r = (int) Double.parseDouble(fillColorTag.getAttribute("r"));
						int g = (int) Double.parseDouble(fillColorTag.getAttribute("g"));
						int b = (int) Double.parseDouble(fillColorTag.getAttribute("b"));
						int a = (int) Double.parseDouble(fillColorTag.getAttribute("a"));
						shape.setFillColor(new Color(r, g, b, a));
					} catch (Exception e) {
					}
					// Properties Map
					try {
						Map<String, Double> properties = new Hashtable<>();
						NodeList propertiesList = doc.getElementsByTagName("property");
						if (propertiesList == null)
							shape.setProperties(null);
						for (int j = 0; j < propertiesList.getLength(); ++j) {
							Element property = (Element) propertiesList.item(j);
							String propertyName = property.getAttribute("name");
							Double val = Double.parseDouble(property.getTextContent());
							properties.put(propertyName, val);
						}
						shape.setProperties(properties);
					} catch (Exception e) {
					}
					shapes.add(shape);
				}
			}
		} catch (Exception e) {
		}
		return shapes;
	}

	public static void write(String path, Shape[] shapes) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// root element
			Element rootElement = doc.createElement("VectorPaintFile");
			doc.appendChild(rootElement);

			for (Shape shape : shapes) {
				Element shapeChild = doc.createElement("shape");

				// setting Class type attribute
				Attr typeAttr = doc.createAttribute("type");
				if (shape == null) {
					typeAttr.setValue("_nullShape_");
				} else {
					typeAttr.setValue(shape.getClass().getName());
				}
				shapeChild.setAttributeNode(typeAttr);
				// Position
				try {
					Element positionTag = doc.createElement("position");
					positionTag.setAttribute("x", Integer.toString(shape.getPosition().x));
					positionTag.setAttribute("y", Integer.toString(shape.getPosition().y));
					shapeChild.appendChild(positionTag);
				} catch (Exception e) {
				}
				// Color
				try {
					Element colorTag = doc.createElement("color");
					Color c = shape.getColor();
					colorTag.setAttribute("r", Integer.toString(c.getRed()));
					colorTag.setAttribute("g", Integer.toString(c.getGreen()));
					colorTag.setAttribute("b", Integer.toString(c.getBlue()));
					colorTag.setAttribute("a", Integer.toString(c.getAlpha()));
					shapeChild.appendChild(colorTag);
				} catch (Exception e) {
				}
				// fill Color
				try {
					Element fillColorTag = doc.createElement("fillColor");
					Color c = shape.getFillColor();
					fillColorTag.setAttribute("r", Integer.toString(c.getRed()));
					fillColorTag.setAttribute("g", Integer.toString(c.getGreen()));
					fillColorTag.setAttribute("b", Integer.toString(c.getBlue()));
					fillColorTag.setAttribute("a", Integer.toString(c.getAlpha()));
					shapeChild.appendChild(fillColorTag);
				} catch (Exception e) {
				}
				// Properties Map
				try {
					Map<String, Double> properties = shape.getProperties();
					for (Map.Entry<String, Double> p : properties.entrySet()) {
						Element property = doc.createElement("property");
						property.setAttribute("name", p.getKey());
						String val = Double.toString(p.getValue());
						property.appendChild(doc.createTextNode(val));
						shapeChild.appendChild(property);
					}
				} catch (Exception e) {
				}

				// add shape to root
				rootElement.appendChild(shapeChild);

			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path));
			transformer.transform(source, result);

			// Output to console for testing
			StreamResult consoleResult = new StreamResult(System.out);
			transformer.transform(source, consoleResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
