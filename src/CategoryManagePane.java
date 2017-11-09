import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.GroupLayout.*;


public class CategoryManagePane extends JPanel implements DocumentListener {
    
	MainFrame parent;
	
    Category selectedCat;    
    MenuItem selectedDish;	

	GroupLayout layout;
	
    
    private JLabel catItem; private JComboBox catCombo; private JButton catButton;
    private JLabel dishItem; private JComboBox dishCombo; private JButton dishButton;
    
    private JButton addSoldCatButton;
    private JButton addUnsoldCatButton;    
    
    private JLabel selection, orLabel, itemsHeader, addHeader, removeHeader, changes;
    
    private JLabel resultingItem; JComboBox resultingCombo;
    private JLabel resultingRemoveItem; JComboBox resultingRemoveCombo;
    private JButton addItem, removeItem;
    
    private JTextArea textArea; private JScrollPane jScrollPane;
    
    
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

    
    public CategoryManagePane(MainFrame parent, String selectedCat, String selectedDishName) {

    	setUp(parent, selectedCat, selectedDishName);
    	initComponents();
    }
    
    
    public void setUp(MainFrame parent, String selectedCat, String selectedDishName) {
    	
    	this.parent = parent;
    	
    	if (selectedCat != null)
    		this.selectedCat = parent.categories.get(selectedCat);
    	else
    		this.selectedCat = null;

    	if (selectedDishName != null)
    		this.selectedDish = parent.itemHash.get(selectedDishName);
    	else 
    		this.selectedDish = null;
    }
    
