import java.util.ArrayList;


public class Category {
	final static String ENTIRE_MENU = "Entire Menu";
	final static String UNSOLD_DISHES = "Unsold Component Dishes";
	
	String name;
	boolean isSold;
	ArrayList<String> dishes;
	
	public Category(String name, boolean isSold) {
		this.name = name;
		this.isSold = isSold;
		dishes = new ArrayList<String>();
	}
	
	public Category(String newName, Category clone) {
		this.name = new String(newName);
		this.isSold = clone.isSold();
		this.dishes = (ArrayList<String>) clone.getDishes().clone();		
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public boolean isSold() {
		return isSold;
	}

	public void setIsSold(boolean newIsSold) {
		isSold = newIsSold;
	}
	
	public void addDish(String dishName) {
		MainFrame.InsertIntoList(this.dishes, dishName);
	}
	
	public void removeDish(String dishName) {
		dishes.remove(dishName);
	}
	
	public ArrayList<String> getDishes() {
		return dishes;
	}
}
