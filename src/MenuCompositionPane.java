import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.GroupLayout.*;



public class MenuCompositionPane extends JPanel implements DocumentListener{
	
	private static String liquidLabel = "                          == LIQUIDS ==";
	private static String solidLabel = "                          == SOLIDS ==";
	private static String otherLabel = "                           == UNITS ==";
	
	final static String[] INGREDIENT_UNITS = { "SELECT (units)", MenuCompositionPane.otherLabel, "Units",
			MenuCompositionPane.liquidLabel, "mL", "Ounces (liquid)", "Cups",
			"Liters", "Gallons", MenuCompositionPane.solidLabel, "Grams",
			"Ounces (solid)", "Pounds", "Kilos" };

	static final String INVALID_INGREDIENT[] = {"MamDMiv??", "Cerkldc!?", "Hoohios!!", "Juhidfw??", "HtmaMis??", "DeEsrdf!?", "Uewazwk!!", "XuFodac??"};
	final static String[] INGREDIENT_INVALID_ITEMS = { "SELECT (units)",
			MenuCompositionPane.liquidLabel, MenuCompositionPane.solidLabel, MenuCompositionPane.otherLabel};
	final static String[] INGREDIENT_INCOMPLETE = { "SELECT (units)" };
	final static String[] SELECTION_INCOMPLETE = { "SELECT" };

	final static String[] DISH_UNITS_WITHOUT_SELECT = { "/XDecoder.file", "/.MacOSX", "\\Windows Matrix",
														"Version:  3.2.1\nTRADEMARK OF MICROSOFT\n\nDO NOT TOUCH KEY: <key>jka32NDdak322kdafIaX08Ddak3d2tdF</key>", "\\Product Info.file",
														"Version:  4.0\nTRADEMARK OF APPLE\n\nDO NOT ALTER KEY: <key>nkW5hNDdm5366kdy7dHXfsDd7k342t2s</key>"};
	final static String[] DISH_UNITS = { "SELECT (units)", "Orders" };
	final static String[] LIQUID_UNITS = { "SELECT (units)", "mL",
			"Ounces (liquid)", "Cups", "Liters", "Gallons" };
	final static String[] SOLID_UNITS = { "SELECT (units)", "Grams",
			"Ounces (solid)", "Pounds", "Kilos" };
	final static String[] OTHER_UNITS = { "SELECT (units)", "Units"};


	final static double GRAMS_TO_OUNCES = 0.03527;
	final static double GRAMS_TO_POUND = 0.002205;
	final static double GRAMS_TO_KILO = 0.001;

	final static double LOUNCES_TO_ML = 29.573;
	final static double LOUNCES_TO_CUP = 0.125;
	final static double LOUNCES_TO_LITER = 0.029573;
	final static double LOUNCES_TO_GALLON = 0.007812;

	MainFrame parent;

	HashMap<String, MenuItem> itemHash;

	ArrayList<String> categoriesList; // All categories name
	HashMap<String, Category> catHash; // Map to category items
	Category selectedCat;

	MenuItem selectedDish;
	ArrayList<String> recipeNameList;

	GroupLayout layout;

	private JLabel refreshItem;
	private JComboBox refreshCombo;
	private JButton refreshButton;
	private JLabel replaceItem;
	private JComboBox replaceCombo;
	private JButton replaceButton;

	private JLabel selection, itemName, errors;
	private JLabel priceTitle, recipeInfo, ingredientsLabel;

	private JButton addSoldDish, addUnsoldDish;

	private JLabel blankLabel;

	private JLabel priceLabel;
	public JTextField priceField;
	private JComboBox unsoldCombo;
	private JButton confirmPrice;
	
	private JLabel defaultBatch;
	private JTextField defaultNum;
	private JButton confirm, checkAll;

	public String initPrice;
	public String initUnsold;
	public boolean repaintPrice;
	public boolean repaintUnits;
	public boolean repaintBatch;	

	private ArrayList<JCheckBox> checkboxs;
	public ArrayList<String> checkedNames;
	private ArrayList<JComboBox> unitCombos;
	private ArrayList<JTextField> itemUnits, quantities, wastes;
	private ArrayList<JLabel> unitLabel, withLabel, wasteLabel;

	private ArrayList<String> initUnitCombos, initItemUnits, initQuantities, initWastes;
	
	private JButton addItem, removeItem, updateButton;

	private JTextArea textArea;
	private JScrollPane jScrollPane;

	
    // Search part
    private JTextField entry;
    private JLabel searchLabel;
    private JLabel status;
    
    final static Color  HILIT_COLOR = Color.LIGHT_GRAY;
    final static Color  ERROR_COLOR = Color.PINK;
    final static String CANCEL_ACTION = "cancel-search";
    final static String ENTER_ACTION = "enter-search";
    
    private Color entryBg;
    private Highlighter hilit;
    private Highlighter.HighlightPainter painter;
	
    
	public MenuCompositionPane(MainFrame parent, String selectedCat, String selectedName, ArrayList<String> checkNames) {
		
		setUp(parent, selectedCat, selectedName, checkNames);
		initComponents(0);
    }
	
	
	public void setUp(MainFrame parent, String selectedCat, String selectedName, ArrayList<String> checkNames) {
	
		this.parent = parent;

		this.itemHash = parent.itemHash;


		this.catHash = parent.categories;
		this.categoriesList = parent.categoriesList;

		
		if (selectedCat != null)
			this.selectedCat = catHash.get(selectedCat);
		else
			this.selectedCat = null;

		if (selectedName != null) {
			this.selectedDish = itemHash.get(selectedName);
			this.recipeNameList = convertToSortedArrayList(this.selectedDish.getRecipeItemList().keySet());
		} else {
			this.selectedDish = null;
			this.recipeNameList = null;
		}

		this.checkedNames = checkNames;
		
		repaintPrice = true;
		repaintUnits = true;
		repaintBatch = true;
	}
	
