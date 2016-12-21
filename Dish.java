import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class Dish extends MenuItem {

	public static enum UNSOLD_TYPE {SOLID, LIQUID, OTHER}
	
	Double price;
	boolean isSoldDish;
		
	Double defaultUnitNum;
	
	HashMap<String, RecipeItem> recipe;	
	ArrayList<String> categories;
	
	public Dish(String name, boolean isSoldDish) {
		super(name, true);
		
		this.isSoldDish = isSoldDish;

		this.price = null;
		this.defaultUnitNum = null;
				
		this.categories = new ArrayList<String>();
		if (isSoldDish) {
			this.units = MenuCompositionPane.DISH_UNITS[1];
			categories.add(Category.ENTIRE_MENU);
		} else {
			categories.add(Category.UNSOLD_DISHES);
		}
		
		this.recipe = new HashMap<String, RecipeItem>();
	}
	
	public Dish(String name, MenuItem oldDish) {
		super(name, true);
		
		this.isSoldDish = oldDish.IsSoldDish();
		
		if (oldDish.getPrice() != null)
			this.price = new Double(oldDish.getPrice());
		else
			this.price = null;
		
		if (oldDish.getCost() != null)
			this.cost = new Double(oldDish.getCost());
		else
			this.cost = null;
		
		if (oldDish.getDefaultBatch() != null)
			this.defaultUnitNum = new Double(oldDish.getDefaultBatch());
		else
			this.defaultUnitNum = null;
		
		if (oldDish.getUnits() != null)
			this.units = new String(oldDish.getUnits());
		else
			this.units = null;
		
		this.unsoldType = oldDish.unsoldType;
		
		this.categories = (ArrayList<String>) oldDish.getCategories().clone();		

		this.recipe = new HashMap<String, RecipeItem>();
		Set<String> oldDishSet = oldDish.getRecipeItemList().keySet();
		
		for (String itemName : oldDishSet) {
			oldDish.getRecipeItem(itemName).getItem().addUse(name, this);
			this.recipe.put(new String(itemName), new RecipeItem(oldDish.getRecipeItem(itemName)));
		}
		
		this.incompleteError = oldDish.incompleteError;
		this.unusedWarning = oldDish.unusedWarning;
	}
	

	public void setUniqueItems(Double newPrice, boolean isSold, Double batchFigure) {
		price = newPrice;
		isSoldDish = isSold;
		defaultUnitNum = batchFigure;
	}
	
	public void setIsSold (boolean sold) {
		isSoldDish = sold;
	}
	
	public boolean IsSoldDish() {
		return isSoldDish;
	}
	
	//Price wont change unless valid (i.e. can never be null)
	public void setPrice(Double newPrice) {
		Double prevPrice = price;
		
		price = newPrice;
		
		if (prevPrice == null || newPrice == null)
			setErrorFlag();
	}
	
	public Double getPrice() {
		return price;
	}
	
	// Instance Methods
	public void setCost() { //will only set cost if there is no error
		cost = 0.0;
		
		//Change each batch size
		Set<String> keys = recipe.keySet();
		
		Iterator<String> it = keys.iterator(); 
		while (it.hasNext())
			cost += recipe.get(it.next()).getCost();
	}
	
	public void setDefaultBatch(Double allBatches) {
		defaultUnitNum = allBatches;
		
		//Change each batch size
		Set<String> keys = recipe.keySet();
		
		Iterator<String> it = keys.iterator(); 
		while (it.hasNext()) {
			recipe.get(it.next()).setBatch(defaultUnitNum);
		}
		
		this.setErrorFlag();
	}
	
	public Double getDefaultBatch() {
		return defaultUnitNum;
	}
	
	public void changeIsSoldDish() {
		if (isSoldDish) {
			price = null;
			
			units = null;
			unsoldType = null;
			
			categories = new ArrayList<String>();
			this.addCategory(Category.UNSOLD_DISHES);
			
			isSoldDish = false;
		} else {			
			units = MenuCompositionPane.DISH_UNITS[1];
			unsoldType = null;
			
			categories = new ArrayList<String>();
			this.addCategory(Category.ENTIRE_MENU);
			
			isSoldDish = true;
		}
		
		RecipeItem tempRecipeItem = null;
		for (MenuItem tempItem : this.getUses().values()) {
			tempRecipeItem = tempItem.getRecipeItem(this.getName());
			tempRecipeItem.setUnits(null);
		}
		
		this.setWarningFlag();
		this.setErrorFlag();
	}
	
	public void addRecipeItem(MenuItem item, Double amountNeeded, String units, Double batchSize, Double waste) {
			// Put recipe item in recipe's
			recipe.put(item.getName(), new RecipeItem(item, amountNeeded, units, batchSize, waste));
			
			// Add this item to recipes used List
			item.addUse(this.getName(), this);

			// Check if it is a complete recipeItem
			this.setErrorFlag();
	}
	
	//Add without checks for opening
	public void addRecipeItem(MenuItem item, Double ingredientCost, Double amountNeeded, String units, Double batchSize, Double waste) {
		// Put recipe item in recipe's
		recipe.put(item.getName(), new RecipeItem(item, ingredientCost, amountNeeded, units, batchSize, waste));
		
		// Add this item to recipes used List
		item.uses.put(this.getName(), this);
	}
	
	public void removeRecipeItem(String recipeItemName) {
		// Take this item out of recipe's used list
		MenuItem recipeItem = recipe.get(recipeItemName).getItem();
		recipeItem.removeUse(this.getName());

		// Remove recipe from list
		recipe.remove(recipeItemName);
		
		// Add this item to recipes used List
		this.setErrorFlag();
	}
		
	public RecipeItem getRecipeItem(String recipeItemName) {
		return recipe.get(recipeItemName);
	}
	
	
	public HashMap<String, RecipeItem> getRecipeItemList() {
		return recipe;
	}
	
	public void addCategory(String catName) {
		MainFrame.InsertIntoList(categories, catName);
	}
	
	public void removeCategory(String catName) {
		categories.remove(catName);
	}
	
	public ArrayList<String> getCategories() {
		return categories;
	}
	
	//For non-sold dish, check whether is has uses
	public void setWarningFlag() {
		if (!isSoldDish && uses.isEmpty())
			unusedWarning = true;
		else
			unusedWarning = false;
	}

	//Needs at least one complete ingredient and no missing missing fields
	public void setErrorFlag() {
		boolean prevError = incompleteError;
		incompleteError = false;		
		
		if (this.units == null)
			incompleteError = true;
		
		if (recipe.size() == 0) 
			incompleteError = true;
	
		if (incompleteError == false) {
			Set<String> keys = recipe.keySet();
			
			Iterator<String> it = keys.iterator(); 
			while (it.hasNext()) {
				if (!recipe.get(it.next()).isComplete()) {
					incompleteError = true;
					break;
				}
			}
		}
		
		if (!incompleteError) {
			this.setCost();
			
			Set<String> keys = uses.keySet();
			Iterator<String> it = keys.iterator(); 
			
			while (it.hasNext())
				uses.get(it.next()).setErrorFlag();
		}
		
		if (incompleteError && !prevError) {
			Set<String> keys = uses.keySet();
			Iterator<String> it = keys.iterator(); 
			
			while (it.hasNext())
				uses.get(it.next()).setErrorFlag();
		}
	}	

	
	//Get array of String as possiblilitys if used in ingredient
	public static String[] getDishPossibilities(MenuItem dish) {
		String[] optionList = null;
		
		if (dish.IsSoldDish()) {
			optionList = MenuCompositionPane.DISH_UNITS;
		} else {
			if (dish.getUnsoldType() == UNSOLD_TYPE.SOLID)
				optionList = MenuCompositionPane.SOLID_UNITS;
			else if (dish.getUnsoldType() == UNSOLD_TYPE.LIQUID)
				optionList = MenuCompositionPane.LIQUID_UNITS;
			else if (dish.getUnsoldType() == UNSOLD_TYPE.OTHER)
				optionList = MenuCompositionPane.OTHER_UNITS;
			else
				optionList = MenuCompositionPane.INGREDIENT_INCOMPLETE;
			
		}
			
		return optionList;
	}
	
	
	// Ingredient Methods 
	//to satisfy abstract class requirement
	public void setCostField(Double quant) { }
	public Double getCostField() { return null; }

	public void setPerFigure(Double quant) { }
	public Double getPerFigure() { return null; }
}
