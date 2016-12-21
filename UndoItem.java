
public class UndoItem {

	public String undoMsg = null;	
	public String redoMsg = null;
	public String state;
	
	
	public UndoItem(String undoMessage, String redoMessage, String state) {
		
		this.undoMsg = undoMessage;
		this.redoMsg = redoMessage;
		this.state = state;
	}
}
