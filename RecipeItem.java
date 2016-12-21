

//WHENEVER ANY FIELD IS MODIFIED THRO UPDATE THEN CALL PARENTS SET ERROR FLAG
public class RecipeItem {	
	MenuItem item;
	Double costPerOrder;
	
	Double amountNeeded;
	String units;
	Double batchSize;
	Double waste;

	RecipeItem(MenuItem item, Double amountNeeded, String units, Double batchSize, Double waste) {
		this.item = item;
		this.amountNeeded = amountNeeded;
		this.units = units;
		this.batchSize = batchSize;
		this.waste = waste;
		
		costPerOrder = null;
		isComplete();
	}

	RecipeItem(MenuItem item, Double ingredientCost, Double amountNeeded, String units, Double batchSize, Double waste) {
		this.item = item;
		
		this.costPerOrder = ingredientCost;
		this.amountNeeded = amountNeeded;
		this.units = units;
		this.batchSize = batchSize;
		this.waste = waste;
	}

	
	RecipeItem(RecipeItem clone) {
		this.item = clone.item;
		
		if (clone.amountNeeded != null)
			this.amountNeeded = new Double(clone.amountNeeded);
		else
			this.amountNeeded = null;
			
		if (clone.units != null)
			this.units = new String(clone.units);
		else
			this.units = null;
		
		if (clone.batchSize != null)
			this.batchSize = new Double(clone.batchSize);
		else
			this.batchSize = null;
		
		if (clone.waste != null)
			this.waste = new Double(clone.waste);
		else
			this.waste = null;
		
		if (clone.costPerOrder != null)
			this.costPerOrder = new Double(clone.costPerOrder);
		else
			this.costPerOrder = null;
	}

	public Double calculateCost() {
		//if ingredient, then must convert to get factor, multiply by price
		Double convertedItemCost = item.getCost() * (1/(1 - (waste/100)));
		
		//Calculate cost with factor
		if (item.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID)
			convertedItemCost *= getSolidFactor(item.getUnits(), units);
		if (item.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID)
			convertedItemCost *= getLiquidFactor(item.getUnits(), units);
			
		//return converted cost of ingredient
		Double totalCost = ((convertedItemCost * amountNeeded) / batchSize);
		return totalCost;
	}
	
	public MenuItem getItem() {
		return item;
	}
	
	public Double getCost() {
		return costPerOrder;
	}
	
	public void setAmountNeeded(Double tempAmount) {
		amountNeeded = tempAmount;
	}
	
	public Double getAmountNeeded() {
		return amountNeeded;
	}
	
	public void setUnits(String tempUnits) {
		units = tempUnits;
	}
	
	public String getUnits() {
		return units;
	}
	
	public void setBatch(Double allBatches) {
		batchSize = allBatches;
	}
	
	public Double getBatch() {
		return batchSize;
	}
	
	public void setWaste(Double tempWaste) {
		waste = tempWaste;
	}
	
	public Double getWaste() {
		return waste;
	}

	
	public boolean isComplete() {
		if (item == null || amountNeeded == null || units == null
				|| batchSize == null || waste == null)
			return false;
		
		if (item.getIncompleteError())
			return false;

		costPerOrder = this.calculateCost();
		return true;
	}
    
	// Gets unit2 in terms of unit1 (e.g if unit1 g, unit2 equal kg, then returns 1000)
	public static Double getSolidFactor(String unit1, String unit2) {
		Double finalFactor = 1.0;
		
		if (unit1 == unit2)
			return finalFactor;
		
		double factor1 = 1.0, factor2 = 1.0;
		
		if(unit1.equals(MenuCompositionPane.SOLID_UNITS[2])) {
			factor1 = MenuCompositionPane.GRAMS_TO_OUNCES;
		} else if(unit1.equals(MenuCompositionPane.SOLID_UNITS[3])) {
			factor1 = MenuCompositionPane.GRAMS_TO_POUND;
		} else if (unit1.equals(MenuCompositionPane.SOLID_UNITS[4])) {
			factor1 = MenuCompositionPane.GRAMS_TO_KILO;
		}
		
		if(unit2.equals(MenuCompositionPane.SOLID_UNITS[2])) {
			factor2 = MenuCompositionPane.GRAMS_TO_OUNCES;
		} else if(unit2.equals(MenuCompositionPane.SOLID_UNITS[3])) {
			factor2 = MenuCompositionPane.GRAMS_TO_POUND;
		} else if (unit2.equals(MenuCompositionPane.SOLID_UNITS[4])) {
			factor2 = MenuCompositionPane.GRAMS_TO_KILO;
		}
		
		finalFactor = (factor1/factor2);
		return finalFactor;
	}

	// Gets unit2 in terms of unit1 (e.g if unit1 g, unit2 equal kg, then returns 1000)
	public static Double getLiquidFactor(String unit1, String unit2) {
		Double finalFactor = 1.0;
		
		if (unit1 == unit2)
			return finalFactor;
		
		double factor1 = 1.0, factor2 = 1.0;
		
		if(unit1.equals(MenuCompositionPane.LIQUID_UNITS[1])) {
			factor1 = MenuCompositionPane.LOUNCES_TO_ML;
		} else if(unit1.equals(MenuCompositionPane.LIQUID_UNITS[3])) {
			factor1 = MenuCompositionPane.LOUNCES_TO_CUP;
		} else if (unit1.equals(MenuCompositionPane.LIQUID_UNITS[4])) {
			factor1 = MenuCompositionPane.LOUNCES_TO_LITER;
		} else if (unit1.equals(MenuCompositionPane.LIQUID_UNITS[5])) {
			factor1 = MenuCompositionPane.LOUNCES_TO_GALLON;
		}
		
		if(unit2.equals(MenuCompositionPane.LIQUID_UNITS[1])) {
			factor2 = MenuCompositionPane.LOUNCES_TO_ML;
		} else if(unit2.equals(MenuCompositionPane.LIQUID_UNITS[3])) {
			factor2 = MenuCompositionPane.LOUNCES_TO_CUP;
		} else if (unit2.equals(MenuCompositionPane.LIQUID_UNITS[4])) {
			factor2 = MenuCompositionPane.LOUNCES_TO_LITER;
		} else if (unit2.equals(MenuCompositionPane.LIQUID_UNITS[5])) {
			factor2 = MenuCompositionPane.LOUNCES_TO_GALLON;
		}
		
		finalFactor = (factor1/factor2);
		return finalFactor;
	}
}
