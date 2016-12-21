import java.io.File;
import java.util.ArrayList;


public class Saver {
	
	final static String TRUE = "TRUE"; 
	final static String FALSE = "FALSE";
	
	final static String NULL = "NULL";
	
	final static String SOLID = "SOLID";
	final static String LIQUID = "LIQUID";
	final static String OTHER = "OTHER";
	
	private MainFrame parent;
	
	
	Saver(MainFrame parent) {
		this.parent = parent;
	}
	
	
    public String introSave() {
    	StringBuilder builder = new StringBuilder("START\n\n\n");
    	
    	builder.append("INGREDIENTS:\n\n");    	
    	for (String ingredientName : parent.ingredientList) {
        	builder.append(ingredientName + "\n");
    	}
    	builder.append("\n");
    	
    	
    	Category tempCat = parent.categories.get(Category.UNSOLD_DISHES);
    	
    	builder.append("UNSOLD DISHES:\n\n");    	
    	for (String uDishName : tempCat.getDishes()) {
        	builder.append(uDishName + "\n");
    	}
    	builder.append("\n");
    	
    	
    	tempCat = parent.categories.get(Category.ENTIRE_MENU);
    	
    	builder.append("SOLD DISHES:\n\n");    	
    	for (String sDishName : tempCat.getDishes()) {
        	builder.append(sDishName + "\n");
    	}
    	builder.append("\n");
    	
    	
    	return builder.toString();
    }
    
    public String categorySave() {
    	StringBuilder builder = new StringBuilder("CATEGORIES:\n\n");
    	
    	for (String catName : parent.categoriesList) {
    		
    		builder.append(catName + "\n");
    		if (parent.categories.get(catName).isSold()) {
          		builder.append(TRUE + "\n");
    		} else {
    			builder.append(FALSE + "\n");
    		}

    		
    		ArrayList<String> dishList = parent.categories.get(catName).getDishes();
	    	for (String dishName : dishList) {
	        	builder.append(dishName + "\n");
	    	}
	    	builder.append("\n");
    	}
    	builder.append("\n");
    	
    	return builder.toString();
    }
    
    public String orderListSave(){
    	StringBuilder builder = new StringBuilder("ORDER LIST:\n\n");
    	
    	for (OrderItem item : parent.currentOrder.values()) {
            	builder.append(item.getName() + "\n");
            	builder.append(item.getOrderNum() + "\n");
    	}
    	builder.append("\n");
    	
    	return builder.toString();
    }
    