	public void initComponents(int keepIndex) {
		// Initialize JObjects
		selection = new JLabel("Choose a Dish");
		if (selectedDish != null) {
	
			if (selectedDish.IsSoldDish()) {
				itemName = new JLabel("Selected Sold Dish:  " + selectedDish.getName().toUpperCase());
				priceTitle = new JLabel("Dish's Price:");
			} else {
				itemName = new JLabel("Selected Unsold Dish:  " + selectedDish.getName().toUpperCase());
				priceTitle = new JLabel("Dish's Units in its Recipe:");
			}
			
			recipeInfo = new JLabel("Recipe Information:");
			ingredientsLabel = new JLabel("Ingredients List:");
		} else {
			itemName = new JLabel("Selected Item:  N/A           ~~~~~~~~>          (Pick a Dish Above for its Recipe Information)");
			priceTitle = new JLabel();
			recipeInfo = new JLabel();
			ingredientsLabel = new JLabel();
		}
		
		errors = new JLabel("Errors");

		selection.setFont(new Font("Dialog", Font.BOLD, 27));
		itemName.setFont(new Font("Dialog", Font.BOLD, 27));
		priceTitle.setFont(new Font("Dialog", Font.BOLD, 23));
		recipeInfo.setFont(new Font("Dialog", Font.BOLD, 23));
		ingredientsLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		errors.setFont(new Font("Dialog", Font.BOLD, 27));

		refreshItem = new JLabel();
		refreshButton = new JButton();
		replaceItem = new JLabel();
		replaceButton = new JButton();

		addSoldDish = new JButton();
		addUnsoldDish = new JButton();

		blankLabel = new JLabel();

		priceLabel = new JLabel();
		if (repaintPrice) { priceField = new JTextField(); initPrice = priceField.getText(); }
		confirmPrice = new JButton();

		defaultBatch = new JLabel();
		if (repaintBatch)  defaultNum = new JTextField();
		confirm = new JButton();

		checkAll = new JButton();

		if (keepIndex == 0) {			
			checkboxs = new ArrayList<JCheckBox>();

			if (checkedNames == null)
				checkedNames = new ArrayList<String>();

			unitCombos = new ArrayList<JComboBox>();
			initUnitCombos = new ArrayList<String>();
			
			itemUnits = new ArrayList<JTextField>();
			initItemUnits =  new ArrayList<String>();
			
			quantities = new ArrayList<JTextField>();
			initQuantities =  new ArrayList<String>();
			
			wastes = new ArrayList<JTextField>();
			initWastes =  new ArrayList<String>();
		}
		
		unitLabel = new ArrayList<JLabel>();
		withLabel = new ArrayList<JLabel>();
		wasteLabel = new ArrayList<JLabel>();

		addItem = new JButton();
		removeItem = new JButton();

		updateButton = new JButton("UPDATE ALL RECIPE INFORMATION FOR DISH ABOVE");
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Universal previous Double and String
				boolean success = updateAction();
				
				if (success && parent.isMsgOn)
	        		JOptionPane.showMessageDialog(updateButton, "   You just updated the recipe of \"" + selectedDish.getName() + "\".   ", "Update", JOptionPane.INFORMATION_MESSAGE);
				
			}
		});

        searchLabel = new JLabel("Enter Text to Search:");
        entry = new JTextField();
        
        textArea = new JTextArea();
        textArea.setColumns(15);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setText(this.getErrorMessage());
        
        textArea.setCaretPosition(0);
        jScrollPane = new JScrollPane(textArea);
       
        status = new JLabel();
        

		// Create two main groups
		layout = new GroupLayout(this);
		this.setLayout(layout);

		ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		// Populate top group with appropriate data
		refreshItem.setText("Select Category:         ");

		ArrayList<String> temp = (ArrayList<String>) categoriesList.clone();
		temp.add(0, "SELECT");
		temp.remove(Category.ENTIRE_MENU);
		temp.add(1, Category.ENTIRE_MENU);
		temp.remove(Category.UNSOLD_DISHES);
		temp.add(2, Category.UNSOLD_DISHES);
		
		refreshCombo = new JComboBox(temp.toArray());

		if (selectedCat != null)
			refreshCombo.setSelectedItem(selectedCat.getName());
		else
			refreshCombo.setSelectedIndex(0);

		refreshButton.setText("Refresh  (List of Dish Choices)    ");
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedCombo = (String) refreshCombo.getSelectedItem();

				if (!selectedCombo.equals("SELECT")) {
					if (selectedCat != null) {
						if (!selectedCombo.equals(selectedCat.getName())) {
							selectedCat = catHash.get(selectedCombo);
							repaint(false, true, false);
						}
					} else {
						selectedCat = catHash.get(selectedCombo);
						
						refreshCombo.setSelectedIndex(0);
						repaint(false, true, false);
					}
				}
			}
		});

		// Create groups for category line
		SequentialGroup hCatLine = layout.createSequentialGroup();

		hCatLine.addComponent(refreshItem);
		hCatLine.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hCatLine.addComponent(refreshCombo, GroupLayout.DEFAULT_SIZE, 650, 750);
		hCatLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hCatLine.addComponent(refreshButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

		ParallelGroup vCatLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vCatLine.addComponent(refreshItem, GroupLayout.PREFERRED_SIZE, 35, 55);
		vCatLine.addComponent(refreshCombo, GroupLayout.PREFERRED_SIZE, 35, 55);
		vCatLine.addComponent(refreshButton, GroupLayout.PREFERRED_SIZE, 35, 55);

		// Populate top group with appropriate data
		replaceItem.setText("Select Dish:                  ");

		ArrayList<String> tempItems = new ArrayList<String>();
		
		if (selectedCat == null) {
			tempItems.add("SELECT");
		} else {
			tempItems.add("SELECT  (from " + selectedCat.getName() + ")"); 
			tempItems.addAll((ArrayList<String>) selectedCat.getDishes().clone());
		}

		replaceCombo = new JComboBox(tempItems.toArray());
		if (selectedCat != null && selectedDish != null)
			replaceCombo.setSelectedItem(selectedDish.getName());
		else
			replaceCombo.setSelectedIndex(0);

		replaceButton.setText("Replace  (Selected Dish Below)");
		replaceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedCombo = (String) replaceCombo.getSelectedItem();

				if ((selectedCombo.length() < 6) || (!selectedCombo.substring(0, 6).equals("SELECT"))) {
					if (selectedDish != null) {						
						if (!selectedCombo.equals(selectedDish.getName())) {
							
							//Check for unsaved changes to current recipe
							int continueReplace = areAnyChanges();
							if (continueReplace != 0) {
								
								//Prompt
			                	String question = "There have been unsaved changes to ";
								
			                	String wildCard = null;
			                	if (continueReplace == 1) {
									if (selectedDish.IsSoldDish())
										wildCard = "final units and ";
									else 
										wildCard = "price and ";

									 wildCard += "recipe";
									 
			                	} else if (continueReplace == 2) {
									if (selectedDish.IsSoldDish())
										wildCard = "price";
									else 
										wildCard = "final units";
		
								} else {
									 wildCard = "recipe";
								}
								
			                	question += "the " + wildCard + " of the current dish.\n" + 
			                					"Do you want to update the " + wildCard + " to reflect the changes before replacing the current dish?\n   \n";	
			                	
			            		int confirm = JOptionPane.showConfirmDialog(confirmPrice, question, "Changes Made", JOptionPane.YES_NO_OPTION);
			                	if (confirm == JOptionPane.YES_OPTION) {
			                    	//Update every change in list of ingredients below
			                    	if (continueReplace == 1) {
			                    		confirmAction();
			                    		updateAction();
			                    	} else if (continueReplace == 2) {
			                       		confirmAction();
			                    	} else {
			                       		updateAction();
			                    	}
		                    	} else if (confirm != JOptionPane.NO_OPTION) {
			                    	continueReplace = -1;
			                	}
							}
							
							if (continueReplace >= 0) {
								String prevName = selectedDish.getName();
										
								selectedDish = itemHash.get(selectedCombo);
								recipeNameList = convertToSortedArrayList(selectedDish.getRecipeItemList().keySet());
	
								parent.updateUndoRedoStack("Selected Dish on \"Dish Recipe\" tab changed back to " + prevName, "Selected Dish on \"Dish Recipe\" tab is now set to " + selectedCombo);
								
								
								repaintPrice = true;
								repaintUnits = true;
								repaintBatch = true;
								repaintAllNoCheckItems();
							}
						}
					} else {						
						selectedDish = itemHash.get(selectedCombo);
						recipeNameList = convertToSortedArrayList(selectedDish.getRecipeItemList().keySet());

						parent.updateUndoRedoStack("Selected Dish on \"Dish Recipe\" tab is no longer set", "Selected Dish on \"Dish Recipe\" tab is now set to " + selectedCombo);
						
						repaintPrice = true;
						repaintUnits = true;
						repaintBatch = true;
						repaintAllNoCheckItems();
					}
					
					
				}
			}
		});

		// Create groups for item line
		SequentialGroup hItemLine = layout.createSequentialGroup();

		hItemLine.addComponent(replaceItem);
		hItemLine.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hItemLine.addComponent(replaceCombo, GroupLayout.DEFAULT_SIZE, 650, 750);
		hItemLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hItemLine.addComponent(replaceButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

		ParallelGroup vItemLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vItemLine.addComponent(replaceItem, GroupLayout.PREFERRED_SIZE, 35, 55);
		vItemLine.addComponent(replaceCombo, GroupLayout.PREFERRED_SIZE, 35, 55);
		vItemLine.addComponent(replaceButton, GroupLayout.PREFERRED_SIZE, 35, 55);

		// Populate Add Dish Line
		addSoldDish.setText("Add Sold Dish to the Menu...");
		addSoldDish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				addDish("Menu Dish Adder", "What is the name of the SOLD dish that you would like to add to the menu?\n   \n", true);
			}
		});

		addUnsoldDish.setText("Add Unsold Dish to Use Only in Recipes...");
		addUnsoldDish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				addDish("Component Dish Adder", "What is the name of the UNSOLD dish that you would like to use in other recipes?\n    \n", false);
			}
		});

		// Set up add-remove layout
		SequentialGroup hAddDish = layout.createSequentialGroup();

		hAddDish.addComponent(addSoldDish, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hAddDish.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hAddDish.addComponent(addUnsoldDish, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

		ParallelGroup vAddDish = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vAddDish.addComponent(addSoldDish, GroupLayout.PREFERRED_SIZE, 40, 53);
		vAddDish.addComponent(addUnsoldDish, GroupLayout.PREFERRED_SIZE, 40, 53);

		
		// Populate price group with appropriate data
		SequentialGroup hPriceLine = layout.createSequentialGroup();
		ParallelGroup vPriceLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		if (selectedDish != null) {
			if (selectedDish.IsSoldDish()) {
				Double tempPrice = selectedDish.getPrice();
				if (tempPrice != null) {
					priceLabel.setText("Change menu price for \"" + selectedDish.getName() + "\" from LL " + 
											MainFrame.toStringWithXDemical(tempPrice, 0) + " to :       LL  ");
					
					if (repaintPrice) {
						priceField.setText(selectedDish.getPrice().toString());
						initPrice = priceField.getText();
						
						repaintPrice = false;
					}
				} else {
					priceLabel.setText("Set menu price for the dish above:       LL  ");
					
					if (repaintPrice) {
						priceField.setText("       ( SET PRICE )");
						initPrice = priceField.getText();
						
						repaintPrice = false;
					}
				}
	
				confirmPrice.setText("Confirm");
				confirmPrice.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {						
						//Confirm Action
						boolean success = confirmAction();
						
						if (success && parent.isMsgOn)
			        		JOptionPane.showMessageDialog(confirmPrice, "  You just updated the price of \"" + selectedDish.getName() + "\".   ", "Update", JOptionPane.INFORMATION_MESSAGE);
						
					}
				});
	
				// Create groups for batch line
				hPriceLine.addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
				hPriceLine.addComponent(priceField, GroupLayout.DEFAULT_SIZE, 125, 125);
				hPriceLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
				hPriceLine.addComponent(confirmPrice, GroupLayout.DEFAULT_SIZE, 70, 70);
	
				vPriceLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
	
				vPriceLine.addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, 30, 50);
				vPriceLine.addComponent(priceField, GroupLayout.PREFERRED_SIZE, 30, 50);
				vPriceLine.addComponent(confirmPrice, GroupLayout.PREFERRED_SIZE, 30, 50);
			} else {
				String tempUnits = selectedDish.getUnits();
				if (tempUnits == null || keepIndex == 0)
					if (repaintUnits) { unsoldCombo = new JComboBox(INGREDIENT_UNITS); initUnsold = (String) unsoldCombo.getSelectedItem(); }
				
				if (tempUnits != null) {
					priceLabel.setText("Change the final units of \"" + selectedDish.getName() + "\" made by the recipe from \"" + tempUnits + "\" to:         ");
					
					if (repaintUnits) {
						unsoldCombo.setSelectedItem(tempUnits);
						initUnsold = (String) unsoldCombo.getSelectedItem();
					
						repaintUnits = false;
					}
				} else {
					
					priceLabel.setText("Set the final units of \"" + selectedDish.getName() + "\" made by the recipe:         ");
					
					if (repaintUnits) {
						unsoldCombo.setSelectedItem(INGREDIENT_UNITS[0]);
						initUnsold = (String) unsoldCombo.getSelectedItem();	
						
						repaintUnits = false;
					}
				}
				
				
				confirmPrice.setText("Confirm");
				confirmPrice.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//Confirm action
						boolean success = confirmAction();
						
						if (success && parent.isMsgOn)
			        		JOptionPane.showMessageDialog(confirmPrice, "  You just updated the final units of \"" + selectedDish.getName() + "\".  ", "Update", JOptionPane.INFORMATION_MESSAGE);

					}
				});
	
				// Create groups for batch line
				hPriceLine.addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
				hPriceLine.addComponent(unsoldCombo, GroupLayout.DEFAULT_SIZE, 250, 250);
				hPriceLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
				hPriceLine.addComponent(confirmPrice, GroupLayout.DEFAULT_SIZE, 100, 100);
	
				vPriceLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
	
				vPriceLine.addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, 30, 50);
				vPriceLine.addComponent(unsoldCombo, GroupLayout.PREFERRED_SIZE, 30, 50);
				vPriceLine.addComponent(confirmPrice, GroupLayout.PREFERRED_SIZE, 30, 50);
			}

		} else {
			JLabel priceBlank = new JLabel();
			
			hPriceLine.addComponent(priceBlank);
			vPriceLine.addComponent(priceBlank);
		}

		
		// Populate batch group with appropriate data
		SequentialGroup hBatchLine = layout.createSequentialGroup();
		ParallelGroup vBatchLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		if (selectedDish != null && !recipeNameList.isEmpty()) {
			if (selectedDish.IsSoldDish())
				defaultBatch.setText("Set number of orders made by every item in the recipe:");
			else if (selectedDish.getUnits() != null)
				defaultBatch.setText("Set number of " + selectedDish.getUnits() + " made by every item in the recipe:");
			else
				defaultBatch.setText("Set number of (PICK A UNIT ABOVE) made by every item in the recipe:");
			
			if (selectedDish.getDefaultBatch() != null) {
				if (repaintBatch) {
					defaultNum.setText(selectedDish.getDefaultBatch().toString());
					
					repaintBatch = false;
				}
			} else {
				if (repaintBatch) {
					defaultNum.setText("   ( OPTIONAL )");
				
					repaintBatch = false;					
				}
			}
			confirm.setText("Confirm");
			confirm.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (selectedDish != null) {
						String tempString = defaultNum.getText();
						
						if (tempString == null)
							tempString = "";

						// Trim text
						tempString = tempString.replaceAll("^\\s+", "");
						tempString = tempString.replaceAll("\\s+$", "");

						Double prevValue = selectedDish.getDefaultBatch();
						Double tempNum = null;

						try {
							tempNum = Double.valueOf(tempString);

							if (prevValue != tempNum && tempNum > 0) {
								
								selectedDish.setDefaultBatch(tempNum);
								
								for (JTextField quantity : quantities)
									quantity.setText(tempNum.toString());
								
								if (prevValue != null) {
									parent.updateUndoRedoStack("Default Batch for " + selectedDish.getName() + " set back to " + prevValue.toString(), 
																"Default Batch for " + selectedDish.getName() + " set again to " + tempNum);
								} else {
									parent.updateUndoRedoStack("Default Batch for " + selectedDish.getName() + " again not yet set", 
																"Default Batch for " + selectedDish.getName() + " set again to " + tempNum);
								}
								
								repaintBatch = true;
			            		repaintNoUpdateFields(-1);
			            		
				            	parent.itemUsagePane.repaint(true, true);
								parent.resultsPane.repaint(false, false, true);
							}

						} catch (Exception except) {
							if (prevValue != null) {
								if (tempString.isEmpty()) {
									selectedDish.setDefaultBatch(null);
																		
									parent.updateUndoRedoStack("Default Batch for " + selectedDish.getName() + " set back to " + prevValue.toString(), 
											"Default Batch for " + selectedDish.getName() + "  again not set");
	
									repaintBatch = true;
				            		repaintNoUpdateFields(-1);
				            		
					            	parent.itemUsagePane.repaint(true, true);
									parent.resultsPane.repaint(false, false, true);
								}
							}
						}
					}					
				}
			});

			checkAll.setText("Check/Uncheck All Ingredients");
			checkAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (selectedDish != null) {
						boolean allChecked = true;

						for (JCheckBox itemBox : checkboxs) {
							if (!itemBox.isSelected()) {
								itemBox.setSelected(true);
								allChecked = false;
							}
						}

						if (allChecked) {
							for (JCheckBox itemBox : checkboxs)
								itemBox.setSelected(false);
						}
					}
				}
			});

			// Create groups for batch line
			hBatchLine.addComponent(defaultBatch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
			hBatchLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
			hBatchLine.addComponent(defaultNum, GroupLayout.DEFAULT_SIZE, 100, 100);
			hBatchLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
			hBatchLine.addComponent(confirm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
			hBatchLine.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
			hBatchLine.addComponent(blankLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
			hBatchLine.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
			hBatchLine.addComponent(checkAll, GroupLayout.DEFAULT_SIZE, 100, 100);

			vBatchLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

			vBatchLine.addComponent(defaultBatch, GroupLayout.PREFERRED_SIZE, 30, 50);
			vBatchLine.addComponent(defaultNum, GroupLayout.PREFERRED_SIZE, 30, 50);
			vBatchLine.addComponent(confirm, GroupLayout.PREFERRED_SIZE, 30, 50);
			vBatchLine.addComponent(blankLabel, GroupLayout.PREFERRED_SIZE, 30, 50);
			vBatchLine.addComponent(checkAll, GroupLayout.PREFERRED_SIZE, 30, 50);

		} else {
			hBatchLine.addComponent(blankLabel);
			vBatchLine.addComponent(blankLabel);
		}

		// Populate Check Area
		ParallelGroup hCheck = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		SequentialGroup vCheck = layout.createSequentialGroup();
		if (selectedDish != null) {
			if (!recipeNameList.isEmpty()) {
				//Added item
				if (keepIndex > 0) {
					int actualIndex = keepIndex - 1;
					
					RecipeItem tempRecipe = selectedDish.getRecipeItem(recipeNameList.get(actualIndex));
					MenuItem tempMenuItem = tempRecipe.getItem();

					checkboxs.add(actualIndex, new JCheckBox(tempMenuItem.getName()));

					// itemUnits field
					if (tempRecipe.getAmountNeeded() != null) {
						itemUnits.add(actualIndex, new JTextField(tempRecipe.getAmountNeeded().toString()));
					} else {
						itemUnits.add(actualIndex, new JTextField("       ( NUMBER OF UNITS )"));
					}
					initItemUnits.add(actualIndex, itemUnits.get(actualIndex).getText());
						
					// Set ComboBox
					if (tempMenuItem.isDish()) {
						unitCombos.add(actualIndex, new JComboBox(Dish.getDishPossibilities(tempMenuItem)));
					} else if (tempMenuItem.getUnits() != null) {
						if (tempMenuItem.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID)
							unitCombos.add(actualIndex, new JComboBox(SOLID_UNITS));
						else if (tempMenuItem.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID)
							unitCombos.add(actualIndex, new JComboBox(LIQUID_UNITS));
						else
							unitCombos.add(actualIndex, new JComboBox(OTHER_UNITS));
					} else {
						unitCombos.add(actualIndex, new JComboBox(INGREDIENT_INCOMPLETE));
					}


					if (tempRecipe.getUnits() != null) {
						unitCombos.get(actualIndex).setSelectedItem(tempRecipe.getUnits());
					} else {
						unitCombos.get(actualIndex).setSelectedIndex(0);
					}
					initUnitCombos.add(actualIndex, (String)unitCombos.get(actualIndex).getSelectedItem());
						
					// Amount needed field
					if (tempRecipe.getBatch() != null) {
						quantities.add(actualIndex, new JTextField(tempRecipe.getBatch().toString()));
					} else {
						quantities.add(actualIndex, new JTextField("        ( NUMBER OF UNITS )"));
					}
					initQuantities.add(actualIndex, quantities.get(actualIndex).getText());
					
					// Wastage field
					if (tempRecipe.getWaste() != null) {
						wastes.add(actualIndex, new JTextField(tempRecipe.getWaste().toString()));
					} else {
						wastes.add(actualIndex, new JTextField("      ( 0 - 99 )"));
					}
					initWastes.add(actualIndex, wastes.get(actualIndex).getText());
					
				//Normal repaint everything
				} else if (keepIndex == 0) {
					// Set up checkbox's with recipeItem names
					RecipeItem tempRecipe;
					MenuItem tempMenuItem;
					for (int i = 0; i < recipeNameList.size(); i++) {
						tempRecipe = selectedDish.getRecipeItem(recipeNameList.get(i));
						tempMenuItem = tempRecipe.getItem();
	
						checkboxs.add(new JCheckBox(tempMenuItem.getName()));
	
						if (checkedNames.contains(tempMenuItem.getName()))
							checkboxs.get(i).setSelected(true);
	
						int indexAdded = itemUnits.size();
						// itemUnits field
						if (tempRecipe.getAmountNeeded() != null) {
							itemUnits.add(new JTextField(tempRecipe.getAmountNeeded().toString()));
						} else {
							itemUnits.add(new JTextField("       ( NUMBER OF UNITS )"));
						}
						initItemUnits.add(itemUnits.get(indexAdded).getText());
							
						// Set ComboBox
						if (tempMenuItem.isDish()) {
							unitCombos.add(new JComboBox(Dish.getDishPossibilities(tempMenuItem)));
						} else if (tempMenuItem.getUnits() != null) {
							if (tempMenuItem.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID)
								unitCombos.add(new JComboBox(SOLID_UNITS));
							else if (tempMenuItem.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID)
								unitCombos.add(new JComboBox(LIQUID_UNITS));
							else
								unitCombos.add(new JComboBox(OTHER_UNITS));
						} else {
							unitCombos.add(new JComboBox(INGREDIENT_INCOMPLETE));
						}
	
						if (tempRecipe.getUnits() != null) {
							unitCombos.get(i).setSelectedItem(tempRecipe.getUnits());
						} else {
							unitCombos.get(i).setSelectedIndex(0);
						}
						initUnitCombos.add((String)unitCombos.get(indexAdded).getSelectedItem());
							
						// Amount needed field
						if (tempRecipe.getBatch() != null) {
							quantities.add(new JTextField(tempRecipe.getBatch().toString()));
						} else {
							quantities.add(new JTextField("        ( NUMBER OF UNITS )"));
						}
						initQuantities.add(quantities.get(indexAdded).getText());
							
						// Wastage field
						if (tempRecipe.getWaste() != null) {
							wastes.add(new JTextField(tempRecipe.getWaste().toString()));
						} else {
							wastes.add(new JTextField("      ( 0 - 99 )"));
						}
						initWastes.add(wastes.get(indexAdded).getText());
					}
				//Remove item
				} else if (keepIndex < -1) {
					int actualIndex = ((keepIndex + 2) * -1);
					
					unitCombos.remove(actualIndex);
					initUnitCombos.remove(actualIndex);
					
					RecipeItem tempRecipe = selectedDish.getRecipeItem(recipeNameList.get(actualIndex));
					MenuItem tempMenuItem = tempRecipe.getItem();
					
					// Set ComboBox
					if (tempMenuItem.isDish()) {
						unitCombos.add(actualIndex, new JComboBox(Dish.getDishPossibilities(tempMenuItem)));
					} else if (tempMenuItem.getUnits() != null) {
						if (tempMenuItem.getUnsoldType() == Dish.UNSOLD_TYPE.SOLID)
							unitCombos.add(actualIndex, new JComboBox(SOLID_UNITS));
						else if (tempMenuItem.getUnsoldType() == Dish.UNSOLD_TYPE.LIQUID)
							unitCombos.add(actualIndex, new JComboBox(LIQUID_UNITS));
						else 
							unitCombos.add(actualIndex, new JComboBox(OTHER_UNITS));
					} else {
						unitCombos.add(actualIndex, new JComboBox(INGREDIENT_INCOMPLETE));
					}

					if (tempRecipe.getUnits() != null) {
						unitCombos.get(actualIndex).setSelectedItem(tempRecipe.getUnits());
					} else {
						unitCombos.get(actualIndex).setSelectedIndex(0);
					}
					initUnitCombos.add(actualIndex, (String)unitCombos.get(actualIndex).getSelectedItem());
				}

				// Set Labels
				for (int i = 0; i < checkboxs.size(); i++) {
					String name = checkboxs.get(i).getText().toLowerCase();
					
					unitLabel.add(i, new JLabel("of USABLE " + name + " needed per"));
					
					String unitString = selectedDish.getUnits();
					if (unitString != null) {
						if (unitString == DISH_UNITS[1])
							unitString = "order(s) of " + selectedDish.getName().toLowerCase() + "  (with";
						else
							unitString = selectedDish.getUnits().toLowerCase() + " of " + selectedDish.getName().toLowerCase() + "  (with";
					} else {
						unitString = "(PICK A UNIT ABOVE) of " + selectedDish.getName().toLowerCase() + "  (with";
					}
					withLabel.add(i, new JLabel(new String(unitString)));
					
					wasteLabel.add(i, new JLabel(" %    of each ORIGINAL " + name + " wasted to make it usable for the recipe)."));
				}

				// Set layout for middle group
				ArrayList<SequentialGroup> hChecks = new ArrayList<SequentialGroup>();
				for (int i = 0; i < recipeNameList.size(); i++) {
					hChecks.add(i, layout.createSequentialGroup());

					hChecks.get(i).addComponent(itemUnits.get(i),GroupLayout.DEFAULT_SIZE, 140, 172);
					hChecks.get(i).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
					hChecks.get(i).addComponent(unitCombos.get(i), GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE, 200);
					hChecks.get(i).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
					hChecks.get(i).addComponent(unitLabel.get(i), GroupLayout.PREFERRED_SIZE, 	GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE);
					hChecks.get(i).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
					hChecks.get(i).addComponent(quantities.get(i), GroupLayout.DEFAULT_SIZE, 135, 182);
					hChecks.get(i).addPreferredGap( LayoutStyle.ComponentPlacement.RELATED);
					hChecks.get(i).addComponent(withLabel.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
					hChecks.get(i).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
					hChecks.get(i).addComponent(wastes.get(i), GroupLayout.DEFAULT_SIZE, 70, 90);
					hChecks.get(i).addComponent(wasteLabel.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
				}

				// Overall hCheck Group
				hCheck.addComponent(ingredientsLabel);
				
				for (int i = 0; i < recipeNameList.size(); i++) {
					hCheck.addComponent(checkboxs.get(i), GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
					hCheck.addGroup(hChecks.get(i));
				}

				// Vertical layout
				ArrayList<ParallelGroup> vChecks = new ArrayList<ParallelGroup>();
				for (int i = 0; i < recipeNameList.size(); i++) {
					// Vertical Side
					vChecks.add(i, layout.createParallelGroup(GroupLayout.Alignment.BASELINE));
					
					vChecks.get(i).addComponent(itemUnits.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
					vChecks.get(i).addComponent(unitCombos.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
					vChecks.get(i).addComponent(unitLabel.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
					vChecks.get(i).addComponent(quantities.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
					vChecks.get(i).addComponent(withLabel.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
					vChecks.get(i).addComponent(wastes.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
					vChecks.get(i).addComponent(wasteLabel.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
				}

				// Overall vertical group
				vCheck.addComponent(ingredientsLabel);
				vCheck.addGap(10);
				
				for (int i = 0; i < recipeNameList.size(); i++) {
					vCheck.addComponent(checkboxs.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
					vCheck.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
					vCheck.addGroup(vChecks.get(i));

					if (i != (recipeNameList.size() - 1))
						vCheck.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
				}
			} else {
				JLabel placeHolder = new JLabel("ADD AN INGREDIENT (BELOW) TO BEGIN COMPOSING THE DISH'S RECIPE");

				hCheck.addComponent(placeHolder, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
				vCheck.addComponent(placeHolder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
			}
		} else {
			JLabel blankItem = new JLabel();
			
			hCheck.addComponent(blankItem, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
			vCheck.addComponent(blankItem, GroupLayout.PREFERRED_SIZE,	GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);

		}

		// Add-Remove Button populate
		addItem.setText("Add an Item to the Recipe...");
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectedDish != null) {
					ArrayList<String> tempList = convertToSortedArrayList(itemHash.keySet());
					
					ArrayList<String> tempListIterate = (ArrayList<String>) tempList.clone();
					for (String item : tempListIterate) {
						if (recipeNameList.contains(item))
							tempList.remove(item);
					}

					// Remove Uses of Item from List
					tempList.remove(selectedDish.getName());
					removeUses(selectedDish, tempList);

					tempList.add(0, "SELECT");
					Object[] possibilities = tempList.toArray();
					String addIngredient = (String) JOptionPane.showInputDialog(updateButton, 
											"Choose an Ingredient to Add to Recipe:\n      \n"
											+ "(Note: You cannot add the item itself, items already in the recipe, or items using the selected item in their recipes.)\n",
									"Ingredient Adder",	JOptionPane.PLAIN_MESSAGE, null, possibilities, possibilities[0]);

					if (addIngredient != null && !addIngredient.equals("SELECT")) {
						
						selectedDish.addRecipeItem(itemHash.get(addIngredient), null, null, null, null);
						MainFrame.InsertIntoList(recipeNameList, addIngredient);
						
						int index = recipeNameList.indexOf(addIngredient);

						parent.updateUndoRedoStack(addIngredient + " taken off the " + selectedDish.getName() + " recipe", addIngredient + " put back on the " + selectedDish.getName() + " recipe");
						
						repaintNoUpdateFields(index);
						parent.iPricingPane.repaint(false, false, true);
						
		            	if (parent.itemUsagePane.getSelectedItem() != null)
		            		parent.itemUsagePane.repaintAll();
		            	else
		            		parent.itemUsagePane.repaint(true, false);
		            	
		            	parent.resultsPane.repaint(false, false, true);
					}
				}				
			}
		});

		removeItem.setText("Remove Checked Items from Recipe...");
		removeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectedDish != null) {
					int numCheck = checkboxs.size();
					JCheckBox tempBox;

					boolean areAnyChecked = false;
					for (int i = 0; i < numCheck; i++) {
						tempBox = checkboxs.get(i);

						if (tempBox.isSelected()) {
							areAnyChecked = true;
							break;
						}
					}

					if (areAnyChecked) {
						Object[] options = { "Yes", "No" };

							int response = JOptionPane.showOptionDialog(updateButton,
											"Are you sure that you want to delete all the checked items from the recipe?",
											"Item Remover", JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE, null,
											options, options[0]);

						if (response == JOptionPane.YES_OPTION) {
														
							for (int i = 0; i < numCheck; i++) {
								tempBox = checkboxs.get(i);

								if (tempBox.isSelected()) {
									i -= 1;
									numCheck -= 1;
									
									//Remove from Mainframe varaibles									
									selectedDish.removeRecipeItem(tempBox.getText());
									
									//Remove from panes variables
									int actualIndex = recipeNameList.indexOf(tempBox.getText());
									
									checkboxs.remove(actualIndex);
									
									itemUnits.remove(actualIndex);
									initItemUnits.remove(actualIndex);
									
									unitCombos.remove(actualIndex);
									initUnitCombos.remove(actualIndex);
									
									quantities.remove(actualIndex);
									initQuantities.remove(actualIndex);
									
									wastes.remove(actualIndex);
									initWastes.remove(actualIndex);
									
									recipeNameList.remove(actualIndex);
								}
							}
							
							checkedNames = new ArrayList<String>();
							
							parent.updateUndoRedoStack("Added ingredients back to the the " + selectedDish.getName() + " recipe", "Removed ingredients from the " + selectedDish.getName() + " recipe");
							
							repaintNoUpdateFields(-1);
							parent.iPricingPane.repaint(false, false, true);
							
			            	if (parent.itemUsagePane.getSelectedItem() != null)
			            		parent.itemUsagePane.repaintAll();
			            	else
			            		parent.itemUsagePane.repaint(true, false);
			            	
			            	parent.resultsPane.repaint(false, false, true);							
						}
					}
				}				
			}
		});

		// Set up add-remove layout
		SequentialGroup hAddRemove = layout.createSequentialGroup();

		hAddRemove.addComponent(addItem, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hAddRemove.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hAddRemove.addComponent(removeItem, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

		ParallelGroup vAddRemove = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vAddRemove.addComponent(addItem, GroupLayout.PREFERRED_SIZE, 40, 53);
		vAddRemove.addComponent(removeItem, GroupLayout.PREFERRED_SIZE, 40, 53);

		
		//Set up search part
		ParallelGroup hJScrollPaneGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		SequentialGroup hJScrollPaneGroup1 = layout.createSequentialGroup();
		
		hJScrollPaneGroup1.addComponent(searchLabel);
		hJScrollPaneGroup1.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hJScrollPaneGroup1.addComponent(entry, GroupLayout.DEFAULT_SIZE, 300, 500);
		
		hJScrollPaneGroup.addGroup(hJScrollPaneGroup1);
		hJScrollPaneGroup.addComponent(jScrollPane);
		hJScrollPaneGroup.addComponent(status);

		SequentialGroup vJScrollPaneGroup = layout.createSequentialGroup();

		ParallelGroup vJScrollPaneGroup1 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		
		vJScrollPaneGroup1.addComponent(searchLabel);
		vJScrollPaneGroup1.addComponent(entry, GroupLayout.DEFAULT_SIZE, 25, 45);
		
		vJScrollPaneGroup.addGroup(vJScrollPaneGroup1);
		vJScrollPaneGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vJScrollPaneGroup.addComponent(jScrollPane, 310, 310, Short.MAX_VALUE);
		vJScrollPaneGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vJScrollPaneGroup.addComponent(status);

		
		
		
		// Set up tab horizontal layout

		ParallelGroup hRealGroup = layout.createParallelGroup();
		hRealGroup.addComponent(selection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addGroup(hCatLine);
		hRealGroup.addGroup(hItemLine);
		hRealGroup.addGroup(hAddDish);
		hRealGroup.addComponent(itemName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addComponent(priceTitle);
		hRealGroup.addGroup(hPriceLine);
		hRealGroup.addComponent(recipeInfo);
		hRealGroup.addGroup(hBatchLine);
		hRealGroup.addGroup(hCheck);
		hRealGroup.addGroup(hAddRemove);
		hRealGroup.addComponent(updateButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addComponent(errors, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addGroup(hJScrollPaneGroup);

		SequentialGroup hFinalGroup = layout.createSequentialGroup();
		hFinalGroup.addContainerGap();
		hFinalGroup.addGroup(hRealGroup);
		hFinalGroup.addContainerGap();

		hGroup.addGroup(GroupLayout.Alignment.TRAILING, hFinalGroup);
		layout.setHorizontalGroup(hGroup);

		// Set up tab vertical layout

		SequentialGroup vFinalGroup = layout.createSequentialGroup();
		vFinalGroup.addContainerGap();
		vFinalGroup.addComponent(selection, GroupLayout.PREFERRED_SIZE,	GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addGap(15);
		vFinalGroup.addGroup(vCatLine);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		vFinalGroup.addGroup(vItemLine);
		vFinalGroup.addGap(12);
		vFinalGroup.addGroup(vAddDish);
		vFinalGroup.addGap(25);
		vFinalGroup.addComponent(itemName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addGap(15);
		vFinalGroup.addComponent(priceTitle);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addGroup(vPriceLine);
		vFinalGroup.addGap(22);
		vFinalGroup.addComponent(recipeInfo);
		vFinalGroup.addGap(15);
		vFinalGroup.addGroup(vBatchLine);
		vFinalGroup.addGap(9);
		vFinalGroup.addGroup(vCheck);
		vFinalGroup.addGap(22);
		vFinalGroup.addGroup(vAddRemove);
		vFinalGroup.addGap(12);
		vFinalGroup.addComponent(updateButton, GroupLayout.PREFERRED_SIZE, 62, 75);
		vFinalGroup.addGap(25);
		vFinalGroup.addComponent(errors, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addGap(15);
		vFinalGroup.addGroup(vJScrollPaneGroup);
		vFinalGroup.addContainerGap();

		vGroup.addGroup(vFinalGroup);
		layout.setVerticalGroup(vGroup);
		
		
    	//Search Functionality        
        hilit = new DefaultHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
        textArea.setHighlighter(hilit);
        
        entryBg = entry.getBackground();
        entry.getDocument().addDocumentListener(this);
        
        InputMap im = entry.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = entry.getActionMap();
        
        im.put(KeyStroke.getKeyStroke("ESCAPE"), CANCEL_ACTION);
        am.put(CANCEL_ACTION, new CancelAction());
        
        im.put(KeyStroke.getKeyStroke("ENTER"), ENTER_ACTION);
        am.put(ENTER_ACTION, new EnterAction());
	}
	
	public void setSelectedCat(Category temp) {
		selectedCat = temp;
	}
	
	public Category getSelectedCat() {
		return selectedCat;
	}

	public void addDish(String title, String dialogMessage, boolean isSold) {
		addCopyDish(title, dialogMessage, isSold, null);
	}
	
	public void addCopyDish(String title, String dialogMessage, boolean isSold, String oldDish) {
		SingleFieldDialog dialogAdd = new SingleFieldDialog(title, dialogMessage, parent.itemHash.keySet(), false, false);

		dialogAdd.pack();
		dialogAdd.setLocationRelativeTo(parent);
		dialogAdd.setVisible(true);

		String newIngredientName = dialogAdd.getValidatedText();
		if (newIngredientName != null && !newIngredientName.toUpperCase().equals("SELECT")) {
			
			newIngredientName = MainFrame.toFirstLettersUpper(newIngredientName);
			
			if (oldDish != null)
				itemHash.put(newIngredientName, new Dish(newIngredientName, itemHash.get(oldDish)));
			else 
				itemHash.put(newIngredientName, new Dish(newIngredientName, isSold));
			
			if (isSold)
				catHash.get(Category.ENTIRE_MENU).addDish(newIngredientName);
			else
				catHash.get(Category.UNSOLD_DISHES).addDish(newIngredientName);
			
			if (oldDish != null) {
				ArrayList<String> oldCats = itemHash.get(oldDish).getCategories();
				
				for (String cat : oldCats) {
					if (!cat.equals(Category.ENTIRE_MENU) && !cat.equals(Category.UNSOLD_DISHES))
						catHash.get(cat).addDish(newIngredientName);
				}
			}


			if (oldDish != null)
	            parent.updateUndoRedoStack("Removed the copied dish \"" + newIngredientName + "\" from the restaurant", "Added the copied dish \"" + newIngredientName + "\" back to the restaurant");
			else
				parent.updateUndoRedoStack("Removed the dish \"" + newIngredientName + "\" from the restaurant", "Added the dish \"" + newIngredientName + "\" back to the restaurant");				
						
			repaint(false, true, true);
			parent.catManagementPane.repaint(false, true, true, true, true);
			if (isSold) {
				if (oldDish != null)
					parent.itemUsagePane.repaint(true, true);
				else
					parent.itemUsagePane.repaint(true, false);
				
				if (parent.resultsPane.getSelectedCat() != null && itemHash.get(newIngredientName).getCategories().contains(parent.resultsPane.getSelectedCat().getName()))
					parent.resultsPane.repaint(false, true, false);
			} else {
				if (oldDish != null)
					parent.itemUsagePane.repaint(true, true);
				else
					parent.itemUsagePane.repaint(true, false);
			}
			
			if (oldDish != null) {
				String finalMessage = "You have successfully copied the dish \"" + oldDish + 
										"\" into a new dish with its name being \"" + newIngredientName +"\"!         ";
				JOptionPane.showMessageDialog(parent, finalMessage);
			} else {
				JOptionPane.showMessageDialog(parent, "You have successfully added the \"" + newIngredientName + "\" dish to the restaurant!         ");
			}
		}		
	}

	public void removeDish() {
		ArrayList<String> tempList = (ArrayList<String>) catHash.get(Category.ENTIRE_MENU).getDishes().clone();
		ArrayList<String> unsoldList = (ArrayList<String>) catHash.get(Category.UNSOLD_DISHES).getDishes().clone();

		tempList.addAll(unsoldList);
		
		ArrayList<String> tempListIterate = (ArrayList<String>) tempList.clone();
		for (String item : tempListIterate) {
			MenuItem tempItem = itemHash.get(item);

			if (!tempItem.getUses().isEmpty())
				tempList.remove(item);
		}
		
		tempList.add(0, "SELECT");

		Object[] possibilities = tempList.toArray();
		String dishName = (String) JOptionPane.showInputDialog(parent,
				"Choose a Dish to Remove:\n    \n", "Dish Remover",
				JOptionPane.PLAIN_MESSAGE, null, possibilities,
				possibilities[0]);

		if (dishName != null && !dishName.equals("SELECT")) {
			MenuItem pickedDish = itemHash.get(dishName);

			boolean redrawInfoPane = false;
			if (parent.currentOrder.keySet().contains(dishName)) {
				parent.currentOrder.remove(dishName);
				redrawInfoPane = true;
			}

			MenuItem tempItem = null;
			for (String recipeItemName : pickedDish.getRecipeItemList().keySet()) {
				tempItem = itemHash.get(recipeItemName);

				tempItem.removeUse(dishName);
			}

			Category tempCategory = null;
			for (String catName : pickedDish.getCategories()) {
				tempCategory = catHash.get(catName);

				tempCategory.removeDish(dishName);
			}

			// Remove from overall list
			itemHash.remove(dishName);
			
			if (selectedDish != null && dishName.equals(selectedDish.getName())) {
				selectedDish = null;
				recipeNameList = null;
				checkedNames = new ArrayList<String>();

				repaintPrice = true;
				repaintUnits = true;
				repaintBatch = true;
				repaintAllNoCheckItems(); //repaint ingredients error message pane too in case of warning 
			} else {
				repaint(false, true, true); //repaint ingredients error message pane too in case of warning
			}
						
			//Redraw other panes
			parent.iPricingPane.repaint(false, false, true);

			if (parent.catManagementPane.getSelectedDish() != null 
					&& dishName.equals(parent.catManagementPane.getSelectedDish().getName())) {
				
				parent.catManagementPane.setSelectedDish(null);
				parent.catManagementPane.repaintAll();
			} else {
				parent.catManagementPane.repaint(false, true, true, true, true);
			}
			
			boolean infoPart = false;
			if (parent.itemUsagePane.getSelectedItem() != null && parent.itemUsagePane.getSelectedItem().getName().equals(dishName)) {
				parent.itemUsagePane.setSelectedItem(null);
				infoPart = true;
			}
			
			if (infoPart)
				parent.itemUsagePane.repaintAll();
			else
				parent.itemUsagePane.repaint(true, true);
			
			
			if (parent.resultsPane.getSelectedDish() != null && parent.resultsPane.getSelectedDish().getName().equals(dishName))
				parent.resultsPane.setSelectedDish(null);
			
			if (redrawInfoPane)	parent.resultsPane.isListSaved = false;
			parent.resultsPane.repaint(false, true, redrawInfoPane);
			
			
            parent.updateUndoRedoStack("Added the dish \"" + dishName + "\" back to the restaurant", "Removed the dish \"" + dishName + "\" from the restaurant");
			JOptionPane.showMessageDialog(parent, "You have successfully removed the \"" + dishName + "\" dish from the restaurant!         ");
		}	
	}
	
	public boolean changeItemName(String oldItemName, String newItemName) {
		boolean found = false;
		
		if (selectedDish != null) {
			if (selectedDish.getName().equals(newItemName))
				found = true;
			
			if (recipeNameList.contains(oldItemName)) {
				int oldIndex = recipeNameList.indexOf(oldItemName);
				recipeNameList.remove(oldIndex);
				
				MainFrame.InsertIntoList(recipeNameList, newItemName);
				int newIndex = recipeNameList.indexOf(newItemName);
				
				checkboxs.get(oldIndex).setText(newItemName);
				checkboxs.add(newIndex + 1, checkboxs.get(oldIndex));
				checkboxs.remove(oldIndex);
				
				initItemUnits.add(newIndex + 1, itemUnits.get(oldIndex).getText());
				itemUnits.add(newIndex + 1, itemUnits.get(oldIndex));
				initItemUnits.remove(oldIndex);
				itemUnits.remove(oldIndex);
				
				initUnitCombos.add(newIndex + 1, (String)unitCombos.get(oldIndex).getSelectedItem());
				unitCombos.add(newIndex + 1, unitCombos.get(oldIndex));
				initUnitCombos.remove(oldIndex);
				unitCombos.remove(oldIndex);
				
				unitLabel.add(newIndex + 1, unitLabel.get(oldIndex));
				unitLabel.remove(oldIndex);
				
				initQuantities.add(newIndex + 1, quantities.get(oldIndex).getText());
				quantities.add(newIndex + 1, quantities.get(oldIndex));
				initQuantities.remove(oldIndex);
				quantities.remove(oldIndex);
				
				withLabel.add(newIndex + 1, withLabel.get(oldIndex));
				withLabel.remove(oldIndex);
				
				initWastes.add(newIndex + 1, wastes.get(oldIndex).getText());
				wastes.add(newIndex + 1, wastes.get(oldIndex));
				initWastes.remove(oldIndex);
				wastes.remove(oldIndex);
				
				
				if (checkedNames.contains(oldItemName)) {
					checkedNames.remove(oldItemName);
					MainFrame.InsertIntoList(checkedNames, newItemName);
				}
					
				found = true;
			}
		}
		
		return found;
	}
	
	public boolean updateAction() {
		boolean success = false;
		
		if (selectedDish != null) {

			Double prevDoubleValue = null;
			String prevStringValue = null;

			ArrayList<String> tempInput = new ArrayList<String>();

			ArrayList<Double> tempAmountNeeded = new ArrayList<Double>();
			ArrayList<Double> tempBatchSize = new ArrayList<Double>();
			ArrayList<Double> tempWaste = new ArrayList<Double>();

			int recipeSize = checkboxs.size();
			boolean setError = false;
			for (int i = 0; i < recipeSize; i++) {
				RecipeItem tempRecipe = selectedDish.getRecipeItem(checkboxs.get(i).getText());

				// Amount needed part
				tempInput.add(i, itemUnits.get(i).getText());
                
                if (tempInput.get(i) == null)
                    tempInput.set(i, "");
                
				// Trim text
				tempInput.set(i, tempInput.get(i).replaceAll("^\\s+", ""));
				tempInput.set(i, tempInput.get(i).replaceAll("\\s+$", ""));

				prevDoubleValue = tempRecipe.getAmountNeeded();
				tempAmountNeeded.add(i, null);
				try {
					tempAmountNeeded.set(i, Double.valueOf(tempInput.get(i)));

			  		if (prevDoubleValue == null && tempAmountNeeded.get(i) > 0) {
						tempRecipe.setAmountNeeded(tempAmountNeeded.get(i));
						setError = true;
            		} else if (prevDoubleValue.compareTo(tempAmountNeeded.get(i)) != 0 && tempAmountNeeded.get(i) > 0) {
						tempRecipe.setAmountNeeded(tempAmountNeeded.get(i));
						setError = true;
					}

				} catch (Exception except) {
					if (prevDoubleValue != null)
						if (tempInput.get(i).isEmpty()) {
							tempRecipe.setAmountNeeded(null);
							setError = true;
						}
				}

				// Batch part
				tempInput.set(i, quantities.get(i).getText());

                if (tempInput.get(i) == null)
                    tempInput.set(i, "");
                
				// Trim text
				tempInput.set(i, tempInput.get(i).replaceAll("^\\s+", ""));
				tempInput.set(i, tempInput.get(i).replaceAll("\\s+$", ""));

				prevDoubleValue = tempRecipe.getBatch();
				tempBatchSize.add(i, null);
				try {
					tempBatchSize.set(i, Double.valueOf(tempInput.get(i)));

			  		if (prevDoubleValue == null && tempBatchSize.get(i) > 0) {
						tempRecipe.setBatch(tempBatchSize.get(i));
						setError = true;
					} else if (prevDoubleValue.compareTo(tempBatchSize.get(i)) != 0 && tempBatchSize.get(i) > 0) {
						tempRecipe.setBatch(tempBatchSize.get(i));
						setError = true;
					}

				} catch (Exception except) {
					if (prevDoubleValue != null)
						if (tempInput.get(i).isEmpty()) {
							tempRecipe.setBatch(null);
							setError = true;
						}
				}

				// Wastage part
				tempInput.set(i, wastes.get(i).getText());

                if (tempInput.get(i) == null)
                    tempInput.set(i, "");
                
				// Trim text
				tempInput.set(i, tempInput.get(i).replaceAll("^\\s+", ""));
				tempInput.set(i, tempInput.get(i).replaceAll("\\s+$", ""));

				prevDoubleValue = tempRecipe.getWaste();
				tempWaste.add(i, null);
				try {
					tempWaste.set(i, Double.valueOf(tempInput.get(i)));

					if (prevDoubleValue == null && tempWaste.get(i) >= 0 && tempWaste.get(i) < 100) {
						tempRecipe.setWaste(tempWaste.get(i));
						setError = true;
					} else if (prevDoubleValue.compareTo(tempWaste.get(i)) != 0 && tempWaste.get(i) >= 0 && tempWaste.get(i) < 100) {
						tempRecipe.setWaste(tempWaste.get(i));
						setError = true;
					}

				} catch (Exception except) {
					if (prevDoubleValue != null)
						if (tempInput.get(i).isEmpty()) {
							tempRecipe.setWaste(null);
							setError = true;
						}
				}

				// Unit part
				tempInput.set(i, (String) unitCombos.get(i).getSelectedItem());
                
                if (tempInput.get(i) == null)
                    tempInput.set(i, "");
                
				prevStringValue = tempRecipe.getUnits();

				if (prevStringValue != null) {
					if (MenuCompositionPane.INGREDIENT_INCOMPLETE[0].equals(tempInput.get(i))) {
						tempRecipe.setUnits(null);
						setError = true;
					} else if (!tempInput.get(i).equals(prevStringValue)) {
						tempRecipe.setUnits(tempInput.get(i));
						setError = true;
					}
				} else {
					if (!MenuCompositionPane.INGREDIENT_INCOMPLETE[0].equals(tempInput.get(i))) {
						tempRecipe.setUnits(tempInput.get(i));
						setError = true;
					}
				}
			}

			if (setError) {
				selectedDish.setErrorFlag();
								
				parent.updateUndoRedoStack("UNDID the changes using the \"Update Button\" to the " + selectedDish.getName() + " recipe", "REDID the changes using the \"Update Button\" to the " + selectedDish.getName() + " recipe");
				repaintAllWithCheckItems();
				
	    		parent.itemUsagePane.repaint(true, true);
	        	parent.resultsPane.repaint(false, false, true);
	        	
	        	success = true;
			}
		}
		
		return success;
	}
	
	public boolean confirmAction() {
		boolean success = false;
		if (selectedDish != null) {
			
			boolean isPrice = selectedDish.IsSoldDish();
			if (isPrice) {
				String tempString = priceField.getText();
				boolean setError = false;
				
				if (tempString == null)
					tempString = "";
				
				// Trim text
				tempString = tempString.replaceAll("^\\s+", "");
				tempString = tempString.replaceAll("\\s+$", "");
	
				Double prevValue = selectedDish.getPrice();
				Double tempNum = null;
	
				try {
					tempNum = Double.valueOf(tempString);
	
					if (prevValue != tempNum && tempNum > 0) {
						selectedDish.setPrice(tempNum);
						setError = true;
					}
				} catch (Exception except) {
					if (prevValue != null && tempString.isEmpty()) {
							selectedDish.setPrice(null);						
							setError = true;
					}
				}
				
				if (setError == true) {
					if (tempString.isEmpty()) {
						parent.updateUndoRedoStack("Menu price of " + selectedDish.getName() + " set back to LL " + MainFrame.toStringWithXDemical(prevValue, 0),  
													"Menu price of " + selectedDish.getName() + " is again not yet set");
					} else if (prevValue == null) {
						parent.updateUndoRedoStack("Menu price of " + selectedDish.getName() + " is now not set", 
											"Menu price of " + selectedDish.getName() + " set again to LL " + MainFrame.toStringWithXDemical(tempNum, 0));
					} else {
						parent.updateUndoRedoStack("Menu price of " + selectedDish.getName() + " set back to LL " + MainFrame.toStringWithXDemical(prevValue, 0),  
								"Menu price of " + selectedDish.getName() + " set again to LL " + MainFrame.toStringWithXDemical(tempNum, 0));
					}
					
	        		repaintPrice = true;
	        		repaintNoUpdateFields(-1);
	        		
	        		parent.itemUsagePane.repaint(true, true);
	            	parent.resultsPane.repaint(false, false, true);	        
	            	
	            	success = true;
				}
			} else {
	        	// Unit part
	        	String tempInput = (String) unsoldCombo.getSelectedItem();
	        	String prevStringValue = selectedDish.getUnits();
	        	
	        	String undoMsg = null;
	        	String redoMsg = null;
	        	
	        	boolean setError = false;
	        	if (prevStringValue != null) {
	        		if (Arrays.asList(INGREDIENT_INVALID_ITEMS).contains(tempInput)){
	        			selectedDish.setUnits(null);
	
	        			undoMsg = selectedDish.getName() + " units set back to " + prevStringValue;
	        			redoMsg = selectedDish.getName() + " units again not set";
	        			
	        			setError = true;
	        		} else if (!tempInput.equals(prevStringValue) 
	        					&& !Arrays.asList(MenuCompositionPane.INGREDIENT_INVALID_ITEMS).contains(tempInput)) {
	        			selectedDish.setUnits(tempInput);
	        			
	        			undoMsg = selectedDish.getName() + " units set back to " + prevStringValue;
	        			redoMsg = selectedDish.getName() + " units set to " + tempInput;
	        			
	    				setError = true;
	        		}
	        	} else {
	        		if (!Arrays.asList(INGREDIENT_INVALID_ITEMS).contains(tempInput)) {
	        			selectedDish.setUnits(tempInput);        			
	        			
	        			undoMsg = selectedDish.getName() + " units are again not yet set";
	        			redoMsg = selectedDish.getName() + " units set to " + tempInput;
	        			
	        			setError = true;
	        		}
	        	}
	    	
	        	if (setError) {		            		
	        		selectedDish.setErrorFlag();
	        		
	        		parent.updateUndoRedoStack(undoMsg, redoMsg);
	
	        		repaintUnits = true;
	        		repaintNoUpdateFields(-1);
	        		
	        		parent.itemUsagePane.repaint(true, true);
	            	parent.resultsPane.repaint(false, false, true);
	            	
	            	success = true;
	        	} else {
	        		repaintUnits = true;
	        		repaintNoUpdateFields(-1);
	        	}
			}
		}
		
		return success;
	}

	public String getErrorMessage() {
		StringBuilder errors = new StringBuilder("\n=== START ===\n\n\n");

		if (selectedDish != null) {
			errors.append(("SELECTED ITEM:    " + selectedDish.getName().toUpperCase()) + "\n\n");

			if (selectedDish.IsSoldDish()) {
				if (selectedDish.getPrice() != null)
					errors.append("The item's price on the menu is:   LL "
							+ MainFrame.toStringWithXDemical(selectedDish.getPrice(), 0) + " / Order\n");
				else
					errors.append("The item's price on the menu is:   N/A\n");
			}

			if (!selectedDish.getIncompleteError()) {
				if (selectedDish.IsSoldDish()) {
					errors.append("The overall cost is:   LL "
						+ MainFrame.toStringWithXDemical(selectedDish.getCost(), 0) + " / Order" + "\n");
				} else { 
					errors.append("The overall cost is:   LL "
							+ MainFrame.toStringWithXDemical(selectedDish.getCost(), 0) + " / " + 
								(selectedDish.getUnits().replaceAll("s", "")).replaceAll("olid","solid") + "\n");
				}
				
				if (selectedDish.IsSoldDish() && selectedDish.getPrice() != null) {
						Double percentage = new Double(selectedDish.getCost() * 100 / selectedDish.getPrice());
						
						errors.append("\n");
						if (percentage > 75)
							errors.append("WATCH OUT  --  ");
						
						errors.append("The percentage of the cost relative to the price is:   "
									+ MainFrame.toStringWithXDemical(percentage, 2) + " %\n\n");
				} else if (selectedDish.IsSoldDish()) {
					
					errors.append("\n");
				}
				
			} else {
				errors.append("The overall cost is:   N/A\n\n");
			}

			boolean anyIssue = false;
			if (selectedDish.getIncompleteError() || (selectedDish.IsSoldDish() && selectedDish.getPrice() == null)) {
				if (selectedDish.IsSoldDish())
					errors.append("ERROR:  The dish either has no price, no items in its recipe, or one of its ingredients is missing a field!\n");
				else
					errors.append("ERROR:  The unsold dish either has no units, no items in its recipe, or one of its ingredients is missing a field!\n");

				anyIssue = true;
			}

			if (!selectedDish.IsSoldDish() && selectedDish.getUnusedWarning()) {
				errors.append("WARNING:  This unsold dish is not used in any recipes!\n");
				anyIssue = true;
			}

			if (!anyIssue)
				errors.append("SUCCESS:  This dish has nothing wrong with it!\n");
			
			errors.append("\n");
			if (!recipeNameList.isEmpty())
				errors.append("\n-- Ingredients --\n\n\n");
			
			RecipeItem item = null;
			int counter = 1;
			for (String itemName : recipeNameList) {
				item = selectedDish.getRecipeItem(itemName);

				errors.append("INGREDIENT " + counter + ":    " + itemName + "\n\n");

				errors.append("Amount Needed:   ");
				if (item.getAmountNeeded() != null)
					errors.append(MainFrame.toStringWithXDemical(item.getAmountNeeded(), 2) + "\n");
				else
					errors.append("N/A\n");

				errors.append("Units (of Amount Needed):   ");
				if (item.getUnits() != null)
					errors.append(item.getUnits() + "\n");
				else
					errors.append("N/A\n");

				String unitType = selectedDish.getUnits();
				if (unitType == null) 
					unitType = "(PICK A UNIT ABOVE)";
				errors.append( unitType + " Created:   ");				
				if (item.getBatch() != null)
					errors.append(MainFrame.toStringWithXDemical(item.getBatch(), 2) + "\n");
				else
					errors.append("N/A\n");

				errors.append("Percentage of Original Ingredient Wasted:   ");
				if (item.getWaste() != null)
					errors.append(MainFrame.toStringWithXDemical(item.getWaste(), 2) + " %\n");
				else
					errors.append("N/A\n");
				
				errors.append("\nSummary:  ");
				if (item.isComplete()) {
					 errors.append("This recipe needs " + MainFrame.toStringWithXDemical(item.getAmountNeeded()/item.getBatch(), 2) +
								" " + item.getUnits().toLowerCase() + " of " + itemName + " - costing LL " 
								+ MainFrame.toStringWithXDemical(item.getCost(), 0) +	" - per ");
					if (selectedDish.IsSoldDish()) {
						errors.append("order");
					} else if (selectedDish.getUnits() != null) {
						if (!selectedDish.getUnits().equals("mL"))
							errors.append(((selectedDish.getUnits().replaceAll("s", "")).replaceAll("olid","solid")).toLowerCase());
						else
							errors.append("mL");
					} else {
						errors.append("(SET UNITS)");
					}
					
					errors.append(" of " + selectedDish.getName() + ".\n");
								
				} else {
					errors.append("There is an error  -  " + itemName + " has either a missing field in the recipe AND/OR " 
								+ itemName.toLowerCase() + " has an error itself\n");
				}
					

				errors.append("\n\n");
				counter++;
			}

			errors.append("\n");
		}

		// Unsold items
		errors.append("=== SOLD DISHES ===\n\n\n");

		boolean anyIssues = false;
		MenuItem tempItem = null;

		ArrayList<String> dishList = (ArrayList<String>) catHash.get(Category.ENTIRE_MENU).getDishes().clone();
		if (selectedDish != null && dishList.contains(selectedDish.getName()))
			dishList.remove(selectedDish.getName());
		
		int counter = 1;
		for (String menuItem : dishList) {
			tempItem = itemHash.get(menuItem);
			anyIssues = false;

			errors.append((tempItem.getName().toUpperCase() + ":\n\n"));

			if (tempItem.getPrice() != null) {
				errors.append("The item's price on the menu is:   LL "
						+ MainFrame.toStringWithXDemical(tempItem.getPrice(), 0)
						+ " / Order" + "\n");
			} else {
				errors.append("The item's price on the menu is:   N/A\n");
				anyIssues = true;
			}

			if (!tempItem.getIncompleteError()) {
				errors.append("The overall cost is:   LL "
						+ MainFrame.toStringWithXDemical(tempItem.getCost(), 2)
						+ " / Order" + "\n\n");
			} else {
				errors.append("The overall cost is:   N/A\n\n");
				anyIssues = true;
			}

			if (!anyIssues) {
				if (tempItem.getPrice() != 0)
					errors.append("The percentage of the cost relative to the price is:   "
							+ MainFrame.toStringWithXDemical(
									new Double(tempItem.getCost() * 100 / tempItem.getPrice()), 2) + "%\n\n");
				else
					errors.append("You are giving the dish away for FREE!\n\n");
			}

			if (!anyIssues)
				errors.append("SUCCESS:  This dish has nothing wrong with it!\n");
			else
				errors.append("ERROR:  The dish either has no price, no items in its recipe, or one of its ingredients is missing a field!\n");

			
			if (counter != dishList.size())
				errors.append("\n---\n\n");
			else
				errors.append("\n\n");
			
			counter++;
		}

		// Unsold items
		errors.append("\n=== UNSOLD DISHES ===\n\n\n");

		dishList = (ArrayList<String>) catHash.get(Category.UNSOLD_DISHES).getDishes().clone();
		if (selectedDish != null && dishList.contains(selectedDish.getName()))
			dishList.remove(selectedDish.getName());
		
		counter = 1;
		for (String menuItem : dishList) {
			tempItem = itemHash.get(menuItem);
			anyIssues = false;

			errors.append((tempItem.getName().toUpperCase() + ":\n\n"));

			if (!tempItem.getIncompleteError()) {
				errors.append("The overall cost is:   LL "
						+ MainFrame.toStringWithXDemical(tempItem.getCost(), 0) + " / " + 
						(tempItem.getUnits().replaceAll("s", "")).replaceAll("olid","solid") + "\n\n");
			} else {
				errors.append("The overall cost is:   N/A\n\n");
				anyIssues = true;
			}

			if (anyIssues)
				errors.append("ERROR:  The unsold dish either has no units, no items in its recipe, or one of its ingredients is missing a field!\n");

			if (tempItem.getUnusedWarning()) {
				errors.append("WARNING:  This unsold dish is not used in any recipes!\n");
				anyIssues = true;
			}

			if (!anyIssues)
				errors.append("SUCCESS:  This dish has nothing wrong with it!\n");

			if (counter != dishList.size())
				errors.append("\n---\n\n");
			else
				errors.append("\n\n");
			
			counter++;
		}

		errors.append("\n=== END ===");

		return errors.toString();
	}

	public void repaint(boolean updateCatCombo, boolean updateDishCombo,
			boolean updateRecipe, boolean withCheckedItems, int indexValue, boolean updateErrors) {

		if (updateCatCombo) {
			ArrayList<String> tempCats = (ArrayList<String>) categoriesList.clone();
			
			tempCats.add(0, "SELECT");
			tempCats.remove(Category.ENTIRE_MENU);
			tempCats.add(1, Category.ENTIRE_MENU);
			tempCats.remove(Category.UNSOLD_DISHES);
			tempCats.add(2, Category.UNSOLD_DISHES);
			
			JComboBox tempReplaceCats = new JComboBox(tempCats.toArray());

            String selectedCatName = (String) refreshCombo.getSelectedItem();
            if (selectedCat != null && tempCats.contains(selectedCatName))
            	tempReplaceCats.setSelectedItem(selectedCatName);
			
			layout.replace(refreshCombo, tempReplaceCats);
			refreshCombo = tempReplaceCats;
		}

		if (updateDishCombo) {
			ArrayList<String> tempDishes = new ArrayList<String>();

			if (selectedCat == null) {
				tempDishes.add("SELECT");
			} else {
				tempDishes.add("SELECT  (from " + selectedCat.getName() + ")"); 
				tempDishes.addAll((ArrayList<String>) selectedCat.getDishes().clone());
			}

			JComboBox tempReplaceDish = new JComboBox(tempDishes.toArray());

            String selectedDishName = (String) replaceCombo.getSelectedItem();
            if (selectedCat != null && tempDishes.contains(selectedDishName))
            	tempReplaceDish.setSelectedItem(selectedDishName);
            
			layout.replace(replaceCombo, tempReplaceDish);
			replaceCombo = tempReplaceDish;
		}

		if (updateRecipe) {
			if (withCheckedItems)
				setCheckBoxes();
			else
				checkedNames = new ArrayList<String>();

			// Call initialize
			this.removeAll();

			this.initComponents(indexValue);

			this.validate();
			this.repaint();
		}

		if (updateErrors) {
			JTextArea tempArea = new JTextArea();
			tempArea.setColumns(15);
			tempArea.setLineWrap(true);
			tempArea.setRows(5);
			tempArea.setWrapStyleWord(true);
			tempArea.setEditable(false);
			tempArea.setText(this.getErrorMessage());
			
    		tempArea.setCaretPosition(0);
    		tempArea.setHighlighter(hilit);
            
            JScrollPane tempPane = new JScrollPane(tempArea);
    		
            layout.replace(jScrollPane, tempPane);
            textArea = tempArea;
            jScrollPane = tempPane;		
        }
	}

	public void repaint(boolean updateCatCombo, boolean updateDishCombo, boolean updateErrors) {

		repaint(updateCatCombo, updateDishCombo, false, false, 0, updateErrors);
	}

	public void repaintAllNoCheckItems() {

		repaint(false, false, true, false, 0, false);
	}

	public void repaintAllWithCheckItems() {

		repaint(false, false, true, true, 0, false);
	}

	// Index -1 means items have been removed and do NOTHING
	// 0 and above are index in RecipeItemList to be added
	public void repaintNoUpdateFields(int index) {
		if (index > -1)
			index = index + 1;
		
		repaint(false, false, true, true, index, false);
	}
	
	public void repaintNoUpdateFields(String ingredientName) {
		if (selectedDish != null && !recipeNameList.isEmpty()) {
			
			int index = recipeNameList.indexOf(ingredientName);
			if (index != -1)
				index = ((index + 2) * -1);

			repaint(false, false, true, true, index, false);
				
		} else {
			repaint(false, false, true, true,  -1, false);
		}
	}
	
	public static ArrayList<String> convertToSortedArrayList(Set<String> set) {

		ArrayList<String> newList = new ArrayList<String>();
		for (String itemName : set)
			MainFrame.InsertIntoList(newList, itemName);

		return newList;
	}

	public void setCheckBoxes() {
		checkedNames = new ArrayList<String>();

		for (JCheckBox tempItem : checkboxs) {
			if (tempItem.isSelected())
				checkedNames.add(tempItem.getText());
		}
	}

	public void removeUses(MenuItem item, ArrayList<String> tempList) {
		HashMap<String, MenuItem> tempUses = item.getUses();

		if (!tempUses.isEmpty()) {
			Set<String> usesSet =  tempUses.keySet();
			for (String use : usesSet) {
				if (tempList.contains(use))
					tempList.remove(use);

				MenuItem tempItem = tempUses.get(use);
				removeUses(tempItem, tempList);
			}
		}
	}
	
	//Returns 0 if no changes, 1 if changes to both price/units and recipe, 2 if price/unit only and 3 if recipe recipe only
	public int areAnyChanges() {
		int returnVal = 0;
		
		if (selectedDish != null) {
			
			Double initDouble = null;
			Double curDouble = null;
			int arraySize = initUnitCombos.size();
			
			String tempComboString = null;
			for (int i = 0; i < arraySize && returnVal == 0; i++) {
				
				tempComboString = (String) unitCombos.get(i).getSelectedItem();
				if (!initUnitCombos.get(i).equals(tempComboString)) {
					returnVal = 3;
				}
			}
	
			for (int i = 0; i < arraySize && returnVal == 0; i++) {			
				try {
					initDouble = Double.parseDouble(initItemUnits.get(i));
					curDouble = Double.parseDouble(itemUnits.get(i).getText());
					
					if (!initDouble.equals(curDouble)) {
						returnVal = 3;
					}
				} catch (Exception e) {
					if (!initItemUnits.get(i).equals(itemUnits.get(i).getText())) {
						returnVal = 3;
					}
				}	
			}
	
			for (int i = 0; i < arraySize && returnVal == 0; i++) {			
				try {
					initDouble = Double.parseDouble(initQuantities.get(i));
					curDouble = Double.parseDouble(quantities.get(i).getText());
					
					if (!initDouble.equals(curDouble)) {
						returnVal = 3;
					}
				} catch (Exception e) {
					if (!initQuantities.get(i).equals(quantities.get(i).getText())) {
						returnVal = 3;
					}
				}
			}
			
			for (int i = 0; i < arraySize && returnVal == 0; i++) {			
				try {
					initDouble = Double.parseDouble(initWastes.get(i));
					curDouble = Double.parseDouble(wastes.get(i).getText());
					
					if (!initDouble.equals(curDouble)) {
						returnVal = 3;
					}
				} catch (Exception e) {
					if (!initWastes.get(i).equals(wastes.get(i).getText())) {
						returnVal = 3;
					}
				}
			}
			
			if (selectedDish.IsSoldDish()) {
				try {
					initDouble = Double.parseDouble(initPrice);
					curDouble = Double.parseDouble(priceField.getText());
					
					if (!initDouble.equals(curDouble)) {
						if (returnVal == 3) {
							returnVal = 1;
						} else {
							returnVal = 2;
						}
					}
				} catch (Exception e) {
					if (!initPrice.equals(priceField.getText())) {
						if (returnVal == 3) {
							returnVal = 1;
						} else {
							returnVal = 2;
						}
					}
				}
			} else {				
				tempComboString = (String) unsoldCombo.getSelectedItem();
				if (!initUnsold.equals(tempComboString)) {
					if (returnVal == 3) {
						returnVal = 1;
					} else {
						returnVal = 2;
					}
				}
				
			}
		}
		
		return returnVal;
	}
	

    //Search functions
    public void search(boolean restart) {
        hilit.removeAllHighlights();
        
        String s = entry.getText().toUpperCase();
        if (s.length() <= 0) {
            hilit.removeAllHighlights();
            entry.setText("");
            entry.setBackground(entryBg);
        	
            message("Nothing to search");
            return;
        }
        
        String content = textArea.getText().toUpperCase();
        
        int startPoint;
        if (restart)
        	startPoint = 0;
        else 
        	startPoint = textArea.getCaretPosition();

        int index = content.indexOf(s, startPoint);
        
        if (index >= 0) {   // match found
            try {
                int end = index + s.length();
                
                hilit.removeAllHighlights();
                hilit.addHighlight(index, end, painter);
                
                textArea.setCaretPosition(end);
                entry.setBackground(entryBg);
                
                message("'" + s + "' found.    Press ENTER to find next occurance.    Press ESC to end search.");
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        } else {
            entry.setBackground(ERROR_COLOR);
            
            textArea.setCaretPosition(0);
            
            message("No more occurances of '" + s + "' found.  Press ESC to start a new search.");
        }
    }

    void message(String msg) {
        status.setText(msg);
    }

    // DocumentListener methods
    
    public void insertUpdate(DocumentEvent ev) {
        search(true);
    }
    
    public void removeUpdate(DocumentEvent ev) {
        search(true);
    }
    
    public void changedUpdate(DocumentEvent ev) {
    }
    
    class CancelAction extends AbstractAction {
        public void actionPerformed(ActionEvent ev) {
            hilit.removeAllHighlights();
            entry.setText("");
            entry.setBackground(entryBg);
        }
    }
    
    class EnterAction extends AbstractAction {
        public void actionPerformed(ActionEvent ev) {
        	search(false);
        }
    }
}
