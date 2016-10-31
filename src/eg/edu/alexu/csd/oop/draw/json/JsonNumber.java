package eg.edu.alexu.csd.oop.draw.json;

public class JsonNumber implements JsonDataType {
	String name;
	Number val;

	public JsonNumber() {
		this("", null);
	}

	public JsonNumber(String name) {
		this(name, null);
	}

	public JsonNumber(String name, Number n) {
		setName(name);
		setContent(n);
	}

	public void setContent(Number n) {
		this.val = n;
	}

	public Number getVal() {
		return val;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return val.toString();
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

	@Override
	public int extractElements(String s, int start) {
		int endIndex = start;
		try {
			char c = s.charAt(start);
			while (Character.isDigit(c) || c == '.') {
				if (endIndex > s.length())
					break;
				c = s.charAt(++endIndex);
			}
			val = Double.parseDouble(s.substring(start, endIndex));
		} catch (Exception e) {
		}
		return endIndex;
	}

}
