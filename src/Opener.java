import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


public class Opener {

	static String[] unit_array = {Saver.NULL, "Units", "mL", "Ounces (liquid)", "Cups", "Gallons", "Liters", "Grams",
									"Ounces (solid)", "Pounds", "Kilos", "Orders"};
	final static ArrayList<String> UNITS_LIST = convertToArrayList(unit_array);
	
	static String[] unsold_type_array = {Saver.NULL, Saver.LIQUID, Saver.SOLID, Saver.OTHER};
	final static ArrayList<String> UNSOLD_TYPE_LIST = convertToArrayList(unsold_type_array);
	
	static String[] boolean_array = {Saver.NULL, Saver.TRUE, Saver.FALSE};
	final static ArrayList<String> BOOL_LIST = convertToArrayList(boolean_array);
	
	static String[] item_type_array = {Saver.NULL, "Ingredients", "Sold Dishes", "Unsold Dishes"};
	final static ArrayList<String> ITEM_TYPE_LIST = convertToArrayList(item_type_array);
	
	public MainFrame parent;

	
	Opener(MainFrame parent) {
		this.parent = parent;
	}
	
	
	public boolean introOpen(BufferedReader bfReader, boolean makeChanges, ArrayList<String> itemList) throws IOException {
		
		String tempString = null;
		
		//Check for correct format for starting indicator
		if ((tempString = bfReader.readLine()) == null || !tempString.equals("START"))
			return false;
		if ((tempString = bfReader.readLine()) == null || !tempString.equals(""))
			return false;
		if ((tempString = bfReader.readLine()) == null || !tempString.equals(""))
			return false;		
		
		//Ingredients portion
		if ((tempString = bfReader.readLine()) == null || !tempString.equals("INGREDIENTS:"))
			return false;
		if ((tempString = bfReader.readLine()) == null || !tempString.equals(""))
			return false;
		
		while(true) {
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (tempString.equals(""))
				break;
			
			if (makeChanges) {
				parent.itemHash.put(tempString, new Ingredient(tempString));
				MainFrame.InsertIntoList(parent.ingredientList, tempString);
			} else {
				itemList.add(tempString);
			}
		}
	
		//Unsold dish portion
		if ((tempString = bfReader.readLine()) == null || !tempString.equals("UNSOLD DISHES:"))
			return false;
		if ((tempString = bfReader.readLine()) == null || !tempString.equals(""))
			return false;
	
		while(true) {
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (tempString.equals(""))
				break;
			
			if (makeChanges) {
				parent.itemHash.put(tempString, new Dish(tempString, false));			
				parent.categories.get(Category.UNSOLD_DISHES).addDish(tempString);
			} else {
				itemList.add(tempString);
			}
		}

		//Sold dish portion
		if ((tempString = bfReader.readLine()) == null || !tempString.equals("SOLD DISHES:"))
			return false;
		if ((tempString = bfReader.readLine()) == null || !tempString.equals(""))
			return false;
	
		while(true) {
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (tempString.equals(""))
				break;
			
			if (makeChanges) {
				parent.itemHash.put(tempString, new Dish(tempString, true));			
				parent.categories.get(Category.ENTIRE_MENU).addDish(tempString);
			} else {
				itemList.add(tempString);
			}
		}
		
		
		return true;
	}
	
	public boolean categoryOpen(BufferedReader bfReader, boolean makeChanges, ArrayList<String> itemList, ArrayList<String> catList) throws IOException {
		
		Category tempCategory = null;
		MenuItem tempItem = null;
		
		String tempString = null;
		
		//Check for correct format for starting category section
		if ((tempString = bfReader.readLine()) == null || !tempString.equals("CATEGORIES:"))
			return false;
		if ((tempString = bfReader.readLine()) == null || !tempString.equals(""))
			return false;
			
		while (true) {
			//Get name
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (tempString.equals(""))
				break;
			
			if (!tempString.equals(Category.ENTIRE_MENU) && !tempString.equals(Category.UNSOLD_DISHES)) {
	        	if (makeChanges)
	        		MainFrame.InsertIntoList(parent.categoriesList, tempString);
				
	        	tempCategory = new Category(tempString, true);
			} else {
				tempCategory = null;
			}
			
			//Get type
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (!BOOL_LIST.contains(tempString))
				return false;
			
			if (makeChanges && tempCategory != null) {
				if (tempString.equals(Saver.TRUE))
					tempCategory.setIsSold(true);
				else
					tempCategory.setIsSold(false);
			}
			
			//Add dishes	
			while (true) {
				if ((tempString = bfReader.readLine()) == null)
					return false;
				
				if (tempString.equals(""))
					break;
		
				if (!makeChanges && !itemList.contains(tempString))
					return false;
				
				if (makeChanges && tempCategory != null) {
					tempItem = parent.itemHash.get(tempString);
					
					tempItem.addCategory(tempCategory.getName());
					tempCategory.addDish(tempString);
				}
			}
			
			if (tempCategory != null) {
				if (makeChanges)
					parent.categories.put(tempCategory.getName(), tempCategory);
				else 
					catList.add(tempCategory.getName());
			}
		}
		
		return true;
	}
	