    public String populateSave() {
    	StringBuilder builder = new StringBuilder("INGREDIENT POPULATE:\n\n");    	
    	
    	MenuItem tempItem = null;
    	for (String ingredientName : parent.ingredientList) {
    		tempItem = parent.itemHash.get(ingredientName);
    		
        	builder.append(ingredientName + "\n");
        	
        	if (tempItem.getCost() != null)
        		builder.append(tempItem.getCost().toString() + "\n");
        	else
        		builder.append(NULL + "\n");
        	
        	if (tempItem.getUnits() != null)
        		builder.append(tempItem.getUnits() + "\n");
        	else
        		builder.append(NULL + "\n");
        	
        	if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID)
        		builder.append(SOLID + "\n");
        	else if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID)
        		builder.append(LIQUID + "\n");
        	else if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.OTHER)
        		builder.append(OTHER + "\n");
        	else
        		builder.append(NULL + "\n");
        	
        	if (tempItem.getIncompleteError())
        		builder.append(TRUE + "\n");
        	else
        		builder.append(FALSE + "\n");
        	
        	if (tempItem.getUnusedWarning())
        		builder.append(TRUE + "\n");
        	else
        		builder.append(FALSE + "\n");
        	
        	if (tempItem.getCostField() != null)
        		builder.append(tempItem.getCostField().toString() + "\n");
        	else
        		builder.append(NULL + "\n");
        	
        	if (tempItem.getPerFigure() != null)
        		builder.append(tempItem.getPerFigure().toString() + "\n");
        	else
        		builder.append(NULL + "\n");
        	
        	for (String useName : tempItem.getUses().keySet()) {
        		builder.append(useName + "\n");
        	}
        	builder.append("\n");
    	}
    	builder.append("\n");

    	
    	builder.append("DISH POPULATE:\n\n");
    	
    	RecipeItem tempRecipeItem = null;
 
    	Category tempCat = parent.categories.get(Category.UNSOLD_DISHES);
    	for (String uDishName : tempCat.getDishes()) {
    		tempItem = parent.itemHash.get(uDishName);
    		
        	builder.append(uDishName + "\n");
        	
        	if (tempItem.getCost() != null)
        		builder.append(tempItem.getCost().toString() + "\n");
        	else
        		builder.append(NULL + "\n");
        	
        	if (tempItem.getUnits() != null)
        		builder.append(tempItem.getUnits() + "\n");
        	else
        		builder.append(NULL + "\n");
        	
        	if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID)
        		builder.append(SOLID + "\n");
        	else if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID)
        		builder.append(LIQUID + "\n");
        	else if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.OTHER)
        		builder.append(OTHER + "\n");        	
        	else
        		builder.append(NULL + "\n");
        	
        	if (tempItem.getIncompleteError())
        		builder.append(TRUE + "\n");
        	else
        		builder.append(FALSE + "\n");
        	
        	if (tempItem.getUnusedWarning())
        		builder.append(TRUE + "\n");
        	else
        		builder.append(FALSE + "\n");
        	
        	if (tempItem.getPrice() != null)
        		builder.append(tempItem.getPrice().toString() + "\n");
        	else
        		builder.append(NULL + "\n");
        	
        	if (tempItem.IsSoldDish())
        		builder.append(TRUE + "\n");
        	else
        		builder.append(FALSE + "\n");

        	if (tempItem.getDefaultBatch() != null)
        		builder.append(tempItem.getDefaultBatch().toString() + "\n");
        	else
        		builder.append(NULL + "\n");
        	
        	for (String useName : tempItem.getUses().keySet()) {
        		builder.append(useName + "\n");
        	}
        	builder.append("\n");
        	
	    	for (String recipeItemName : tempItem.getRecipeItemList().keySet()) {
	    		tempRecipeItem = tempItem.getRecipeItem(recipeItemName);
	    		
	    		builder.append(recipeItemName + "\n");
	
	    		if (tempRecipeItem.getCost() != null)
	    			builder.append(tempRecipeItem.getCost().toString() + "\n");
	    		else
	    			builder.append(NULL + "\n");
	    		
	    		if (tempRecipeItem.getAmountNeeded() != null)
	    			builder.append(tempRecipeItem.getAmountNeeded().toString() + "\n");
	    		else
	    			builder.append(NULL + "\n");
	    		
	    		if (tempRecipeItem.getUnits() != null)
	    			builder.append(tempRecipeItem.getUnits() + "\n");
	    		else
	    			builder.append(NULL + "\n");
	
	    		if (tempRecipeItem.getBatch() != null)
	    			builder.append(tempRecipeItem.getBatch().toString() + "\n");
	    		else
	    			builder.append(NULL + "\n");
	    		
	    		if (tempRecipeItem.getWaste() != null)
	    			builder.append(tempRecipeItem.getWaste().toString() + "\n");
	    		else
	    			builder.append(NULL + "\n");	    		
	    	}
        	builder.append("\n\n");
    	}
    	
    	tempCat = parent.categories.get(Category.ENTIRE_MENU);
    	for (String sDishName : tempCat.getDishes()) {
    		tempItem = parent.itemHash.get(sDishName);
    		
        	builder.append(sDishName + "\n");
        	
        	if (tempItem.getCost() != null)
        		builder.append(tempItem.getCost().toString() + "\n");
        	else
        		builder.append(NULL + "\n");
        	
        	if (tempItem.getUnits() != null)
        		builder.append(tempItem.getUnits() + "\n");
        	else
        		builder.append(NULL + "\n");
        	
       		builder.append(NULL + "\n");
        	
        	if (tempItem.getIncompleteError())
        		builder.append(TRUE + "\n");
        	else
        		builder.append(FALSE + "\n");
        	
        	if (tempItem.getUnusedWarning())
        		builder.append(TRUE + "\n");
        	else
        		builder.append(FALSE + "\n");
        	
        	if (tempItem.getPrice() != null)
        		builder.append(tempItem.getPrice().toString() + "\n");
        	else
        		builder.append(NULL + "\n");
        	
        	if (tempItem.IsSoldDish())
        		builder.append(TRUE + "\n");
        	else
        		builder.append(FALSE + "\n");

        	if (tempItem.getDefaultBatch() != null)
        		builder.append(tempItem.getDefaultBatch().toString() + "\n");
        	else
        		builder.append(NULL + "\n");
        	
        	for (String useName : tempItem.getUses().keySet()) {
        		builder.append(useName + "\n");
        	}
        	builder.append("\n");
        	
	    	for (String recipeItemName : tempItem.getRecipeItemList().keySet()) {
	    		tempRecipeItem = tempItem.getRecipeItem(recipeItemName);
	    		
	    		builder.append(recipeItemName + "\n");
	
	    		if (tempRecipeItem.getCost() != null)
	    			builder.append(tempRecipeItem.getCost().toString() + "\n");
	    		else
	    			builder.append(NULL + "\n");
	    		
	    		if (tempRecipeItem.getAmountNeeded() != null)
	    			builder.append(tempRecipeItem.getAmountNeeded().toString() + "\n");
	    		else
	    			builder.append(NULL + "\n");
	    		
	    		if (tempRecipeItem.getUnits() != null)
	    			builder.append(tempRecipeItem.getUnits() + "\n");
	    		else
	    			builder.append(NULL + "\n");
	
	    		if (tempRecipeItem.getBatch() != null)
	    			builder.append(tempRecipeItem.getBatch().toString() + "\n");
	    		else
	    			builder.append(NULL + "\n");
	    		
	    		if (tempRecipeItem.getWaste() != null)
	    			builder.append(tempRecipeItem.getWaste().toString() + "\n");
	    		else
	    			builder.append(NULL + "\n");
	    	}
        	builder.append("\n\n");
    	}
    	builder.append("\n");
    	
    	
    	return builder.toString();
    }
    
    public String tabSave() {
    	
    	StringBuilder builder = new StringBuilder("TAB 1:\n");
    
    	if (parent.iPricingPane.selected != null)
    		builder.append(parent.iPricingPane.selected.getName() + "\n");
    	else
    		builder.append(NULL + "\n");

    	String button = parent.iPricingPane.radioGroup.getSelection().getActionCommand();
		if (button.equals("ONE"))
			builder.append("0\n");
		else if (button.equals("TWO"))
			builder.append("1\n");
		else 
			builder.append("2\n");

    	if (parent.iPricingPane.displayed1 != null)
    		builder.append(parent.iPricingPane.displayed1.getName() + "\n");
    	else
    		builder.append(NULL + "\n");
    	
    	if (parent.iPricingPane.displayed2 != null)
    		builder.append(parent.iPricingPane.displayed2.getName() + "\n");
    	else
    		builder.append(NULL + "\n");
    	
    	if (parent.iPricingPane.displayed3 != null)
    		builder.append(parent.iPricingPane.displayed3.getName() + "\n\n");
    	else
    		builder.append(NULL + "\n\n");
    	
    	
    	builder.append("TAB 2:\n");
        
    	if (parent.dishCompPane.selectedCat != null)
    		builder.append(parent.dishCompPane.selectedCat.getName() + "\n");
    	else
    		builder.append(NULL + "\n");

    	if (parent.dishCompPane.selectedDish != null)
    		builder.append(parent.dishCompPane.selectedDish.getName() + "\n");
    	else
    		builder.append(NULL + "\n");
    	
    	for (String checkName : parent.dishCompPane.checkedNames) {
    		builder.append(checkName + "\n");
    	}
    	builder.append("\n");
    	
    	
    	builder.append("TAB 3:\n");
        
    	if (parent.catManagementPane.selectedCat != null)
    		builder.append(parent.catManagementPane.selectedCat.getName() + "\n");
    	else
    		builder.append(NULL + "\n");

    	if (parent.catManagementPane.selectedDish != null)
    		builder.append(parent.catManagementPane.selectedDish.getName() + "\n\n");
    	else
    		builder.append(NULL + "\n\n");
    	
    	
    	builder.append("TAB 4:\n");
        
    	if (parent.itemUsagePane.selectedType != null)
    		builder.append(parent.itemUsagePane.selectedType + "\n");
    	else
    		builder.append(NULL + "\n");

    	if (parent.itemUsagePane.selectedItem != null)
    		builder.append(parent.itemUsagePane.selectedItem.getName() + "\n");
    	else
    		builder.append(NULL + "\n");
    	
    	if (parent.itemUsagePane.displayInventory)
    		builder.append(TRUE + "\n\n");
    	else
    		builder.append(FALSE + "\n\n");
    	
    	
    	builder.append("TAB 5:\n");
        
    	if (parent.resultsPane.selectedCat != null)
    		builder.append(parent.resultsPane.selectedCat.getName() + "\n");
    	else
    		builder.append(NULL + "\n");

    	if (parent.resultsPane.selectedDish != null)
    		builder.append(parent.resultsPane.selectedDish.getName() + "\n");
    	else
    		builder.append(NULL + "\n");
    	
    	if (parent.resultsPane.displayBreakdown)
    		builder.append(TRUE + "\n");
    	else
    		builder.append(FALSE + "\n");
    	
    	if (parent.resultsPane.displayInventory)
    		builder.append(TRUE + "\n\n");
    	else
    		builder.append(FALSE + "\n\n");
    	
    	
       	builder.append("\nEND");
    	
    	
    	return builder.toString();
    }
    
    public String saveCurrentState() {
    	
    	StringBuilder builder = new StringBuilder(introSave());
    	builder.append(categorySave());
    	builder.append(orderListSave());
    	builder.append(populateSave());
    	builder.append(tabSave());
    	
    	return builder.toString();
    }
}
