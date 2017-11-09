import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public abstract class MenuItem {
	String name;
	Double cost;
	
	boolean isDish;
	HashMap<String, MenuItem> uses;
	
	String units;
	Dish.UNSOLD_TYPE unsoldType;
	
	//Incomplete recipe (cannot add ingredient until complete) or fields
	boolean incompleteError;
	
	//Unused component dish or ingredient
	boolean unusedWarning;
	
	public MenuItem(String name, boolean isDish){
		this.name = name;
		this.isDish = isDish;
		
		this.cost = null;
		this.uses = new HashMap<String, MenuItem>();
		
		this.units = null;
		this.unsoldType = null;
		
		this.incompleteError = true;
		this.unusedWarning = true;
	}
	
	//Real Inherited methods
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public boolean isDish() {
		return isDish;
	}
	
	public Double getCost() {
		return cost;
	}
	
	public void setCost(Double newCost) {
		cost = newCost;
	}
	
	public void addUse(String dishName, MenuItem dish) {
		uses.put(dishName, dish);
		this.setWarningFlag();
	}
	
	public void removeUse(String dishName) {
		uses.remove(dishName);
		this.setWarningFlag();
	}
	
	public HashMap<String, MenuItem> getUses() {
		return uses;
	}
	
	public void setUnits(String tempUnit) {
		Dish.UNSOLD_TYPE lastUnitType = unsoldType;
		units = tempUnit;
		
		//Check to determine whethere solid or liquid
		unsoldType = null;
		if (units != null) {
			int liqNum = MenuCompositionPane.LIQUID_UNITS.length;
			for (int i = 1; i < liqNum; i++) {
				if (MenuCompositionPane.LIQUID_UNITS[i].equals(units))	
					unsoldType = Dish.UNSOLD_TYPE.LIQUID;
			}
			
			int solidNum = MenuCompositionPane.SOLID_UNITS.length;
			for (int i = 1; i < solidNum; i++) {
				if (MenuCompositionPane.SOLID_UNITS[i].equals(units))	
					unsoldType = Dish.UNSOLD_TYPE.SOLID;
			}
			
			int otherNum = MenuCompositionPane.OTHER_UNITS.length;
			for (int i = 1; i < otherNum; i++) {
				if (MenuCompositionPane.OTHER_UNITS[i].equals(units))	
					unsoldType = Dish.UNSOLD_TYPE.OTHER;
			}
		}
		
		if (lastUnitType != unsoldType) {
			Set<String> keys = uses.keySet();
			Iterator<String> it = keys.iterator(); 
			
			while (it.hasNext()) {
				RecipeItem rItem = uses.get(it.next()).getRecipeItemList().get(this.name);
				rItem.setUnits(null);
			}
		}
	}
	
	public String getUnits() {
		return units;
	}
	
	public void setUnsoldType(Dish.UNSOLD_TYPE newType) {
		unsoldType = newType;
	}
	
	public Dish.UNSOLD_TYPE getUnsoldType() {
		return unsoldType;
	}
	
	public boolean getIncompleteError() {
		return incompleteError;
	}
	
	public void setIncompleteError(boolean newError) {
		incompleteError = newError;
	}
	
	public boolean getUnusedWarning() {
		setWarningFlag();
		return unusedWarning;
	}
	
	public void setUnusedWarning(boolean newError) {
		unusedWarning = newError;
	}

	
	//Real abstract methods
	public abstract void setCost();
	public abstract void setWarningFlag();
	public abstract void setErrorFlag();
	
	
	//Ingredient specific methods
	public abstract void setCostField(Double quant);	
	public abstract Double getCostField();
	
	public abstract void setPerFigure(Double quant);	
	public abstract Double getPerFigure();
	
	
	//Dish Specific methods
	public abstract void setUniqueItems(Double newPrice, boolean isSold, Double batchFigure);
	
	public abstract void setPrice(Double tempPrice);
	public abstract Double getPrice();	
	public abstract void changeIsSoldDish();
	
	public abstract void setIsSold(boolean sold);
	public abstract boolean IsSoldDish();
	
	public abstract void setDefaultBatch(Double allBatches);
	public abstract Double getDefaultBatch();
	
	public abstract void addRecipeItem(MenuItem item, Double amountNeeded, String units, Double batchSize, Double waste);
	public abstract void addRecipeItem(MenuItem item, Double ingredientCost, Double amountNeeded, String units, Double batchSize, Double waste);
	public abstract void removeRecipeItem(String recipeItemName);
	public abstract RecipeItem getRecipeItem(String recipeItemName);
	public abstract HashMap<String, RecipeItem> getRecipeItemList();
	
	public abstract void addCategory(String catName);
	public abstract void removeCategory(String catName);
	public abstract ArrayList<String> getCategories();	
}
