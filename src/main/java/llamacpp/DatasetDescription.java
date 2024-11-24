package llamacpp;

import java.util.Vector;

public class DatasetDescription {
	public String title = "";
	public String context = "";
	public Vector<String> variableNames = new Vector<String>();
	public Vector<String> variableDescriptions = new Vector<String>();

	public String toString() {
		String text = "title: \"" + title + "\"\n" + "context: \"" + context + "\"\n"
				+ "column names: \tcolumn descriptions:\n";
		for (int i = 0; i < variableNames.size(); i++) {
			text += "\"" + variableNames.get(i) + "\"\t\"" + variableDescriptions.get(i) + "\"\n";
		}
		return text;
	}
}