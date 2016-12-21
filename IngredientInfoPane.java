import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.GroupLayout.*;


public class IngredientInfoPane extends JPanel implements DocumentListener {
    
	public final static String[] itemTypes = {"SELECT", "Ingredients", "Sold Dishes", "Unsold Dishes"}; 
	public final static String[] suffices = {"   (USED)", "   (NOT USED)"};
	
	public static enum ErrorType {NO_ERRORS, ERROR_WITH_ITEM, ZERO_OVERALL_USES, ZERO_SOLD_DISHES}
	
	
	MainFrame parent;
	
	JFileChooser fcSaveInfo = new JFileChooser();
	
    HashMap<String, MenuItem> itemHash;
    
    String selectedType;
    
	ArrayList<String> curIngredients;
	MenuItem selectedItem;
	ErrorType areErrors;
	
	boolean displayInventory;

	
	GroupLayout layout;
	
    private JLabel selection, itemName, usageInfo;
	
	private JLabel refreshItem; private JComboBox refreshCombo; private JButton refreshButton;
    private JLabel replaceItem; private JComboBox replaceCombo; private JButton replaceButton;
    
    private JTextArea textArea; private JScrollPane jScrollPane;
    private JButton reportButton;
    
    
    // Search part
    private JTextField entry;
    private JLabel searchLabel;
    private JButton changeUnits;
    private JLabel status;
    
    final static Color  HILIT_COLOR = Color.LIGHT_GRAY;
    final static Color  ERROR_COLOR = Color.PINK;
    final static String CANCEL_ACTION = "cancel-search";
    final static String ENTER_ACTION = "enter-search";
    
    private Color entryBg;
    private Highlighter hilit;
    private Highlighter.HighlightPainter painter;
    
    
    public IngredientInfoPane(MainFrame parent, String selectedType, String selectedItem, boolean displayInventory) {

    	setUp(parent, selectedType, selectedItem, displayInventory);
    	initComponents();
    }
    
    
    public void setUp(MainFrame parent, String selectedType, String selectedItem, boolean displayInventory) {
    	
    	this.parent = parent;
    	
    	this.itemHash = parent.itemHash;
    	this.selectedType = selectedType;
    	
    	if (selectedType != null)
    		this.selectedType = selectedType;
    	else
    		this.selectedType = null;
    		
    	if (selectedItem != null)
    		this.selectedItem = itemHash.get(selectedItem);
    	else
    		this.selectedItem = null;
    	
    	this.displayInventory = displayInventory;
    }
    
