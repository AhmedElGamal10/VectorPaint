package eg.edu.alexu.csd.oop.draw.json;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class JsonObject implements JsonDataType, Iterable {
	private Map<String, JsonDataType> map;
	private String name;

	public JsonObject(String name) {
		setName(name);
		map = new Hashtable<>();
	}

	public JsonObject() {
		this("");
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void add(JsonDataType o) {
		map.put(o.getName(), o);
	}

	public JsonDataType get(String name) {
		return map.get(name);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (!name.isEmpty()) {
			builder.append("\"");
			builder.append(getName());
			builder.append("\":");
		}
		builder.append(getContent());
		return builder.toString();
	}

	public String getContent() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (JsonDataType val : map.values()) {
			builder.append(val);
			builder.append(",");
		}
		if (!map.isEmpty())
			builder.deleteCharAt(builder.length() - 1);
		builder.append("}");
		return builder.toString();
	}

	public Iterator iterator() {
		return map.values().iterator();
	}

	public int extractElements(String s, int start) {
		Class[] dataTypes = { JsonNumber.class, JsonString.class, JsonArray.class, JsonObject.class };
		int endIndex = start;
		if (s.charAt(start) == '{') {
			endIndex++;
			while (endIndex < s.length()) {
				if (s.charAt(endIndex) == '}')
					return ++endIndex;
				int tmpIndex = endIndex;
				JsonString name = new JsonString();
				tmpIndex = name.extractElements(s, endIndex);
				String objectName = name.getContent();
				if (tmpIndex == endIndex)
					return tmpIndex;
				endIndex = tmpIndex + 1;
				for (Class dt : dataTypes) {
					try {
						JsonDataType tmpObject = (JsonDataType) dt.newInstance();
						tmpObject.setName(objectName);
						tmpIndex = tmpObject.extractElements(s, endIndex);
						if (tmpIndex > endIndex) {
							this.add(tmpObject);
							endIndex = tmpIndex;
							char tmpC = s.charAt(endIndex);
							if (tmpC == ',')
								endIndex++;
							else if (tmpC == '}')
								return ++endIndex;
							else
								return start;
							break;
						}
					} catch (Exception e) {
					}
				}
				if (tmpIndex == endIndex)
					break;
			}
		}
		return endIndex;
	}
}