	public boolean orderListOpen(BufferedReader bfReader, boolean makeChanges, ArrayList<String> itemList) throws IOException {
		
		MenuItem tempItem = null;
		String tempString = null;
		String tempIntString = null;
		Integer tempInt = null;
		
		//Check for correct format for starting category section
		if ((tempString = bfReader.readLine()) == null || !tempString.equals("ORDER LIST:"))
			return false;
		if ((tempString = bfReader.readLine()) == null || !tempString.equals(""))
			return false;
		
		while (true) {
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (tempString.equals(""))
				break;
	
			if (!makeChanges && !itemList.contains(tempString))
				return false;
			
			if ((tempIntString = bfReader.readLine()) == null)
				return false;
			
			try {
				tempInt = Integer.parseInt(tempIntString);
			} catch (Exception e) {
				return false;
			}
			
			if (makeChanges)
				parent.currentOrder.put(tempString, new OrderItem(tempString, tempInt));
		}	
		
		
		return true;
	}

	public boolean IngredientPopulateOpen(BufferedReader bfReader, boolean makeChanges, ArrayList<String> itemList) throws IOException {
		
		MenuItem tempItem = null;
		MenuItem testerItem = null;
		
		String tempString = null;
		String units = null;
		Double costNum = null; Double costFieldNum = null; Double perFieldNum = null;
		Dish.UNSOLD_TYPE tempType = null;
		boolean incompleteError = true; boolean warningError = true;
		
		//Check for correct format for starting category section
		if ((tempString = bfReader.readLine()) == null || !tempString.equals("INGREDIENT POPULATE:"))
			return false;
		if ((tempString = bfReader.readLine()) == null || !tempString.equals(""))
			return false;
			
		while (true) {			
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (tempString.equals(""))
				break;
			
			if (!makeChanges && !itemList.contains(tempString))
				return false;
			
			if (makeChanges)
				tempItem = parent.itemHash.get(tempString);
			
			//Cost component
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (!tempString.equals(Saver.NULL)) {
				try {
					costNum = Double.parseDouble(tempString);
				} catch (Exception e) {
					return false;
				}			
			}  else {
				costNum = null;
			}
			
			//Units component
			if ((units = bfReader.readLine()) == null)
				return false;
			
			if (!UNITS_LIST.contains(units))
				return false;
			
			if (units.equals(Saver.NULL))
				units = null;
			
			//Unsold type component
			if ((tempString = bfReader.readLine()) == null)
				return false;

			if (!UNSOLD_TYPE_LIST.contains(tempString))
				return false;
			
			if (makeChanges) {
				if (tempString.equals(Saver.LIQUID))
					tempType = Dish.UNSOLD_TYPE.LIQUID;
				else if (tempString.equals(Saver.SOLID))
					tempType = Dish.UNSOLD_TYPE.SOLID;
				else if (tempString.equals(Saver.OTHER))
					tempType = Dish.UNSOLD_TYPE.OTHER;
				else
					tempType = null;
			}
			
			//Errors component
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (!BOOL_LIST.contains(tempString))
				return false;
			
			if (makeChanges) {
				if (tempString.equals(Saver.TRUE))
					incompleteError = true;
				else
					incompleteError = false;
			}
			
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (!BOOL_LIST.contains(tempString))
				return false;
			
			if (makeChanges) {
				if (tempString.equals(Saver.TRUE))
					warningError = true;
				else
					warningError = false;
			}
			
			//Cost field Component
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (!tempString.equals(Saver.NULL)) {
				try {
					costFieldNum = Double.parseDouble(tempString);
				} catch (Exception e) {
					return false;
				}
			} else {
				costFieldNum = null;
			}
			
			//Per field Component
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (!tempString.equals(Saver.NULL)) {
				try {
					perFieldNum = Double.parseDouble(tempString);
				} catch (Exception e) {
					return false;
				}
			} else {
				perFieldNum = null;
			}
			
			//Make changes
			if (makeChanges) {
				tempItem.setCost(costNum);
				tempItem.setUnits(units);
				tempItem.setUnsoldType(tempType);
				tempItem.setIncompleteError(incompleteError);
				tempItem.setUnusedWarning(warningError);
				tempItem.setCostField(costFieldNum);
				tempItem.setPerFigure(perFieldNum);
			}
			
			//Add uses
			while (true) {
				if ((tempString = bfReader.readLine()) == null)
					return false;
				
				if (tempString.equals(""))
					break;

				if (!makeChanges && !itemList.contains(tempString))
					return false;
								
				if (makeChanges) {
					testerItem = parent.itemHash.get(tempString);
					tempItem.addUse(tempString, testerItem);
				}
			}
		}
		
		
		return true;
	}

