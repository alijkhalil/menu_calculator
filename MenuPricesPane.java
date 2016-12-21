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
import java.util.HashMap;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.GroupLayout.*;

public class MenuPricesPane extends JPanel{
    
    HashMap<String, MenuItem> itemHash;
    
	ArrayList<String> categories; //All categories name
    HashMap<String, Category> catHash; //Map to category items
    Category selectedCat;
    
    MenuItem selectedDish;
  	
	
    private JLabel refreshItem; private JComboBox refreshCombo; private JButton refreshButton;
    private JLabel replaceItem; private JComboBox replaceCombo; private JButton replaceButton;
    
    private JTextArea textArea; private JScrollPane jScrollPane;
    private JLabel catItems, selection, itemName;
    
    private JLabel priceLabel;
    private JTextField priceNum;
    
    private JButton updateButton;
  
    final public static String[] UNITS = {"SELECT (units)", "Liters", "Gallons", "Kilos", "Pounds"};
    
    public MenuPricesPane(HashMap<String, MenuItem> itemHash, ArrayList<String> categories, HashMap<String, Category> catMap,
    		String selectedCat, String selectedName) {
    	this.itemHash = itemHash;
    	
    	this.categories = categories;
    	this.catHash = catMap;

    	if (selectedCat != null)
    		this.selectedCat = catHash.get(selectedCat);
    	else
    		this.selectedCat = null;

    	if (selectedDish != null)
    		this.selectedDish = itemHash.get(selectedName);
    	else
    		this.selectedDish = null;    	
    	
    	initComponents();
	}
    
    /** This method is called from within the constructor to
     * initialize the form.
     */

    private void initComponents() {
    	//Initialize JObjects
    	catItems = new JLabel("Category's Prices"); 
    	catItems.setFont(new Font("Dialog", Font.BOLD, 20));
    	
    	selection = new JLabel("Item Selection (from Category Above)"); 
    	selection.setFont(new Font("Dialog", Font.BOLD, 20));
    	
    	if (selectedDish != null)
    		itemName = new JLabel(selectedDish.getName()); 
    	else
    		itemName = new JLabel("Pick an Item!");
    	
    	itemName.setFont(new Font("Dialog", Font.BOLD, 20));
    	
        refreshItem = new JLabel(); refreshButton = new JButton();
        
        textArea = new JTextArea();
        textArea.setColumns(15);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(true); //CHANGE LATER TO FALSE
        jScrollPane = new JScrollPane(textArea);
        
    	replaceItem = new JLabel(); replaceButton = new JButton();
    	
    	priceLabel = new JLabel();
    	priceNum = new JTextField();	
    	
    	updateButton = new JButton("UPDATE");
        
        // Create two main groups
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        
		ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

        // Populate top group with appropriate data
        refreshItem.setText("Select Category:  ");
        
        ArrayList<String> temp = (ArrayList<String>)categories.clone();
        temp.add(0, "SELECT");
        refreshCombo = new JComboBox(temp.toArray());

        if (selectedCat != null)
        	refreshCombo.setSelectedItem(selectedCat.getName());
        else
        	refreshCombo.setSelectedIndex(0);
        
        refreshButton.setText("Refresh");
        
        // Create groups for category line
		SequentialGroup hCatLine = layout.createSequentialGroup();
		
		hCatLine.addComponent(refreshItem);
		hCatLine.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hCatLine.addComponent(refreshCombo, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE);
		hCatLine.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hCatLine.addComponent(refreshButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		
		ParallelGroup vCatLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vCatLine.addComponent(refreshItem, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vCatLine.addComponent(refreshCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vCatLine.addComponent(refreshButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);		
		
        // Populate top group with appropriate data
        replaceItem.setText("Select Menu Item:");
        
		ArrayList<String> tempItems = new ArrayList<String>();
		
		tempItems.add("SELECT");
		if (selectedCat != null)
    		tempItems.addAll((ArrayList<String>)selectedCat.getDishes().clone());

		replaceCombo = new JComboBox(tempItems.toArray());
		if (selectedCat != null && selectedDish != null)
			replaceCombo.setSelectedItem(selectedDish.getName());
		else
        	replaceCombo.setSelectedIndex(0);
        
        replaceButton.setText("Replace");
        
        // Create groups for item line
		SequentialGroup hItemLine = layout.createSequentialGroup();
		
		hItemLine.addComponent(replaceItem);
		hItemLine.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hItemLine.addComponent(replaceCombo, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE);
		hItemLine.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hItemLine.addComponent(replaceButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		
		ParallelGroup vItemLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vItemLine.addComponent(replaceItem, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vItemLine.addComponent(replaceCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vItemLine.addComponent(replaceButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);

        // Populate batch group with appropriate data
       if (selectedDish != null) {
			priceLabel.setText(selectedDish.getName() + " Price:    $ ");
			priceNum.setText(selectedDish.getPrice().toString());
       } else {
			priceLabel.setText("Item Price:    $ ");
       }
        
        // Create groups for batch line
		SequentialGroup hPriceLine = layout.createSequentialGroup();
		
		hPriceLine.addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		hPriceLine.addComponent(priceNum, GroupLayout.DEFAULT_SIZE, 100, 100);
		
		ParallelGroup vPriceLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vPriceLine.addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vPriceLine.addComponent(priceNum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		
		//Set up tab hortizontal layout
		
		ParallelGroup hRealGroup = layout.createParallelGroup();
		hRealGroup.addComponent(catItems, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addGroup(hCatLine);
		hRealGroup.addComponent(jScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addComponent(selection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);				
		hRealGroup.addGroup(hItemLine);
		hRealGroup.addComponent(itemName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addGroup(hPriceLine);
		hRealGroup.addComponent(updateButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		
		SequentialGroup hFinalGroup = layout.createSequentialGroup();
		hFinalGroup.addContainerGap();
		hFinalGroup.addGroup(hRealGroup);
		hFinalGroup.addContainerGap();
		
		hGroup.addGroup(GroupLayout.Alignment.TRAILING, hFinalGroup);
		layout.setHorizontalGroup(hGroup);
		
		//Set up tab vertical layout
		
		SequentialGroup vFinalGroup = layout.createSequentialGroup();
		vFinalGroup.addContainerGap();
		vFinalGroup.addComponent(catItems, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addGroup(vCatLine);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addComponent(jScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);		
		vFinalGroup.addGap(15);
		vFinalGroup.addComponent(selection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addGroup(vItemLine);
		vFinalGroup.addGap(15);
		vFinalGroup.addComponent(itemName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addGroup(vPriceLine);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addComponent(updateButton, GroupLayout.PREFERRED_SIZE, 40, 40);
		vFinalGroup.addContainerGap();
		
		vGroup.addGroup(vFinalGroup);
		layout.setVerticalGroup(vGroup);
    }
}