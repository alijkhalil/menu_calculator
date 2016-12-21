import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.GroupLayout.*;


public class ResultsPane extends JPanel implements DocumentListener {
	
	MainFrame parent;
	
	JFileChooser fc = new JFileChooser();
	JFileChooser fcSaveInfo = new JFileChooser();
	boolean isListSaved = true;
	
	
    Category selectedCat;
    MenuItem selectedDish;
        
    boolean areErrors;

    boolean displayBreakdown;
    boolean displayInventory;
	
    GroupLayout layout;
    
    private JLabel catSelection, dishSelection, items, stats;
	
    private JLabel refreshItem; private JComboBox refreshCombo; private JButton refreshButton;
    private JLabel itemLabel; private JComboBox itemCombo;
    
    private JButton addEntireButton, addOneButton, removeOneButton;
    
    private JTextArea textAreaList; private JScrollPane jScrollPaneList;
    private JTextArea textAreaStats; private JScrollPane jScrollPaneStats;
           
    private JButton importButton, saveButton, clearButton, reportButton;
    
    
    // Search part
    private JTextField entry;
    private JLabel searchLabel;
    private JButton changeBreakdown;
    private JButton changeUnits; 
    private JLabel status;
    
    final static Color  HILIT_COLOR = Color.LIGHT_GRAY;
    final static Color  ERROR_COLOR = Color.PINK;
    final static String CANCEL_ACTION = "cancel-search";
    final static String ENTER_ACTION = "enter-search";
    
    private Color entryBg;
    private Highlighter hilit;
    private Highlighter.HighlightPainter painter;
    
      
    public ResultsPane(MainFrame parent, String selectedCat, String selectedName, boolean displayBreakdown, boolean displayInventory) {

    	setUp(parent, selectedCat, selectedName, displayBreakdown, displayInventory);
    	initComponents(0);
  	}

    
    public void setUp(MainFrame parent, String selectedCat, String selectedName, boolean displayBreakdown, boolean displayInventory) {

    	this.parent = parent;
    
    	if (selectedCat != null)
    		this.selectedCat = parent.categories.get(selectedCat);
    	else
    		this.selectedCat = null;

    	if (selectedName != null)
    		this.selectedDish = parent.itemHash.get(selectedName);
    	else
    		this.selectedDish = null;
    	
    	this.displayBreakdown =  displayBreakdown;
    	this.displayInventory = displayInventory;
    }
    