	public boolean DishPopulateOpen(BufferedReader bfReader, boolean makeChanges, ArrayList<String> itemList) throws IOException {
		
		String tempString = null;
		boolean oneSpace = false; boolean doubleSpace = false;
		
		MenuItem tempItem = null;
		String units = null;
		Double costNum = null; Double priceNum = null; Double defaultBatch = null;
		Dish.UNSOLD_TYPE tempType = null;
		boolean incompleteError = true; boolean warningError = true; boolean isSoldDish = false;
		
		
		MenuItem testerItem = null;
		
		
		RecipeItem tempRecipeItem = null;
		MenuItem recipeMenuItem = null;
		String recipeUnits = null;
		Double recipeCost = null; Double recipeAmountNeeded = null; Double recipeBatch = null; Double recipeWaste = null; 
		

		//Check for correct format for starting category section
		if ((tempString = bfReader.readLine()) == null || !tempString.equals("DISH POPULATE:"))
			return false;
		if ((tempString = bfReader.readLine()) == null || !tempString.equals(""))
			return false;
			
		while (true) {			
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (tempString.equals(""))
				break;
			
			if (!makeChanges && !itemList.contains(tempString))
				return false;
			
			if (makeChanges)
				tempItem = parent.itemHash.get(tempString);
				
			//Cost component
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (!tempString.equals(Saver.NULL)) {
				try {
					costNum = Double.parseDouble(tempString);
				} catch (Exception e) {
					return false;
				}
			} else {
				costNum = null;
			}
			
			//Units component
			if ((units = bfReader.readLine()) == null)
				return false;
			
			if (!UNITS_LIST.contains(units))
				return false;
			
			if (units.equals(Saver.NULL))
				units = null;
			
			//Unsold type component
			if ((tempString = bfReader.readLine()) == null)
				return false;

			if (!UNSOLD_TYPE_LIST.contains(tempString))
				return false;
			
			if (makeChanges) {
				if (tempString.equals(Saver.LIQUID))
					tempType = Dish.UNSOLD_TYPE.LIQUID;
				else if (tempString.equals(Saver.SOLID))
					tempType = Dish.UNSOLD_TYPE.SOLID;
				else if (tempString.equals(Saver.OTHER))
					tempType = Dish.UNSOLD_TYPE.OTHER;
				else 
					tempType = null;
			}
			
			//Errors component
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (!BOOL_LIST.contains(tempString))
				return false;
			
			if (makeChanges) {
				if (tempString.equals(Saver.TRUE))
					incompleteError = true;
				else
					incompleteError = false;
			}
			
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (!BOOL_LIST.contains(tempString))
				return false;
			
			if (makeChanges) {
				if (tempString.equals(Saver.TRUE))
					warningError = true;
				else
					warningError = false;
			}
			
			//Price field Component
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (!tempString.equals(Saver.NULL)) {
				try {
					priceNum = Double.parseDouble(tempString);
				} catch (Exception e) {
					return false;
				}
			} else {
				priceNum = null;
			}
				
			//Sold dish boolean
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (!BOOL_LIST.contains(tempString))
				return false;
			
			if (makeChanges) {
				if (tempString.equals(Saver.TRUE))
					isSoldDish = true;
				else
					isSoldDish = false;
			}
			
			//Default batch field Component
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (!tempString.equals(Saver.NULL)) {
				try {
					defaultBatch = Double.parseDouble(tempString);
				} catch (Exception e) {
					return false;
				}
			} else {
				defaultBatch = null;
			}
			
			//Make changes to actual item
			if (makeChanges) {
				tempItem.cost = costNum;
				tempItem.units = units;
				tempItem.unsoldType = tempType;
				tempItem.incompleteError = incompleteError;
				tempItem.unusedWarning = warningError;
				
				tempItem.setUniqueItems(priceNum, isSoldDish, recipeBatch);
			}
			
			while (true) {
				if ((tempString = bfReader.readLine()) == null)
					return false;
				
				if (tempString.equals(""))
					break;

				if (!makeChanges && !itemList.contains(tempString))
					return false;
				
				if (makeChanges) {
					testerItem = parent.itemHash.get(tempString);
					tempItem.addUse(tempString, testerItem);
				}
			}
			
			while (true) {
				if ((tempString = bfReader.readLine()) == null)
					return false;
				
				if (tempString.equals("")) {
					if ((tempString = bfReader.readLine()) == null || !tempString.equals(""))
						return false;
					else
						break;
				}
				
				if (!makeChanges && !itemList.contains(tempString))
					return false;
				
				if (makeChanges)
					recipeMenuItem = parent.itemHash.get(tempString);
				
				//Recipe cost component
				if ((tempString = bfReader.readLine()) == null)
					return false;
				
				if (!tempString.equals(Saver.NULL)) {
					try {
						recipeCost = Double.parseDouble(tempString);
					} catch (Exception e) {
						return false;
					}
				} else {
					recipeCost = null;
				}
				
				//Recipe amount needed component
				if ((tempString = bfReader.readLine()) == null)
					return false;
				
				if (!tempString.equals(Saver.NULL)) {
					try {
						recipeAmountNeeded = Double.parseDouble(tempString);
					} catch (Exception e) {
						return false;
					}
				} else {
					recipeAmountNeeded = null;
				}
					
				//Units component
				if ((recipeUnits = bfReader.readLine()) == null)
					return false;
				
				if (!UNITS_LIST.contains(recipeUnits))
					return false;
				
				if (recipeUnits.equals(Saver.NULL))
					recipeUnits = null;

				//Recipe batch component
				if ((tempString = bfReader.readLine()) == null)
					return false;
				
				if (!tempString.equals(Saver.NULL)) {
					try {
						recipeBatch = Double.parseDouble(tempString);
					} catch (Exception e) {
						return false;
					}
				} else {
					recipeBatch = null;
				}
				
				//Recipe waste component			    			    		
				if ((tempString = bfReader.readLine()) == null)
					return false;
				
				if (!tempString.equals(Saver.NULL)) {
					try {
						recipeWaste = Double.parseDouble(tempString);
					} catch (Exception e) {
						return false;
					}
				} else {
					recipeWaste = null;
				}
	

				if (makeChanges)
					tempItem.addRecipeItem(recipeMenuItem, recipeCost, recipeAmountNeeded, recipeUnits, recipeBatch, recipeWaste);
			}
		}

		
		return true;
	}
		
