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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.GroupLayout.*;


public class IngredientPricing extends JPanel implements DocumentListener {
	
	final static String BUTTON_ONE = new String("ONE");
	final static String BUTTON_TWO = new String("TWO");
	final static String BUTTON_THREE = new String("THREE");
	
	final static String EMPTY_PRICE = new String("              ( PRICE )");
	final static String EMPTY_QUANTITY = new String("           ( NUMBER OF UNITS )");
	
	
	MainFrame parent;
	
	ArrayList<String> ingredientList;	
    HashMap<String, MenuItem> itemHash;
    
    MenuItem selected, displayed1, displayed2, displayed3;
    Integer radioPlace; //Base 0
    
    
    GroupLayout layout;
    
    private JLabel replaceItem; private JComboBox replaceCombo; private JButton replaceButton;
    private JLabel selection, itemName, errors;    
    
    public ButtonGroup radioGroup;
    private JRadioButton radio1, radio2, radio3;
    private JLabel dollarLabel1, perLabel1, unitLabel1, dollarLabel2, perLabel2, unitLabel2, dollarLabel3, perLabel3, unitLabel3; 
    
    private JTextField price1, price2, price3;
    private JTextField quantity1, quantity2, quantity3;    
    private JComboBox unitCombo1; private JComboBox unitCombo2; private JComboBox unitCombo3;

    private String initPrice1, initPrice2, initPrice3;
    private String initQuantity1, initQuantity2, initQuantity3;
    private String initCombo1, initCombo2, initCombo3;
  
           
    private JButton addItem, updateButton;
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
    
    
    public IngredientPricing(MainFrame parent, String selected, Integer radioPlace, String displayed1, String displayed2, String displayed3) {
		
    	setUp(parent, selected, radioPlace, displayed1, displayed2, displayed3);
    	initComponents(0);
    }
    
    
    public void setUp(MainFrame parent, String selected, Integer radioPlace, String displayed1, String displayed2, String displayed3) {
    
    	this.parent = parent;
    	
    	this.itemHash = parent.itemHash;
    	this.ingredientList = parent.ingredientList;
    	
    	if (selected != null)
    		this.selected = itemHash.get(selected);
    	else
    		this.selected = null;
    	
    	if (radioPlace != null)
    		this.radioPlace = radioPlace;
    	else
    		this.radioPlace = 0;
    			
		if (displayed1 != null)
			this.displayed1 = itemHash.get(displayed1);
		else
			this.displayed1 = null;

		if (displayed2 != null)
			this.displayed2 = itemHash.get(displayed2);
		else
			this.displayed2 = null;
		
		if (displayed3 != null)
			this.displayed3 = itemHash.get(displayed3);
		else
			this.displayed3 = null;
    }
    
