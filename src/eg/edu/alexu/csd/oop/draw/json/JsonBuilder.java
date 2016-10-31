package eg.edu.alexu.csd.oop.draw.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

public class JsonBuilder extends JsonObject {

	public void buildTo(OutputStream out) throws IOException {
		out.write(this.toString().getBytes());
		out.close();
	}

	public JsonBuilder parse(File inputFile) {
		try {
			FileReader fileReader = new FileReader(inputFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int br;
			StringBuilder strBuilder = new StringBuilder();
			boolean ignoreSpace = true;
			while ((br = bufferedReader.read()) != -1) {
				char c = (char) br;
				ignoreSpace = ignoreSpace ^ (c == '"');
				if (!ignoreSpace || !Character.isWhitespace(c))
					strBuilder.append(c);
			}
			bufferedReader.close();
			if (strBuilder.charAt(0) == '{' && strBuilder.charAt(strBuilder.length() - 1) == '}') {
				this.extractElements(strBuilder.toString(), 0);
			}
			//System.out.println(this);
		} catch (FileNotFoundException ex) {
			// ex.printStackTrace();
			throw new RuntimeException("no saved history found");
		} catch (IOException ex) {
			// ex.printStackTrace();
			throw new RuntimeException("error while loading history");
		} catch (Exception ex) {
			throw new RuntimeException("error loading history: file is not well-formated");
		}

		return null;
	}
}