	public boolean TabPopulateOpen(BufferedReader bfReader, boolean makeChanges, ArrayList<String> itemList, ArrayList<String> catList) throws IOException {
		
		String tempString = null;
		Integer testInt = null;
		boolean testBool = false;
		boolean testBoolAlt = false;
		
		MenuItem testerItem = null;
		Category testCat = null;
		
		ArrayList<String> testerArray = new ArrayList<String>();
		
		String testerItemString1 = null; String testerItemString2 = null; String testerItemString3 = null; String testerItemString4 = null;
		
		
		//Check for correct format for starting Tab 1 section
		if ((tempString = bfReader.readLine()) == null || !tempString.equals("TAB 1:"))
			return false;

		//Check selected variable
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (!tempString.equals(Saver.NULL)) {
			if (!makeChanges && !itemList.contains(tempString))
				return false;
			
			if (makeChanges)
				testerItemString1 = tempString;
		} else {
			testerItemString1 = null;
		}
				
		//Check radioplace
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (!tempString.equals(Saver.NULL)) {
			try {
				testInt = Integer.parseInt(tempString);
				if (testInt < 0 || testInt > 2)
					return false;
			} catch (Exception e) {
				return false;
			}
		} else {
			testInt = null;
		}
					
		//Check for displayed1
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (!tempString.equals(Saver.NULL)) {
			if (!makeChanges && !itemList.contains(tempString))
				return false;
			
			if (makeChanges)
				testerItemString2 = tempString;
		} else {
			testerItemString2 = null;
		}

		//Check for displayed2
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (!tempString.equals(Saver.NULL)) {
			if (!makeChanges && !itemList.contains(tempString))
				return false;
			
			if (makeChanges)
				testerItemString3 = tempString;
		} else {
			testerItemString3 = null;
		}

		//Check for displayed3
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (!tempString.equals(Saver.NULL)) {
			if (!makeChanges && !itemList.contains(tempString))
				return false;
			
			if (makeChanges)
				testerItemString4 = tempString;
		} else {
			testerItemString4 = null;
		}
		
		//Repaint entire tab
		if (makeChanges) {
			parent.iPricingPane.setUp(parent, testerItemString1, testInt, testerItemString2, testerItemString3, testerItemString4);
			parent.iPricingPane.repaint(true, true, true);
		}
		
		
		//Tab 2 formating check
		if ((tempString = bfReader.readLine()) == null || !tempString.equals(""))
			return false;
		if ((tempString = bfReader.readLine()) == null || !tempString.equals("TAB 2:"))
			return false;
		
		//Check for category selection
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (!tempString.equals(Saver.NULL)) {
			if (!makeChanges && !catList.contains(tempString))
				return false;
			
			if (makeChanges)
				testerItemString1 = tempString;
		} else {
			testerItemString1 = null;
		}
				
		//Check for selected dish
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (!tempString.equals(Saver.NULL)) {
			if (!makeChanges && !itemList.contains(tempString))
				return false;
			
			if (makeChanges)
				testerItemString2 = tempString;
		} else {
			testerItemString2 = null;
		}
		
		//Add checked items
		while (true) {
			if ((tempString = bfReader.readLine()) == null)
				return false;
			
			if (tempString.equals(""))
				break;
			
			if (!makeChanges && !itemList.contains(tempString))
				return false;
						
			if (makeChanges)
				MainFrame.InsertIntoList(testerArray, tempString);
		}
		
		//Repaint Tab 2
		if (makeChanges) {
			
			parent.dishCompPane.removeAll();
			
			parent.dishCompPane.setUp(parent, testerItemString1, testerItemString2, testerArray);
			parent.dishCompPane.initComponents(0);
			
			parent.dishCompPane.validate();
			parent.dishCompPane.repaint();

		}

		
		//Check formating for Tab 3
		if ((tempString = bfReader.readLine()) == null || !tempString.equals("TAB 3:"))
			return false;
		
		//Check for category selection
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (!tempString.equals(Saver.NULL)) {
			if (!makeChanges && !catList.contains(tempString))
				return false;
			
			if (makeChanges)
				testerItemString1 = tempString;
		} else {
			testerItemString1 = null;
		}
				
		//Check for selected dish
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (!tempString.equals(Saver.NULL)) {
			if (!makeChanges && !itemList.contains(tempString))
				return false;
			
			if (makeChanges)
				testerItemString2 = tempString;
		} else {
			testerItemString2 = null;
		}
	
		//Repaint Tab 3
		if (makeChanges) {
			parent.catManagementPane.setUp(parent, testerItemString1, testerItemString2);
			parent.catManagementPane.repaintAll();
		}
		
		
		//Check formating for Tab 4
		if ((tempString = bfReader.readLine()) == null || !tempString.equals(""))
			return false;
		if ((tempString = bfReader.readLine()) == null || !tempString.equals("TAB 4:"))
			return false;
		
		//Check for selected type
		if ((testerItemString1 = bfReader.readLine()) == null)
			return false;
		
		if (!testerItemString1.equals(Saver.NULL)) {
			if (!makeChanges && !ITEM_TYPE_LIST.contains(testerItemString1))
				return false;

		} else {
			testerItemString1 = null;
		}
		
		//Check for selected item
		if ((testerItemString2 = bfReader.readLine()) == null)
			return false;
		
		if (!testerItemString2.equals(Saver.NULL)) {
			if (!makeChanges && !itemList.contains(testerItemString2))
				return false;
			
		} else {
			testerItemString2 = null;
		}
	
		//Check for displayed units
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (tempString.equals(Saver.TRUE)) {
			testBool = true;
		} else if (tempString.equals(Saver.FALSE)) {
			testBool = false;
		} else {
			return false;
		}
				
		//Repaint entire tab
		if (makeChanges) {
			parent.itemUsagePane.setUp(parent, testerItemString1, testerItemString2, testBool);
			parent.itemUsagePane.repaintAll();
		}

		
		//Check formating for Tab 5
		if ((tempString = bfReader.readLine()) == null || !tempString.equals(""))
			return false;
		if ((tempString = bfReader.readLine()) == null || !tempString.equals("TAB 5:"))
			return false;
		
		//Check for category selection
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (!tempString.equals(Saver.NULL)) {
			if (!makeChanges && !catList.contains(tempString))
				return false;
			
			if (makeChanges)
				testerItemString1 = tempString;
		} else {
			testerItemString1 = null;
		}
				
		//Check for selected dish
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (!tempString.equals(Saver.NULL)) {
			if (!makeChanges && !itemList.contains(tempString))
				return false;
			
			if (makeChanges)
				testerItemString2 = tempString;
		} else {
			testerItemString2 = null;
		}
		
		//Check for breakdown units
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (tempString.equals(Saver.TRUE)) {
			testBool = true;
		} else if (tempString.equals(Saver.FALSE)) {
			testBool = false;
		} else {
			return false;
		}
		
		//Check for displayed units
		if ((tempString = bfReader.readLine()) == null)
			return false;
		
		if (tempString.equals(Saver.TRUE)) {
			testBoolAlt = true;
		} else if (tempString.equals(Saver.FALSE)) {
			testBoolAlt = false;
		} else {
			return false;
		}
		
		//Repaint Tab 5
		if (makeChanges) {
			parent.resultsPane.setUp(parent, testerItemString1, testerItemString2, testBool, testBoolAlt);
			parent.resultsPane.repaintAll();
		}
		
		
		return true;
	}
	
