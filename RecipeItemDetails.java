import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;


public class RecipeItemDetails {

	public String name;
	public Double amountPerOrder;
	public String orginalUnits;
	public Double cost;
	
	RecipeItemDetails(String title, Double amount, String units, Double money) {
		this.name = title;
		this.amountPerOrder = amount;
		this.orginalUnits = units;
		this.cost = money;
	}
	
	public static HashMap<String, RecipeItemDetails> getAllIngredientsBase(MenuItem dish, Double factor, HashMap<String, RecipeItemDetails> modifableMap, boolean inventoryUnits) {
		
		Collection<RecipeItem> recipeList = dish.getRecipeItemList().values();
		
		MenuItem tempItem = null;
		for (RecipeItem tempRecipeItem : recipeList) {
			tempItem = tempRecipeItem.getItem();
			if (!tempItem.isDish()) {
				if (modifableMap.get(tempItem.getName()) == null) {
					if (inventoryUnits) {
						if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID) {
							modifableMap.put(tempItem.getName(), new RecipeItemDetails(tempItem.getName(), 
										(RecipeItem.getSolidFactor(tempItem.getUnits(), tempRecipeItem.getUnits()) * factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), 
										tempItem.getUnits(), (factor * tempRecipeItem.getCost())));
						
						} else if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID) {
							modifableMap.put(tempItem.getName(), new RecipeItemDetails(tempItem.getName(), 
										(RecipeItem.getLiquidFactor(tempItem.getUnits(), tempRecipeItem.getUnits()) * factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), 
										tempItem.getUnits(), (factor * tempRecipeItem.getCost())));
						
						} else {
							modifableMap.put(tempItem.getName(), new RecipeItemDetails(tempItem.getName(), (factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), 
										tempItem.getUnits(), (factor * tempRecipeItem.getCost())));
						}
					} else {
						modifableMap.put(tempItem.getName(), new RecipeItemDetails(tempItem.getName(), 
													(factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), 
															tempRecipeItem.getUnits(), (factor * tempRecipeItem.getCost())));
					}
				} else {
					if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID) {
						modifableMap.get(tempItem.getName()).amountPerOrder += (factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))) *
																			RecipeItem.getSolidFactor(modifableMap.get(tempItem.getName()).orginalUnits, tempRecipeItem.getUnits());
					
					} else if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID) {
						modifableMap.get(tempItem.getName()).amountPerOrder += (factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))) *
																			RecipeItem.getLiquidFactor(modifableMap.get(tempItem.getName()).orginalUnits, tempRecipeItem.getUnits());
					
					} else {	
						modifableMap.get(tempItem.getName()).amountPerOrder += (factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100))));
					}
					
					modifableMap.get(tempItem.getName()).cost += (factor * tempRecipeItem.getCost());
					
				}
			} else if (tempItem.IsSoldDish()) {
				
				getAllIngredientsBase(tempItem, factor * (tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), modifableMap, inventoryUnits);
			} else {
				if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID) {
					getAllIngredientsBase(tempItem, factor * RecipeItem.getSolidFactor(tempItem.getUnits(), tempRecipeItem.getUnits()) * 
														(tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), modifableMap, inventoryUnits);
				
				} else if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID) {
					getAllIngredientsBase(tempItem, factor * RecipeItem.getLiquidFactor(tempItem.getUnits(), tempRecipeItem.getUnits()) * 
							(tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), modifableMap, inventoryUnits);
				
				} else {
					getAllIngredientsBase(tempItem, factor * (tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), modifableMap, inventoryUnits);
				}
			}
		}
		
		return modifableMap;
	}
	
	public static HashMap<String, RecipeItemDetails> getAllIngredientsInInventoryUnits(MenuItem dish, Double factor, HashMap<String, RecipeItemDetails> modifableMap) {
		return getAllIngredientsBase(dish, factor, modifableMap, true);
	}
	
	public static HashMap<String, RecipeItemDetails> getAllIngredients(MenuItem dish, Double factor, HashMap<String, RecipeItemDetails> modifableMap) {
		return getAllIngredientsBase(dish, factor, modifableMap, false);
	}

	public static HashMap<String, RecipeItemDetails> getAllComponentsBase(MenuItem dish, Double factor, HashMap<String, RecipeItemDetails> modifableMap, boolean inventoryUnits) {
		
		Collection<RecipeItem> recipeList = dish.getRecipeItemList().values();
		
		MenuItem tempItem = null;
		for (RecipeItem tempRecipeItem : recipeList) {
			tempItem = tempRecipeItem.getItem();
			
			if (modifableMap.get(tempItem.getName()) == null) {
				if (inventoryUnits) {
					if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID) {
						modifableMap.put(tempItem.getName(), new RecipeItemDetails(tempItem.getName(), 
									(RecipeItem.getSolidFactor(tempItem.getUnits(), tempRecipeItem.getUnits()) * factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), 
									tempItem.getUnits(), (factor * tempRecipeItem.getCost())));
					
					} else if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID) {
						modifableMap.put(tempItem.getName(), new RecipeItemDetails(tempItem.getName(), 
									(RecipeItem.getLiquidFactor(tempItem.getUnits(), tempRecipeItem.getUnits()) * factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), 
									tempItem.getUnits(), (factor * tempRecipeItem.getCost())));
					
					} else {
						modifableMap.put(tempItem.getName(), new RecipeItemDetails(tempItem.getName(), (factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), 
									tempItem.getUnits(), (factor * tempRecipeItem.getCost())));
					}
				} else {
					modifableMap.put(tempItem.getName(), new RecipeItemDetails(tempItem.getName(), 
												(factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), 
														tempRecipeItem.getUnits(), (factor * tempRecipeItem.getCost())));
				}
			} else {
				if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID) {
					modifableMap.get(tempItem.getName()).amountPerOrder += (factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))) *
																		RecipeItem.getSolidFactor(modifableMap.get(tempItem.getName()).orginalUnits, tempRecipeItem.getUnits());
				
				} else if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID) {
					modifableMap.get(tempItem.getName()).amountPerOrder += (factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))) *
																		RecipeItem.getLiquidFactor(modifableMap.get(tempItem.getName()).orginalUnits, tempRecipeItem.getUnits());
				
				} else {	
					modifableMap.get(tempItem.getName()).amountPerOrder += (factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100))));
				}
				
				modifableMap.get(tempItem.getName()).cost += (factor * tempRecipeItem.getCost());
				
			}
		}
		
		return modifableMap;
	}

	public static HashMap<String, RecipeItemDetails> getAllComponentsInInventoryUnits(MenuItem dish, Double factor, HashMap<String, RecipeItemDetails> modifableMap) {
		return getAllComponentsBase(dish, factor, modifableMap, true);
	}
	
	public static HashMap<String, RecipeItemDetails> getAllComponents(MenuItem dish, Double factor, HashMap<String, RecipeItemDetails> modifableMap) {
		return getAllComponentsBase(dish, factor, modifableMap, false);
	}
	
	public static void getItemInfoBase(MenuItem dish, MenuItem infoItem, Double factor, RecipeItemDetails returnRecipeItem, boolean inventoryUnits) {
		Collection<RecipeItem> recipeList = dish.getRecipeItemList().values();
		
		MenuItem tempItem = null;
		for (RecipeItem tempRecipeItem : recipeList) {
			tempItem = tempRecipeItem.getItem();
		
			if (infoItem.getName().equals(tempItem.getName())) {
				if (returnRecipeItem.name == null) {
					
					returnRecipeItem.name = tempItem.getName();
					returnRecipeItem.cost = (factor * tempRecipeItem.getCost());
					
					if (inventoryUnits) {
						if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID) {
							returnRecipeItem.amountPerOrder = (RecipeItem.getSolidFactor(tempItem.getUnits(), tempRecipeItem.getUnits()) *
																factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100))));						
						
						} else if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID) {
							returnRecipeItem.amountPerOrder = (RecipeItem.getLiquidFactor(tempItem.getUnits(), tempRecipeItem.getUnits()) *
																factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100))));
						
						} else {
							returnRecipeItem.amountPerOrder = (factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100))));
						}
						
						returnRecipeItem.orginalUnits = tempItem.getUnits();	
					
					} else {
						returnRecipeItem.amountPerOrder = (factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100))));
						returnRecipeItem.orginalUnits = tempRecipeItem.getUnits();
					}
				} else {
					if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID) {
						returnRecipeItem.amountPerOrder += (factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))) *
																			RecipeItem.getSolidFactor(returnRecipeItem.orginalUnits, tempRecipeItem.getUnits());
					} else if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID) {
						returnRecipeItem.amountPerOrder += (factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))) *
																			RecipeItem.getLiquidFactor(returnRecipeItem.orginalUnits, tempRecipeItem.getUnits());
					} else {
						returnRecipeItem.amountPerOrder += (factor * tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100))));
					}
					
					returnRecipeItem.cost += (factor * tempRecipeItem.getCost());
					
				}
			} else if (tempItem.isDish()) {
				if (tempItem.IsSoldDish()) {
					
					getItemInfoBase(tempItem, infoItem, factor * (tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), returnRecipeItem, inventoryUnits);
				} else {
					if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID) {
						getItemInfoBase(tempItem, infoItem, factor * RecipeItem.getSolidFactor(tempItem.getUnits(), tempRecipeItem.getUnits()) * 
															(tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), returnRecipeItem, inventoryUnits);
					
					} else if (tempItem.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID) {
						getItemInfoBase(tempItem, infoItem, factor * RecipeItem.getLiquidFactor(tempItem.getUnits(), tempRecipeItem.getUnits()) * 
								(tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), returnRecipeItem, inventoryUnits);
					
					} else {
						getItemInfoBase(tempItem, infoItem, factor * (tempRecipeItem.getAmountNeeded()/(tempRecipeItem.getBatch() * (1 - (tempRecipeItem.getWaste()/100)))), returnRecipeItem, inventoryUnits);

					}
				}
			}
		}
	}
	
	public static void getItemInfoWithInventoryUnits(MenuItem dish, MenuItem infoItem, Double factor, RecipeItemDetails returnRecipeItem) {
		getItemInfoBase(dish, infoItem, factor, returnRecipeItem, true);
	}
	
	public static void getItemInfo(MenuItem dish, MenuItem infoItem, Double factor, RecipeItemDetails returnRecipeItem) {
		getItemInfoBase(dish, infoItem, factor, returnRecipeItem, false);
	}

	
	public static LinkedList<Double> sortDoubles(Collection<Double> list) {
		LinkedList<Double> newList = new LinkedList<Double>();
		
		int correctIndex = 0;
		for (Double dbl : list) {
			
			if (newList.isEmpty()) {
				newList.add(dbl);
			} else {
				correctIndex = 0;
				for (Double newDouble : newList) {
					if (dbl < newDouble)
						break;
					
					correctIndex++;
				}
				
				if (correctIndex != newList.size())
					newList.add(correctIndex, dbl);
				else
					newList.add(dbl);
			}	
		}
		
		return newList;
	}
}