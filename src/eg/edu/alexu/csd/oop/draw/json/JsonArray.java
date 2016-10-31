package eg.edu.alexu.csd.oop.draw.json;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class JsonArray implements JsonDataType, Iterable {
	private List<JsonDataType> list;
	private String name;

	public JsonArray(String name) {
		setName(name);
		list = new LinkedList<>();
	}

	public JsonArray() {
		this("");
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getContent() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (JsonDataType o : list) {
			builder.append(o.getContent());
			builder.append(",");
		}
		if (!list.isEmpty())
			builder.deleteCharAt(builder.length() - 1);
		builder.append("]");
		return builder.toString();
	}

	public void add(JsonDataType val) {
		list.add(val);
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

	public Iterator iterator() {
		return list.iterator();
	}

	public int extractElements(String s, int start) {
		Class[] dataTypes = { JsonNumber.class, JsonString.class, JsonArray.class, JsonObject.class };
		int endIndex = start;
		if (s.charAt(start) == '[') {
			endIndex++;
			while (endIndex < s.length()) {
				int tmpIndex = endIndex;
				for (Class dt : dataTypes) {
					try {
						JsonDataType tmpObject = (JsonDataType) dt.newInstance();
						tmpIndex = tmpObject.extractElements(s, endIndex);
						if (tmpIndex > endIndex) {
							this.add(tmpObject);
							endIndex = tmpIndex;
							char tmpC = s.charAt(endIndex);
							if (tmpC == ',')
								endIndex++;
							else if (tmpC == ']')
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
