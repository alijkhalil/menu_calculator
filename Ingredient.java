import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class Ingredient extends MenuItem {
	Double costField;
	Double perFigure;
	
	public Ingredient(String name) {
		super(name, false);
		
		costField = null;
		perFigure = null;		
	}
	
	public Ingredient(String name, MenuItem oldIngredient) {
		super(name, false);
		
		if (oldIngredient.cost != null)
			this.cost = new Double(oldIngredient.cost);
		else
			this.cost = null;
	
		if (oldIngredient.units != null)
			this.units = new String(oldIngredient.units);
		else
			this.units = null;
		
		this.unsoldType = oldIngredient.unsoldType;
		
		this.incompleteError = oldIngredient.incompleteError;
		this.unusedWarning = oldIngredient.unusedWarning;
		
		if (oldIngredient.getCostField() != null)
			this.costField = new Double(oldIngredient.getCostField());
		else
			this.costField = null;
			
		if (oldIngredient.getPerFigure() != null)
			this.perFigure = new Double(oldIngredient.getPerFigure());
		else
			this.perFigure = null;
	}

	// Instance Methods 
	public void setCost() {
		cost = (costField/perFigure);
	}
	
	public void setCostField(Double field) {
		costField = field;
	}
	
	public Double getCostField() {
		return costField;
	}
	
	public void setPerFigure(Double quant) {
		perFigure = quant;
	}
	
	public Double getPerFigure() {
		return perFigure;
	}
	
	//call both in the event of any change in fields from ANY SETTER method
	public void setWarningFlag() {
		if (uses.isEmpty())
			unusedWarning = true;
		else
			unusedWarning = false;
	}

	public void setErrorFlag() {
		boolean prevError = incompleteError;
		
		if ((perFigure == null) || (units == null) || (costField == null))
			incompleteError = true;
		else
			incompleteError = false;
		
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
	
	// Dish Methods to satisfy abstract class requirement
	public void setUniqueItems(Double newPrice, boolean isSold, Double batchFigure) { }
	
	public void setIsSold(boolean sold) { }
	public boolean IsSoldDish() { return false; }
	
	public void setPrice(Double tempPrice) { }
	public Double getPrice() { return null; }
	public void changeIsSoldDish() { }

	public void setDefaultBatch(Double allBatches) { }
	public Double getDefaultBatch() { return null; }
	
	public void addRecipeItem(MenuItem item, Double amountNeeded, String units, Double batchSize, Double waste) { }
	public void addRecipeItem(MenuItem item, Double ingredientCost, Double amountNeeded, String units, Double batchSize, Double waste) { }
	public RecipeItem getRecipeItem(String recipeItemName) { return null; }
	public void removeRecipeItem(String recipeItemName) { }
	public HashMap<String, RecipeItem> getRecipeItemList() { return null; }

	public void addCategory(String catName) { }
	public void removeCategory(String catName) { }
	public ArrayList<String> getCategories() { return null; }
}	