    private void initComponents() {
    	//Initialize JObjects
        selection = new JLabel("Choose a Category or Dish");
    	selection.setFont(new Font("Dialog", Font.BOLD, 27));
    	
        orLabel = new JLabel("OR"); 
        orLabel.setFont(new Font("Dialog", Font.BOLD, 17));
    	        
    	
    	if (selectedCat != null)
    		changes = new JLabel("Selected Category:  " + selectedCat.getName().toUpperCase()); 
    	else if (selectedDish != null)
    		changes = new JLabel("Selected Dish:  " + selectedDish.getName()); 
    	else
    		changes = new JLabel("Selected Item:  N/A        ~~~~~~~~~>      (Pick a Category or Dish Above to Modify its Category Information)"); 
    		
    	changes.setFont(new Font("Dialog", Font.BOLD, 27));

    	addHeader = new JLabel("Add:  "); 
    	addHeader.setFont(new Font("Dialog", Font.BOLD, 18));
    	
    	removeHeader = new JLabel("Remove:  ");
    	removeHeader.setFont(new Font("Dialog", Font.BOLD, 18));
    	
    	
        if (selectedCat != null)
        	itemsHeader = new JLabel("Dishes Associated with " + selectedCat.getName() + " (Category)");
        else if (selectedDish != null)
        	itemsHeader = new JLabel("Categories Associated with " + selectedDish.getName() + " (Dish)");
        else
        	itemsHeader = new JLabel("Associated Categories/Dishes");

       	itemsHeader.setFont(new Font("Dialog", Font.BOLD, 27));
    	
        catItem = new JLabel(); catButton = new JButton();
    	dishItem = new JLabel(); dishButton = new JButton();	
    	           
    	addSoldCatButton = new JButton();
    	addUnsoldCatButton = new JButton();

    	
        addItem = new JButton();
        removeItem = new JButton();
        
        resultingItem = new JLabel(); 
        resultingRemoveItem = new JLabel();
        
        searchLabel = new JLabel("Enter Text to Search:");
        entry = new JTextField();
        
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
        
        // Create two main groups
        layout = new GroupLayout(this);
        this.setLayout(layout);
        
		ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

        // Populate top group with appropriate data
        catItem.setText("Select Category:    ");
        
        ArrayList<String> temp = (ArrayList<String>)parent.categoriesList.clone();
    	temp.remove(Category.ENTIRE_MENU);
    	temp.remove(Category.UNSOLD_DISHES);
        
    	temp.add(0, "SELECT");
    	
        catCombo = new JComboBox(temp.toArray());
        
        if (selectedCat != null)
        	catCombo.setSelectedItem(selectedCat.getName());
        else
        	catCombo.setSelectedIndex(0);
        
        catButton.setText("Replace  (Selected Item Below)");
        catButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String comboString = (String) catCombo.getSelectedItem();
				
				if (!comboString.equals("SELECT")) {
					if (!(selectedCat != null && selectedCat.getName().equals(comboString))) {
												
						boolean continueReplace = updateChanges();
						if (continueReplace) {
							boolean repaintAll = false;
							if (selectedCat == null && selectedDish == null)
								repaintAll = true;
							
							String undoMsg;
							if (repaintAll)
								undoMsg = "Selected Item in \"Category Management\" tab is now not set";
							else if (selectedCat != null)
								undoMsg = "Selected Item in \"Category Management\" tab is set back to the " + selectedCat.getName() + " category";
							else 
								undoMsg = "Selected Item in \"Category Management\" tab is set back to the " + selectedDish.getName() + " dish";
							
							selectedCat = parent.categories.get(comboString);
							selectedDish = null;
							
							dishCombo.setSelectedIndex(0);
							
							changes.setText("Selected Category:  " + selectedCat.getName().toUpperCase()); 
							resultingItem.setText("Possible Dishes to Add to " + selectedCat.getName() + " (Category):                 ");
							resultingRemoveItem.setText("Possible Dishes to Remove from " + selectedCat.getName() + " (Category):    ");
							itemsHeader.setText("Dishes Associated with " + selectedCat.getName() + " (Category)");
							
							parent.updateUndoRedoStack(undoMsg, "Selected Item in \"Category Management\" tab changed again to the " + comboString + " category");
							
							if (repaintAll)
								repaintAll();
							else
								repaint(false, true, true, true, true);
						}
					}
				}
			}
		} );
        
        // Create groups for category line
		SequentialGroup hCatLine = layout.createSequentialGroup();
		
		hCatLine.addComponent(catItem);
		hCatLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hCatLine.addComponent(catCombo, GroupLayout.DEFAULT_SIZE, 650, 750);
		hCatLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hCatLine.addComponent(catButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		
		ParallelGroup vCatLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vCatLine.addComponent(catItem, GroupLayout.PREFERRED_SIZE, 35, 55);
		vCatLine.addComponent(catCombo, GroupLayout.PREFERRED_SIZE, 35, 55);
		vCatLine.addComponent(catButton, GroupLayout.PREFERRED_SIZE, 35, 55);
		
        // Populate top group with appropriate data        
        dishItem.setText("Select Menu Dish:  ");
		
		ArrayList<String> tempDishList = new ArrayList<String>();
		for (String soldDish : parent.categories.get(Category.ENTIRE_MENU).getDishes()) {
			MainFrame.InsertIntoList(tempDishList, soldDish);
		}
		for (String unsoldDish : parent.categories.get(Category.UNSOLD_DISHES).getDishes()) {
			MainFrame.InsertIntoList(tempDishList, unsoldDish);
		}		
		tempDishList.add(0, "SELECT");

		dishCombo = new JComboBox(tempDishList.toArray());
        
        if (selectedDish != null)
        	dishCombo.setSelectedItem(selectedDish.getName());
        else
        	dishCombo.setSelectedIndex(0);
        
        dishButton.setText("Replace  (Selected Item Below)");
        dishButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String comboString = (String) dishCombo.getSelectedItem();

				if (!comboString.equals("SELECT")) {
					if (!(selectedDish != null && selectedDish.getName().equals(comboString))) {
						
						boolean continueReplace = updateChanges();
 						if (continueReplace) {
							boolean repaintAll = false;
							if (selectedCat == null && selectedDish == null)
								repaintAll = true;
							
							String undoMsg;
							if (repaintAll)
								undoMsg = "Selected Item in \"Category Management\" tab is now not set";
							else if (selectedCat != null)
								undoMsg = "Selected Item in \"Category Management\" tab is set back to the " + selectedCat.getName() + " category";
							else 
								undoMsg = "Selected Item in \"Category Management\" tab is set back to the " + selectedDish.getName() + " dish";
								
							selectedCat = null;
							selectedDish = parent.itemHash.get(comboString);
					
							catCombo.setSelectedIndex(0);
							
							changes.setText("Selected Dish:  " + selectedDish.getName().toUpperCase()); 
							resultingItem.setText("Possible Categories to Add to " + selectedDish.getName() + " (Dish):                 ");
							resultingRemoveItem.setText("Possible Categories to Remove from " + selectedDish.getName() + " (Dish):    ");
							itemsHeader.setText("Categories Associated with " + selectedDish.getName() + " (Dish)");
							
							parent.updateUndoRedoStack(undoMsg, "Selected Item in \"Category Management\" tab changed again to " + comboString + " dish");
							
							if (repaintAll)
								repaintAll();
							else
								repaint(false, false, true, true, true);
 						}
					}
				}
			}
        } );
				
        // Create groups for item line
		SequentialGroup hItemLine = layout.createSequentialGroup();
		
		hItemLine.addComponent(dishItem);
		hItemLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hItemLine.addComponent(dishCombo, GroupLayout.DEFAULT_SIZE, 650, 750);
		hItemLine.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hItemLine.addComponent(dishButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		
		ParallelGroup vItemLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vItemLine.addComponent(dishItem, GroupLayout.PREFERRED_SIZE, 35, 55);
		vItemLine.addComponent(dishCombo, GroupLayout.PREFERRED_SIZE, 35, 55);
		vItemLine.addComponent(dishButton, GroupLayout.PREFERRED_SIZE, 35, 55);
		
		
		//Set up add-remove layout
		addSoldCatButton.setText("Add Category of SOLD Dishes to Restaurant...");
		addSoldCatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            		
        		addCategory(true);
            }   	
        } );
		
		//Set up add-remove layout
		addUnsoldCatButton.setText("Add Category of UNSOLD Dishes to Restaurant...");
		addUnsoldCatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            		
        		addCategory(false);
            }   	
        } );
		
		SequentialGroup hAddCat = layout.createSequentialGroup();

		hAddCat.addComponent(addSoldCatButton, 35, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hAddCat.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hAddCat.addComponent(addUnsoldCatButton, 35, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		
		ParallelGroup vAddCat = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vAddCat.addComponent(addSoldCatButton, GroupLayout.PREFERRED_SIZE,  45, 60);
		vAddCat.addComponent(addUnsoldCatButton, GroupLayout.PREFERRED_SIZE,  45, 60);
		
		// Set-up resulting item and category combo box
		if (selectedCat != null) {
			resultingItem.setText("Possible Dishes to Add to " + selectedCat.getName() + " (Category):                 ");
			
			ArrayList<String> tempMenu = null;
			if (selectedCat.isSold())
				tempMenu = (ArrayList<String>) parent.categories.get(Category.ENTIRE_MENU).getDishes().clone();
			else
				tempMenu = (ArrayList<String>) parent.categories.get(Category.UNSOLD_DISHES).getDishes().clone();				
			
			for (String dish : selectedCat.getDishes()) {
				if (tempMenu.contains(dish))
					tempMenu.remove(dish);
			}
			
			tempMenu.add(0, "SELECT");
			
			resultingCombo = new JComboBox(tempMenu.toArray());
			resultingCombo.setSelectedIndex(0);
		} else if (selectedDish != null) {
			resultingItem.setText("Possible Categories to Add to " + selectedDish.getName() + " (Dish):                 ");
			
			ArrayList<String> tempCats = (ArrayList<String>) parent.categoriesList.clone();
			
			boolean eliminateSold = false;
			if (selectedDish.IsSoldDish()) {
				tempCats.remove(Category.ENTIRE_MENU);
				eliminateSold = false;
			} else { 
				tempCats.remove(Category.UNSOLD_DISHES);
				eliminateSold = true;
			}
				
			for (Category cat : parent.categories.values()) {
				if (cat.isSold() == eliminateSold)
					tempCats.remove(cat.getName());
			}
			
			for (String dishCategory : selectedDish.getCategories()) {
				if (tempCats.contains(dishCategory))
					tempCats.remove(dishCategory);
			}
			
			tempCats.add(0, "SELECT");
			
			resultingCombo = new JComboBox(tempCats.toArray());
			resultingCombo.setSelectedIndex(0);
		}
		
		addItem.setText("Add Dish to Category...   ");
		
        //Create groups
		ParallelGroup hResultingLine = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		SequentialGroup vResultingLine = layout.createSequentialGroup();

		if ((selectedCat != null) || (selectedDish != null)) {
			//Create action listener for resulting combo
			addItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	
	            	addToCatAction();
	            }
			} );
			
			SequentialGroup hResultingLine1 = layout.createSequentialGroup();
	
			hResultingLine1.addComponent(resultingItem);
			hResultingLine1.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
			hResultingLine1.addComponent(resultingCombo, GroupLayout.DEFAULT_SIZE,  650, 750);
			hResultingLine1.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
			hResultingLine1.addComponent(addItem, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE);
			
			SequentialGroup hResultingLine2 = layout.createSequentialGroup();
			
			hResultingLine2.addComponent(addHeader, GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE);		
			
			
			ParallelGroup vResultingLine1 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
	
			vResultingLine1.addComponent(resultingItem, GroupLayout.PREFERRED_SIZE, 35, 55);
			vResultingLine1.addComponent(resultingCombo, GroupLayout.PREFERRED_SIZE, 35, 55);
			vResultingLine1.addComponent(addItem, GroupLayout.PREFERRED_SIZE, 35, 55);
			
			ParallelGroup vResultingLine2 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
	
			vResultingLine2.addComponent(addHeader, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);	
			
			
			hResultingLine.addGroup(hResultingLine2);
			hResultingLine.addGroup(hResultingLine1);
			
			vResultingLine.addGroup(vResultingLine2);
			vResultingLine.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
			vResultingLine.addGroup(vResultingLine1);
		} else {
			JLabel blankLabel1 = new JLabel();
			
			hResultingLine.addComponent(blankLabel1, GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE);
			vResultingLine.addComponent(blankLabel1, GroupLayout.PREFERRED_SIZE, 35, 45);
		}
		
		
		// Set-up resulting remove item and category combo box
		if (selectedCat != null) {
			resultingRemoveItem.setText("Possible Dishes to Remove from " + selectedCat.getName() + " (Category):  ");
			
			ArrayList<String> tempMenu = (ArrayList<String>) selectedCat.getDishes().clone();	
			tempMenu.add(0, "SELECT");
			
			resultingRemoveCombo = new JComboBox(tempMenu.toArray());
			resultingRemoveCombo.setSelectedIndex(0);
		} else if (selectedDish != null) {
			resultingRemoveItem.setText("Possible Categories to Remove from " + selectedDish.getName() + " (Dish):  ");
			
			ArrayList<String> tempCats = (ArrayList<String>) selectedDish.getCategories().clone();
			if (selectedDish.IsSoldDish())
				tempCats.remove(Category.ENTIRE_MENU);
			else
				tempCats.remove(Category.UNSOLD_DISHES);
	    	
			tempCats.add(0, "SELECT");
			resultingRemoveCombo = new JComboBox(tempCats.toArray());
			resultingRemoveCombo.setSelectedIndex(0);
		}
		
		removeItem.setText("Remove Dish from Category...");
		
       //Create groups
		ParallelGroup hResultingRemoveLine = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		SequentialGroup vResultingRemoveLine = layout.createSequentialGroup();

		if ((selectedCat != null) || (selectedDish != null)) {
			//Create action listener
			removeItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	
	            	removeFromCatAction();
	            }
			} );
			
			SequentialGroup hResultingRemoveLine1 = layout.createSequentialGroup();
	
			hResultingRemoveLine1.addComponent(resultingRemoveItem);
			hResultingRemoveLine1.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
			hResultingRemoveLine1.addComponent(resultingRemoveCombo, GroupLayout.DEFAULT_SIZE,  650, 750);
			hResultingRemoveLine1.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
			hResultingRemoveLine1.addComponent(removeItem, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE);
			
			SequentialGroup hResultingRemoveLine2 = layout.createSequentialGroup();
			
			hResultingRemoveLine2.addComponent(removeHeader, GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE);		
			
			
			ParallelGroup vResultingRemoveLine1 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
	
			vResultingRemoveLine1.addComponent(resultingRemoveItem, GroupLayout.PREFERRED_SIZE, 35, 55);
			vResultingRemoveLine1.addComponent(resultingRemoveCombo, GroupLayout.PREFERRED_SIZE, 35, 55);
			vResultingRemoveLine1.addComponent(removeItem, GroupLayout.PREFERRED_SIZE, 35, 50);
			
			ParallelGroup vResultingRemoveLine2 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
	
			vResultingRemoveLine2.addComponent(removeHeader, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);	
			
			
			hResultingLine.addGroup(hResultingRemoveLine2);
			hResultingLine.addGroup(hResultingRemoveLine1);
			
			vResultingRemoveLine.addGroup(vResultingRemoveLine2);
			vResultingRemoveLine.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
			vResultingRemoveLine.addGroup(vResultingRemoveLine1);
		} else {
			JLabel blankLabel2 = new JLabel();
			
			hResultingLine.addComponent(blankLabel2, GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE);
			vResultingLine.addComponent(blankLabel2, GroupLayout.PREFERRED_SIZE, 35, 45);
		}
		
		
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
		vJScrollPaneGroup.addComponent(jScrollPane, 300, 300, Short.MAX_VALUE);
		vJScrollPaneGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vJScrollPaneGroup.addComponent(status);

		
		//Set up tab horizontal layout
		
		ParallelGroup hRealGroup = layout.createParallelGroup();
		hRealGroup.addComponent(selection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);				
		hRealGroup.addGroup(hCatLine);
		hRealGroup.addComponent(orLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addGroup(hItemLine);
		hRealGroup.addGroup(hAddCat);
		hRealGroup.addComponent(changes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);				
		hRealGroup.addGroup(hResultingLine);
		hRealGroup.addGroup(hResultingRemoveLine);
		hRealGroup.addComponent(itemsHeader, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addGroup(hJScrollPaneGroup);
		
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
		vFinalGroup.addGap(15);
		vFinalGroup.addGroup(vCatLine);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addComponent(orLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addGroup(vItemLine);
		vFinalGroup.addGap(24);
		vFinalGroup.addGroup(vAddCat);
		vFinalGroup.addGap(27);
		vFinalGroup.addComponent(changes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addGap(15);
		vFinalGroup.addGroup(vResultingLine);
		vFinalGroup.addGap(16);
		vFinalGroup.addGroup(vResultingRemoveLine);
		vFinalGroup.addGap(27);
		vFinalGroup.addComponent(itemsHeader, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
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
    
    
    public MenuItem getSelectedDish(){
    	return selectedDish;
    }
    
    public void setSelectedDish(MenuItem newDish){
    	selectedDish = newDish;
    }
    
    public void addToCatAction() {
		String comboString = (String) resultingCombo.getSelectedItem();

		if (!comboString.equals("SELECT")) {
			
			String undoMsg = null;
			String redoMsg = null;
			if (selectedCat != null) {
					
				selectedCat.addDish(comboString);
				parent.itemHash.get(comboString).addCategory(selectedCat.getName());
				
				undoMsg = comboString + " removed from the \"" + selectedCat.getName() + "\" category";
				redoMsg = comboString + " added to the \"" + selectedCat.getName() + "\" category";
			} else {
				
				parent.categories.get(comboString).addDish(selectedDish.getName());
				selectedDish.addCategory(comboString);
				
				undoMsg = selectedDish.getName() + " removed from the \"" + comboString + "\" category";
				redoMsg = selectedDish.getName() + " added to the \"" + comboString + "\" category";
			}
			
			
			if (parent.dishCompPane.getSelectedCat() != null) {
				if (selectedCat != null && parent.dishCompPane.getSelectedCat() != null
						&& parent.dishCompPane.getSelectedCat().getName().equals(selectedCat.getName()))
					parent.dishCompPane.repaint(false, true, false);
				else if (selectedCat == null && parent.dishCompPane.getSelectedCat() != null
						&& parent.dishCompPane.getSelectedCat().getName().equals(comboString))
					parent.dishCompPane.repaint(false, true, false);

			}
			
			repaint(false, false, true, true, true);
			if (parent.resultsPane.getSelectedCat() != null)
				parent.resultsPane.repaint(false, true, false);
		
			if (parent.isMsgOn)
        		JOptionPane.showMessageDialog(addItem, "  " + redoMsg + ".  ", "Update", JOptionPane.INFORMATION_MESSAGE);
			
			if (undoMsg != null)
				parent.updateUndoRedoStack(undoMsg, redoMsg);
		}
    }
    
    public void removeFromCatAction() {
		String comboString = (String) resultingRemoveCombo.getSelectedItem();

		String undoMsg = null;
		String redoMsg = null;
		if (!comboString.equals("SELECT")) {
			if (selectedCat != null) {
										
				selectedCat.removeDish(comboString);
				parent.itemHash.get(comboString).removeCategory(selectedCat.getName());
				
				undoMsg = comboString + " added  to the \"" + selectedCat.getName() + "\" category";
				redoMsg = comboString + " removed from the \"" + selectedCat.getName() + "\" category";
			} else {
											
				parent.categories.get(comboString).removeDish(selectedDish.getName());
				selectedDish.removeCategory(comboString);
			
				undoMsg = selectedDish.getName() + " added to the \"" +  comboString + "\" category";
				redoMsg = selectedDish.getName() + " removed from the \"" + comboString + "\" category";
			}
			
			if (parent.dishCompPane.getSelectedCat() != null) {
				if (selectedCat != null && parent.dishCompPane.getSelectedCat() != null
						&& parent.dishCompPane.getSelectedCat().getName().equals(selectedCat.getName())) 
					parent.dishCompPane.repaint(false, true, false);
				else if (selectedCat == null && parent.dishCompPane.getSelectedCat() != null
						&& parent.dishCompPane.getSelectedCat().getName().equals(comboString))
					parent.dishCompPane.repaint(false, true, false);
			}
			
			if (parent.resultsPane.getSelectedCat() != null) {
				if (selectedCat != null && parent.resultsPane.getSelectedCat() != null
						&& parent.resultsPane.getSelectedCat().getName().equals(selectedCat.getName())) {
					if (parent.resultsPane.getSelectedDish() != null
							&& parent.resultsPane.getSelectedDish().getName().equals(comboString))
						parent.resultsPane.setSelectedDish(null);
				
					parent.resultsPane.repaint(false, true, false);
				} else if (selectedCat == null && parent.resultsPane.getSelectedCat() != null
						&& parent.resultsPane.getSelectedCat().getName().equals(comboString)) {
					if (parent.resultsPane.getSelectedDish() != null
							&& parent.resultsPane.getSelectedDish().getName().equals(selectedDish.getName()))
						parent.resultsPane.setSelectedDish(null);
				
					parent.resultsPane.repaint(false, true, false);
				}
			}
			
			repaint(false, false, true, true, true);
			
			if (parent.isMsgOn)
        		JOptionPane.showMessageDialog(removeItem, "  " + redoMsg + ".  ", "Update", JOptionPane.INFORMATION_MESSAGE);
			
			if (undoMsg != null)
				parent.updateUndoRedoStack(undoMsg, redoMsg);
		}	            						
    }
    
    public void addCategory(boolean isSold) {
    	addCategoryCopy(null, isSold);
    }
    
    public void addCategoryCopy(String oldCatName, boolean isSold) {
    	String title = null;	
    	String soldDescript = null;
		if (oldCatName != null) {
			title = "Category Copier";
			
			if (parent.categories.get(oldCatName).isSold())
				soldDescript = "of SOLD dishes";
			else
				soldDescript = "of UNSOLD dishes";
		} else {
			title = "Category Adder";
			
			if (isSold)
				soldDescript = "of SOLD dishes";
			else
				soldDescript = "of UNSOLD dishes";				
		}
		
    	SingleFieldDialog dialogAdd = new SingleFieldDialog(title, 
				"  What is the name of the new category " + soldDescript + " that you would like add to the program?  \n   \n", parent.categories.keySet(), true, false);
    	
		dialogAdd.pack();
		dialogAdd.setLocationRelativeTo(parent);
		dialogAdd.setVisible(true);
    	
    	String newCategoryName = dialogAdd.getValidatedText();
        if (newCategoryName != null && !newCategoryName.toUpperCase().equals("SELECT")) {
        	
        	newCategoryName = MainFrame.toFirstLettersUpper(newCategoryName);
        	
        	if (oldCatName != null)
        		parent.categories.put(newCategoryName, new Category(newCategoryName, parent.categories.get(oldCatName)));
        	else
        		parent.categories.put(newCategoryName, new Category(newCategoryName, isSold));
        	
        	if (oldCatName != null) {
	        	MenuItem tempItem = null;
	        	for (String itemName : parent.categories.get(newCategoryName).getDishes()) {
	        		tempItem = parent.itemHash.get(itemName);
	        		
	        		tempItem.addCategory(newCategoryName);
	        	}
        	}
        	
        	MainFrame.InsertIntoList(parent.categoriesList, newCategoryName);
        	
        	parent.dishCompPane.repaint(true, false, false);
        	this.repaint(true, false, true, false, true);
        	parent.resultsPane.repaint(true, false, false);
        	
        	if (oldCatName != null) {
				String finalMessage = "You have successfully copied the category \"" + oldCatName + 
										"'s\" dishes to a new category with its name being \"" + newCategoryName +"\"!         ";
				
	            parent.updateUndoRedoStack("Removed the copied category \"" + newCategoryName + "\" from the restaurant", "Added the copied category \"" + newCategoryName + "\" back to the restaurant");
	            
				JOptionPane.showMessageDialog(parent, finalMessage);
			} else {
				String catType = "";
				if (isSold)
					catType ="SOLD";
				else
					catType = "UNSOLD";
				
				parent.updateUndoRedoStack("Removed the " + catType + " category \"" + newCategoryName + "\" from the restaurant", "Added the " + catType + " category \"" + newCategoryName + "\" back to the restaurant");
				JOptionPane.showMessageDialog(parent, "You have successfully added \"" + newCategoryName + "\" as a " + catType + " category to the restaurant!         ");
			}
        }
    }
    
    public void removeCategory() {
    	
    	ArrayList<String> tempList = (ArrayList<String>) parent.categoriesList.clone();
    	tempList.remove(Category.ENTIRE_MENU);
    	tempList.remove(Category.UNSOLD_DISHES);
    	
        tempList.add(0, "SELECT");
        
    	Object [] possibilities = tempList.toArray();
        String catName = (String)JOptionPane.showInputDialog(
                parent, "Choose a Category to Remove:\n   \n",
                "Dish Remover",
                JOptionPane.PLAIN_MESSAGE,
                null, possibilities,
                possibilities[0]);
        
        if (catName != null && !catName.equals("SELECT")) {
        	ArrayList<String> itemList = parent.categories.get(catName).getDishes();
        	
        	MenuItem item = null;
        	for (String itemName : itemList) {
        		item = parent.itemHash.get(itemName);
        		
        		item.removeCategory(catName);
        	}
        	
        	parent.categories.remove(catName);
        	parent.categoriesList.remove(catName);
        	
        	if (parent.dishCompPane.getSelectedCat() != null 
        			&& parent.dishCompPane.getSelectedCat().getName().equals(catName))
        		parent.dishCompPane.setSelectedCat(null);
                	
        	parent.dishCompPane.repaint(true, true, false);
        	
        	if (selectedCat != null && selectedCat.getName().equals(catName)) {
        		selectedCat = null;
        		this.repaintAll();
        	} else {
        		this.repaint(true, false, true, true, true);
        	}
        	
        	if (parent.resultsPane.getSelectedCat() != null 
        			&& parent.resultsPane.getSelectedCat().getName().equals(catName))
        		parent.resultsPane.setSelectedCat(null);
        	
        	parent.resultsPane.repaintAll();
        	
            parent.updateUndoRedoStack("Added the category \"" + catName + "\" back to the restaurant", "Removed the category \"" + catName + "\" from the restaurant");
			JOptionPane.showMessageDialog(parent, "You have successfully removed the \"" + catName + "\" category from the restaurant!         ");
        }        
    }
    
    public String getInfoMessage() {
    	StringBuilder info = new StringBuilder("\n=== START ===\n\n\n");
    	
    	if (selectedCat != null) {
    		
    		info.append("CONTAINS THE FOLLOWING DISHES:\n\n\n");

    		if (selectedCat.getDishes().isEmpty()) {
    			info.append("-None-\n"); 
    			
    		} else {
	    		for (String dish : selectedCat.getDishes())
	    				info.append((dish + "\n"));
    		}
    	} else if (selectedDish != null) {
    		info.append("FOUND IN THE FOLLOWING CATEGORIES:\n\n\n");

    		if (selectedDish.getCategories().isEmpty()) {
    			info.append("-None-\n"); 
    			
    		} else {
    			for (String category : selectedDish.getCategories())
	    			info.append((category + "\n"));
    		}
    	} else {
    		info.append("\n");
    	}
    	
    	info.append("\n\n=== END ===");
    	
    	return info.toString();
    } 
    
    public void repaint(boolean catLine, boolean dishLine, boolean addDishLine, boolean removeDishLine, boolean infoArea) {
    	if (catLine) {
			ArrayList<String> tempCats = (ArrayList<String>) parent.categoriesList.clone();
	    	tempCats.remove(Category.ENTIRE_MENU);
	    	tempCats.remove(Category.UNSOLD_DISHES);
	    	
			tempCats.add(0, "SELECT");
			JComboBox tempReplaceCats = new JComboBox(tempCats.toArray());

            String selectedCatName = (String) catCombo.getSelectedItem();
            if (tempCats.contains(selectedCatName))
            	tempReplaceCats.setSelectedItem(selectedCatName);
			
			layout.replace(catCombo, tempReplaceCats);
			catCombo = tempReplaceCats;
    	}
    	
    	if (dishLine) {
    		ArrayList<String> tempDishes = new ArrayList<String>();
    		for (String soldDish : parent.categories.get(Category.ENTIRE_MENU).getDishes()) {
    			MainFrame.InsertIntoList(tempDishes, soldDish);
    		}
    		for (String unsoldDish : parent.categories.get(Category.UNSOLD_DISHES).getDishes()) {
    			MainFrame.InsertIntoList(tempDishes, unsoldDish);
    		}		
    		tempDishes.add(0, "SELECT");

    		JComboBox tempReplaceDishes = new JComboBox(tempDishes.toArray());

            String selectedDishName = (String) catCombo.getSelectedItem();
            if (tempDishes.contains(selectedDishName))
            	tempReplaceDishes.setSelectedItem(selectedDishName);
			
			layout.replace(dishCombo, tempReplaceDishes);
			dishCombo = tempReplaceDishes;
    	}
    	
    	if (addDishLine) {
    		if ((selectedCat != null) || (selectedDish != null)) {
	    		
    			ArrayList<String> tempResulting = new ArrayList<String>();
	    		if (selectedCat != null) {
	    			tempResulting = null;
	    			if (selectedCat.isSold())
	    				tempResulting = (ArrayList<String>) parent.categories.get(Category.ENTIRE_MENU).getDishes().clone();
	    			else
	    				tempResulting = (ArrayList<String>) parent.categories.get(Category.UNSOLD_DISHES).getDishes().clone();				
				
	    			for (String dish : selectedCat.getDishes()) {
	    				if (tempResulting.contains(dish))
	    					tempResulting.remove(dish);
	    			}
	
	    		} else if (selectedDish != null) {
	    			
	    			tempResulting = (ArrayList<String>) parent.categoriesList.clone();
	    			
	    			boolean eliminateSold = false;
	    			if (selectedDish.IsSoldDish()) {
	    				tempResulting.remove(Category.ENTIRE_MENU);
	    				eliminateSold = false;
	    			} else { 
	    				tempResulting.remove(Category.UNSOLD_DISHES);
	    				eliminateSold = true;
	    			}
	    				
	    			for (Category cat : parent.categories.values()) {
	    				if (cat.isSold() == eliminateSold)
	    					tempResulting.remove(cat.getName());
	    			}
	    			
	    			for (String dishCategory : selectedDish.getCategories()) {
	    				if (tempResulting.contains(dishCategory))
	    					tempResulting.remove(dishCategory);
	    			}	    					
	    		}
	    			
	    		tempResulting.add(0, "SELECT");
				JComboBox tempReplaceAddDish = new JComboBox(tempResulting.toArray());
				tempReplaceAddDish.setSelectedIndex(0);
				
				layout.replace(resultingCombo, tempReplaceAddDish);
				resultingCombo = tempReplaceAddDish;
    		}
    	} 
    	
    	if (removeDishLine) {
    		if ((selectedCat != null) || (selectedDish != null)) {

	    		ArrayList<String> tempRemoveResulting = new ArrayList<String>();	
	    		if (selectedCat != null) {
	    			tempRemoveResulting = (ArrayList<String>) selectedCat.getDishes().clone();
	    		} else if (selectedDish != null) {
	    			tempRemoveResulting = (ArrayList<String>) selectedDish.getCategories().clone();			
	    			if (selectedDish.IsSoldDish())
	    				tempRemoveResulting.remove(Category.ENTIRE_MENU);
	    			else
	    				tempRemoveResulting.remove(Category.UNSOLD_DISHES);
	    		}
	    		
	    		tempRemoveResulting.add(0, "SELECT");
				JComboBox tempReplaceRemoveDish = new JComboBox(tempRemoveResulting.toArray());
				tempReplaceRemoveDish.setSelectedIndex(0);
				
				layout.replace(resultingRemoveCombo, tempReplaceRemoveDish);
				resultingRemoveCombo = tempReplaceRemoveDish;
    		}
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
    
    public boolean updateChanges() {
    	boolean continueReplace = true;
    	
		ArrayList<String> unSavedItems = areAnyChanges();
		if (unSavedItems != null) {
			String question = null;							
			
        	String addString = unSavedItems.get(0);
        	if (addString != null) {
        		if (selectedCat != null)
        			question = "You forgot to add " + addString + " to the \"" + selectedCat.getName() + "\" category";
        		else
        			question = "You forgot to add " + selectedDish.getName() + " to the \"" + addString + "\" category";
        	}
        	
        	String removeString = unSavedItems.get(1);
        	if (removeString != null) {
        		if (question == null)
        			question = "You forgot to ";
        		else
        			question += ", and forgot to ";
        			
        		if (selectedCat != null)
        			question += "remove " + removeString + " from the \"" + selectedCat.getName() + "\" category";
        		else
        			question += "remove " + selectedDish.getName() + " from the \"" + removeString + "\" category";
        	}
        	
        	question += ".\nWould you like to update the restaurant to reflect these changes before replacing the selected item?\n    \n";
       
    		int confirm = JOptionPane.showConfirmDialog(resultingCombo, question, "Changes Made", JOptionPane.YES_NO_OPTION);
    		if (confirm == JOptionPane.YES_OPTION) {
    			continueReplace = true;
    			
    			if (addString != null)
    				addToCatAction();
    			
    			if (removeString != null)
    				removeFromCatAction();
    				
        	} else if (confirm == JOptionPane.NO_OPTION) {
            	continueReplace = true;
        	} else {
        		continueReplace = false;
        	}		                	
		}
		
		return continueReplace;
    }
    
    public ArrayList<String> areAnyChanges() {
    	ArrayList<String> returnList = null;
    	
    	String selectedItem = null;
    	if (selectedCat != null || selectedDish != null) {
    		if (!("SELECT").equals((String)resultingCombo.getSelectedItem()) || !("SELECT").equals((String)resultingRemoveCombo.getSelectedItem())) {
    			returnList = new ArrayList<String>();
    			
	    		selectedItem = (String) resultingCombo.getSelectedItem();
	    		if (!selectedItem.equals("SELECT"))
	    			returnList.add(selectedItem);
	    		else
	    			returnList.add(null);
	    		
	    		selectedItem = (String) resultingRemoveCombo.getSelectedItem();
	    		if (!selectedItem.equals("SELECT"))
	    			returnList.add(selectedItem);
				else
	    			returnList.add(null);
    		}
    	}
    	
    	return returnList;
    	
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