    private void initComponents(int showAll) {
    	//Initialize JObjects
    	catSelection = new JLabel("Choose a Category"); 
    	catSelection.setFont(new Font("Dialog", Font.BOLD, 27));
    	
    	if (selectedCat != null)
    		dishSelection = new JLabel("Selected Category:  " + selectedCat.getName().toUpperCase()); 
    	else
    		dishSelection = new JLabel("Selected Category:  N/A             ~~~~~~~~~~~~>            (Pick a Category Above to Begin Adding Dishes for Cost Analysis)");    
    	
    	dishSelection.setFont(new Font("Dialog", Font.BOLD, 22));
    	
    	items = new JLabel("Dishes for Analysis"); 
    	items.setFont(new Font("Dialog", Font.BOLD, 27));
    	
    	if (showAll == 1)
    		stats = new JLabel("Resulting Statistics"); 
    	else 
    		stats = new JLabel(""); 
    	stats.setFont(new Font("Dialog", Font.BOLD, 27));
    	
        refreshItem = new JLabel(); refreshButton = new JButton();        
    	itemLabel = new JLabel();
    	
    	addEntireButton = new JButton(); addOneButton = new JButton(); removeOneButton = new JButton(); 
    	
        textAreaList = new JTextArea();
        textAreaList.setColumns(15);
        textAreaList.setLineWrap(true);
        textAreaList.setRows(5);
        textAreaList.setWrapStyleWord(true);
        textAreaList.setEditable(false);
        textAreaList.setText(this.getListInfoMessage());        
        textAreaList.setCaretPosition(0);
        
        jScrollPaneList = new JScrollPane(textAreaList);
  
        
        searchLabel = new JLabel("Enter Text to Search:");
        entry = new JTextField();
        if (displayBreakdown)
        	changeBreakdown = new JButton("Display Recipes Including Dishes");
        else
        	changeBreakdown = new JButton("Display Recipes In Only Ingriedents");

        	
        if (displayInventory)
        	changeUnits = new JButton("Display in Recipes' Units");
        else
        	changeUnits = new JButton("Display in Inventory's Units");
        
                
        textAreaStats = new JTextArea();
        textAreaStats.setColumns(15);
        textAreaStats.setLineWrap(true);
        textAreaStats.setRows(5);
        textAreaStats.setWrapStyleWord(true);
        textAreaStats.setEditable(false);
        textAreaStats.setText(this.getStatsInfoMessage());        
        textAreaStats.setCaretPosition(0);
        
        jScrollPaneStats = new JScrollPane(textAreaStats);
        
        status = new JLabel();

    
        importButton = new JButton(); saveButton = new JButton(); clearButton = new JButton();
        reportButton = new JButton("Save Information Above as a Document...");
        
        // Create two main groups
        layout = new GroupLayout(this);
        this.setLayout(layout);
        
		ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

        // Populate top group with appropriate data
        refreshItem.setText("Select Category:  ");
        
		ArrayList<String> tempList = new ArrayList<String>();        		
		tempList.add("SELECT");
		tempList.add(Category.ENTIRE_MENU);
		
		for (String cat : parent.categoriesList) {
			if (parent.categories.get(cat).isSold() && cat != Category.ENTIRE_MENU)
				tempList.add(cat);
		}        
        refreshCombo = new JComboBox(tempList.toArray());
        
        if (selectedCat != null)
        	refreshCombo.setSelectedItem(selectedCat.getName());
        
        refreshButton.setText("Refresh  (List of Dishes Below)");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				String comboString = (String) refreshCombo.getSelectedItem();

				if (!comboString.equals("SELECT")) {
					if (!(selectedCat != null && selectedCat.getName().equals(comboString))) {
						if (selectedCat == null) {							
							selectedCat = parent.categories.get(comboString);
							selectedDish = null;
							
				            parent.updateUndoRedoStack("Selected Category is again not set on the \"Cost Results\" tab", "Selected Category is now \"" + comboString + "\" on the \"Cost Results\" tab");
							
							repaintAll();
						} else {
							String prevName = selectedCat.getName();
							
							selectedCat = parent.categories.get(comboString);
							selectedDish = null;
														
				    		dishSelection.setText("Selected Category:  " + selectedCat.getName().toUpperCase()); 
							
				            parent.updateUndoRedoStack("Selected Category is set back to " + prevName + " on the \"Cost Results\" tab", 
        							"Selected Category is now \"" + comboString + "\"" + " on the \"Cost Results\" tab");

				    		repaint(false, true, false);
						}
					}
				}
            }
        } );
        
        // Create groups for category line
		SequentialGroup hCatLine = layout.createSequentialGroup();
		
		hCatLine.addComponent(refreshItem);
		hCatLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hCatLine.addComponent(refreshCombo, GroupLayout.DEFAULT_SIZE, 250, 700);
		hCatLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hCatLine.addComponent(refreshButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		
		ParallelGroup vCatLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vCatLine.addComponent(refreshItem, GroupLayout.PREFERRED_SIZE, 35, 55);
		vCatLine.addComponent(refreshCombo, GroupLayout.PREFERRED_SIZE, 35, 55);
		vCatLine.addComponent(refreshButton, GroupLayout.PREFERRED_SIZE, 35, 55);	
		
		
        // Populate top group with appropriate data
		SequentialGroup hItemLine = layout.createSequentialGroup();
		ParallelGroup vItemLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		
		if (selectedCat != null) {
	        itemLabel.setText("Select Dish:       ");
	        
			ArrayList<String> tempItems = new ArrayList<String>();	
			tempItems.add("SELECT  (from " + selectedCat.getName() + ")"); 
			tempItems.addAll((ArrayList<String>) selectedCat.getDishes().clone());
	
			itemCombo = new JComboBox(tempItems.toArray());
				
			if (selectedDish != null)
				itemCombo.setSelectedItem(selectedDish.getName());
	        
	        // Create groups for item line			
			hItemLine.addComponent(itemLabel);
			hItemLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
			hItemLine.addComponent(itemCombo, GroupLayout.DEFAULT_SIZE, 250, 500);
				
			vItemLine.addComponent(itemLabel, GroupLayout.PREFERRED_SIZE, 35, 55);
			vItemLine.addComponent(itemCombo, GroupLayout.PREFERRED_SIZE, 35, 55);
		} else {
			
			hItemLine.addComponent(itemLabel);
			vItemLine.addComponent(itemLabel);
		}

		
        // Populate buttons group with appropriate data
		ParallelGroup hFinalButtonLine = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		SequentialGroup vFinalButtonLine = layout.createSequentialGroup();
		
		if (selectedCat != null) {
					
			// Create add item buttons line
			addOneButton.setText("Add Dish to List    ");
			addOneButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	String comboItem = (String) itemCombo.getSelectedItem();
	            	
	            	if (((comboItem.length() < 6) || (!comboItem.substring(0, 6).equals("SELECT")))) {
	            		MenuItem tempDish = parent.itemHash.get(comboItem);
	            				
	            		NumberInputDialog dialogAdd = new NumberInputDialog("Item Adder", 
								"How many " + tempDish.getName().toUpperCase() + " would you like to ADD to the analysis?\n    \n" , true);
			
						dialogAdd.pack();
						dialogAdd.setLocationRelativeTo(textAreaList);
						dialogAdd.setVisible(true);
						
						
						String newIngredientNum = dialogAdd.getValidatedText();
						if (newIngredientNum != null) {
							try {
								Integer newIngredientInt = Integer.valueOf(dialogAdd.getValidatedText());
								
								OrderItem analysisItem = parent.currentOrder.get(tempDish.getName());
								
								if (newIngredientInt > 0) {
									if (analysisItem != null)
										analysisItem.addOrders(newIngredientInt);
									else
										 parent.currentOrder.put(tempDish.getName(), new OrderItem(tempDish.getName(), newIngredientInt));
								
									parent.updateUndoRedoStack("Removed " + newIngredientInt + " dishes of " + tempDish.getName() + " from the list on the \"Cost Results\" tab", 
					            							"Added " + newIngredientInt + " dishes of " + tempDish.getName() + " back to the list on the \"Cost Results\" tab");
								
									isListSaved = false;
									repaint(false, false, true);
								}
							} catch (Exception except) { }
						}
	            	}
	            }
			} );
			
			removeOneButton.setText("Remove Dish from List");
			removeOneButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String comboItem = (String) itemCombo.getSelectedItem();
            	
	            	if (((comboItem.length() < 6) || (!comboItem.substring(0, 6).equals("SELECT")))) {
	            		MenuItem tempDish = parent.itemHash.get(comboItem);
	            				
	            		NumberInputDialog dialogAdd = new NumberInputDialog("Item Remover", 
								"How many " + tempDish.getName().toUpperCase() + " would you like to REMOVE from the analysis?\n    \n" , true);
			
						dialogAdd.pack();
						dialogAdd.setLocationRelativeTo(textAreaList);
						dialogAdd.setVisible(true);
						
						
						String removeIngredientNum = dialogAdd.getValidatedText();
						if (removeIngredientNum != null) {
							try {
								Integer removeIngredientInt = Integer.valueOf(dialogAdd.getValidatedText());
																
								OrderItem analysisItem = parent.currentOrder.get(tempDish.getName());
								if (analysisItem != null && removeIngredientInt > 0) {
									if (!analysisItem.removeOrders(removeIngredientInt))
										parent.currentOrder.remove(tempDish.getName());
								
									parent.updateUndoRedoStack("Added " + removeIngredientInt + " dishes of " + tempDish.getName() + " back to the list on the \"Cost Results\" tab", 
													"Removed " + removeIngredientInt + " dishes of " + tempDish.getName() + " from the list on the \"Cost Results\" tab");
									
									isListSaved = false;
									repaint(false, false, true);
								}
							} catch (Exception except) { }
						}
	            	}	            	
				}
			} );
			
			SequentialGroup hButtonsLine = layout.createSequentialGroup();
			hButtonsLine.addComponent(addOneButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
			hButtonsLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
			hButtonsLine.addComponent(removeOneButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		
			ParallelGroup vButtonsLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
			vButtonsLine.addComponent(addOneButton, GroupLayout.PREFERRED_SIZE, 45, 65);
			vButtonsLine.addComponent(removeOneButton, GroupLayout.PREFERRED_SIZE, 45, 65);
			
			//Create add cat line
			addEntireButton.setText("Add Entire Category to List");
			addEntireButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
	            	
	            	if (selectedCat != null) {	            				
	            		NumberInputDialog dialogAdd = new NumberInputDialog("Category Adder", 
								"How many of each item in " + selectedCat.getName().toUpperCase() + " would you like to ADD to the analysis?\n    \n" , true);
			
						dialogAdd.pack();
						dialogAdd.setLocationRelativeTo(textAreaList);
						dialogAdd.setVisible(true);
						
						
						String newIngredientNum = dialogAdd.getValidatedText();
						if (newIngredientNum != null) {
							try {
								Integer newIngredientInt = Integer.valueOf(dialogAdd.getValidatedText());
	
								if (newIngredientInt > 0) {
									
									OrderItem analysisItem = null;
									for (String tempDishName : selectedCat.getDishes()) {
										
										analysisItem = parent.currentOrder.get(tempDishName);
										if (analysisItem != null)
											analysisItem.addOrders(newIngredientInt);
										else
											 parent.currentOrder.put(tempDishName, new OrderItem(tempDishName, newIngredientInt));
									}
								
									parent.updateUndoRedoStack("Removed " + newIngredientInt + " of each dish in the " + selectedCat.getName() + " category from the list on the \"Cost Results\" tab", 
            							"Added " + newIngredientInt + " of each dish in the " + selectedCat.getName() + " category back into the list on the \"Cost Results\" tab");
								
									isListSaved = false;
									repaint(false, false, true);
								}
							} catch (Exception except) { }
						}
	            	}	            	
				}
			} );
			
			SequentialGroup hCatButtonLine = layout.createSequentialGroup();
			hCatButtonLine.addComponent(addEntireButton, 0, 150, Short.MAX_VALUE);

			ParallelGroup vCatButtonLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
			vCatButtonLine.addComponent(addEntireButton, GroupLayout.PREFERRED_SIZE, 53, 73);
			
			
			hFinalButtonLine.addGroup(hButtonsLine);
			hFinalButtonLine.addGroup(hCatButtonLine);
			
			vFinalButtonLine.addGroup(vButtonsLine);
			vFinalButtonLine.addGap(10);
			vFinalButtonLine.addGroup(vCatButtonLine);
		} else {
			JLabel blankButtonLabel = new JLabel("");
			
			hFinalButtonLine.addComponent(blankButtonLabel);
			vFinalButtonLine.addComponent(blankButtonLabel);
		}
		
		
		// Create add item buttons line
		importButton.setText("Import List for Analysis...               ");
		importButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				openList();
			}
		} );
		
		saveButton.setText("Save List for Anaylsis in the Future...");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				saveList();
			}
		} );
				
				
		SequentialGroup hOpenSaveLine = layout.createSequentialGroup();
		hOpenSaveLine.addComponent(importButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hOpenSaveLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hOpenSaveLine.addComponent(saveButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
	
		ParallelGroup vOpenSaveLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		vOpenSaveLine.addComponent(importButton, GroupLayout.PREFERRED_SIZE, 45, 65);
		vOpenSaveLine.addComponent(saveButton, GroupLayout.PREFERRED_SIZE, 45, 65);
		
		//Create add cat line
		clearButton.setText("CLEAR LIST ABOVE AND STATISTICS BELOW");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (parent.currentOrder != null && !parent.currentOrder.isEmpty()) {
	                int n = JOptionPane.showConfirmDialog(
	                		textAreaList, "Are you sure that you want to clear everything?\n    \n",
	                        "Clear Confirmation",
	                        JOptionPane.OK_CANCEL_OPTION);
	                
	                if (n == JOptionPane.OK_OPTION) {
	                	                	
						parent.currentOrder = new HashMap<String, OrderItem>();
						parent.currentOrder = parent.currentOrder;
						
			            parent.updateUndoRedoStack("Restored the item list on the \"Cost Results\" tab", 
													"Cleared the item list again on the \"Cost Results\" tab");
						
						repaint(false, false, true);
	                }              
				}
  			}
		} );
		
		
		SequentialGroup hClearLine = layout.createSequentialGroup();
		hClearLine.addComponent(clearButton, 0, 150, Short.MAX_VALUE);

		ParallelGroup vClearLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		vClearLine.addComponent(clearButton, GroupLayout.PREFERRED_SIZE, 60, 73);
		
		
		ParallelGroup hFinalListLine = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		SequentialGroup vFinalListLine = layout.createSequentialGroup();
		
		hFinalListLine.addGroup(hOpenSaveLine);
		hFinalListLine.addGroup(hClearLine);
		
		vFinalListLine.addGroup(vOpenSaveLine);
		vFinalListLine.addGap(10);
		vFinalListLine.addGroup(vClearLine);
		
		
		//Set up invisible part
		SequentialGroup hTitlePaneGroup = layout.createSequentialGroup();
		ParallelGroup vTitlePaneGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
		ParallelGroup hJScrollPaneGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		SequentialGroup vJScrollPaneGroup = layout.createSequentialGroup();
		
		ParallelGroup hFinalLine = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		SequentialGroup vFinalLine = layout.createSequentialGroup();
		
		if (showAll == 1) {
			JLabel blankLabelSearch1 = new JLabel("");
			JLabel blankLabelSearch2 = new JLabel("");
			
			//Add breakdown action lister
			changeBreakdown.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					if (displayBreakdown) {
						displayBreakdown = false;
			        	changeBreakdown.setText("Display Recipes In Only Ingriedents");
					} else { 
						displayBreakdown = true;
			        	changeBreakdown.setText("Display Recipes Including Dishes");
					}
					
					repaint(false, false, true);
				}
			} );
			
			
			hTitlePaneGroup.addComponent(stats); 
			hTitlePaneGroup.addComponent(blankLabelSearch1, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
			hTitlePaneGroup.addComponent(changeBreakdown, 150, 285, 325);
			
			vTitlePaneGroup.addComponent(stats);
			vTitlePaneGroup.addComponent(blankLabelSearch1, GroupLayout.DEFAULT_SIZE, 35, 55);
			vTitlePaneGroup.addComponent(changeBreakdown, GroupLayout.DEFAULT_SIZE, 35, 55);

			
			//Set up search part
			changeUnits.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					if (displayInventory) {
						displayInventory = false;
			        	changeUnits.setText("Display in Inventory's Units");
					} else { 
						displayInventory = true;
			        	changeUnits.setText("Display in Recipes' Units");
					}
					
					repaint(false, false, true);
				}
			} );
			
			
			hJScrollPaneGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
	
			SequentialGroup hJScrollPaneGroup1 = layout.createSequentialGroup();
						
			hJScrollPaneGroup1.addComponent(searchLabel);
			hJScrollPaneGroup1.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
			hJScrollPaneGroup1.addComponent(entry, GroupLayout.DEFAULT_SIZE, 300, 500);
			hJScrollPaneGroup1.addComponent(blankLabelSearch2, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
			hJScrollPaneGroup1.addComponent(changeUnits, 150, 285, 325);
			
			hJScrollPaneGroup.addGroup(hJScrollPaneGroup1);
			hJScrollPaneGroup.addComponent(jScrollPaneStats);
			hJScrollPaneGroup.addComponent(status);
	
			vJScrollPaneGroup = layout.createSequentialGroup();
	
			ParallelGroup vJScrollPaneGroup1 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
			
			vJScrollPaneGroup1.addComponent(searchLabel);
			vJScrollPaneGroup1.addComponent(entry, GroupLayout.DEFAULT_SIZE, 35, 55);
			vJScrollPaneGroup1.addComponent(blankLabelSearch2, GroupLayout.DEFAULT_SIZE, 35, 55);
			vJScrollPaneGroup1.addComponent(changeUnits, GroupLayout.DEFAULT_SIZE, 35, 55);
			
			vJScrollPaneGroup.addGroup(vJScrollPaneGroup1);
			vJScrollPaneGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
			vJScrollPaneGroup.addComponent(jScrollPaneStats, 400, 425, Short.MAX_VALUE);
			vJScrollPaneGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
			vJScrollPaneGroup.addComponent(status);
			
			
			//Create final group
			JLabel first = new JLabel("=====");
			JLabel middle = new JLabel("                ");
			JLabel last = new JLabel("=====");
			
			SequentialGroup hFinalLine1 = layout.createSequentialGroup();
			hFinalLine1.addComponent(first, GroupLayout.DEFAULT_SIZE, 100, 100);
			hFinalLine1.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
			hFinalLine1.addComponent(middle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
			hFinalLine1.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
			hFinalLine1.addComponent(last, 0, 40, 40);
			
			ParallelGroup vFinalLine1 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
			vFinalLine1.addComponent(first, GroupLayout.PREFERRED_SIZE,  30, 50);
			vFinalLine1.addComponent(middle, GroupLayout.PREFERRED_SIZE, 30, 50);
			vFinalLine1.addComponent(last, GroupLayout.PREFERRED_SIZE, 30, 50);

			
			hFinalLine.addGroup(hFinalLine1);
			hFinalLine.addComponent(reportButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
			
			vFinalLine.addGroup(vFinalLine1);
			vFinalLine.addGap(5);
			vFinalLine.addComponent(reportButton, 0, 65, 70);
						
		} else {
			JLabel blankLabel1 = new JLabel("");
			
			hTitlePaneGroup.addComponent(blankLabel1);
			vTitlePaneGroup.addComponent(blankLabel1);
			
			JLabel blankLabel2 = new JLabel("");
			
			hJScrollPaneGroup.addComponent(blankLabel2);
			vJScrollPaneGroup.addComponent(blankLabel2);

			JLabel blankLabel3 = new JLabel("");
			
			hFinalLine.addComponent(blankLabel3);
			vFinalLine.addComponent(blankLabel3);
		}
		
		
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
	                        int overWrite = JOptionPane.showConfirmDialog(fcSaveInfo, "Your selected file, \"" + file.getName() + "\",	already exists.  Would you like to OVERWRITE it?",
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
		            	saver.writeToFile(getStatsInfoMessage());
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
		hRealGroup.addComponent(catSelection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addGroup(hCatLine);
		hRealGroup.addComponent(dishSelection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addGroup(hItemLine);
		hRealGroup.addGroup(hFinalButtonLine);
		hRealGroup.addComponent(items, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);				
		hRealGroup.addComponent(jScrollPaneList, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addGroup(hFinalListLine);		
		hRealGroup.addGroup(hTitlePaneGroup);				
		hRealGroup.addGroup(hJScrollPaneGroup);
		hRealGroup.addGroup(hFinalLine);
		
		SequentialGroup hFinalGroup = layout.createSequentialGroup();
		hFinalGroup.addContainerGap();
		hFinalGroup.addGroup(hRealGroup);
		hFinalGroup.addContainerGap();
		
		hGroup.addGroup(GroupLayout.Alignment.TRAILING, hFinalGroup);
		layout.setHorizontalGroup(hGroup);
		
		
		//Set up tab vertical layout
		SequentialGroup vFinalGroup = layout.createSequentialGroup();
		vFinalGroup.addContainerGap();
		vFinalGroup.addComponent(catSelection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addGap(23);
		vFinalGroup.addGroup(vCatLine);
		vFinalGroup.addGap(17);
		vFinalGroup.addComponent(dishSelection, 0, 50, 60);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addGroup(vItemLine);
		vFinalGroup.addGap(13);
		vFinalGroup.addGroup(vFinalButtonLine);
		vFinalGroup.addGap(10);
		vFinalGroup.addComponent(items, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addComponent(jScrollPaneList, 275, 275, Short.MAX_VALUE);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addGroup(vFinalListLine);
		vFinalGroup.addGap(25);
		vFinalGroup.addGroup(vTitlePaneGroup);
		vFinalGroup.addGap(17);
		vFinalGroup.addGroup(vJScrollPaneGroup);	
		vFinalGroup.addGap(5);
		vFinalGroup.addGroup(vFinalLine);
		vFinalGroup.addContainerGap();
		
		vGroup.addGroup(vFinalGroup);
		layout.setVerticalGroup(vGroup);
		
		
    	//Search Functionality        
        hilit = new DefaultHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
        textAreaStats.setHighlighter(hilit);
        
        entryBg = entry.getBackground();
        entry.getDocument().addDocumentListener(this);
        
        InputMap im = entry.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = entry.getActionMap();
        
        im.put(KeyStroke.getKeyStroke("ESCAPE"), CANCEL_ACTION);
        am.put(CANCEL_ACTION, new CancelAction());
        
        im.put(KeyStroke.getKeyStroke("ENTER"), ENTER_ACTION);
        am.put(ENTER_ACTION, new EnterAction());
    }
    
    public Category getSelectedCat() {
    	return selectedCat;
    }
    
    public void setSelectedCat(Category newCat) {
    	selectedCat = newCat;
    }
    
    public MenuItem getSelectedDish() {
    	return selectedDish;
    }
    
    public void setSelectedDish(MenuItem newDish) {
    	selectedDish = newDish;
    }
    
    public void saveList() {
    	
    	File file = null;
        String finalFilePath = null;
    	
        int returnVal = -1;
        boolean continueToSave = false;
                		        
        returnVal = fc.showDialog(parent, "Save");
        while (returnVal == JFileChooser.APPROVE_OPTION) {

            file = fc.getSelectedFile();
            continueToSave = Menu.checkFileFormat(file, "txt", true);
            
            if (!continueToSave) {
            	String warningString = "Your selected file, \"" + file.getName() + "\", has an invalid file format!\n\nThe file must satisfy the following two properties:  \n" 
                    					+ "1.  It can contain only (upper or lower case) letters and spaces (i.e. no symbols like *&/.~, ect.)\n" 
                    					+ "2.  It can end with either NO extension or a .doc extension";	                    	
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
                    int overWrite = JOptionPane.showConfirmDialog(fc, "Your selected file, \"" + file.getName() + "\",	already exists.  Would you like to OVERWRITE it?",
                            										"Overwrite File", JOptionPane.YES_NO_OPTION);
                    if (overWrite == JOptionPane.YES_OPTION) {
                    	break;
                    }
            	} else {
            		break;
            	}
            }
            
            continueToSave = false;
            returnVal = fc.showDialog(parent, "Save");
        }
        
        if (continueToSave) {                                      
        	
            WriteFile saver = new WriteFile(finalFilePath);
            try {
            	saver.writeToFile(parent.saveItem.orderListSave());            	
            	
            	isListSaved = true;
            	JOptionPane.showMessageDialog(parent, "You have successfully saved your item list as \"" + file.getName() + "\"!", "Success", JOptionPane.INFORMATION_MESSAGE);		            	
            } catch (Exception except) {
            	except.printStackTrace();
            	except.getMessage();
            	JOptionPane.showMessageDialog(parent, "There was an error saving! Try again or contact the creator of the program using the email address found in the \"Help\" tab", 
            									"Saving Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
   public void openList() {
    	
       File file = null;
       String finalFilePath = null;
   	
       int returnVal = -1;
       boolean continueToSave = true;
   
       if (!isListSaved) {
	       int overWrite = JOptionPane.showConfirmDialog(fc, "Would you like to SAVE your list of dishes before opening a different one?", "Save Before Opening", JOptionPane.YES_NO_OPTION);
		   if (overWrite == JOptionPane.YES_OPTION || overWrite == JOptionPane.NO_OPTION) {
		       if (overWrite == JOptionPane.YES_OPTION) {
		           
		           saveList();
		           JOptionPane.showMessageDialog(fc, "You can now OPEN the dish list of your choice!", "Open", JOptionPane.INFORMATION_MESSAGE);;
		       }
		   } else {
		       return;
		   }
       }
		
	   continueToSave = false;
	   returnVal = fc.showOpenDialog(parent);
	   while (returnVal == JFileChooser.APPROVE_OPTION) {

	       file = fc.getSelectedFile();
	       continueToSave = Menu.checkFileFormat(file, "txt", false);
	   
		   if (!continueToSave) {
		       String warningString = "Your selected file, \"" + file.getName() + "\", has an invalid file format!\n\nThe file must end in \".txt\".";	                    	
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
	    	   FileReader f = new FileReader(finalFilePath);
	    	   BufferedReader br = new BufferedReader(f);
	       	
	    	   boolean errorCheck = false;
	       	
		       errorCheck = parent.openItem.openItemList(br, errorCheck);
		       br.close();
		       	
		       if (errorCheck) {
		       		f = new FileReader(finalFilePath);
		       		br = new BufferedReader(f);
		       		
		       		parent.openItem.openItemList(br, errorCheck);
		       		parent.updateUndoRedoStack("UNDID opening the new list in the \"Cost Results\" tab", "REDID opening the new list in the \"Cost Results\" tab");
		       		
		       		JOptionPane.showMessageDialog(fc, "You have successfully opened the item list in \"" + file.getName() + "\"!", "Success", JOptionPane.INFORMATION_MESSAGE);
		
		       } else {
					JOptionPane.showMessageDialog(fc, "There was an error opening your selected file!  It has an incorrect format or contains non-existent dishes.\n\n" +
										"If you believe that it is a mistake, you can contact the creator of the program using the email address found in the \"Help\" tab.", 
										"Invalid File Format Error", JOptionPane.ERROR_MESSAGE);
		       }
   	
		   } catch (Exception except) {             
			   	except.printStackTrace();
			   	except.getMessage();
			   	
			   	JOptionPane.showMessageDialog(fc, "There was an error opening your selected file!\n\nTry again or contact the creator of the program using the email address found in the \"Help\" tab.", 
							"Open Error", JOptionPane.ERROR_MESSAGE);
		   }	       
	   }
   }
    
    public String getListInfoMessage() {
    	StringBuilder info = new StringBuilder("");
    	areErrors = false;
    	
    	info.append("\n\n");
    	
    	info.append("         Quantity                                    Name\n");
    	info.append(" ---------------------------------------------------------------------------------\n\n");
    	
    	int numDigits;
    	int tempNum;
    	
    	ArrayList<String> orderKeys = MenuCompositionPane.convertToSortedArrayList(parent.currentOrder.keySet());
    	for (String item : orderKeys) {
    		tempNum = parent.currentOrder.get(item).getOrderNum();
    		
    		numDigits = 0;
    		while (tempNum > 0) {
    			tempNum /= 10;
    			numDigits++;
    		}
    		
    		info.append("          " + parent.currentOrder.get(item).getOrderNum());
    		for (int i = 0; i < (25 - numDigits); i++)
    			info.append("  ");
    		
    		info.append(parent.currentOrder.get(item).getName());
    		if (parent.itemHash.get(parent.currentOrder.get(item).getName()).getIncompleteError() || parent.itemHash.get(parent.currentOrder.get(item).getName()).getPrice() == null) {
    			info.append("  (ERROR)");
    			areErrors = true;
    		}
    		info.append("\n");
    	}
    	
    	info.append("\n\n\n");
    			
    	return info.toString();
    }
    
    public String getStatsInfoMessage() {
    	StringBuilder infoStart = new StringBuilder("\n=== START ===\n\n\n");
    	StringBuilder infoMid = new StringBuilder("-- Total Ingredient Info --\n\n");
    	StringBuilder infoEnd = new StringBuilder("=== INDIVIDUAL DISH STATISTICS ===\n");

    	//Beginning Portion
    	if (areErrors || parent.currentOrder.isEmpty()) {
    		infoStart.append("\nTHERE ARE EITHER NO DISHES IN THE LIST ABOVE OR SOME OF THE DISHES HAVE ERRORS!!!\n");
    	} else {
    		infoStart.append("\nOVERVIEW:\n\n");
    		
    		int totalDishes = 0;
    		
    		int totalRev = 0;
    		int totalCost = 0;
    		
    		HashMap<String, Double> margins = new HashMap<String, Double>(); 
    		
    		MenuItem tempDish = null;
    		int tempMultiplier = 0;
    		for (OrderItem orderItem : parent.currentOrder.values()) {
    			tempDish = parent.itemHash.get(orderItem.getName());
    			tempMultiplier = orderItem.getOrderNum();
    			
    			totalDishes += tempMultiplier;
    			
    			totalRev += (tempMultiplier * tempDish.getPrice());
    			totalCost += (tempMultiplier * tempDish.getCost());  			
    			
    			margins.put(orderItem.getName(), tempDish.getCost()/tempDish.getPrice());
    		}
    	
    		Double avgCost = new Double(0);
    		   		
    		boolean evenNum = false;
    		if (parent.currentOrder.size() % 2 == 0)
    			evenNum = true;
    		int medianIndex = ((parent.currentOrder.size() - 1)/2);
    		
    		Double medianCost = new Double(0);
    		
    		LinkedList<Double> tempMargins = RecipeItemDetails.sortDoubles(margins.values());
    		for (int i = 0; i < tempMargins.size(); i++) {
    			avgCost += tempMargins.get(i);
    			
    			if (i == medianIndex)
    				medianCost += tempMargins.get(i);
    			else if (evenNum && i == medianIndex + 1)
    				medianCost = ((medianCost + tempMargins.get(i))/2);
    		}
    		
    		avgCost = avgCost/tempMargins.size();
    		
    		infoStart.append("Total Number of Dishes:  " + MainFrame.toStringWithXDemical((double)(totalDishes), 0) + "\n\n");
    		
    		infoStart.append("Total Profit:  LL " + MainFrame.toStringWithXDemical((double)(totalRev - totalCost), 0) + "\n\n");
    		
    		infoStart.append("Total Revenue:  LL " + MainFrame.toStringWithXDemical((double)(totalRev), 0) + "\n");
    		infoStart.append("Total Costs:  LL " + MainFrame.toStringWithXDemical((double)(totalCost), 0) + "\n\n");
    		
    		infoStart.append("Total Cost-to-Revenue Ratio (using all " + MainFrame.toStringWithXDemical((double)(totalDishes), 0) + " dishes):  " + MainFrame.toStringWithXDemical(((double)(100 * totalCost)/totalRev), 2) + " %\n\n");
    		infoStart.append("Average Cost-to-Revenue Ratio (using only one of each different dish):  " + MainFrame.toStringWithXDemical((100 * avgCost), 2) + " %\n");
    		infoStart.append("Median Cost-to-Revenue Ratio (using only one of each different dish):  " + MainFrame.toStringWithXDemical((100 * medianCost), 2) + " %\n\n\n");
    		
    		
    		//Middle and End Portion
    		HashMap<String, RecipeItemDetails> totalIngredientInfo = new HashMap<String, RecipeItemDetails>();
    		HashMap<String, RecipeItemDetails> allIngredientsInfo = null;
    		
     		ArrayList<String> orderNames = MenuCompositionPane.convertToSortedArrayList(parent.currentOrder.keySet());
     		ArrayList<String> ingredientNames = null;

     		MenuItem ingredientItem = null;
    		OrderItem tempOrderItem = null;
    		RecipeItemDetails tempDetailedItem = null;

    		int numOrders = 0;
    		for (String dishName : orderNames) {
    			tempOrderItem = parent.currentOrder.get(dishName);
    			tempDish = parent.itemHash.get(dishName);
    			
    			infoEnd.append("\n\n" + dishName.toUpperCase() + ":\n\n");
    			
    			infoEnd.append("Number of " + dishName + " Dishes:  " + tempOrderItem.getOrderNum() + "\n\n");
    			
    			infoEnd.append("Total Profit (from all " + dishName  + "):  LL " 
    						+ MainFrame.toStringWithXDemical((tempOrderItem.getOrderNum() * (tempDish.getPrice() - tempDish.getCost())), 0) + "\n\n");
    			
    			infoEnd.append("Total Revenues (from all " + dishName  + "):  LL " 
						+ MainFrame.toStringWithXDemical((tempOrderItem.getOrderNum() * (tempDish.getPrice())), 0) + "\n");    			
    			infoEnd.append("Total Costs (from all " + dishName  + "):  LL " 
						+ MainFrame.toStringWithXDemical((tempOrderItem.getOrderNum() * (tempDish.getCost())), 0) + "\n\n");    		
    			
        		infoEnd.append(dishName + " Cost-to-Revenue Ratio:  " + MainFrame.toStringWithXDemical((100 * tempDish.getCost()/tempDish.getPrice()), 2) + " %\n");
        		infoEnd.append(dishName + " Relative Profitablity Rating (i.e. Median Cost-to-Revenue divided by " + dishName + " Cost-to-Revenue):  " 
        							+ MainFrame.toStringWithXDemical((medianCost * tempDish.getPrice()/tempDish.getCost()), 2) + " \n\n\n");
        		
        		infoEnd.append("-- " + dishName + " Ingredient Info --\n\n");
        		
        		if (displayInventory) {
        			if (displayBreakdown)
        				allIngredientsInfo = RecipeItemDetails.getAllIngredientsInInventoryUnits(tempDish, (double) tempOrderItem.getOrderNum(), new HashMap<String, RecipeItemDetails>());
        			else 
        				allIngredientsInfo = RecipeItemDetails.getAllComponentsInInventoryUnits(tempDish, (double) tempOrderItem.getOrderNum(), new HashMap<String, RecipeItemDetails>());
        		} else {        			if (displayBreakdown)        			
        				allIngredientsInfo = RecipeItemDetails.getAllIngredients(tempDish, (double) tempOrderItem.getOrderNum(), new HashMap<String, RecipeItemDetails>());
        			else
        				allIngredientsInfo = RecipeItemDetails.getAllComponents(tempDish, (double) tempOrderItem.getOrderNum(), new HashMap<String, RecipeItemDetails>());
        		}
    			ingredientNames = MenuCompositionPane.convertToSortedArrayList(allIngredientsInfo.keySet());

    			RecipeItemDetails totalDetails = null;
    			RecipeItemDetails blankDetailedItem = null;
    			
        		numOrders++;
        		int counter = 1;   		      		
        		for (String infoItemName : ingredientNames) {
        			
        			tempDetailedItem = allIngredientsInfo.get(infoItemName);
    				totalDetails = totalIngredientInfo.get(infoItemName);

    				//Middle Computations
    				if (displayInventory) {
    					ingredientItem = parent.itemHash.get(infoItemName);
    					
    					blankDetailedItem = new RecipeItemDetails(tempDetailedItem.name, null, null, tempDetailedItem.cost);
    					
    					if (Opener.convertToArrayList(MenuCompositionPane.LIQUID_UNITS).contains(tempDetailedItem.orginalUnits))
    						blankDetailedItem.amountPerOrder = tempDetailedItem.amountPerOrder * RecipeItem.getLiquidFactor(ingredientItem.getUnits(), tempDetailedItem.orginalUnits);
    					else if (Opener.convertToArrayList(MenuCompositionPane.SOLID_UNITS).contains(tempDetailedItem.orginalUnits))
    						blankDetailedItem.amountPerOrder = tempDetailedItem.amountPerOrder * RecipeItem.getSolidFactor(ingredientItem.getUnits(), tempDetailedItem.orginalUnits);
    					else
    						blankDetailedItem.amountPerOrder = tempDetailedItem.amountPerOrder;
    					
    					blankDetailedItem.orginalUnits = ingredientItem.getUnits();    				
    				
    				} else {	
    					blankDetailedItem = tempDetailedItem;  
    				}
    				
    				if (totalDetails == null) {
    					totalIngredientInfo.put(infoItemName, blankDetailedItem);
    				} else {
    					totalDetails.amountPerOrder += blankDetailedItem.amountPerOrder;	    				
    					totalDetails.cost += blankDetailedItem.cost;
    				}
    				
    				
    				//End String Info
        			infoEnd.append("Ingredient " + counter + ":  " + infoItemName + "\n\n");
        			
        			infoEnd.append("Total Amount Needed (for " + tempOrderItem.getOrderNum() +  " Orders):  " 
        						+ MainFrame.toStringWithXDemical(tempDetailedItem.amountPerOrder, 2) + " " + tempDetailedItem.orginalUnits + "\n");
        			
        			infoEnd.append("Total Cost (for " + tempOrderItem.getOrderNum() +  " Orders):  LL " 
        						+ MainFrame.toStringWithXDemical(tempDetailedItem.cost, 0));
        			
        			if (numOrders != orderNames.size() || counter != ingredientNames.size())
        				infoEnd.append("\n\n");
        			if (numOrders != orderNames.size() && counter == ingredientNames.size())
        				infoEnd.append("\n---\n\n");
        			
        			counter++;
        		}
    		}
    		
    		//Middle String Info
    		ingredientNames = MenuCompositionPane.convertToSortedArrayList(totalIngredientInfo.keySet());
    		int index = 1;
    		for (String infoItemName : ingredientNames) {
    			tempDetailedItem = totalIngredientInfo.get(infoItemName);
    			
    			infoMid.append("Ingredient " + index + ":  " + infoItemName + "\n\n");
    			
    			infoMid.append("Total Amount Needed (for Entire List):  " + MainFrame.toStringWithXDemical(tempDetailedItem.amountPerOrder, 2) 
    							+ " " + tempDetailedItem.orginalUnits + "\n");
    			
    			infoMid.append("Total Cost (for Entire List):  LL " + MainFrame.toStringWithXDemical(tempDetailedItem.cost, 0));
    			
    			
    			if (index != ingredientNames.size())
    				infoMid.append("\n\n");
    			else
    				infoMid.append("\n\n\n\n");

    			index++;
    		}
    		
        	infoStart.append(infoMid);
        	infoStart.append(infoEnd);
    	}
    	
    	infoStart.append("\n\n\n=== END ===");
    	    	
    	return infoStart.toString();
    }
    
    public void repaint(boolean catLine, boolean dishLine, boolean infoMessages) {
    	
    	if (catLine) {
    		ArrayList<String> tempCats = new ArrayList<String>();        		
    		tempCats.add("SELECT");
    		tempCats.add(Category.ENTIRE_MENU);
    		
    		for (String cat : parent.categoriesList) {
    			if (parent.categories.get(cat).isSold() && cat != Category.ENTIRE_MENU)
    				tempCats.add(cat);
    		}
            
    		JComboBox tempReplaceCats = new JComboBox(tempCats.toArray());
            
            String selectedCatName = (String) refreshCombo.getSelectedItem();
            if (selectedCat != null && tempCats.contains(selectedCatName))
            	tempReplaceCats.setSelectedItem(selectedCatName);
			
			layout.replace(refreshCombo, tempReplaceCats);
			refreshCombo = tempReplaceCats;
    	}
    	
    	if (dishLine && selectedCat != null) {
			ArrayList<String> tempDishes = new ArrayList<String>();

			if (selectedCat == null) {
				tempDishes.add("SELECT");
			} else {
				tempDishes.add("SELECT  (from " + selectedCat.getName() + ")"); 
				tempDishes.addAll((ArrayList<String>) selectedCat.getDishes().clone());
			}

			JComboBox tempReplaceDish = new JComboBox(tempDishes.toArray());

            
            if (selectedCat != null) {
            	String selectedDishName = (String) itemCombo.getSelectedItem();
            	
            	if (tempDishes.contains(selectedDishName))
            		tempReplaceDish.setSelectedItem(selectedDishName);
            }
            
			layout.replace(itemCombo, tempReplaceDish);
			itemCombo = tempReplaceDish;
    	}
    	
    	if (infoMessages) {
    		if (parent.currentOrder != null) {
	    		//Repaint normal Area
				JTextArea listTempArea = new JTextArea();
				listTempArea.setColumns(15);
				listTempArea.setLineWrap(true);
				listTempArea.setRows(5);
				listTempArea.setWrapStyleWord(true);
				listTempArea.setEditable(false);
				
				listTempArea.setText(this.getListInfoMessage());
				listTempArea.setCaretPosition(0);
	            
	            JScrollPane listTempPane = new JScrollPane(listTempArea);
	    		
	            layout.replace(jScrollPaneList, listTempPane);
	            textAreaList = listTempArea;
	            jScrollPaneList = listTempPane;		
	            
	            
	    		// Repaint area with search
	            if (!stats.getText().isEmpty()) {
					JTextArea statTempArea = new JTextArea();
					statTempArea.setColumns(15);
					statTempArea.setLineWrap(true);
					statTempArea.setRows(5);
					statTempArea.setWrapStyleWord(true);
					statTempArea.setEditable(false);
					
					statTempArea.setText(this.getStatsInfoMessage());
					statTempArea.setCaretPosition(0);
					statTempArea.setHighlighter(hilit);
					
		            JScrollPane statTempPane = new JScrollPane(statTempArea);
		    		
		            layout.replace(jScrollPaneStats, statTempPane);
		            textAreaStats = statTempArea;
		            jScrollPaneStats = statTempPane;
	            }
    		} else {
    			repaintAll();
    		}
    	}
    }
    
    public void repaintAll() {
		this.removeAll();

		this.initComponents(1);

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
        
        String content = textAreaStats.getText().toUpperCase();
        
        int startPoint;
        if (restart)
        	startPoint = 0;
        else 
        	startPoint = textAreaStats.getCaretPosition();

        int index = content.indexOf(s, startPoint);
        
        if (index >= 0) {   // match found
            try {
                int end = index + s.length();
                
                hilit.removeAllHighlights();
                hilit.addHighlight(index, end, painter);
                
                textAreaStats.setCaretPosition(end);
                entry.setBackground(entryBg);
                
                message("'" + s + "' found.    Press ENTER to find next occurance.    Press ESC to end search.");
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        } else {
            entry.setBackground(ERROR_COLOR);
            
            textAreaStats.setCaretPosition(0);
            
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