package eg.edu.alexu.csd.oop.draw.json;

public interface JsonDataType {

	public String getName();

	public void setName(String name);

	public String getContent();

	public String toString();
	
	public int extractElements(String s, int start);
}
