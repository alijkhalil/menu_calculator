

public class OrderItem {
	String dishName;
	int quantity;
	
	OrderItem (String dishName, int quantity) {
		this.dishName = dishName;
		this.quantity = quantity;
	}
	
	public String getName() {
		return dishName;
	}
	
	public void setName(String newName) {
		dishName = newName;
	}
	
	public int getOrderNum() {
		return quantity;
	}
	
	public void addOrders(int newOrders) {
		quantity += newOrders;
	}
	
	public boolean removeOrders(int newOrders) {
		quantity -= newOrders;
		
		if (quantity < 1)
			return false;
		
		return true;
	}
}
