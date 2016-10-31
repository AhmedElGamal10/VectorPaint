package eg.edu.alexu.csd.oop.draw.json;

public class JsonString implements JsonDataType {
	private String name, content;

	public JsonString() {
		this("", "");
	}

	public JsonString(String name) {
		this(name, "");
	}

	public JsonString(String name, String content) {
		setName(name);
		setContent(content);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (!name.isEmpty()) {
			builder.append("\"");
			builder.append(getName());
			builder.append("\":");
		}
		builder.append("\"");
		builder.append(getContent());
		builder.append("\"");
		return builder.toString();
	}

	public int extractElements(String s, int start) {
		int endIndex = start;
		if (s.charAt(start) == '"') {
			endIndex = s.indexOf('\"', start + 1);
			if (endIndex != -1)
				content = s.substring(start + 1, endIndex++);
			else
				endIndex = start;
		}
		return endIndex;
	}

}
