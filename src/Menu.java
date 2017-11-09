import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

public class Menu {
	
	final private String helpMessage = "<html><br>To ask for help or make suggestions to improve the program, you can contact:" +
										"&#32;&#32;&#32;&#32;&#32;&#32;&#32;<br><br>" +
										"Ali Jad Khalil<br><br>" +
										"Phone:   +1-202-630-9399<br>" +
										"Email:   MenuCalculatorHelp@gmail.com<br><br><br><html>";
		
	private static enum OPEN_FLAG {NORMAL, BLANK, UNDO, REDO}

	
	JTextArea output;
	JScrollPane scrollPane;

    JFileChooser fc = new JFileChooser();
    
    JDialog undoRedoDialog;
    JLabel dialogLabel;
    
	MainFrame parent;
	
	private JMenuItem messagingSwitch;

	
	public JMenuBar createMenuBar(MainFrame frame) {
		this.parent = frame;
				
		JMenuBar menuBar;
		
		JMenu menu, saveSubMenu;
		
		JMenuItem newFileItem, openMenuItem;
		JMenuItem dataFileSave, reportFileSave;
		JMenuItem undoItem, redoItem;
		JMenuItem closeItem;

		
	   //Create some component for the JDialog
	   dialogLabel = new JLabel("");

	   undoRedoDialog = new JDialog(parent.frame, "Undo/Redo Info");
	   undoRedoDialog.setLocationRelativeTo(parent.frame);
	   undoRedoDialog.setLayout(new FlowLayout());
	   undoRedoDialog.add(dialogLabel);
	   undoRedoDialog.setVisible(false);

		
		// Build the Main Menu
		menuBar = new JMenuBar();
				
		menu = new JMenu("Main Menu");
		menu.setFont(new Font("Dialog", Font.BOLD, 13));
		menu.setMnemonic(KeyEvent.VK_M);
		menu.getAccessibleContext().setAccessibleDescription("The main functionality in the program can be found here.");
		menuBar.add(menu);

		// Create a new cost analysis
		newFileItem = new JMenuItem("New                            ");
		newFileItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            	openEntireProgram(OPEN_FLAG.BLANK);            	
            }	
        } );
		
		newFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK)); //can use constructor instead
		newFileItem.getAccessibleContext().setAccessibleDescription("This provides option to open a blank project");
		
		menu.add(newFileItem);
		menu.addSeparator();
		
		//Open an existing cost analysis
		openMenuItem = new JMenuItem("Open...");
		openMenuItem.getAccessibleContext().setAccessibleDescription("This provides option to save before opening an existing menu");

		openMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            
            	openEntireProgram(OPEN_FLAG.NORMAL);
            }	
        } );
		
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK)); //can use constructor instead		
		menu.add(openMenuItem);
		
		// Save an existing cost analysis
		saveSubMenu = new JMenu("Save Options");

		// DETERMINE APPROPIATE KEYS... Should be Ctrl-S
		saveSubMenu.setMnemonic(KeyEvent.VK_S); //can use constructor instead
		saveSubMenu.getAccessibleContext().setAccessibleDescription("This provides option to save before opening an existing menu");
		
		// Add save option for either a data or report version
		dataFileSave = new JMenuItem("Save");
		dataFileSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	saveEntireProgram(false);
			}
		} );
		dataFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK)); //can use constructor instead

		
		reportFileSave = new JMenuItem("Save As...");
		reportFileSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                  
            	saveEntireProgram(true);
            }
		} );
		
		saveSubMenu.add(dataFileSave);
		saveSubMenu.add(reportFileSave);
		
		// Add save submenu to menu bar
		menu.add(saveSubMenu);
		menu.addSeparator();
		
		// Add undo and redo buttons
		undoItem = new JMenuItem("Undo");
		undoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (parent.currentIndex > 0) {
            		parent.currentIndex--;
            		            		
            		openEntireProgram(OPEN_FLAG.UNDO);
            	} else {
        			dialogLabel.setText("CANNOT UNDO ANYMORE   ");
        			undoRedoDialog.setTitle("Undo Info  ");

        			undoRedoDialog.pack();
        			undoRedoDialog.setVisible(true);
        	        parent.frame.setVisible(true);
            	}
            }
		} );
		
		// DETERMINE APPROPIATE KEYS... Should be Ctrl-Z
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK)); //can use constructor instead
		undoItem.getAccessibleContext().setAccessibleDescription("Undo until last update");
		
		// Add redo and redo buttons
		redoItem = new JMenuItem("Redo");
		redoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                  
            	if (parent.currentIndex < parent.undoRedoStack.size() - 1) {
            		parent.currentIndex++;
            		
            		openEntireProgram(OPEN_FLAG.REDO);
            	} else {
        			dialogLabel.setText("CANNOT REDO ANYMORE    ");
        			undoRedoDialog.setTitle("Redo Info  ");

        			undoRedoDialog.pack();
        			undoRedoDialog.setVisible(true);
        	        parent.frame.setVisible(true);
            	}
            }
		} );
		
		// DETERMINE APPROPIATE KEYS... Should be Ctrl-Z
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK)); //can use constructor instead
		redoItem.getAccessibleContext().setAccessibleDescription("Redo to next update");		
		
		// Add undo-redo to menu
		menu.add(undoItem);
		menu.add(redoItem);
		menu.addSeparator();
				
		// Add universal update button
		closeItem = new JMenuItem("Exit");
		closeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
		
            	int confirm = JOptionPane.showConfirmDialog(fc, "Are you sure that you want to EXIT the program?", "Exit", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					if (!parent.isSaved) {
						int overWrite = JOptionPane.showConfirmDialog(fc, "Would you like to SAVE before exiting the program?", "Save", JOptionPane.YES_NO_OPTION);
				        
						if (overWrite == JOptionPane.YES_OPTION || overWrite == JOptionPane.NO_OPTION) {
					        if (overWrite == JOptionPane.YES_OPTION)
								saveEntireProgram(false);
					        
					        System.exit(0);
						}     
					} else {
						System.exit(0);
					}
				}
            }
        } );
            
		menu.add(closeItem);

		
		
		// Edit Menu
		JMenu editMenu;
		JMenu addSubMenu, removeSubMenu, createSubMenu, dishSubMenu, catSubMenu, changeNameSubMenu, editDishSubMenu;
		
		JMenuItem addIngredient, addMenuDish, addUnsoldDish, addSoldCatItem, addUnsoldCatItem;		
		JMenuItem removeIngredient, removeDish, removeCatItem;
		
		JMenuItem createIngredient,createDish, createCat;		
		JMenuItem changeNameIngredient, changeNameDish, changeNameCatItem;
		
		JMenuItem changeDishPrice; 
		JMenuItem changeToSold, changeToUnsold; 
		
		JMenuItem getIngredientInfo;
		
		
		editMenu = new JMenu("Edit Restaurant");
		
		editMenu.setFont(new Font("Dialog", Font.BOLD, 13));
		editMenu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(editMenu);
		
		// Save an existing cost analysis
		addSubMenu = new JMenu("Add Item/Category");
		
		// DETERMINE APPROPIATE KEYS... 
		addSubMenu.setMnemonic(KeyEvent.VK_S); //can use constructor instead
		addSubMenu.getAccessibleContext().setAccessibleDescription("This provides option to save before opening an existing menu");
						
		// Add new category and remove category item buttons
		addIngredient = new JMenuItem("Ingredient...     ");
		addIngredient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	parent.iPricingPane.addIngredientItem();
            }
        } );

		addMenuDish = new JMenuItem("Sold on the Menu...        ");
		addMenuDish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            		
            	parent.dishCompPane.addDish("Menu Dish Adder", "What is the name of the SOLD dish that you would like to add to the menu?\n   \n", true);
            }
		} );
		
		addUnsoldDish = new JMenuItem("Only Used in Recipes..."); 
		addUnsoldDish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	parent.dishCompPane.addDish("Component Dish Adder", "What is the name of the UNSOLD dish that you would like to use in other recipes?\n    \n", false);
            }
		} );
	
		
		dishSubMenu = new JMenu("Dish");
		dishSubMenu.add(addMenuDish);
		dishSubMenu.add(addUnsoldDish);

		addSoldCatItem = new JMenuItem("Consisting of Sold Dishes...   ");
		addSoldCatItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	parent.catManagementPane.addCategory(true);
            }
        } );
		
		addUnsoldCatItem = new JMenuItem("Consisting of Unsold Dishes...");
		addUnsoldCatItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	parent.catManagementPane.addCategory(false);
            }
        } );

		catSubMenu = new JMenu("Category");
		catSubMenu.add(addSoldCatItem);
		catSubMenu.add(addUnsoldCatItem);
		
		
		//Add all items and sub menus
		addSubMenu.add(addIngredient);
		addSubMenu.add(dishSubMenu);
		addSubMenu.add(catSubMenu);

		editMenu.add(addSubMenu);
		
		
		// Create a copy
		createSubMenu = new JMenu("Copy Existing Item/Category   ");
								
		// Add new category and remove category item buttons
		createIngredient = new JMenuItem("Ingredient...   ");
		createIngredient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

        		ArrayList<String> tempList = null;
    			tempList = (ArrayList<String>) parent.ingredientList.clone();
        		
        		tempList.add(0, "SELECT");

        		Object[] possibilities = tempList.toArray();
        		String oldIngredientName = (String) JOptionPane.showInputDialog(parent,
        				"Choose an INGREDIENT to Copy:\n    \n", "Ingredient Copier",
        			JOptionPane.PLAIN_MESSAGE, null, possibilities,
        			possibilities[0]);
        		
        		if (oldIngredientName != null && (!oldIngredientName.equals("SELECT"))) {
        			parent.iPricingPane.addIngredientCopy(oldIngredientName);
        		}
            }
		} );
		
		createDish = new JMenuItem("Dish...");
		createDish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

        		ArrayList<String> tempList = null;
    			tempList = (ArrayList<String>) parent.categories.get(Category.ENTIRE_MENU).getDishes().clone();
    			
    			for (String newDish : parent.categories.get(Category.UNSOLD_DISHES).getDishes())
    				MainFrame.InsertIntoList(tempList, newDish);
        		
        		tempList.add(0, "SELECT");
    					
				Object[] possibilities = tempList.toArray();
				String oldIngredientName = (String) JOptionPane.showInputDialog(parent,
						"Choose a DISH to Copy:\n    \n", "Dish Copier",
					JOptionPane.PLAIN_MESSAGE, null, possibilities,
					possibilities[0]);
				
				if (oldIngredientName != null && (!oldIngredientName.equals("SELECT"))) {
					boolean isDish = parent.itemHash.get(oldIngredientName).IsSoldDish();
					
					String descriptor = null;
					if (isDish)
						descriptor = "SOLD";
					else
						descriptor = "UNSOLD";
					
					parent.dishCompPane.addCopyDish("Dish Copier", "What is the name of the new " + descriptor + " dish that you would like to add to the menu?\n   \n", 
							isDish, oldIngredientName);
				}

            }
		} );
		
		createCat = new JMenuItem("Category...");
		createCat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

        		ArrayList<String> tempList = new ArrayList<String>();        		
        		tempList.add("SELECT");
        		tempList.add(Category.ENTIRE_MENU);
        		tempList.add(Category.UNSOLD_DISHES);
        		
        		for (String cat : parent.categoriesList) {
        			if (cat != Category.ENTIRE_MENU && cat != Category.UNSOLD_DISHES)
        				tempList.add(cat);
        		}
    					
				Object[] possibilities = tempList.toArray();
				String oldCatName = (String) JOptionPane.showInputDialog(parent,
						"Choose a CATEGORY to Copy:\n    \n", "Category Copier",
					JOptionPane.PLAIN_MESSAGE, null, possibilities,
					possibilities[0]);
				
				if (oldCatName != null && (!oldCatName.equals("SELECT"))) {
					parent.catManagementPane.addCategoryCopy(oldCatName, true);
				}
            }
		} );
		
		createSubMenu.add(createIngredient);
		createSubMenu.add(createDish);
		createSubMenu.add(createCat);

		editMenu.add(createSubMenu);

		
		// Save an existing cost analysis
		removeSubMenu = new JMenu("Remove Item/Category");
								
		// Add new category and remove category item buttons
		removeIngredient = new JMenuItem("Ingredient...    ");
		removeIngredient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	parent.iPricingPane.removeIngredientItem();
            }
        } );
		
		removeDish = new JMenuItem("Dish...");		
		removeDish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            	parent.dishCompPane.removeDish();
            }
		} );
		
		removeCatItem = new JMenuItem("Category...");
		removeCatItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            		parent.catManagementPane.removeCategory();
            }
		} );
		
		removeSubMenu.add(removeIngredient);
		removeSubMenu.add(removeDish);
		removeSubMenu.add(removeCatItem);

		editMenu.add(removeSubMenu);
		editMenu.addSeparator();
		
		
		// Change Name Field
		changeNameSubMenu = new JMenu("Change Name");
		
		// DETERMINE APPROPIATE KEYS... 
		changeNameSubMenu.setMnemonic(KeyEvent.VK_S); //can use constructor instead
		changeNameSubMenu.getAccessibleContext().setAccessibleDescription("This provides option to save before opening an existing menu");
						
		
		// Add new category and remove category item buttons
		changeNameIngredient = new JMenuItem("Ingredient...    ");
		changeNameIngredient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            	changeName(false);
            }
		} );
		
		changeNameDish = new JMenuItem("Dish...");		
		changeNameDish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	 
            	changeName(true);
            }
		} );
				
		changeNameCatItem = new JMenuItem("Category...");
		changeNameCatItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
        		ArrayList<String> tempList = (ArrayList<String>) parent.categoriesList.clone();        		
        		
        		tempList.add(0, "SELECT");
        		tempList.remove(Category.ENTIRE_MENU);
        		tempList.remove(Category.UNSOLD_DISHES);
        		
        		Object[] possibilities = tempList.toArray();
        		String oldIngredientName = (String) JOptionPane.showInputDialog(parent,
        				"Choose a Category to Change its Name:\n    \n", "Name Changer",
        			JOptionPane.PLAIN_MESSAGE, null, possibilities,
        			possibilities[0]);
        		
        		if (oldIngredientName != null && (!oldIngredientName.equals("SELECT"))) {
        			SingleFieldDialog dialogAdd = new SingleFieldDialog("Change Name of " + oldIngredientName, 
        					"What would you like the NEW name of \"" + oldIngredientName.toLowerCase() + "\" to be?\n   \n", parent.itemHash.keySet(), false, false);
        	
        			dialogAdd.pack();
        			dialogAdd.setLocationRelativeTo(parent);
        			dialogAdd.setVisible(true);
        			
        			String newName = dialogAdd.getValidatedText();
        			if (newName != null && !newName.toUpperCase().equals("SELECT")) {    				
        				
        				newName = MainFrame.toFirstLettersUpper(newName);
        				Category category = parent.categories.get(oldIngredientName);

        				
        				category.setName(newName);
        				parent.categories.put(newName, category);
        				parent.categories.remove(oldIngredientName);
        				
        				parent.categoriesList.remove(oldIngredientName);
        				MainFrame.InsertIntoList(parent.categoriesList, newName);
        				
        				MenuItem tempItem = null;
        				for (String dishName : category.getDishes()) {
        					tempItem = parent.itemHash.get(dishName);
        					
        					tempItem.removeCategory(oldIngredientName);
        					tempItem.addCategory(newName);
        				}
        				
        				//Repaint
        				parent.dishCompPane.repaint(true, true, false);
        				parent.catManagementPane.repaintAll();
        				parent.resultsPane.repaintAll();
        				
        				String finalMessage = "You have successfully change the category name from \"" + oldIngredientName + "\" to \"" + newName +"\"!         ";
        				
        				JOptionPane.showMessageDialog(parent, finalMessage);
                		parent.updateUndoRedoStack("Changed the category name from \"" + newName + "\" back to \"" + oldIngredientName +"\"", "Changed the category name from \"" + oldIngredientName + "\" to \"" + newName +"\"");
        			}
        		}
        		
            }
		} );
				
		changeNameSubMenu.add(changeNameIngredient);
		changeNameSubMenu.add(changeNameDish);
		changeNameSubMenu.add(changeNameCatItem);

		editMenu.add(changeNameSubMenu);
		editMenu.addSeparator();
		
		// Change Sold Dish Price
		changeDishPrice= new JMenuItem("Change Menu Price of a Dish...    ");
		changeDishPrice.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		ArrayList<String> tempList = (ArrayList<String>) parent.categories.get(Category.ENTIRE_MENU).getDishes().clone();        		
        		tempList.add(0, "SELECT");

        		Object[] possibilities = tempList.toArray();
        		String dishName = (String) JOptionPane.showInputDialog(parent,
        				"Choose a Sold Dish to Change its Menu Price:\n    \n", "Price Setter",
        				JOptionPane.PLAIN_MESSAGE, null, possibilities,
        				possibilities[0]);

        		if (dishName != null && !dishName.equals("SELECT")) {	            	
            		MenuItem tempDish = parent.itemHash.get(dishName);
            				
            		NumberInputDialog dialogAdd = new NumberInputDialog("New Pricer", 
							"What would you like to set the NEW price of \"" + tempDish.getName() + "\" to be?\n    \n" , true);
		
					dialogAdd.pack();
					dialogAdd.setLocationRelativeTo(parent);
					dialogAdd.setVisible(true);
					
					String newPrice = dialogAdd.getValidatedText();
					if (newPrice != null) {
						try {
							Double newPriceInt = Double.valueOf(newPrice);
							Double prevPrice = tempDish.getPrice();
							
							if (newPriceInt > 0 && newPriceInt != prevPrice) {
								tempDish.setPrice(newPriceInt);
								
								if (prevPrice == null)
									parent.updateUndoRedoStack("Price of \"" + tempDish.getName() + "\" again is not set", 
																"Price of \"" + tempDish.getName() + "\" is now set to LL " + MainFrame.toStringWithXDemical(newPriceInt, 0));
								else
									parent.updateUndoRedoStack("Price of \"" + tempDish.getName() + "\" is set back to LL " + MainFrame.toStringWithXDemical(prevPrice, 0), 
																"Price of \"" + tempDish.getName() + "\" is now set to LL " + MainFrame.toStringWithXDemical(newPriceInt, 0));
								
								//Repaint all appropriate tabs
								if (parent.dishCompPane.selectedDish.getName().equals(tempDish.getName())) {
									parent.dishCompPane.priceField.setText(tempDish.getPrice().toString());
									parent.dishCompPane.initPrice = parent.dishCompPane.priceField.getText();
								}
								
								parent.dishCompPane.repaint(false, false, true);
				            	parent.itemUsagePane.repaint(true, true);
								parent.resultsPane.repaint(false, false, true);
								
							}
						}catch (Exception except) { }					
					}
            	}	            	
            }
		} );
		
		editMenu.add(changeDishPrice);
		
		// Change Dish Type
		editDishSubMenu = new JMenu("Change Dish Type");
		
		// DETERMINE APPROPIATE KEYS... 
		editDishSubMenu.setMnemonic(KeyEvent.VK_S); //can use constructor instead
		editDishSubMenu.getAccessibleContext().setAccessibleDescription("This provides option to save before opening an existing menu");		
		
		changeToUnsold = new JMenuItem("To Sold Dish...    ");
		changeToUnsold.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	 
            	changeIsSold(false);
            }
		} );
		
		changeToSold = new JMenuItem("To Unsold Dish...");
		changeToSold.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	 
            	changeIsSold(true);
            }
		} );

		editDishSubMenu.add(changeToUnsold);
		editDishSubMenu.add(changeToSold);
		
		editMenu.add(editDishSubMenu);
		editMenu.addSeparator();
		
		//Add Info Item leading to a non-modal box
		
		getIngredientInfo = new JMenuItem("Get Details of an Ingredient...                   ");
		getIngredientInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
    			ArrayList<String> tempList = (ArrayList<String>) parent.ingredientList.clone();
    			tempList.add(0, "SELECT");
    			
    			Object[] possibilities = tempList.toArray();
    			String ingredient = (String) JOptionPane.showInputDialog(parent,
   					"Choose an Ingredient to Get its Information:\n    \n", "Ingredient Information",
					JOptionPane.PLAIN_MESSAGE, null, possibilities,
					possibilities[0]);
    			
    			String errors = "<html><br><br><br>";
    			if (ingredient != null && (!ingredient.equals("SELECT"))) {
    				MenuItem item = parent.itemHash.get(ingredient);
    				boolean anyIssues = false;
    				
    				//Put header for item
    				errors += "Ingredient Name:&#32;&#32;&#32;&#32;" + (item.getName().toUpperCase() + "<br><br>");
    				
    				if (item.getIncompleteError()) {
    					//Item Info
    					errors += "Cost:&#32;&#32;&#32;";
    					if (item.getCostField() != null)
    						errors += MainFrame.toStringWithXDemical(item.getCostField(), 0) + " LL<br>";
    					else
    						errors += "N/A<br>";
    					
    					errors += "Per:&#32;&#32;&#32;";
    					if (item.getPerFigure() != null)
    						errors += MainFrame.toStringWithXDemical(item.getPerFigure(), 2) + "<br>";
    					else
    						errors += "N/A<br>";
    					
    					errors += "Units:&#32;&#32;&#32;";
    					if (item.getUnits() != null)
    						errors += item.getUnits() + "&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;<br>";
    					else
    						errors += "N/A" + "&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;&#32;<br>";
    					
    					errors += "<br>";
    					
    					// Error Info
    					errors += "ERROR: There is an incomplete field!<br>";
    					anyIssues = true;
    				} else {
    					// Print success message
    					errors += "The overall cost is:&#32;&#32;&#32;LL " + MainFrame.toStringWithXDemical(item.getCost(), 0) + " / " 
    								+ (item.getUnits().replaceAll("s", "")).replaceAll("olid","solid");
    					errors += "<br><br>";
    				}
    					
    				if (item.getUnusedWarning()) {
    					errors += "WARNING: This ingredient is not used in any recipes!<br><br>";
    					anyIssues = true;
    				}
    				
    				if (!anyIssues)
    					errors += "This ingredient has ZERO errors or warnings!<br><br>";

    				
    				errors += "<br><br><br><html>";
    				//Display info
                	JDialog temp = new JDialog(parent.frame, item.getName() + " Information");
                	temp.add(new JLabel(errors, SwingConstants.CENTER));
                	
                	temp.setBounds(100, 200, 450, 250);
                	temp.setVisible(true);
    			}
            }
		} );
			
		editMenu.add(getIngredientInfo);
		

		
		// Help
		JMenu helpMenu;
		JMenuItem calculateWasteItem;
		JMenuItem helpItem;
		
		helpMenu = new JMenu("Help");
		helpMenu.setFont(new Font("Dialog", Font.BOLD, 13));
		helpMenu.setMnemonic(KeyEvent.VK_H);
		helpMenu.getAccessibleContext().setAccessibleDescription("The main functionality in the program can be found here.");
		menuBar.add(helpMenu);
		
		calculateWasteItem = new JMenuItem("Calculate Wastage     ");
		calculateWasteItem.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
	            	
				WastageCalculatorDialog dialogAdd = new WastageCalculatorDialog();
				
				dialogAdd.pack();
				dialogAdd.setLocationRelativeTo(parent);
				dialogAdd.setVisible(true);

				String percentageInfo = dialogAdd.getValidatedPercentage();
				if (percentageInfo != null) {
					String[] resultTokens =  percentageInfo.split("\\|");
					
					String msg = "Starting Amount:  " + resultTokens[0] + " " + resultTokens[1] + "\n";
					msg += "Final Amount:  " + resultTokens[2] + " " + resultTokens[3] + "\n\n";
					msg += resultTokens[4] + "% of the original product was wasted to get the final product.";
					
					JOptionPane.showMessageDialog(parent, msg);
				}
			}
		} );
		
		helpMenu.add(calculateWasteItem);

		messagingSwitch = new JMenuItem("Turn Off Notifications from Update Buttons   ");
		messagingSwitch.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 changeMessagingLabel();
			}
		} );		
		helpMenu.add(messagingSwitch);		
		
		helpMenu.addSeparator();
		
		helpItem = new JMenuItem("Get Support         ");
		helpItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
				JOptionPane.showMessageDialog(parent, helpMessage);
            }
		} );
		
		helpMenu.add(helpItem);
	
		
		// Return menubar
		return menuBar;

	}
	
	public static boolean checkFileFormat(File file, String validExt, boolean isSave) {
		
		if (file.isDirectory())
			return false;
		
        String ext = null;
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');

        if (i > 0 &&  i < fileName.length() - 1)
            ext = fileName.substring(i+1).toLowerCase();
        
        if (!isSave && ext == null)
        	return false;
        
        if (ext != null && !ext.equals(validExt)) 	      
        	return false;
        
        boolean onePeriod = true;
        boolean oneNonWhiteChar = false;
        
		for (int w = 0; w < fileName.length(); w++) {
			if (!Character.isWhitespace(fileName.charAt(w))) {
				
				oneNonWhiteChar = true;
				if (!Character.isLetterOrDigit(fileName.charAt(w))) {
				    if (fileName.charAt(w) == '.' && onePeriod)
				    	onePeriod = false;
				    else
				    	return false;
				}
			}
		}
		
		if (!oneNonWhiteChar)
			return false;
		
		
        return true;
	}
	
	public void openEntireProgram(OPEN_FLAG openFlag) {
    
		File file = null;
        String finalFilePath = null;
    	
        int returnVal = -1;
        boolean continueToSave = true;
    
        
        if (openFlag != OPEN_FLAG.UNDO && openFlag != OPEN_FLAG.REDO && !parent.isSaved) {
        	
	        int overWrite = JOptionPane.showConfirmDialog(fc, "Would you like to SAVE your work before opening a different file?", "Save Before Opening", JOptionPane.YES_NO_OPTION);
	        if (overWrite == JOptionPane.YES_OPTION || overWrite == JOptionPane.NO_OPTION) {
				if (overWrite == JOptionPane.YES_OPTION) {
					saveEntireProgram(false);
					
					if (openFlag == OPEN_FLAG.NORMAL)
						JOptionPane.showMessageDialog(fc, "You can now OPEN a file of your choice!", "Open", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				return;
			}
        }       
 
        Reader f1 = null;
        Reader f2 = null;
		if (openFlag == OPEN_FLAG.NORMAL) {
			
			continueToSave = false;
			
	        returnVal = fc.showOpenDialog(parent);
	        while (returnVal == JFileChooser.APPROVE_OPTION) {
	
	            file = fc.getSelectedFile();
	            continueToSave = checkFileFormat(file, "txt", false);
	            
	            if (!continueToSave) {
	            	String warningString = "Your selected file, \"" + file.getName() + "\", has an invalid file format!\n\nThe filename must end in \".txt\" and have no characters except letters and spaces.";	                    	
	            	JOptionPane.showMessageDialog(fc, warningString, "Invalid File", JOptionPane.WARNING_MESSAGE);
	            } else {	
	            	finalFilePath = file.getAbsolutePath();
	            	
	            	file = new File(finalFilePath);
	            	break;
	            }
	            
	            continueToSave = false;
	        	returnVal = fc.showOpenDialog(parent);
	        }
	        
	        if (continueToSave) {
		        try {
			        f1 = new FileReader(finalFilePath);
			        f2 = new FileReader(finalFilePath);
		        } catch (Exception except) {             
		        	
		        	continueToSave = false;
		        	JOptionPane.showMessageDialog(fc, "There was an error opening your selected file!\n\nTry again or contact the creator of the program using the email address found in the \"Help\" tab", 
		        										"Open Error", JOptionPane.ERROR_MESSAGE);
		        }
	        }
		} else if (openFlag == OPEN_FLAG.BLANK) {
		    					
			f1 = new StringReader(MainFrame.initialState);
			f2 = new StringReader(MainFrame.initialState);
		} else {
						
			f1 = new StringReader(parent.undoRedoStack.get(parent.currentIndex).state);
			f2 = new StringReader(parent.undoRedoStack.get(parent.currentIndex).state);
		}
	
        if (continueToSave) {                                                  
            try {
            	BufferedReader br = new BufferedReader(f1);
            	boolean errorCheck = false;
            	
            	errorCheck = parent.openItem.openEntireFile(br, errorCheck);
            	br.close();
            	
            	if (errorCheck) {
            		br = new BufferedReader(f2);
            		
            		parent.openItem.openEntireFile(br, errorCheck);
            		
            		if (openFlag == OPEN_FLAG.NORMAL) {
            			parent.currentFilePathName = finalFilePath;
            			parent.tabbedPane.setSelectedIndex(0);
            			
                		JOptionPane.showMessageDialog(fc, "You have successfully opened \"" + file.getName() + "\"!", "Success", JOptionPane.INFORMATION_MESSAGE);
            		}
            		
            		if (openFlag == OPEN_FLAG.BLANK) {
            			parent.currentFilePathName = null;
                        parent.tabbedPane.setSelectedIndex(0);
                        
                		JOptionPane.showMessageDialog(fc, "You have successfully opened a blank program!", "Success", JOptionPane.INFORMATION_MESSAGE);
            		}
            		
            		if (openFlag != OPEN_FLAG.UNDO && openFlag != OPEN_FLAG.REDO) {
                		parent.undoRedoStack = new LinkedList<UndoItem>();
                        parent.undoRedoStack.add(new UndoItem(null, null, parent.saveItem.saveCurrentState()));
                        parent.currentIndex = 0;
                        
                        parent.isSaved = true;
            		} else if (openFlag != OPEN_FLAG.REDO) {
            			dialogLabel.setText(parent.undoRedoStack.get(parent.currentIndex + 1).undoMsg);
            			undoRedoDialog.setTitle("Undo Info  ");
            			
            			undoRedoDialog.pack();
            			undoRedoDialog.setVisible(true);
            	        parent.frame.setVisible(true);
            			
            			parent.isSaved = false;
            		} else {
            			dialogLabel.setText(parent.undoRedoStack.get(parent.currentIndex).redoMsg);
            			undoRedoDialog.setTitle("Redo Info  ");

            			undoRedoDialog.pack();
            			undoRedoDialog.setVisible(true);
            	        parent.frame.setVisible(true);
            			
            			parent.isSaved = false;
            		}
            			
            		
            	} else {
            		JOptionPane.showMessageDialog(fc, "There was an error opening your selected file!  It has an incorrect format.\n\n" +
            											"If you believe that it is a mistake, you can contact the creator of the program using the email address found in the \"Help\" tab.", 
            											"Invalid File Format Error", JOptionPane.ERROR_MESSAGE);
            	}
            	
            } catch (Exception except) {             
            	
            	JOptionPane.showMessageDialog(fc, "There was an error opening your selected file!\n\nTry again or contact the creator of the program using the email address found in the \"Help\" tab", 
            										"Open Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

	public void changeMessagingLabel() {
        if (parent.isMsgOn) {
        	parent.isMsgOn = false;
        	messagingSwitch.setText("Turn On Notifications from Update Buttons  ");      	
        } else {
        	parent.isMsgOn = true;
        	messagingSwitch.setText("Turn Off Notifications from Update Buttons  ");	            	
        }
	}
	
	public boolean saveEntireProgram(boolean saveAs) {
		
    	File file = null;
        String finalFilePath = null;
    	
        int returnVal = -1;
        boolean continueToSave = false;
                
        String initialSelect = "Save";
        if (saveAs)
        	initialSelect = "Save As";
        	
    	if (!saveAs && parent.currentFilePathName == null) {
    		String noFile = "There is no saved version of your program yet.  You must choose a filename to save your program's current state.";
    		JOptionPane.showMessageDialog(fc, noFile, "Filename Neeeded", JOptionPane.INFORMATION_MESSAGE);
    		
    		saveAs = true;
    	}

        if (saveAs) {        	
            
        	returnVal = fc.showDialog(parent, initialSelect);
	        while (returnVal == JFileChooser.APPROVE_OPTION) {

                file = fc.getSelectedFile();
                continueToSave = checkFileFormat(file, "txt", true);
                
                if (!continueToSave) {
                	String warningString = "Your selected file, \"" + file.getName() + "\", has an invalid file format!\n\nThe file must satisfy the following two properties:  \n" 
	                    					+ "1.  It can contain only (upper or lower case) letters and spaces (i.e. no symbols like *&/.~, ect.)\n" 
	                    					+ "2.  It can end with either NO extension or a .txt extension";	                    	
                	JOptionPane.showMessageDialog(fc, warningString, "Invalid File", JOptionPane.WARNING_MESSAGE);
                } else {
                    boolean addTxt = false; 
                    if (file.getName().lastIndexOf('.') < 0)                    
                    	addTxt = true;
                   
                	finalFilePath = file.getAbsolutePath();
                    if (addTxt)
                    	finalFilePath += ".txt";

                	file = new File(finalFilePath);
                	if (file.exists()) {
                        int overWrite = JOptionPane.showConfirmDialog(fc, "Your selected file, \"" + file.getName() + "\",	 already exists.  Would you like to OVERWRITE it?",
                                										"Overwrite File", JOptionPane.YES_NO_OPTION);
                        if (overWrite == JOptionPane.YES_OPTION) {
                        	break;
                        }
                	} else {
                		break;
                	}
                }
                
                continueToSave = false;
                returnVal = fc.showDialog(parent, initialSelect);
	        }
        } else {
        	
        	finalFilePath = parent.currentFilePathName;
        	file = new File(finalFilePath);
        	continueToSave = true;
        }
        
        if (continueToSave) {                                      

            WriteFile saver = new WriteFile(finalFilePath);
            try {
            	//Make sure that there are no unsaved changes
            	if (parent.iPricingPane.areAnyChanges()) {
            		parent.iPricingPane.updateAction();
            	}
            	
            	int changeStatus = parent.dishCompPane.areAnyChanges();
            	if (changeStatus != 0) {
            		if (changeStatus == 1 || changeStatus == 2)
            			parent.dishCompPane.confirmAction();
            		
            		if (changeStatus == 1 || changeStatus == 3)
            			parent.dishCompPane.updateAction();
            	}
            	
            	//Saved current state to file path
            	saver.writeToFile(parent.undoRedoStack.get(parent.currentIndex).state);
            	JOptionPane.showMessageDialog(fc, "You have successfully saved your work as \"" + file.getName() + "\"!", "Success", JOptionPane.INFORMATION_MESSAGE);           	
            	
            	if (saveAs)
            		parent.currentFilePathName = finalFilePath;

            	parent.isSaved = true;
            	return true;
            	
            } catch (Exception except) {
            	except.printStackTrace();
            	except.getMessage();
            	JOptionPane.showMessageDialog(fc, "There was an error saving!\nTry again or contact the creator of the program using the email address found in the \"Help\" tab.", 
            									"Saving Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return false;
	}
	
	public void changeIsSold(boolean initiallySold) {
		
		ArrayList<String> tempList = null;
		String message = "";
		if (initiallySold) {
			tempList = (ArrayList<String>) parent.categories.get(Category.ENTIRE_MENU).getDishes().clone();
			tempList.add(0, "SELECT  (from Entire Menu)");
			
			message = "Choose a Dish to Convert from SOLD to UNSOLD:\n    \n";
		} else {
			tempList = (ArrayList<String>) parent.categories.get(Category.UNSOLD_DISHES).getDishes().clone();
			tempList.add(0, "SELECT  (from Unsold Dishes)");
			
			message = "Choose a Dish to Convert from UNSOLD to SOLD:\n    \n";
		}

	
		Object[] possibilities = tempList.toArray();
		String dishName = (String) JOptionPane.showInputDialog(parent,
				message, "Dish Converter",
				JOptionPane.PLAIN_MESSAGE, null, possibilities,
				possibilities[0]);
	
		if (dishName != null && ((dishName.length() < 6) || (!dishName.substring(0, 6).equals("SELECT")))) {
			MenuItem pickedDish = parent.itemHash.get(dishName);
	
			if (initiallySold && parent.currentOrder.keySet().contains(dishName))
				parent.currentOrder.remove(dishName);
	
			Category tempCategory = null;
			for (String catName : pickedDish.getCategories()) {
				tempCategory = parent.categories.get(catName);
	
				tempCategory.removeDish(dishName);
			}
			
			if (initiallySold)
				parent.categories.get(Category.UNSOLD_DISHES).addDish(dishName);
			else
				parent.categories.get(Category.ENTIRE_MENU).addDish(dishName);
			
			pickedDish.changeIsSoldDish();
				
			
			//Repaint Panes 2,3,4,5
			parent.dishCompPane.repaintNoUpdateFields(dishName);
			
			if (initiallySold && parent.catManagementPane.getSelectedDish() != null 
				&& parent.catManagementPane.getSelectedDish().getName().equals(dishName))
					parent.catManagementPane.setSelectedDish(null);
			parent.catManagementPane.repaintAll();
			
			parent.itemUsagePane.repaintAll();
			
			parent.resultsPane.repaintAll();
			
			//Confirmation
			String finalMessage = "";
			
			if (initiallySold) {
				finalMessage = "You have successfully converted \"" + dishName + "\" from a SOLD to an UNSOLD dish!         ";
				parent.updateUndoRedoStack("Converted \"" + dishName + "\" from an UNSOLD back to a SOLD dish", "Converted \"" + dishName + "\" from a SOLD to a UNSOLD dish");
			} else {
				finalMessage = "You have successfully converted \"" + dishName + "\" from an UNSOLD to a SOLD dish!         ";
				parent.updateUndoRedoStack("Converted \"" + dishName + "\" from a SOLD back to an UNSOLD dish", "Converted \"" + dishName + "\" from an UNSOLD to a SOLD dish");
			}
			
			JOptionPane.showMessageDialog(parent, finalMessage);
		}
	}
	
	public void changeName(boolean isDish) {
		ArrayList<String> tempList = null;
		if (isDish) {
			tempList = (ArrayList<String>) parent.categories.get(Category.ENTIRE_MENU).getDishes().clone();
			
			for (String newDish : parent.categories.get(Category.UNSOLD_DISHES).getDishes())
				MainFrame.InsertIntoList(tempList, newDish);
		} else {
			tempList = (ArrayList<String>) parent.ingredientList.clone();
		}
		
		tempList.add(0, "SELECT");
		
		Object[] possibilities = tempList.toArray();
		String oldIngredientName = (String) JOptionPane.showInputDialog(parent,
				"Choose an Item to Change its Name:\n    \n", "Name Changer",
			JOptionPane.PLAIN_MESSAGE, null, possibilities,
			possibilities[0]);
		
		if (oldIngredientName != null && (!oldIngredientName.equals("SELECT"))) {
			SingleFieldDialog dialogAdd = new SingleFieldDialog("Change Name of " + oldIngredientName, 
					"What would you like the NEW name of \"" + oldIngredientName.toLowerCase() + "\" to be?\n   \n", parent.itemHash.keySet(), false, false);
	
			dialogAdd.pack();
			dialogAdd.setLocationRelativeTo(parent);
			dialogAdd.setVisible(true);
			
			String newName = dialogAdd.getValidatedText();
			if (newName != null && !newName.toUpperCase().equals("SELECT")) {
			
				newName = MainFrame.toFirstLettersUpper(newName);
	        	MenuItem ingredient = parent.itemHash.get(oldIngredientName);
	        		        	
	        	if (!isDish) {
		        	parent.ingredientList.remove(oldIngredientName);
		        	MainFrame.InsertIntoList(parent.ingredientList, newName);
	        	}
	        	
	        	for (MenuItem use : ingredient.getUses().values()) {
	        		use.getRecipeItemList().put(newName, new RecipeItem(use.getRecipeItem(oldIngredientName)));
	        		use.getRecipeItemList().remove(oldIngredientName);
	        	}
	    	
	        	if (isDish) {
	        		for (String category : ingredient.getCategories()) {
	        			parent.categories.get(category).removeDish(oldIngredientName);
	        			parent.categories.get(category).addDish(newName);
	        		}
	        		
	        		MenuItem ingredientItem = null;
	        		for (RecipeItem use : ingredient.getRecipeItemList().values()) {
	        			ingredientItem = use.getItem();
	        			
	        			ingredientItem.addUse(newName, ingredient);
	        			ingredientItem.removeUse(oldIngredientName);
	        		}
	        		
	        		if (parent.currentOrder.keySet().contains(oldIngredientName)) {
	        			int pastQuant = parent.currentOrder.get(oldIngredientName).getOrderNum();
	        			
	        			parent.currentOrder.remove(oldIngredientName);
	        			parent.currentOrder.put(newName, new OrderItem(newName, pastQuant));
	        		}
	        	}
	        	
	        	ingredient.setName(newName);
	        	
	        	parent.itemHash.put(newName, parent.itemHash.get(oldIngredientName));
	        	parent.itemHash.remove(oldIngredientName);
	        	
	        	boolean redrawAllPaneTwo = parent.dishCompPane.changeItemName(oldIngredientName, newName);
	        	
	        	//Redraw panes
	        	if (!isDish) {	        			
	        		parent.iPricingPane.repaintAllWithNoFieldChanges();
	        	}
	        	
	        	if (redrawAllPaneTwo)
	        		parent.dishCompPane.repaintNoUpdateFields(-1);
	        	else
	        		parent.dishCompPane.repaint(true, true, true);
	        	
	        	parent.catManagementPane.repaintAll();
	        	parent.itemUsagePane.repaintAll();
	        	parent.resultsPane.repaintAll();
	        	
				//Confirmation
				String finalMessage = null;
				
				if (isDish) {
					finalMessage = "You have successfully change the dish name from \"" + oldIngredientName + "\" to \"" + newName +"\"!         ";
					parent.updateUndoRedoStack("Changed the dish name from \"" + newName + "\" back to \"" + oldIngredientName +"\"", "Changed the dish name from \"" + oldIngredientName + "\" to \"" + newName +"\"");
				} else {
					finalMessage = "You have successfully change the ingredient name from \"" + oldIngredientName + "\" to \"" + newName +"\"!         ";
					parent.updateUndoRedoStack("Changed the ingredient name from \"" + newName + "\" back to \"" + oldIngredientName +"\"", "Changed the ingredient name from \"" + oldIngredientName + "\" to \"" + newName +"\"");
				}
				
				JOptionPane.showMessageDialog(parent, finalMessage);
			}
		}
	}
}