    private void initComponents(int keepUnchanged) {
    	
    	//Initialize JObjects
        selection = new JLabel("Choose Ingredients"); 
    	selection.setFont(new Font("Dialog", Font.BOLD, 27));
    	
    	itemName = new JLabel("Selected Ingredient Details"); 
    	itemName.setFont(new Font("Dialog", Font.BOLD, 27));
    	
    	errors = new JLabel("Errors"); 
    	errors.setFont(new Font("Dialog", Font.BOLD, 27));      

    	replaceItem = new JLabel(); replaceButton = new JButton();
        
    	radioGroup = new ButtonGroup();
        radio1 = new JRadioButton(); radio2 = new JRadioButton(); radio3 = new JRadioButton();
        dollarLabel1 = new JLabel(); perLabel1 = new JLabel(); 
        dollarLabel2 = new JLabel(); perLabel2 = new JLabel();
        dollarLabel3 = new JLabel(); perLabel3 = new JLabel();
        
        if (keepUnchanged == 0) {
	        price1 = new JTextField();
	        price2 = new JTextField(); 
	        price3 = new JTextField();
	        initPrice1 = price1.getText();
	        initPrice2 = price2.getText();
	        initPrice3 = price3.getText();
	        
	        quantity1 = new JTextField();
	        quantity2 = new JTextField();
	        quantity3 = new JTextField();
	        initQuantity1 = quantity1.getText();
	        initQuantity2 = quantity2.getText();
	        initQuantity3 = quantity3.getText();
	        
	        unitCombo1 = new JComboBox(MenuCompositionPane.INGREDIENT_UNITS);
	        unitCombo2 = new JComboBox(MenuCompositionPane.INGREDIENT_UNITS);
	        unitCombo3 = new JComboBox(MenuCompositionPane.INGREDIENT_UNITS);
	        initCombo1 = (String) unitCombo1.getSelectedItem();
	        initCombo2 = (String) unitCombo2.getSelectedItem();
	        initCombo3 = (String) unitCombo3.getSelectedItem();
        }
        
        unitLabel1 = new JLabel();
        unitLabel2 = new JLabel();
        unitLabel3 = new JLabel();
        
        addItem = new JButton();
        
        updateButton = new JButton("UPDATE INFORMATION FOR ALL INGREDIENTS ABOVE");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            		//Perform update action
            		boolean success = updateAction();
            		
        			if (success && parent.isMsgOn)
                		JOptionPane.showMessageDialog(updateButton, "   You just updated the information used to calculate ingredient prices.   ", "Update", JOptionPane.INFORMATION_MESSAGE);

               }
        } );
        
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
        replaceItem.setText("Select Ingredient:");
        
        ArrayList<String> temp = (ArrayList<String>)ingredientList.clone();
        if (displayed1 != null)
        	temp.remove(displayed1.getName());
        if (displayed2 != null)
        	temp.remove(displayed2.getName());
        if (displayed3 != null)
        	temp.remove(displayed3.getName());
        
        temp.add(0, "SELECT");
        replaceCombo = new JComboBox(temp.toArray());
        
        if (keepUnchanged == 0) {
	        if (selected != null)
	        	replaceCombo.setSelectedItem(selected.getName());
	        else
	        	replaceCombo.setSelectedIndex(0);
        }
        
        replaceButton.setText("Replace  (Selected Ingredient Below)");
        replaceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
         		boolean continueReplace = false;
         		
         		String buttonNumber = radioGroup.getSelection().getActionCommand();
         		if (replaceCombo.getSelectedIndex() != 0) {
	            	//Check if an update desired before replacing
	            	if (buttonNumber == BUTTON_ONE && displayed1 != null) {
	            		if (areFirstRowChanges()) {
	            			continueReplace = true;
	            		}
	            	} else if (buttonNumber == BUTTON_TWO && displayed2 != null) {
	            		if (areSecondRowChanges()) {
	            			continueReplace = true;	
	            		}
	            	} else {
	            		if (areThirdRowChanges() && displayed3 != null) {
	            			continueReplace = true;
	            		}            		
	            	} 
	            	
	            	if (continueReplace) {
	                	String question = "There have been unsaved changes to ingredient that you are replacing below.\n" + 
								"Do you want to update the restaruant to reflect the changes below before replacing the ingredient?\n   \n";
	
	            		int confirm = JOptionPane.showConfirmDialog(addItem, question, "Changes Made", JOptionPane.YES_NO_OPTION);
	                	if (confirm == JOptionPane.YES_OPTION) {
	                    	continueReplace = true;
	                    	
	                    	//Update every change in list of ingredients below
	                    	updateAction();
	                	} else if (confirm == JOptionPane.NO_OPTION) {
	                    	continueReplace = true;
	                
	                	} else {
	                		continueReplace = false;
	                	}
	            	} else {
	            		continueReplace = true;
	            	}
         		}
         		
            	if (continueReplace) {
            		// Initialize variables needed for replacement
            		String insertedItem = (String) replaceCombo.getSelectedItem();
            		MenuItem newIngredient = itemHash.get(insertedItem);

            		// Set temp replacement variables
            	    JRadioButton tempRadio = new JRadioButton(newIngredient.getName());
            	    radioGroup.add(tempRadio);

            	    tempRadio.setSelected(true);
            	    tempRadio.setActionCommand(buttonNumber);
        			
            	    JTextField tempPrice = new JTextField();
        	        if (newIngredient.getCostField() != null)
        	        	tempPrice.setText(newIngredient.getCostField().toString());
        	        else
        	        	tempPrice.setText(EMPTY_PRICE);
        	        	
            	    JTextField tempQuantity = new JTextField();
        	        if (newIngredient.getPerFigure() != null)
        	        	tempQuantity.setText(newIngredient.getPerFigure().toString());
        	        else
        	        	tempQuantity.setText(EMPTY_QUANTITY);
  
        	        JComboBox tempCombo;
            		if (buttonNumber == BUTTON_ONE)
                	    tempCombo = unitCombo1;            	        
            		else if (buttonNumber == BUTTON_TWO)	
            			tempCombo = unitCombo2;
            		else 
            			tempCombo = unitCombo3;

        	        if (newIngredient.getUnits() != null)
        	        	tempCombo.setSelectedItem(newIngredient.getUnits());
        	        else
        	        	tempCombo.setSelectedIndex(0);
        	        
        	        JLabel tempUnitLabel = new JLabel("of " + newIngredient.getName().toLowerCase() +".");
        	        
            	    // Update row according to selection
            		if (buttonNumber == BUTTON_ONE) {
            			displayed1 = newIngredient;
            			
            			radioGroup.remove(radio1);
            			layout.replace(radio1, tempRadio);
            			radio1 = tempRadio;
            			
            			layout.replace(price1, tempPrice);
            			price1 = tempPrice;
            			initPrice1 = price1.getText();
            			
            			layout.replace(quantity1, tempQuantity);
            			quantity1 = tempQuantity;
            			initQuantity1 = quantity1.getText();
            			
            			layout.replace(unitCombo1, tempCombo);
            			unitCombo1 = tempCombo;
            			initCombo1 = (String) unitCombo1.getSelectedItem();
            			
            			layout.replace(unitLabel1, tempUnitLabel);
            			unitLabel1 = tempUnitLabel;
            			
            		} else if (buttonNumber == BUTTON_TWO) {
            			displayed2 = newIngredient;
            			
            			radioGroup.remove(radio2);
            			layout.replace(radio2, tempRadio);
            			radio2 = tempRadio;
            			
            			layout.replace(price2, tempPrice);
            			price2 = tempPrice;
            			initPrice2 = price2.getText();
            			
            			layout.replace(quantity2, tempQuantity);
            			quantity2 = tempQuantity;
            			initQuantity2 = quantity2.getText();
            			
            			layout.replace(unitCombo2, tempCombo);
            			unitCombo2 = tempCombo;
            			initCombo2 = (String) unitCombo2.getSelectedItem();
            			
            			layout.replace(unitLabel2, tempUnitLabel);
            			unitLabel2 = tempUnitLabel;
            			
            		} else {
            			displayed3 = newIngredient;
            			
            			radioGroup.remove(radio3);
            			layout.replace(radio3, tempRadio);
            			radio3 = tempRadio;
            			
            			layout.replace(price3, tempPrice);
            			price3 = tempPrice;
            			initPrice3 = price3.getText();
            			
            			layout.replace(quantity3, tempQuantity);
            			quantity3 = tempQuantity;
            			initQuantity3 = quantity3.getText();
            			
            			layout.replace(unitCombo3, tempCombo);
            			unitCombo3 = tempCombo;
            			initCombo3 = (String) unitCombo3.getSelectedItem();
            			
            			layout.replace(unitLabel3, tempUnitLabel);
            			unitLabel3 = tempUnitLabel;
            		}
            		
            		repaint(true, false, false);
            		
            		parent.updateUndoRedoStack(insertedItem + " taken off the list of 3 ingredients on \"Ingredient Prices\" tab", insertedItem + " put back on the list of 3 ingredients on \"Enter the Cost of Ingredients\" tab");
            	}
            }
        } );
        
        // Create groups for top line
		SequentialGroup hTopLine = layout.createSequentialGroup();
		
		hTopLine.addComponent(replaceItem);
		hTopLine.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hTopLine.addComponent(replaceCombo, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE);
		hTopLine.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hTopLine.addComponent(replaceButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		
		ParallelGroup vTopLine = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vTopLine.addComponent(replaceItem, GroupLayout.PREFERRED_SIZE, 35, 55);
		vTopLine.addComponent(replaceCombo, GroupLayout.PREFERRED_SIZE, 35, 55);
		vTopLine.addComponent(replaceButton, GroupLayout.PREFERRED_SIZE, 35, 55);

		// Populate Radio Area
		radio1.setActionCommand(BUTTON_ONE);
		if (radioPlace == 0)
			radio1.setSelected(true);
		
		if (displayed1 != null) {
			radio1.setText(displayed1.getName());  

			if (keepUnchanged == 0) {
		        if (displayed1.getCostField() != null) {
		        	price1.setText(displayed1.getCostField().toString());
		        } else {
		        	price1.setText(EMPTY_PRICE);
		        }
		        initPrice1 = price1.getText();
		        
		        if (displayed1.getPerFigure() != null) {
		        	quantity1.setText(displayed1.getPerFigure().toString());
		        } else {
		        	quantity1.setText(EMPTY_QUANTITY);
		        }
		        initQuantity1 = quantity1.getText();
		        
		        
		        if (displayed1.getUnits() != null) {
		        	unitCombo1.setSelectedItem(displayed1.getUnits());
		        } else {
		        	unitCombo1.setSelectedIndex(0);
		        }
		        initCombo1 = (String) unitCombo1.getSelectedItem();
		        
			}
		} else {
	        radio1.setText(" PICK AN INGREDIENT ABOVE"); //Make parameter determine this and create
	        unitCombo1.setSelectedIndex(0);
		}

		radio2.setActionCommand(BUTTON_TWO);		
		if (radioPlace == 1)
			radio2.setSelected(true);

		if (displayed2 != null) {
			radio2.setText(displayed2.getName());
			
			if (keepUnchanged == 0) {
		        if (displayed2.getCostField() != null) {
		        	price2.setText(displayed2.getCostField().toString());
		        } else {
		        	price2.setText(EMPTY_PRICE);
		        }
		        initPrice2 = price2.getText();
		        
		        if (displayed2.getPerFigure() != null) {
		        	quantity2.setText(displayed2.getPerFigure().toString());
		        } else {
		        	quantity2.setText(EMPTY_QUANTITY);
		        }
		        initQuantity2 = quantity2.getText();
		        
		        if (displayed2.getUnits() != null) {
		        	unitCombo2.setSelectedItem(displayed2.getUnits());
		        } else {
		        	unitCombo2.setSelectedIndex(0);
		        }
		        initCombo2 = (String) unitCombo2.getSelectedItem();
			}
		} else {
	        radio2.setText(" PICK AN INGREDIENT ABOVE"); //Make parameter determine this and create
	        unitCombo2.setSelectedIndex(0);
		}
		
		radio3.setActionCommand(BUTTON_THREE);
		if (radioPlace == 2)
			radio3.setSelected(true);
		
		if (displayed3 != null) {
			radio3.setText(displayed3.getName());
			
			if (keepUnchanged == 0) {
		        if (displayed3.getCostField() != null) {
		        	price3.setText(displayed3.getCostField().toString());
		        } else {
		        	price3.setText(EMPTY_PRICE);
		        }
		        initPrice3 = price3.getText();
		        
		        if (displayed3.getPerFigure() != null) {
		        	quantity3.setText(displayed3.getPerFigure().toString());
		        } else {
		        	quantity3.setText(EMPTY_QUANTITY);
		        }
		        initQuantity3 = quantity3.getText();
		        
		        if (displayed3.getUnits() != null) {
		        	unitCombo3.setSelectedItem(displayed3.getUnits());
		        } else {
		        	unitCombo3.setSelectedIndex(0);
		        }
		        initCombo3 = (String) unitCombo3.getSelectedItem();
			}
		} else {
	        radio3.setText(" PICK AN INGREDIENT ABOVE"); //Make parameter determine this and create
	        unitCombo3.setSelectedIndex(0);
		}
        
        radioGroup.add(radio1);
        radioGroup.add(radio2);
        radioGroup.add(radio3);
        
        dollarLabel1.setText("LL");
        dollarLabel2.setText("LL");
        dollarLabel3.setText("LL");
        
        perLabel1.setText("per");
        perLabel2.setText("per");
        perLabel3.setText("per");
        
        unitLabel1.setText("of XXXXXXX.");
        unitLabel2.setText("of XXXXXXX.");
        unitLabel3.setText("of XXXXXXX.");
        
        if (displayed1 != null)
        	unitLabel1.setText("of " + displayed1.getName().toLowerCase() +".");
        if (displayed2 != null)
        	unitLabel2.setText("of " + displayed2.getName().toLowerCase() +".");
        if (displayed3 != null)	
        	unitLabel3.setText("of " + displayed3.getName().toLowerCase() +".");
        
        // Set layout for middle group
		SequentialGroup hRadio1 = layout.createSequentialGroup();
		
		hRadio1.addComponent(dollarLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		hRadio1.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hRadio1.addComponent(price1, GroupLayout.DEFAULT_SIZE, 150, 150);
		hRadio1.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hRadio1.addComponent(perLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		hRadio1.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hRadio1.addComponent(quantity1, GroupLayout.DEFAULT_SIZE, 200, 200);
		hRadio1.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hRadio1.addComponent(unitCombo1, GroupLayout.DEFAULT_SIZE, 250, 250);
		hRadio1.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hRadio1.addComponent(unitLabel1, GroupLayout.DEFAULT_SIZE, 150, 350);

		SequentialGroup hRadio2 = layout.createSequentialGroup();		
		
		hRadio2.addComponent(dollarLabel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		hRadio2.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hRadio2.addComponent(price2, GroupLayout.DEFAULT_SIZE, 150, 150);
		hRadio2.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hRadio2.addComponent(perLabel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		hRadio2.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hRadio2.addComponent(quantity2, GroupLayout.DEFAULT_SIZE, 200, 200);
		hRadio2.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hRadio2.addComponent(unitCombo2, GroupLayout.DEFAULT_SIZE, 250, 250);
		hRadio2.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hRadio2.addComponent(unitLabel2, GroupLayout.DEFAULT_SIZE, 150, 350);
		
		SequentialGroup hRadio3 = layout.createSequentialGroup();		
		
		hRadio3.addComponent(dollarLabel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		hRadio3.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hRadio3.addComponent(price3, GroupLayout.DEFAULT_SIZE, 150, 150);
		hRadio3.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hRadio3.addComponent(perLabel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		hRadio3.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hRadio3.addComponent(quantity3, GroupLayout.DEFAULT_SIZE, 200, 200);
		hRadio3.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		hRadio3.addComponent(unitCombo3, GroupLayout.DEFAULT_SIZE, 250, 250);
		hRadio3.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		hRadio3.addComponent(unitLabel3, GroupLayout.DEFAULT_SIZE, 150, 350);


		//Overall radio hortizontal group
		ParallelGroup hRadio = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		hRadio.addComponent(radio1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRadio.addGroup(hRadio1);
		hRadio.addComponent(radio2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRadio.addGroup(hRadio2);
		hRadio.addComponent(radio3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRadio.addGroup(hRadio3);
		
		//Vertical Side
		ParallelGroup vRadio1 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		
		vRadio1.addComponent(dollarLabel1, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio1.addComponent(price1, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio1.addComponent(perLabel1, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio1.addComponent(quantity1, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio1.addComponent(unitCombo1, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio1.addComponent(unitLabel1, GroupLayout.PREFERRED_SIZE, 22, 30);

		ParallelGroup vRadio2 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		
		vRadio2.addComponent(dollarLabel2, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio2.addComponent(price2, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio2.addComponent(perLabel2, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio2.addComponent(quantity2, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio2.addComponent(unitCombo2, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio2.addComponent(unitLabel2, GroupLayout.PREFERRED_SIZE, 22, 30);

		
		ParallelGroup vRadio3 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		vRadio3.addComponent(dollarLabel3, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio3.addComponent(price3, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio3.addComponent(perLabel3, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio3.addComponent(quantity3, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio3.addComponent(unitCombo3, GroupLayout.PREFERRED_SIZE, 22, 30);
		vRadio3.addComponent(unitLabel3, GroupLayout.PREFERRED_SIZE, 22, 30);

		
		//Overall radio vertical group
		SequentialGroup vRadio = layout.createSequentialGroup();

		vRadio.addComponent(radio1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vRadio.addGap(7);
		vRadio.addGroup(vRadio1);
		vRadio.addGap(12);
		vRadio.addComponent(radio2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vRadio.addGap(7);
		vRadio.addGroup(vRadio2);
		vRadio.addGap(12);
		vRadio.addComponent(radio3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vRadio.addGap(7);
		vRadio.addGroup(vRadio3);		

		
		//Add-Remove Button populate restaurant
        addItem.setText("Add Ingredient to Restaurant...");
        addItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            		
            		addIngredientItem();
                }   	
            } );
        
        
		//Set up add-remove layout
		SequentialGroup hAddRemove = layout.createSequentialGroup();
		
		hAddRemove.addComponent(addItem, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

		ParallelGroup vAddRemove = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		
		vAddRemove.addComponent(addItem, GroupLayout.PREFERRED_SIZE,  50, 57);
		
		
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
		
		
		//Set up tab hortizontal layout
		
		ParallelGroup hRealGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		hRealGroup.addComponent(selection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);				
		hRealGroup.addGroup(hTopLine);
		hRealGroup.addGroup(hAddRemove);
		hRealGroup.addComponent(itemName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);				
		hRealGroup.addGroup(hRadio);
		hRealGroup.addComponent(updateButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		hRealGroup.addComponent(errors, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
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
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addGroup(vTopLine);
		vFinalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
		vFinalGroup.addGroup(vAddRemove);
		vFinalGroup.addGap(24);
		vFinalGroup.addComponent(itemName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addGap(12);
		vFinalGroup.addGroup(vRadio);
		vFinalGroup.addGap(14);
		vFinalGroup.addComponent(updateButton, GroupLayout.PREFERRED_SIZE, 52, 70);
		vFinalGroup.addGap(24);
		vFinalGroup.addComponent(errors, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vFinalGroup.addGap(12);
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
        
        //Reset radioButton
        radioPlace = null;
    }
    
	public void addIngredientItem() {
		addIngredientCopy(null);
	}
    
	public void addIngredientCopy(String oldIngredient) {
		String title = null;	
		if (oldIngredient != null)
			title = "Ingredient Copier";
		else
			title = "Ingredient Adder";
		
     	SingleFieldDialog dialogAdd = new SingleFieldDialog(title, 
				"What is the name of the ingredient that you would like to add to the restaurant?\n   \n", itemHash.keySet(), false, false);

		dialogAdd.pack();
		dialogAdd.setLocationRelativeTo(updateButton);
		dialogAdd.setVisible(true);
		
		String newIngredientName = dialogAdd.getValidatedText();
		if (newIngredientName != null && !newIngredientName.toUpperCase().equals("SELECT")) {
			
			newIngredientName = MainFrame.toFirstLettersUpper(newIngredientName);
			
			if (oldIngredient != null)
				itemHash.put(newIngredientName, new Ingredient(newIngredientName, itemHash.get(oldIngredient)));
			else
				itemHash.put(newIngredientName, new Ingredient(newIngredientName));
			
			MainFrame.InsertIntoList(ingredientList, newIngredientName);
			
			repaint(true, false, true);
			if (parent.itemUsagePane.selectedType != null && parent.itemUsagePane.selectedType.equals(IngredientInfoPane.itemTypes[1]))
				parent.itemUsagePane.repaint(true, false);
			
			if (oldIngredient != null) {
				
				parent.updateUndoRedoStack("Removed the copied ingredient \"" + newIngredientName + "\" from the restaurant", 
											"Copied the ingredient \"" + oldIngredient + "'s\" info into a new ingredient with its name being \"" + newIngredientName +"\"");
				
				String finalMessage = "You have successfully copied the ingredient \"" + oldIngredient + 
										"'s\" info into a new ingredient with its name being \"" + newIngredientName +"\"!         ";
				JOptionPane.showMessageDialog(parent, finalMessage);
			} else {
				
				parent.updateUndoRedoStack("Removed the ingredient \"" + newIngredientName + "\" from the restaurant", 
						"Added the ingredient \"" + newIngredientName + "\" to the restaurant");
				
				JOptionPane.showMessageDialog(parent, "You have successfully added the \"" + newIngredientName + "\" ingredient to the restaurant!         ");
			}
		}
	}

	
	public void removeIngredientItem() {
    	ArrayList<String> tempList = (ArrayList<String>) ingredientList.clone();

        tempList.add(0, "SELECT");
    	for (String item : ingredientList) {
    		MenuItem tempItem = itemHash.get(item);
    		
    		if (!tempItem.getUses().isEmpty())
    			tempList.remove(item);            		            			
    	}

    	Object [] possibilities = tempList.toArray();
        String removeIngredient = (String)JOptionPane.showInputDialog(
                            parent,
                            "Choose an Ingredient to Remove:\n   \n",
                            "Ingredient Remover",
                            JOptionPane.PLAIN_MESSAGE,
                            null, possibilities,
                            possibilities[0]);
    	
        if (removeIngredient != null && !removeIngredient.equals("SELECT")) {
        	itemHash.remove(removeIngredient);
        	ingredientList.remove(removeIngredient);
        	
        	boolean anyChanged = false;
        	if (!anyChanged && displayed1 != null) {
        		if (removeIngredient.equals(displayed1.getName())) {
        			displayed1 = null;
        			anyChanged = true;
        		}
        	} 
        	
        	if (!anyChanged && displayed2 != null) {
        		if (removeIngredient.equals(displayed2.getName())) {
        			displayed2 = null;
        			anyChanged = true;
        		}
        	}
        	
        	if (!anyChanged && displayed3 != null) {
        		if (removeIngredient.equals(displayed3.getName())) {
        			displayed3 = null;
        			anyChanged = true;
        		}
        	}
        	
        	// Repaint everything
        	repaint(true, anyChanged, true);
        	
        	if (parent.itemUsagePane.getSelectedItem() != null && parent.itemUsagePane.getSelectedItem().getName().equals(removeIngredient)) {
				parent.itemUsagePane.setSelectedItem(null);
				parent.itemUsagePane.repaintAll();
			} else if (parent.itemUsagePane.getSelectedType() != null && parent.itemUsagePane.getSelectedType().equals(IngredientInfoPane.itemTypes[1])) {
   				parent.itemUsagePane.repaint(true, false);
			}
        	 
            parent.updateUndoRedoStack("Added the ingredient \"" + removeIngredient + "\" back to the restaurant", "Removed the ingredient \"" + removeIngredient + "\" from the restaurant");			
			JOptionPane.showMessageDialog(parent, "You have successfully removed the \"" + removeIngredient + "\" ingredient from the restaurant!         ");
        }      
  	}

	public boolean updateAction() {
     	// Universal previous Double and String
    	Double prevValue = null;
    	String prevStringValue = null;
    	
    	boolean setError = false;
    	boolean setUnits = false;
    	boolean anyChange = false;
    	
    	boolean success = false;
    	if (displayed1 != null) {
    		// Price part
        	String tempInput1 = price1.getText();

        	if (tempInput1 == null)
        		tempInput1 = "";
        		
        	//Trim text
        	tempInput1 = tempInput1.replaceAll("^\\s+", "");
        	tempInput1 = tempInput1.replaceAll("\\s+$", "");

        	prevValue = displayed1.getCostField();
        	
        	Double tempPrice1 = null;
        	try {
    			tempPrice1 = Double.valueOf(tempInput1);
    			
        		if (prevValue == null && tempPrice1 >= 0) {
        			displayed1.setCostField(tempPrice1);
        			setError = true;
        		} else if (prevValue.compareTo(tempPrice1) != 0 && tempPrice1 >= 0) {
        			displayed1.setCostField(tempPrice1);
        			setError = true;
        		}

        	} catch (Exception except) {
        		if (prevValue != null)
        			if (tempInput1.isEmpty()) {
        				displayed1.setCostField(null);
        				setError = true;
        			}
        	}
        	
        	// Quantity part
        	tempInput1 = quantity1.getText();
        	
        	if (tempInput1 == null)
        		tempInput1 = "";
        	
        	//Trim text
        	tempInput1 = tempInput1.replaceAll("^\\s+", "");
        	tempInput1 = tempInput1.replaceAll("\\s+$", "");
        	
        	prevValue = displayed1.getPerFigure();
        	
        	Double tempQuantity1 = null;
        	try {
    			tempQuantity1 = Double.valueOf(tempInput1);
        		if (prevValue == null && tempQuantity1 > 0) {
        			displayed1.setPerFigure(tempQuantity1);
        			setError = true;
        		} else if (prevValue.compareTo(tempQuantity1) != 0 && tempQuantity1 > 0) {
        			displayed1.setPerFigure(tempQuantity1);
        			setError = true;
        		}

        	} catch (Exception except) {
        		if (prevValue != null)
        			if (tempInput1.isEmpty()) {
        				displayed1.setPerFigure(null);
        				setError = true;
        			}
        	}

        	// Unit part
        	tempInput1 = (String) unitCombo1.getSelectedItem();
        	prevStringValue = displayed1.getUnits();
        	
        	if (prevStringValue != null) {
        		if (Arrays.asList(MenuCompositionPane.INGREDIENT_INVALID_ITEMS).contains(tempInput1)){
        			displayed1.setUnits(null);
        			setError = true;
        			setUnits = true;
        		} else if (!tempInput1.equals(prevStringValue)
        				&& !Arrays.asList(MenuCompositionPane.INGREDIENT_INVALID_ITEMS).contains(tempInput1)) {
    				displayed1.setUnits(tempInput1);
    				
					setError = true;        				
    				if (!((Arrays.asList(MenuCompositionPane.LIQUID_UNITS).contains(tempInput1) 
    						&& Arrays.asList(MenuCompositionPane.LIQUID_UNITS).contains(prevStringValue)) || 
    						(Arrays.asList(MenuCompositionPane.SOLID_UNITS).contains(tempInput1) 
            				&& Arrays.asList(MenuCompositionPane.SOLID_UNITS).contains(prevStringValue)) || 
            				(Arrays.asList(MenuCompositionPane.OTHER_UNITS).contains(tempInput1) 
                    				&& Arrays.asList(MenuCompositionPane.OTHER_UNITS).contains(prevStringValue))))
    					setUnits = true;
        		}
        	} else {
        		if (!Arrays.asList(MenuCompositionPane.INGREDIENT_INVALID_ITEMS).contains(tempInput1)) {
        			displayed1.setUnits(tempInput1);
        			setError = true;
        			setUnits = true;
        		}
        	}
    	}
    	
    	if (setError) {
    		displayed1.setErrorFlag();
    		anyChange = true;
    	}
    		
    	if(setUnits)
    		parent.dishCompPane.repaintNoUpdateFields(displayed1.getName());
    	
    	setError = false;
    	setUnits = false;
    	if (displayed2 != null) {
    		// Price part
        	String tempInput2 = price2.getText();
        	
        	if (tempInput2 == null)
        		tempInput2 = "";
        	
        	//Trim text
        	tempInput2 = tempInput2.replaceAll("^\\s+", "");
        	tempInput2 = tempInput2.replaceAll("\\s+$", "");

        	prevValue = displayed2.getCostField();
        	
        	Double tempPrice2 = null;
        	try {
    			tempPrice2 = Double.valueOf(tempInput2);
    			
        		if (prevValue == null && tempPrice2 >= 0) {
        			displayed2.setCostField(tempPrice2);
        			setError = true;
        		} else if (prevValue.compareTo(tempPrice2) != 0 && tempPrice2 >= 0) {
        			displayed2.setCostField(tempPrice2);
        			setError = true;
        		}

        	} catch (Exception except) {
        		if (prevValue != null)
        			if (tempInput2.isEmpty()) {
        				displayed2.setCostField(null);
        				setError = true;
        			}
        	}
        	
        	// Quantity part
        	tempInput2 = quantity2.getText();
        	
        	if (tempInput2 == null)
        		tempInput2 = "";
        	
        	//Trim text
        	tempInput2 = tempInput2.replaceAll("^\\s+", "");
        	tempInput2 = tempInput2.replaceAll("\\s+$", "");
        	
        	prevValue = displayed2.getPerFigure();
        	
        	Double tempQuantity2 = null;
        	try {
    			tempQuantity2 = Double.valueOf(tempInput2);
    			
        		if (prevValue == null && tempQuantity2 > 0) {
        			displayed2.setPerFigure(tempQuantity2);
        			setError = true;
        		} else if (prevValue.compareTo(tempQuantity2) != 0 && tempQuantity2 > 0) {
        			displayed2.setPerFigure(tempQuantity2);
        			setError = true;
        		}

        	} catch (Exception except) {
        		if (prevValue != null)
        			if (tempInput2.isEmpty()) {
        				displayed2.setPerFigure(null);
        				setError = true;
        			}
        	}

        	// Unit part
        	tempInput2 = (String) unitCombo2.getSelectedItem();
        	prevStringValue = displayed2.getUnits();
        	
        	if (prevStringValue != null) {
        		if (Arrays.asList(MenuCompositionPane.INGREDIENT_INVALID_ITEMS).contains(tempInput2)){
        			displayed1.setUnits(null);
        			setError = true;
        			setUnits = true;
        		} else if (!tempInput2.equals(prevStringValue)
        				&& !Arrays.asList(MenuCompositionPane.INGREDIENT_INVALID_ITEMS).contains(tempInput2)) {
    				displayed2.setUnits(tempInput2);   		
    				
					setError = true;
    				if (!((Arrays.asList(MenuCompositionPane.LIQUID_UNITS).contains(tempInput2) 
    						&& Arrays.asList(MenuCompositionPane.LIQUID_UNITS).contains(prevStringValue)) || 
    						(Arrays.asList(MenuCompositionPane.SOLID_UNITS).contains(tempInput2) 
            				&& Arrays.asList(MenuCompositionPane.SOLID_UNITS).contains(prevStringValue)) || 
            				(Arrays.asList(MenuCompositionPane.OTHER_UNITS).contains(tempInput2) 
                    				&& Arrays.asList(MenuCompositionPane.OTHER_UNITS).contains(prevStringValue))))
    					setUnits = true;
        		}
        	} else {
        		if (!Arrays.asList(MenuCompositionPane.INGREDIENT_INVALID_ITEMS).contains(tempInput2)) {
        			displayed2.setUnits(tempInput2);
        			setError = true;
        			setUnits = true;
        		}
        	}
    	}
    	
    	if (setError) {
    		displayed2.setErrorFlag();
    		anyChange = true;
    	}
    		
    	if(setUnits)
    		parent.dishCompPane.repaintNoUpdateFields(displayed2.getName());
    	
    	setError = false;
    	setUnits = false;
    	if (displayed3 != null) {
    		// Price part
        	String tempInput3 = price3.getText();
        	
        	if (tempInput3 == null)
        		tempInput3 = "";

        	//Trim text
        	tempInput3 = tempInput3.replaceAll("^\\s+", "");
        	tempInput3 = tempInput3.replaceAll("\\s+$", "");

        	prevValue = displayed3.getCostField();
        	
        	Double tempPrice3 = null;
        	try {
    			tempPrice3 = Double.valueOf(tempInput3);
        		
          		if (prevValue == null && tempPrice3 >= 0) {
        			displayed3.setCostField(tempPrice3);
        			setError = true;
        		} else if (prevValue.compareTo(tempPrice3) != 0 && tempPrice3 >= 0) {
        			displayed3.setCostField(tempPrice3);
        			setError = true;
        		}

        	} catch (Exception except) {
        		if (prevValue != null)
        			if (tempInput3.isEmpty()) {
        				displayed3.setCostField(null);
        				setError = true;
        			}
        	}
        	
        	// Quantity part
        	tempInput3 = quantity3.getText();
        	
        	if (tempInput3 == null)
        		tempInput3 = "";
        	
        	//Trim text
        	tempInput3 = tempInput3.replaceAll("^\\s+", "");
        	tempInput3 = tempInput3.replaceAll("\\s+$", "");
        	
        	prevValue = displayed3.getPerFigure();
        	
        	Double tempQuantity3 = null;
        	try {
    			tempQuantity3 = Double.valueOf(tempInput3);
        		
          		if (prevValue == null && tempQuantity3 > 0) {
          			displayed3.setPerFigure(tempQuantity3);
        			setError = true;
        		} else if (prevValue.compareTo(tempQuantity3) != 0 && tempQuantity3 > 0) {
        			displayed3.setPerFigure(tempQuantity3);
        			setError = true;
        		}

        	} catch (Exception except) {
        		if (prevValue != null)
        			if (tempInput3.isEmpty()) {
        				displayed3.setPerFigure(null);
        				setError = true;
        			}
        	}

        	// Unit part
        	tempInput3 = (String) unitCombo3.getSelectedItem();
        	prevStringValue = displayed3.getUnits();
        	
        	if (prevStringValue != null) {
        		if (Arrays.asList(MenuCompositionPane.INGREDIENT_INVALID_ITEMS).contains(tempInput3)){
        			displayed3.setUnits(null);
        			setError = true;
        			setUnits = true;
        		} else if (!tempInput3.equals(prevStringValue)
        				&& !Arrays.asList(MenuCompositionPane.INGREDIENT_INVALID_ITEMS).contains(tempInput3)) {
    				displayed3.setUnits(tempInput3);   		
    				
    				setError = true;
    				if (!((Arrays.asList(MenuCompositionPane.LIQUID_UNITS).contains(tempInput3) 
    						&& Arrays.asList(MenuCompositionPane.LIQUID_UNITS).contains(prevStringValue)) || 
    						(Arrays.asList(MenuCompositionPane.SOLID_UNITS).contains(tempInput3) 
            				&& Arrays.asList(MenuCompositionPane.SOLID_UNITS).contains(prevStringValue)) || 
            				(Arrays.asList(MenuCompositionPane.OTHER_UNITS).contains(tempInput3) 
                    				&& Arrays.asList(MenuCompositionPane.OTHER_UNITS).contains(prevStringValue))))
    					setUnits = true;
        		}
        	} else {
        		if (!Arrays.asList(MenuCompositionPane.INGREDIENT_INVALID_ITEMS).contains(tempInput3)) {
        			displayed3.setUnits(tempInput3);
        			setError = true;
        			setUnits = true;
        		}
        	}
    	}
    	
    	if (setError) {
    		displayed3.setErrorFlag();
    		anyChange = true;
    	}
    		
    	if (setUnits)
    		parent.dishCompPane.repaintNoUpdateFields(displayed3.getName());
    	
    	
    	if (anyChange) {
    		
    		//Repaint tabs
        	repaint(false, true, true);
    		parent.dishCompPane.repaint(false, false, true);
        	parent.itemUsagePane.repaint(true, true);
        	parent.resultsPane.repaint(false, false, true);
        	
    		parent.updateUndoRedoStack("UNDID Ingredient Information Update", "REDID Ingredient Information Update");
    		
    		success = true;
    	}
    	
    	return success;
	}
	
    public String getErrorMessage() {
		StringBuilder errors = new StringBuilder("\n=== START === \n\n\n");
		
		MenuItem item;
		boolean anyIssues; 
		
		int counter = 0;
		for (String ingredient : ingredientList) {
			item = itemHash.get(ingredient);
			anyIssues = false;
			
			//Put header for item
			errors.append((item.getName().toUpperCase() + ":\n\n"));
			
			//Item Info
			errors.append("Cost:   ");
			if (item.getCostField() != null) {
				errors.append("LL " + MainFrame.toStringWithXDemical(item.getCostField(), 0) + "\n");
			} else {
				errors.append("N/A\n");
				anyIssues = true;
			}
			
			errors.append("Per:   ");
			if (item.getPerFigure() != null) {
				errors.append(MainFrame.toStringWithXDemical(item.getPerFigure(), 2) + "\n");
			} else {
				errors.append("N/A\n");
				anyIssues = true;
			}
				
			errors.append("Units:   ");
			if (item.getUnits() != null) {
				errors.append(item.getUnits() + "\n");
			} else {
				errors.append("N/A\n");
				anyIssues = true;
			}
				
			errors.append("\n");
			
			// Error Info
			if (anyIssues) {
				errors.append("ERROR:  There is an incomplete field!\n");
			} else {
				// Print success message
				errors.append("The overall cost is:   LL " + MainFrame.toStringWithXDemical(item.getCost(), 0) + " / " 
							+ (item.getUnits().replaceAll("s", "")).replaceAll("olid","solid") + "\n\n");
			}
				
			if (item.getUnusedWarning()) {
				errors.append("WARNING:  This ingredient is not used in any recipes!\n");
				anyIssues = true;
			}
			
			if (!anyIssues)
				errors.append("SUCCESS:  This dish has nothing wrong with it!\n");
			
			counter++;
			if (counter != ingredientList.size())
				errors.append("\n---\n\n");
		}
    	
		errors.append("\n\n=== END ===");
		
    	return errors.toString();
    }
    
    public void repaint(boolean selectionArea, boolean ingredientsDetails, boolean errorArea) {
    	
    	if (selectionArea) {
            ArrayList<String> temp = (ArrayList<String>)ingredientList.clone();
            temp.add(0, "SELECT");
            if (displayed1 != null)
            	temp.remove(displayed1.getName());
            if (displayed2 != null)
            	temp.remove(displayed2.getName());
            if (displayed3 != null)
            	temp.remove(displayed3.getName());

            JComboBox tempReplaceCombo = new JComboBox(temp.toArray());
            
            String selectedIngredient = (String) replaceCombo.getSelectedItem();
            if (temp.contains(selectedIngredient))
            	tempReplaceCombo.setSelectedItem(selectedIngredient);
            
            layout.replace(replaceCombo, tempReplaceCombo);
            replaceCombo = tempReplaceCombo;
    	}
    	
    	if (ingredientsDetails) {
    		// Initialize variables needed for replacement
     		String buttonNumber = radioGroup.getSelection().getActionCommand();
     		
     		if (radioPlace != null) {
     			if (radioPlace == 0)
     				buttonNumber = BUTTON_ONE;
     			else if (radioPlace == 1)
     				buttonNumber = BUTTON_TWO;
     			else
     				buttonNumber = BUTTON_THREE;
     		}
     			
     		//Item 1
	        boolean displayNotNull = true;
	        if (displayed1 == null)
	        	displayNotNull = false;
			
    	    JRadioButton tempRadio1 = new JRadioButton(" PICK AN INGREDIENT ABOVE");
    	    
    	    JTextField tempPrice1 = new JTextField();
    	    JTextField tempQuantity1 = new JTextField();
    	    
    	    JComboBox tempCombo1 = unitCombo1;
        	tempCombo1.setSelectedIndex(0);
        	
        	JLabel tempUnitLabel1 = new JLabel("of XXXXXXX.");
	        
    	    if (displayNotNull) {
    	    	tempRadio1.setText(displayed1.getName());
    	    	
    	    	if (displayed1.getCostField() != null)
    	    		tempPrice1.setText(displayed1.getCostField().toString());
       	        else
    	        	tempPrice1.setText(EMPTY_PRICE);
    	    	
    	        if (displayed1.getPerFigure() != null)
    	        	tempQuantity1.setText(displayed1.getPerFigure().toString());
    	        else
    	        	tempQuantity1.setText(EMPTY_QUANTITY);
    	        
    	        if (displayed1.getUnits() != null)
    	        	tempCombo1.setSelectedItem(displayed1.getUnits());
    	        
    	        tempUnitLabel1.setText("of " + displayed1.getName().toLowerCase() + ".");
    	    }
    	    
    	    radioGroup.add(tempRadio1);
    		if (buttonNumber.equals(BUTTON_ONE))
        	    tempRadio1.setSelected(true);
    	    tempRadio1.setActionCommand(radio1.getActionCommand());
			radioGroup.remove(radio1);
			
			layout.replace(radio1, tempRadio1);
			radio1 = tempRadio1;
    	    
			layout.replace(price1, tempPrice1);
			price1 = tempPrice1;
			initPrice1 = tempPrice1.getText();
			
			layout.replace(quantity1, tempQuantity1);
			quantity1 = tempQuantity1;
			initQuantity1 = tempQuantity1.getText();
			
			layout.replace(unitCombo1, tempCombo1);
			unitCombo1 = tempCombo1;
			initCombo1 = (String) tempCombo1.getSelectedItem();			
			
			layout.replace(unitLabel1, tempUnitLabel1);
			unitLabel1 = tempUnitLabel1;
			
			
			//Item 2
	        displayNotNull = true;
	        if (displayed2 == null)
	        	displayNotNull = false;
			
    	    JRadioButton tempRadio2 = new JRadioButton(" PICK AN INGREDIENT ABOVE");
    	    
    	    JTextField tempPrice2 = new JTextField();
    	    JTextField tempQuantity2 = new JTextField();
    	    
    	    JComboBox tempCombo2 = unitCombo2;
        	tempCombo2.setSelectedIndex(0);
	        
        	JLabel tempUnitLabel2 = new JLabel("of XXXXXXX.");
        	
    	    if (displayNotNull) {
    	    	tempRadio2.setText(displayed2.getName());
    	    	
    	    	if (displayed2.getCostField() != null)
    	    		tempPrice2.setText(displayed2.getCostField().toString());
       	        else
    	        	tempPrice2.setText(EMPTY_PRICE);
    	    	
    	        if (displayed2.getPerFigure() != null)
    	        	tempQuantity2.setText(displayed2.getPerFigure().toString());
       	        else
       	        	tempQuantity2.setText(EMPTY_QUANTITY);
    	        
    	        if (displayed2.getUnits() != null)
    	        	tempCombo2.setSelectedItem(displayed2.getUnits());
    	        
    	        tempUnitLabel2.setText("of " + displayed2.getName().toLowerCase() +".");
    	    }
    	    
    	    radioGroup.add(tempRadio2);
    		if (buttonNumber.equals(BUTTON_TWO))
        	    tempRadio2.setSelected(true);
    	    tempRadio2.setActionCommand(radio2.getActionCommand());
			radioGroup.remove(radio2);
			
			layout.replace(radio2, tempRadio2);
			radio2 = tempRadio2;
    	    
			layout.replace(price2, tempPrice2);
			price2 = tempPrice2;
			initPrice2 = tempPrice2.getText();
			
			layout.replace(quantity2, tempQuantity2);
			quantity2 = tempQuantity2;
			initQuantity2 = tempQuantity2.getText();
			
			layout.replace(unitCombo2, tempCombo2);
			unitCombo2 = tempCombo2;
			initCombo2 = (String) tempCombo2.getSelectedItem();			
			
			layout.replace(unitLabel2, tempUnitLabel2);
			unitLabel2 = tempUnitLabel2;
			
			
			//Item 3
	        displayNotNull = true;
	        if (displayed3 == null)
	        	displayNotNull = false;
			
    	    JRadioButton tempRadio3 = new JRadioButton(" PICK AN INGREDIENT ABOVE");
    	    
    	    JTextField tempPrice3 = new JTextField();
    	    JTextField tempQuantity3 = new JTextField();
    	    
    	    JComboBox tempCombo3 = unitCombo3;
        	tempCombo3.setSelectedIndex(0);
	        
        	JLabel tempUnitLabel3 = new JLabel("of XXXXXXX.");
        	
    	    if (displayNotNull) {
    	    	tempRadio3.setText(displayed3.getName());
    	    	
    	    	if (displayed3.getCostField() != null)
    	    		tempPrice3.setText(displayed3.getCostField().toString());
    	        else
    	        	tempPrice3.setText(EMPTY_PRICE);
    	    	
    	        if (displayed3.getPerFigure() != null)
    	        	tempQuantity3.setText(displayed3.getPerFigure().toString());
    	        else
    	        	tempQuantity3.setText(EMPTY_QUANTITY);
    	        
    	        if (displayed3.getUnits() != null)
    	        	tempCombo3.setSelectedItem(displayed3.getUnits());
    	        
    	        tempUnitLabel3.setText("of " + displayed3.getName().toLowerCase() +".");
    	    }
    	    
    	    radioGroup.add(tempRadio3);
    		if (buttonNumber.equals(BUTTON_THREE))
        	    tempRadio3.setSelected(true);
    	    tempRadio3.setActionCommand(radio3.getActionCommand());
			radioGroup.remove(radio3);
			
			layout.replace(radio3, tempRadio3);
			radio3 = tempRadio3;
    	    
			layout.replace(price3, tempPrice3);
			price3 = tempPrice3;
			initPrice3 = tempPrice3.getText();
			
			layout.replace(quantity3, tempQuantity3);
			quantity3 = tempQuantity3;
			initQuantity3 = tempQuantity3.getText();
			
			layout.replace(unitCombo3, tempCombo3);
			unitCombo3 = tempCombo3;
			initCombo3 = (String) tempCombo3.getSelectedItem();			
			
			layout.replace(unitLabel3, tempUnitLabel3);
			unitLabel3 = tempUnitLabel3;

			//Reset radioPlace
			radioPlace = null;
    	}
    	
    	if (errorArea) {
    		
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
    
    public void repaintAllWithNoFieldChanges() {
		this.removeAll();
		
		if (radioPlace == null) {
			String buttonNumber = radioGroup.getSelection().getActionCommand();
					
    		if (buttonNumber == IngredientPricing.BUTTON_ONE)
    			radioPlace = 0;            	        
    		else if (buttonNumber == IngredientPricing.BUTTON_TWO)	
    			radioPlace = 1;
    		else 
    			radioPlace = 2;
		}
		
		this.initComponents(1);

		this.validate();
		this.repaint();
    }
    
    public boolean areAnyChanges() {
    	
    	if (areFirstRowChanges() || areSecondRowChanges() || areThirdRowChanges())
    		return true;
    	else
    		return false;
    }

    private boolean areFirstRowChanges() {
		Double initDouble = null;
		Double curDouble = null;    	
    	String tempComboItem = null;
    	
    	try {
			initDouble = Double.parseDouble(initPrice1);
			curDouble = Double.parseDouble(price1.getText());
			
			if (!initDouble.equals(curDouble)) {
				return true;
			}
		} catch (Exception e) {
			if (!initPrice1.equals(price1.getText())) {
				return true;
			}
		}
    	
    	try {
			initDouble = Double.parseDouble(initQuantity1);
			curDouble = Double.parseDouble(quantity1.getText());
			
			if (!initDouble.equals(curDouble)) {
				return true;
			}
		} catch (Exception e) {
			if (!initQuantity1.equals(quantity1.getText())) {
				return true;
			}
		}
    	
    	tempComboItem = (String) unitCombo1.getSelectedItem();
		if (!initCombo1.equals(tempComboItem)) {
			return true;
		}
		
		
		return false;
    }
    
    private boolean areSecondRowChanges() {
		Double initDouble = null;
		Double curDouble = null;    	
    	String tempComboItem = null;
    	
       	try {
			initDouble = Double.parseDouble(initPrice2);
			curDouble = Double.parseDouble(price2.getText());
			
			if (!initDouble.equals(curDouble)) {
				return true;
			}
		} catch (Exception e) {
			if (!initPrice2.equals(price2.getText())) {
				return true;
			}
		}
    	
    	try {
			initDouble = Double.parseDouble(initQuantity2);
			curDouble = Double.parseDouble(quantity2.getText());
			
			if (!initDouble.equals(curDouble)) {
				return true;
			}
		} catch (Exception e) {
			if (!initQuantity2.equals(quantity2.getText())) {
				return true;
			}
		}
    	
    	tempComboItem = (String) unitCombo2.getSelectedItem();
		if (!initCombo2.equals(tempComboItem)) {
			return true;
		}
		
		
		return false;
    }

    private boolean areThirdRowChanges() {
		Double initDouble = null;
		Double curDouble = null;    	
    	String tempComboItem = null;
    	
       	try {
			initDouble = Double.parseDouble(initPrice3);
			curDouble = Double.parseDouble(price3.getText());
			
			if (!initDouble.equals(curDouble)) {
				return true;
			}
		} catch (Exception e) {
			if (!initPrice3.equals(price3.getText())) {
				return true;
			}
		}
    	
    	try {
			initDouble = Double.parseDouble(initQuantity3);
			curDouble = Double.parseDouble(quantity3.getText());
			
			if (!initDouble.equals(curDouble)) {
				return true;
			}
		} catch (Exception e) {
			if (!initQuantity3.equals(quantity3.getText())) {
				return true;
			}
		}
    	
    	tempComboItem = (String) unitCombo3.getSelectedItem();
		if (!initCombo3.equals(tempComboItem)) {
			return true;
		}
		
		
		return false;
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