    private void initComponents() {
    	
    	//Initialize JObjects
    	selection = new JLabel("Choose an Item"); 
    	selection.setFont(new Font("Dialog", Font.BOLD, 27));
    	
    	if (selectedItem != null)
    		if (selectedItem.isDish())
    			itemName = new JLabel("Selected Dish:  " + selectedItem.getName().toUpperCase()); 
    		else
        		itemName = new JLabel("Selected Ingredient:  " + selectedItem.getName().toUpperCase()); 
    	else
    		itemName = new JLabel("Selected Item:  N/A          ~~~~~~~~~>         (Pick an Item Above to Get its Usage Information)"); 
    	itemName.setFont(new Font("Dialog", Font.BOLD, 27));
    	
    	usageInfo = new JLabel("Usage Information"); 
    	usageInfo.setFont(new Font("Dialog", Font.BOLD, 19));
    	
    	refreshItem = new JLabel(); refreshButton = new JButton();
    	replaceItem = new JLabel(); replaceButton = new JButton();
    	
        searchLabel = new JLabel("Enter Text to Search:");
        entry = new JTextField();
        if (displayInventory)
        	changeUnits = new JButton("Display in Recipes' Units");
        else
        	changeUnits = new JButton("Display in Inventory's Units");
        
        
        textArea = new JTextArea();
        textArea.setColumns(15);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setText(this.getInfoMessage());        
        textArea.setCaretPosition(0);

        status = new JLabel();
        
        jScrollPane = new JScrollPane(textArea);
        
        reportButton = new JButton("Save Information Above as a Document...");
        
        // Create refresh main groups
        layout = new GroupLayout(this);
        this.setLayout(layout);
        
		ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
		
        // Populate top group with appropriate data
		refreshItem.setText("Select Type of Item:                                 ");
        refreshCombo = new JComboBox(itemTypes);
        if (selectedType != null)
        	refreshCombo.setSelectedItem(selectedType);
        
        refreshButton.setText("Refresh  (List of Items Below)");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
				String comboString = (String) refreshCombo.getSelectedItem();

				if (!comboString.equals("SELECT")) {
					if (!(selectedType != null && selectedType.equals(comboString))) {
							selectedType = comboString;
							repaint(true, false);
					}
				}
            }
        } );
        
        // Create groups for item line
		SequentialGroup hRefreshItem = layout.createSequentialGroup();
		
		hRefreshItem.addComponent(refreshItem);
		hRefreshItem.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hRefreshItem.addComponent(refreshCombo, GroupLayout.DEFAULT_SIZE, 250, 600);
		hRefreshItem.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hRefreshItem.addComponent(refreshButton, GroupLayout.PREFERRED_SIZE, 200, Short.MAX_VALUE);
		
		ParallelGroup vRefreshItem = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vRefreshItem.addComponent(refreshItem, GroupLayout.PREFERRED_SIZE,  35, 60);
		vRefreshItem.addComponent(refreshCombo, GroupLayout.PREFERRED_SIZE,  35, 60);
		vRefreshItem.addComponent(refreshButton, GroupLayout.PREFERRED_SIZE,  35, 60);
		
		
		//Set-up replace part
		replaceItem.setText("Select Specific Ingredient or Dish:      ");

		curIngredients = new ArrayList<String>();
		if (selectedType == null)
			curIngredients.add(0, "SELECT");
		else {
	   		if (selectedType.equals(itemTypes[1]))
    			curIngredients = (ArrayList<String>) parent.ingredientList.clone();
    		else if (selectedType.equals(itemTypes[2]))
    			curIngredients = (ArrayList<String>) parent.categories.get(Category.ENTIRE_MENU).getDishes().clone();
    		else 
    			curIngredients = (ArrayList<String>) parent.categories.get(Category.UNSOLD_DISHES).getDishes().clone();
    		
    		for (int i = 0; i < curIngredients.size(); i++) {
    			MenuItem item = itemHash.get(curIngredients.get(i));
    			
    			if (!getSoldDishUses(item.getName()).isEmpty())
    				curIngredients.set(i, curIngredients.get(i) + suffices[0]);
    			else 
    				curIngredients.set(i, curIngredients.get(i) + suffices[1]);
    		}
    		
			curIngredients.add(0, "SELECT  (from " + selectedType + ")");
		}

		replaceCombo = new JComboBox(curIngredients.toArray());
		
		if (selectedType != null && selectedItem != null) {
			String selectedDishName = selectedItem.getName();
			
    		if (!getSoldDishUses(selectedDishName).isEmpty())
    			selectedDishName += suffices[0];
			else 
				selectedDishName += suffices[1];
            
    		if (curIngredients.contains(selectedDishName))
    			replaceCombo.setSelectedItem(selectedDishName);
		}
		
		updateAreErrors();
		
        replaceButton.setText("Replace  (Selected Item Below)");	
        replaceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
				String comboString = (String) replaceCombo.getSelectedItem();

				if (((comboString.length() < 6) || (!comboString.substring(0, 6).equals("SELECT")))) {
					
					comboString = stripToRealItemName(comboString);
					if (!(selectedItem != null && selectedItem.getName().equals(comboString))) {
						
						String undoMsg;
						String redoMsg;
						if (selectedItem != null) {
							undoMsg = "Selected Item in \"Ingredient Usage\" tab is changed back to " + selectedItem.getName();
							redoMsg = "Selected Item in \"Ingredient Usage\" tab is again changed to " + comboString;
						} else {
							undoMsg = "Selected Item in \"Ingredient Usage\" tab is now not set";
							redoMsg = "Selected Item in \"Ingredient Usage\" tab is again changed to " + comboString;
						}
						
						selectedItem = itemHash.get(comboString);

						// Label Replacement
						if (selectedItem.isDish())
		        			itemName.setText("Selected Dish:  " + selectedItem.getName().toUpperCase()); 
		        		else
		            		itemName.setText("Selected Ingredient:  " + selectedItem.getName().toUpperCase()); 
			    		
						// Error replacement
						updateAreErrors();
						
						parent.updateUndoRedoStack(undoMsg, redoMsg);
						
						repaint(false, true);
					}
				}
            }
        } );
		
        // Create groups for item line
		SequentialGroup hItemLine = layout.createSequentialGroup();
		
		hItemLine.addComponent(replaceItem);
		hItemLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hItemLine.addComponent(replaceCombo, GroupLayout.DEFAULT_SIZE, 250, 600);
		hItemLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hItemLine.addComponent(replaceButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		
		ParallelGroup vItemLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vItemLine.addComponent(replaceItem, GroupLayout.PREFERRED_SIZE,  35, 60);
		vItemLine.addComponent(replaceCombo, GroupLayout.PREFERRED_SIZE, 35, 60);
		vItemLine.addComponent(replaceButton, GroupLayout.PREFERRED_SIZE, 35, 60);
		
		
		//Set up Search Part
		changeUnits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (displayInventory) {
					displayInventory = false;
		        	changeUnits.setText("Display in Inventory's Units");
				} else { 
					displayInventory = true;
		        	changeUnits.setText("Display in Recipes' Units");
				}
				
				repaint(false, true);
			}
		} );
		
		ParallelGroup hJScrollPaneGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		SequentialGroup hJScrollPaneGroup1 = layout.createSequentialGroup();
		
		JLabel blankLabelSearch = new JLabel("");
		
		hJScrollPaneGroup1.addComponent(searchLabel);
		hJScrollPaneGroup1.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hJScrollPaneGroup1.addComponent(entry, GroupLayout.DEFAULT_SIZE, 300, 500);
		hJScrollPaneGroup1.addComponent(blankLabelSearch, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hJScrollPaneGroup1.addComponent(changeUnits, GroupLayout.DEFAULT_SIZE, 125, 175);
		
		hJScrollPaneGroup.addGroup(hJScrollPaneGroup1);
		hJScrollPaneGroup.addComponent(jScrollPane, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE);
		hJScrollPaneGroup.addComponent(status);

		SequentialGroup vJScrollPaneGroup = layout.createSequentialGroup();

		ParallelGroup vJScrollPaneGroup1 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		
		vJScrollPaneGroup1.addComponent(searchLabel);
		vJScrollPaneGroup1.addComponent(entry, GroupLayout.DEFAULT_SIZE, 25, 55);
		vJScrollPaneGroup1.addComponent(blankLabelSearch, GroupLayout.DEFAULT_SIZE, 25, 55);
		vJScrollPaneGroup1.addComponent(changeUnits, GroupLayout.DEFAULT_SIZE, 25, 55);		
		
		vJScrollPaneGroup.addGroup(vJScrollPaneGroup1);
		vJScrollPaneGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vJScrollPaneGroup.addComponent(jScrollPane, 125, 300, Short.MAX_VALUE);
		vJScrollPaneGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vJScrollPaneGroup.addComponent(status);

		
		//Create final group
		SequentialGroup hFinalLine = layout.createSequentialGroup();
		
		JLabel first = new JLabel("=====");
		JLabel middle = new JLabel("                ");
		JLabel last = new JLabel("=====");
		
		hFinalLine.addComponent(first, GroupLayout.DEFAULT_SIZE, 100, 100);
		hFinalLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hFinalLine.addComponent(middle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hFinalLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hFinalLine.addComponent(last, 0, 40, 40);
		
		ParallelGroup vFinalLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vFinalLine.addComponent(first, GroupLayout.PREFERRED_SIZE,  40, 60);
		vFinalLine.addComponent(middle, GroupLayout.PREFERRED_SIZE, 40, 60);
		vFinalLine.addComponent(last, GroupLayout.PREFERRED_SIZE, 40, 60);
		
		
		//Set up Report Button
		reportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
		    	File file = null;
		        String finalFilePath = null;
		    	
		        int returnVal = -1;
		        boolean continueToSave = false;
		                		        
		        returnVal = fcSaveInfo.showDialog(parent, "Save");
		        while (returnVal == JFileChooser.APPROVE_OPTION) {

	                file = fcSaveInfo.getSelectedFile();
	                continueToSave = Menu.checkFileFormat(file, "doc", true);
	                
	                if (!continueToSave) {
	                	String warningString = "Your selected file, \"" + file.getName() + "\", has an invalid file format!\n\nThe file must satisfy the following two properties:  \n" 
		                    					+ "1.  It can contain only (upper or lower case) letters and spaces (i.e. no symbols like *&/.~, ect.)\n" 
		                    					+ "2.  It can end with either NO extension or a .doc extension";	                    	
	                	JOptionPane.showMessageDialog(fcSaveInfo, warningString, "Invalid File", JOptionPane.WARNING_MESSAGE);
	                } else {
	                    boolean addTxt = false; 
	                    if (file.getName().lastIndexOf('.') < 0)                    
	                    	addTxt = true;
	                   
	                	finalFilePath = file.getAbsolutePath();
	                    if (addTxt)
	                    	finalFilePath += ".doc";

	                	file = new File(finalFilePath);
	                	if (file.exists()) {
	                        int overWrite = JOptionPane.showConfirmDialog(fcSaveInfo, "Your selected file, \"" + file.getName() + "\",	already exists.  Would you like to overwrite it?",
	                                										"Overwrite File", JOptionPane.YES_NO_OPTION);
	                        if (overWrite == JOptionPane.YES_OPTION) {
	                        	break;
	                        }
	                	} else {
	                		break;
	                	}
	                }
	                
	                continueToSave = false;
	                returnVal = fcSaveInfo.showDialog(parent, "Save");
		        }
		        
		        if (continueToSave) {                                      
		        	
		            WriteFile saver = new WriteFile(finalFilePath);
		            try {
		            	saver.writeToFile(getInfoMessage());
		            	JOptionPane.showMessageDialog(parent, "You have successfully saved your usage information as \"" + file.getName() + "\"!", "Success", JOptionPane.INFORMATION_MESSAGE);		            	
		            } catch (Exception except) {
		            	except.printStackTrace();
		            	except.getMessage();
		            	JOptionPane.showMessageDialog(parent, "There was an error saving! Try again or contact the creator of the program using the email address found in the \"Help\" tab", 
		            									"Saving Error", JOptionPane.ERROR_MESSAGE);
		            }
		        }
			}
		} );
		
		
		//Set up tab hortizontal layout
		ParallelGroup hRealGroup = layout.createParallelGroup();
		hRealGroup.addComponent(selection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addGroup(hRefreshItem);
		hRealGroup.addGroup(hItemLine);
		hRealGroup.addComponent(itemName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addComponent(usageInfo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addGroup(hJScrollPaneGroup);
		hRealGroup.addGroup(hFinalLine);
		hRealGroup.addComponent(reportButton, 15, 30, Short.MAX_VALUE);
		
		SequentialGroup hFinalGroup = layout.createSequentialGroup();
		hFinalGroup.addContainerGap();
		hFinalGroup.addGroup(hRealGroup);
		hFinalGroup.addContainerGap();
		
		hGroup.addGroup(GroupLayout.Alignment.TRAILING, hFinalGroup);
		layout.setHorizontalGroup(hGroup);
		
		//Set up tab vertical layout
		
		SequentialGroup vFinalGroup = layout.createSequentialGroup();
		vFinalGroup.addContainerGap();
		vFinalGroup.addComponent(selection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addGap(17);
		vFinalGroup.addGroup(vRefreshItem);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addGroup(vItemLine);
		vFinalGroup.addGap(45);
		vFinalGroup.addComponent(itemName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addGap(25);
		vFinalGroup.addComponent(usageInfo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addGap(17);
		vFinalGroup.addGroup(vJScrollPaneGroup);
		vFinalGroup.addGap(7);
		vFinalGroup.addGroup(vFinalLine);
		vFinalGroup.addGap(7);
		vFinalGroup.addComponent(reportButton, 35, 50, 50);
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
    
    public String getSelectedType() {
    	return selectedType;
    }
    
    public MenuItem getSelectedItem() {
    	return selectedItem;
    }
    
    public void updateAreErrors() {
    	if (selectedItem == null) {
    		areErrors = null;
    	} else {
			if (selectedItem.getIncompleteError())
				areErrors = ErrorType.ERROR_WITH_ITEM;
			else if (selectedItem.getUses().isEmpty())
				areErrors = ErrorType.ZERO_OVERALL_USES;
			else if (getSoldDishUses(selectedItem.getName()).isEmpty())
				areErrors = ErrorType.ZERO_SOLD_DISHES;
			else
				areErrors = ErrorType.NO_ERRORS;
    	}
    }
    
    public void setSelectedItem(MenuItem newItem) {
    	selectedItem = newItem;
    	updateAreErrors(); 
    }
    
    public ArrayList<String> getSoldDishUses(String itemName) {
    	Set<String> returnList = new TreeSet<String>();
    	getDishesHelper(itemHash.get(itemName), returnList, false);
    	    	
    	return MenuCompositionPane.convertToSortedArrayList(returnList);
    }
    
    public ArrayList<String> getAllDishUses(String itemName) {
    	Set<String> returnList = new TreeSet<String>();
    	getDishesHelper(itemHash.get(itemName), returnList, true);
    	    	
    	return MenuCompositionPane.convertToSortedArrayList(returnList);
    }
    
    private void getDishesHelper(MenuItem item, Set<String> curList, boolean unsoldOK) {
    	
    	for (MenuItem tempItem : item.getUses().values()) {
    		if (unsoldOK || tempItem.IsSoldDish())
    			curList.add(tempItem.getName());
    		
    		if (!tempItem.getUses().isEmpty())
    			getDishesHelper(tempItem, curList, unsoldOK);
    	}
    }
    
    public String stripToRealItemName(String itemName) {
    	int endIndex = itemName.lastIndexOf(suffices[0]);
		if ((endIndex > 0) && (endIndex == (itemName.length() - suffices[0].length())))
			return itemName.substring(0, endIndex);
		
		endIndex = itemName.lastIndexOf(suffices[1]);
		if ((endIndex > 0) && (endIndex == (itemName.length() - suffices[1].length())))
			return itemName.substring(0, endIndex);
		
		return null;
    }
    
    public String getInfoMessage() {
    	StringBuilder info = new StringBuilder("\n=== START ===\n\n\n\n");
    	if (selectedItem != null) { 
    		updateAreErrors();
    		
			if (areErrors == ErrorType.ERROR_WITH_ITEM) {
				
				info.append("SELECTED ITEM:  " + selectedItem.getName().toUpperCase());
				info.append("\n\n      --> HAS AN ERROR  (needs to be fixed to get its usage information)"); 
				info.append("\n\n(If it is a dish, it may be that the recipe is missing fields or the actual items in the recipe may have errors." +
						"\nIf it is an ingredient, then the ingredient is missing fields.)");
			} else if (areErrors == ErrorType.ZERO_OVERALL_USES) {
				
				info.append("SELECTED ITEM:  " + selectedItem.getName().toUpperCase());
				info.append("\n\n      --> IS NOT USED IN ANY OTHER DISHES  (therefore no usage information)"); 
				info.append("\n\n(You can add the dish to a recipe in the \"Dish Recipes\" tab.)");
			} else if (areErrors == ErrorType.ZERO_SOLD_DISHES) {
				
				info.append("SELECTED ITEM:  " + selectedItem.getName().toUpperCase());
				info.append("\n\n      --> IS NOT USED IN ANY SOLD DISHES ON THE MENU  (therefore no detailed usage information)");
			} else {
				
				info.append("SELECTED ITEM:  " + selectedItem.getName().toUpperCase());
				info.append("\n\n      --> IS USED IN AT LEAST ONE SOLD DISH  (all usage information available)");
			}
			
			info.append("\n\n\n");
    	}
    	
    	info.append("=== ITEM'S USES ===");
    
    	ArrayList<String> allUses = null;
    	if ((selectedItem != null) && (areErrors != ErrorType.ERROR_WITH_ITEM)) {
    		info.append("\n\n\nUSED IN THE FOLLOWING DISHES (WITH UNSOLD DISHES DENOTED WITH PARENTHESES):\n\n\n");
    		if (areErrors == ErrorType.ZERO_OVERALL_USES)
    			info.append("-None-\n");
    		
    		if ((areErrors == ErrorType.ZERO_SOLD_DISHES) || (areErrors == ErrorType.NO_ERRORS)) {
	    		
    			allUses = getAllDishUses(selectedItem.getName());
    			for (String use : allUses) {
	    			if (itemHash.get(use).IsSoldDish())
	    				info.append(use + "\n");
	    			else
	    				info.append("(" + use + ")\n");
	    		}
    		}
    	} else {
    		info.append("\n");
    	}
 
    	info.append("\n\n=== DETAILED INFORMATION ON USAGE IN SOLD DISHES ===");
    	
    	if ((selectedItem != null) && (areErrors == ErrorType.NO_ERRORS)) {
    		info.append("\n\n\n");
    		
    		MenuItem tempDish = null;
    		RecipeItem tempRecipeItem = null;
    		
    		String supplementalMsg = null;
    		RecipeItemDetails itemInfo = null;
    		
    		int counter = 0;
    		for (String use : allUses) {
    			tempDish = itemHash.get(use);
    			
				supplementalMsg = " (per each order" + " of " + tempDish.getName() + "):  ";
				
    			if (tempDish.IsSoldDish()) {
  
	    			if (counter != 0)
	    				info.append("\n---\n\n");

	    			tempRecipeItem = tempDish.getRecipeItem(selectedItem.getName());
	    			
	    			info.append(tempDish.getName().toUpperCase() + ":\n\n");
	    			
	    			if (!tempDish.getIncompleteError()) {
	    				itemInfo = new RecipeItemDetails(null, null, null, null);
	    				if (displayInventory)
	    					RecipeItemDetails.getItemInfoWithInventoryUnits(tempDish, selectedItem, new Double(1), itemInfo);
	    				else
	    					RecipeItemDetails.getItemInfo(tempDish, selectedItem, new Double(1), itemInfo);
	    				
		    			info.append("Amount Needed" + supplementalMsg + 
		    					MainFrame.toStringWithXDemical(itemInfo.amountPerOrder, 2) +
								" " + itemInfo.orginalUnits + " of " + itemInfo.name + "\n");
		    			
		    			info.append("Cost of " + itemInfo.name + supplementalMsg +  
		    					"LL " + MainFrame.toStringWithXDemical(itemInfo.cost, 0) + "\n");
		    			
		    			counter++;
		    			
	    			} else {
	    				info.append("ERROR:  There is nothing to show for " + use + " because it is missing some fields in the recipe!\n\n");
	    			}
    			}
    		}
    	}
    	
    	info.append("\n\n\n=== END ===");
    	return info.toString();
    }
    
    public void repaint(boolean selectedItemLine, boolean infoArea) {
    	
    	if (selectedItemLine) {
			ArrayList<String> tempItems = new ArrayList<String>();
    		
			if (selectedType == null) {
				tempItems.add("SELECT");
			} else {
				
	    		if (selectedType.equals(itemTypes[1]))
	    			tempItems = (ArrayList<String>) parent.ingredientList.clone();
	    		else if (selectedType.equals(itemTypes[2]))
	    			tempItems = (ArrayList<String>) parent.categories.get(Category.ENTIRE_MENU).getDishes().clone();
	    		else 
	    			tempItems = (ArrayList<String>) parent.categories.get(Category.UNSOLD_DISHES).getDishes().clone();
	    		
	    		for (int i = 0; i < tempItems.size(); i++) {
	    			MenuItem item = itemHash.get(tempItems.get(i));
	    			
	    			if (!getSoldDishUses(item.getName()).isEmpty())
	    				tempItems.set(i, tempItems.get(i) + suffices[0]);
	    			else 
	    				tempItems.set(i, tempItems.get(i) + suffices[1]);
	    		}
	    		
				tempItems.add(0, "SELECT  (from " + selectedType + ")");
			}
				
			JComboBox tempReplaceItems = new JComboBox(tempItems.toArray());

			String selectedDishName = (String) replaceCombo.getSelectedItem();
            if ((selectedDishName.length() < 6) || (!selectedDishName.substring(0, 6).equals("SELECT"))) {
					            
	            selectedDishName = stripToRealItemName(selectedDishName);
	    		if (!getSoldDishUses(selectedDishName).isEmpty())
	    			selectedDishName += suffices[0];
				else 
					selectedDishName += suffices[1];
	            
	            if (tempItems.contains(selectedDishName))
	            	tempReplaceItems.setSelectedItem(selectedDishName);
    		}
			
			layout.replace(replaceCombo, tempReplaceItems);
			replaceCombo = tempReplaceItems;
    	}
    	
    	if (infoArea) {  			
    		
			JTextArea tempArea = new JTextArea();
			tempArea.setColumns(15);
			tempArea.setLineWrap(true);
			tempArea.setRows(5);
			tempArea.setWrapStyleWord(true);
			tempArea.setEditable(false);
			tempArea.setText(this.getInfoMessage());
			
    		tempArea.setCaretPosition(0);
    		tempArea.setHighlighter(hilit);
    		
            JScrollPane tempPane = new JScrollPane(tempArea);
    		
            layout.replace(jScrollPane, tempPane);
            textArea = tempArea;
            jScrollPane = tempPane;	
    	}
    }
    
    public void repaintAll() {
		this.removeAll();

		this.initComponents();

		this.validate();
		this.repaint();
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