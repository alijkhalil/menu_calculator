import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class WriteFile {
	
	final public String path;
	private boolean appendToFile = false;
	
	public  WriteFile(String filePath) {
		this.path = filePath;
	}
	
	public  WriteFile(String filePath, boolean appendVal) {
		this.path = filePath;
		this.appendToFile = appendVal;
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean getAppendBool() {
		return appendToFile;
	}
	
	public void setAppendBool(boolean newAppendVal) {
		appendToFile = newAppendVal;
	}
	
	public void writeToFile(String inputText) throws IOException {
			
			FileWriter write = new FileWriter(path, appendToFile);
			PrintWriter printLine = new PrintWriter(write);
			
			printLine.printf("%s", inputText);
			
			printLine.close();
	}
}