	public boolean openEntireFile(BufferedReader bfReader, boolean makeChanges) throws IOException {
		boolean returnVal = true;
		
		ArrayList<String> itemList = new ArrayList<String>();
		
		ArrayList<String> catList = new ArrayList<String>();
		catList.add(Category.ENTIRE_MENU);
		catList.add(Category.UNSOLD_DISHES);
		
		if (!makeChanges) {
			
			returnVal = introOpen(bfReader, makeChanges, itemList);
			if (!returnVal)
				return false;			
			
			returnVal = categoryOpen(bfReader, makeChanges, itemList, catList);
			if (!returnVal)
				return false;			
			
			returnVal = orderListOpen(bfReader, makeChanges, itemList);
			if (!returnVal)
				return false;			

			returnVal = IngredientPopulateOpen(bfReader, makeChanges, itemList);
			if (!returnVal)
				return false;			

			returnVal = DishPopulateOpen(bfReader, makeChanges, itemList);
			if (!returnVal)
				return false;			
						
			returnVal = TabPopulateOpen(bfReader, makeChanges, itemList, catList);
			if (!returnVal)
				return false;			
			
			return true;
		} else {
			
			//Refresh everything
			parent.itemHash = new  HashMap<String, MenuItem>();
			parent.ingredientList = new ArrayList<String>();
			
			
			parent.categories = new HashMap<String, Category>();
			parent.categoriesList = new ArrayList<String>();
			
			parent.categories.put(Category.ENTIRE_MENU, new Category(Category.ENTIRE_MENU, true));
	        MainFrame.InsertIntoList(parent.categoriesList, Category.ENTIRE_MENU);
	        
	        parent.categories.put(Category.UNSOLD_DISHES, new Category(Category.UNSOLD_DISHES, false));
	        MainFrame.InsertIntoList(parent.categoriesList, Category.UNSOLD_DISHES);

			
			parent.currentOrder = new HashMap<String, OrderItem>();
			
			
			//Update everything
			introOpen(bfReader, makeChanges, itemList);
			categoryOpen(bfReader, makeChanges, itemList, catList);
			orderListOpen(bfReader, makeChanges, itemList);
			IngredientPopulateOpen(bfReader, makeChanges, itemList);
			DishPopulateOpen(bfReader, makeChanges, itemList);
			TabPopulateOpen(bfReader, makeChanges, itemList, catList);
			
			
			return true;
		}	
	}
			
	public boolean openItemList(BufferedReader bfReader, boolean makeChanges) throws IOException {
	
		ArrayList<String> itemList = MenuCompositionPane.convertToSortedArrayList(parent.itemHash.keySet());
				
		if (!makeChanges) {
			return orderListOpen(bfReader, makeChanges, itemList);
		
		} else {
			parent.currentOrder = new HashMap<String, OrderItem>();
			orderListOpen(bfReader, makeChanges, itemList);
			
			parent.resultsPane.repaint(false, false, true);
			
			return true;
		}	
	}
	
	static ArrayList<String> convertToArrayList(String[] original) {
		
		ArrayList<String> returnList = new ArrayList(original.length);
		for (int i = 0; i < original.length; i++)
			returnList.add(original[i]);
		
		return returnList;
	}
}