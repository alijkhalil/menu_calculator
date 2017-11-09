import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Toolkit;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

public class MainFrame extends JPanel {
	
	static final int TABBEDPANE_SCROLLBAR_WIDTH = 21;
	static final int UNDO_REDO_STACK_LIMIT = 35;
	
	static final String TITLE = "Password";
	static final String QUESTION = "What is the password?\n   \n";

	public JFrame frame;
	public JTabbedPane tabbedPane;
	
	public Menu menuBar;
	
	public Opener openItem = new Opener(this);
	public Saver saveItem = new Saver(this);
	public String currentFilePathName = null;
	
	public LinkedList<UndoItem> undoRedoStack = new LinkedList<UndoItem>();
	public static String initialState = null;
	public Integer currentIndex = null;
	public boolean isSaved = true;

	public boolean isMsgOn = true;
	
	public HashMap<String, MenuItem> itemHash = new  HashMap<String, MenuItem>();
	public ArrayList<String> ingredientList = new ArrayList<String>();

	public HashMap<String, Category> categories = new HashMap<String, Category>();
	public ArrayList<String> categoriesList = new ArrayList<String>();
	
	public HashMap<String, OrderItem> currentOrder = new HashMap<String, OrderItem>();
	
	public IngredientPricing iPricingPane;
	public MenuCompositionPane dishCompPane;
	public CategoryManagePane catManagementPane;
	public IngredientInfoPane itemUsagePane;
	public ResultsPane resultsPane;
	
	
    public MainFrame() {
        super(new GridLayout(1, 1));        
        
        // Populate two main categories in HashMap and Table
        categories.put(Category.ENTIRE_MENU, new Category(Category.ENTIRE_MENU, true));
        InsertIntoList(categoriesList, Category.ENTIRE_MENU);
        
        categories.put(Category.UNSOLD_DISHES, new Category(Category.UNSOLD_DISHES, false));
        InsertIntoList(categoriesList, Category.UNSOLD_DISHES);
        
        
        tabbedPane = new JTabbedPane();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        String spaces = "";
        int extraFactor = screenSize.width/280;
        for (int i = 0; i < 13 + extraFactor; i++)
        	spaces += "&#32;&#32;&#32;";
        
        iPricingPane = new IngredientPricing(this, null, null, null, null, null);
        
        JScrollPane ingredientScrollPane = new JScrollPane(iPricingPane);
        JScrollBar scrollBar1 = ingredientScrollPane.getVerticalScrollBar();
        scrollBar1.setPreferredSize(new Dimension(TABBEDPANE_SCROLLBAR_WIDTH, scrollBar1.getPreferredSize().height));
        
        ingredientScrollPane.getVerticalScrollBar().setUnitIncrement(200);
        ingredientScrollPane.getVerticalScrollBar().setBlockIncrement(350);
        
        tabbedPane.addTab("<html><body leftmargin=13 topmargin=7 marginwidth=13 marginheight=4>Ingredient Prices  (EDIT)" + spaces +
        		"</body></html>", null, ingredientScrollPane, "Enter the Cost of Ingredients");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        
        
        dishCompPane = new MenuCompositionPane(this, null, null, null);
        
        JScrollPane dishScrollPane = new JScrollPane(dishCompPane);
        JScrollBar scrollBar2 = dishScrollPane.getVerticalScrollBar();
        scrollBar2.setPreferredSize(new Dimension(TABBEDPANE_SCROLLBAR_WIDTH, scrollBar2.getPreferredSize().height));
        
        dishScrollPane.getVerticalScrollBar().setUnitIncrement(200);
        dishScrollPane.getVerticalScrollBar().setBlockIncrement(350);
        
        spaces = "";
        extraFactor = screenSize.width/190;
        for (int i = 0; i < 13 + extraFactor; i++)
        	spaces += " ";
        
        tabbedPane.addTab("Dish Recipes  (EDIT)" + spaces, null, dishScrollPane, "Enter Recipes for the Dishes in the Restaruant");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        
        
        catManagementPane = new CategoryManagePane(this, null, null);
        
        JScrollPane catScrollPane = new JScrollPane(catManagementPane);
        JScrollBar scrollBar3 = catScrollPane.getVerticalScrollBar();
        scrollBar3.setPreferredSize(new Dimension(TABBEDPANE_SCROLLBAR_WIDTH, scrollBar3.getPreferredSize().height));
        
        catScrollPane.getVerticalScrollBar().setUnitIncrement(200);
        catScrollPane.getVerticalScrollBar().setBlockIncrement(350);
        
        spaces = "";
        extraFactor = screenSize.width/700;
        for (int i = 0; i < 3 + extraFactor; i++)
        	spaces += " ";
        	
        tabbedPane.addTab("Category Management  (EDIT)" + spaces, null, catScrollPane, "Add/Remove Dishes for Categories");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
 
        
        itemUsagePane = new IngredientInfoPane(this, null, null, true);
        itemUsagePane.setPreferredSize(new Dimension(screenSize.width/2, screenSize.height/3));
        //System.out.println(screenSize.width + "... + Height: " + screenSize.height);
        
        JScrollPane infoScrollPane = new JScrollPane(itemUsagePane);
        JScrollBar scrollBar4 = infoScrollPane.getVerticalScrollBar();
        scrollBar4.setPreferredSize(new Dimension(TABBEDPANE_SCROLLBAR_WIDTH, scrollBar4.getPreferredSize().height));

        infoScrollPane.getVerticalScrollBar().setUnitIncrement(200);
        infoScrollPane.getVerticalScrollBar().setBlockIncrement(350);
        
        spaces = "";
        extraFactor = screenSize.width/95;
        for (int i = 0; i < 40 + extraFactor; i++)
        	spaces += " ";
        
        tabbedPane.addTab("Ingredient Usage  (RESULTS)" + spaces, null, infoScrollPane, "Detailed Ingredient Uses Information");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
        
      
        resultsPane = new ResultsPane(this, null, null, true, true);
        
        JScrollPane resultsScrollPane = new JScrollPane(resultsPane);
        JScrollBar scrollBar5 = resultsScrollPane.getVerticalScrollBar();
        scrollBar5.setPreferredSize(new Dimension(TABBEDPANE_SCROLLBAR_WIDTH, scrollBar5.getPreferredSize().height));

        resultsScrollPane.getVerticalScrollBar().setUnitIncrement(200);
        resultsScrollPane.getVerticalScrollBar().setBlockIncrement(350);
        
        spaces = "";
        extraFactor = screenSize.width/375;
        for (int i = 0; i < 5 + extraFactor; i++)
        	spaces += " ";
        
        tabbedPane.addTab("Cost Results  (RESULTS)" + spaces, null, resultsScrollPane, "Cost Analysis for a List of Dishes");
        tabbedPane.setMnemonicAt(4, KeyEvent.VK_5);
        
        //Add the tabbed pane to this panel.
        add(tabbedPane);
        
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        
        //Set Undo-Redo stack
        undoRedoStack.add(new UndoItem(null, null, saveItem.saveCurrentState()));
        currentIndex = 0;
    }
    
    public void updateUndoRedoStack(String undoMessage, String redoMessage) {
    	
    	currentIndex++;
    	if (currentIndex != undoRedoStack.size())
    		undoRedoStack.add(currentIndex, new UndoItem(undoMessage, redoMessage, saveItem.saveCurrentState()));
    	else
    		undoRedoStack.addLast(new UndoItem(undoMessage, redoMessage, saveItem.saveCurrentState()));
    	
    	if (undoRedoStack.size() > currentIndex + 1) {
    		while (undoRedoStack.size() > currentIndex + 1) {
    			undoRedoStack.remove(currentIndex + 1);
    		}		
    	}
    	
    	if (UNDO_REDO_STACK_LIMIT < undoRedoStack.size()) {	
    		undoRedoStack.removeFirst();
    		currentIndex--;
    	}
    	
    	isSaved = false;
    }
    
    public static void InsertIntoList(ArrayList<String> list, String addedItem) {    	

    	InsertIntoListHelper(list, addedItem, 0, list.size());
	}
    
    private static void InsertIntoListHelper(ArrayList<String> list, String addedItem, int startIndex, int endIndex) {    	
    	int rightIndex = startIndex;
    	int listSize = endIndex - startIndex;
    	
    	if (listSize < 8) {
	       	if (listSize == 0) {
	    		list.add(rightIndex, addedItem);
	    	} else {    		
		    	while(rightIndex < endIndex) {
		    		if ((addedItem.toUpperCase()).compareTo(list.get(rightIndex).toUpperCase()) < 0)
		    			break;
		    		
		    		rightIndex++;
		    	}
		    	
		    	if (rightIndex != list.size())
	    			list.add(rightIndex, addedItem);
		    	else
	    			list.add(addedItem);
	    	}
    	} else {
    		int newStart = startIndex;
    		int newEnd = endIndex;
    		
    		if ((addedItem.toUpperCase()).compareTo(list.get(startIndex + (listSize - 1)/2).toUpperCase()) <= 0) {
    			newEnd = (startIndex + (listSize - 1)/2) + 1;
    		} else {
    			newStart = ((startIndex + (listSize - 1)/2) + 1);
    		}
    		
    		InsertIntoListHelper(list, addedItem, newStart, newEnd);
    	}
	}
  
    public static String toFirstLettersUpper(String starter) {
    	starter = starter.toLowerCase();
    	String[] allWords = starter.split("\\s+");
    	
    	StringBuilder builder = new StringBuilder(starter);
    	
    	int placeHolder = 0;
    	char tempLetter = 0;
    	
    	boolean capNeeded = false;
    	String curWord = null;
    	String smallWordCheck = null;
    	for (int i = 0; i < allWords.length; i++) {
    		curWord = allWords[i];
    		
    		smallWordCheck = curWord.toUpperCase();
    		if (!smallWordCheck.equals("AND") || !smallWordCheck.equals("OR")|| !smallWordCheck.equals("WITH") || !smallWordCheck.equals("A")
    				|| !smallWordCheck.equals("IN") || !smallWordCheck.equals("AN") || !smallWordCheck.equals("AS") 
    				|| !smallWordCheck.equals("OF") || !smallWordCheck.equals("NOT") || !smallWordCheck.equals("BY")) {
	    		
    			placeHolder = builder.indexOf(curWord, placeHolder);
	
	    		capNeeded = true;
	    		for (int wordIndex = placeHolder; wordIndex < placeHolder + curWord.length(); wordIndex++) {
	        		tempLetter = builder.charAt(wordIndex);
	        		
		    		if (Character.isLetter(tempLetter)){
		    			if (capNeeded) {
		    				builder.setCharAt(wordIndex, Character.toUpperCase(tempLetter));
		    				capNeeded = false;
		    			}
		    		} else {
		    			capNeeded = true;
		    		}
	    		}
    		
	    		placeHolder += curWord.length();
    		}
    	}
    	
    	return builder.toString();
    }
    
    public static String toStringWithXDemical(Double doub, int limit) {
		double doubleNum = doub;
		int limitPower = 1;
		
		String returnVal = "";
		
		if (limit >= 0) {
			if (doubleNum < 0) {
				returnVal += "-";
				doubleNum *= -1;
			}
			
			for (int i = 0; i < limit; i++)
				limitPower *= 10;
			
			//Inflate number to get necessary digits
			doubleNum *= limitPower;
			
			int temp = (int) doubleNum;				
			Integer inflatedInt = temp; 
			String finalDouble = inflatedInt.toString();			
			
			if (temp < limitPower) {
				returnVal += "0.";
				
				while (true) {
					limitPower /= 10;
					
					if (temp < limitPower) {
						returnVal += "0";
					} else {
						if (temp != 0)
							returnVal += finalDouble;
						
						break;
					}
				}
			} else {
				returnVal += (finalDouble.substring(0, finalDouble.length() - limit) + "." 
								+ finalDouble.substring(finalDouble.length() - limit, finalDouble.length()));
			}
    	} else {
    		return returnVal;
    	}
    	
		if (limit == 0)
			returnVal = returnVal.substring(0, returnVal.length() - 1);
		
		return addCommasToNum(returnVal);
    }
    
    public static String addCommasToNum(String num) {
   	
    	String beforeDecimal;
    	String afterDecimal;
    	
    	int demicalPt = num.indexOf(".");
    	if (demicalPt > 0) {
    		beforeDecimal = num.substring(0, demicalPt);
    		afterDecimal = num.substring(demicalPt);
    	} else {
    		beforeDecimal = num;
    		afterDecimal = null;
    	}
    	
    	boolean wasNegative = false;
    	if (beforeDecimal.contains("-")) {
    		beforeDecimal = beforeDecimal.substring(1);
    		wasNegative = true;
    	}
    	
    	StringBuilder withCommas = new StringBuilder("");
    	for (int letter = 0; letter < beforeDecimal.length(); letter++) {
    		if ((letter % 3) == 0 && letter != 0) {
    			withCommas.insert(0, ',');
    		}
    		
    		withCommas.insert(0, beforeDecimal.charAt(beforeDecimal.length() - letter - 1));  		
    	}
    	
    	if (wasNegative)
    		withCommas.insert(0, '-');
    	
    	if (afterDecimal != null)
    		withCommas.append(afterDecimal);
    	
    	return withCommas.toString();
    }
    
    private void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("Menu Calculator");
        
        //Add menu to frame
        menuBar = new Menu();
        frame.setJMenuBar(menuBar.createMenuBar(this));

        //Add tabs to GUI
        frame.add(this, BorderLayout.CENTER);
        
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        frame.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		
            	int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure that you want to EXIT the program?", "Exit", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					if (!isSaved) {
				        int overWrite = JOptionPane.showConfirmDialog(frame, "Would you like to SAVE before exiting the program?", "Save", JOptionPane.YES_NO_OPTION);
						if (overWrite == JOptionPane.YES_OPTION || overWrite == JOptionPane.NO_OPTION) {
					        if (overWrite == JOptionPane.YES_OPTION)
								menuBar.saveEntireProgram(false);
						
					        System.exit(0);
						}
					} else {
						System.exit(0);
					}
				}
        	}
        } );
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
    	//Input password to be granted access to program
   		File file = null;
   		
   		File rootDir = new File(System.getProperty("user.home"));
		if (OSDetector.isWindows()) {
			file = new File(rootDir, MenuCompositionPane.DISH_UNITS_WITHOUT_SELECT[2] + MenuCompositionPane.DISH_UNITS_WITHOUT_SELECT[4]);
		} else if (OSDetector.isMac()) {
			file = new File(rootDir, MenuCompositionPane.DISH_UNITS_WITHOUT_SELECT[1] + MenuCompositionPane.DISH_UNITS_WITHOUT_SELECT[0]);
		} else if (OSDetector.isLinux()) {
			System.exit(0);
		}
		

		SingleFieldDialog dialogAdd;
    	String newIngredientName;
    	Set<String> keys = new HashSet<String>();
    	
    	boolean done = false;
		if (!OSDetector.isLinux() && !file.exists()) {
	    	while (!done) {
    		
 	    		//Do only if never previously validiated
	         	dialogAdd = new SingleFieldDialog(TITLE, QUESTION, keys, false, true);
	    		
	    		dialogAdd.pack();
	    		dialogAdd.setLocationRelativeTo(frame);
	    		dialogAdd.setVisible(true);
	         	
	         	newIngredientName = dialogAdd.getValidatedText();
	
	    		if (newIngredientName == null) {
	    			if (!keys.contains("...")) {
	    				JOptionPane.showMessageDialog(dialogAdd, "Sorry...\n\nYou have entered the incorrect password!\n" + "Please retype password.",
	                            						"Try again", JOptionPane.ERROR_MESSAGE);
	    			} else {
	    				System.exit(0);
					}
				} else {
	    			if (newIngredientName.length() == 8) {
	    				
	    				int index = 0;
	    				for (; index < 8; index++) {
	    					if (index == 5 || index == 0 || index == 2) {
	    						if (newIngredientName.charAt(index) != MenuCompositionPane.INVALID_INGREDIENT[1].charAt(index) + (3*5/15))
	    							break;
	    					} else if (index == 6) { 
	    						if (newIngredientName.charAt(index) != ((7 * 2 + 10 - 1) * (15))/3)
	    							break;
	    					} else if (newIngredientName.charAt(index) != MenuCompositionPane.INVALID_INGREDIENT[1].charAt(index)) {
	    						break;
	    					}
	    				}
	    			
	    				if (index == 8) {
	    					done = true;
	    					
	    					WriteFile saver = null;
	    		            try {
	    		            	if (OSDetector.isWindows()) {    		            		
								
	    		            		file = new File(rootDir, MenuCompositionPane.DISH_UNITS_WITHOUT_SELECT[2]);
	    		            		boolean made = file.mkdir();
	    		            		if (made) {
	    		            			saver = new WriteFile(file.getAbsolutePath() + MenuCompositionPane.DISH_UNITS_WITHOUT_SELECT[4]);
	    		            			saver.writeToFile(MenuCompositionPane.DISH_UNITS_WITHOUT_SELECT[3]);
	    		            		}
	    		            	} else if (OSDetector.isMac()) {
	    		            		
	    		            		file = new File(rootDir, MenuCompositionPane.DISH_UNITS_WITHOUT_SELECT[1]);
	    		            		boolean made = file.mkdir();
	    		            		if (made) {
	    		            			saver = new WriteFile(file.getAbsolutePath() + MenuCompositionPane.DISH_UNITS_WITHOUT_SELECT[0]);
	    		            			saver.writeToFile(MenuCompositionPane.DISH_UNITS_WITHOUT_SELECT[5]);
	    		            		}	    		            	
	    		            	}    		            	
	    		            } catch (Exception except) {  }

	    				} else {
	    					JOptionPane.showMessageDialog(dialogAdd,
		                            "Sorry...\n\nYou have entered the incorrect password!\n" + "Please retype password.",
	    				             "Try again", JOptionPane.ERROR_MESSAGE);
	    				}
	    			} else {
						JOptionPane.showMessageDialog(dialogAdd,
	                            "Sorry...\n\nYou have entered the incorrect password!\n" + "Please retype password.",
	                                "Try again", JOptionPane.ERROR_MESSAGE);	        					        			
					}
	    		}
	    	}
    	} else if (!OSDetector.isLinux()) {
	    	while (!done) {
	    		
 	    		//Do only if never previously validiated
	         	dialogAdd = new SingleFieldDialog(TITLE, QUESTION, keys, false, true);
	    		
	    		dialogAdd.pack();
	    		dialogAdd.setLocationRelativeTo(frame);
	    		dialogAdd.setVisible(true);
	         	
	         	newIngredientName = dialogAdd.getValidatedText();
	
	    		if (newIngredientName == null) {
	    			if (!keys.contains("...")) {
	    				JOptionPane.showMessageDialog(dialogAdd, "Sorry...\n\nYou have entered the incorrect password!\n" + "Please retype password.",
	                            						"Try again", JOptionPane.ERROR_MESSAGE);
	    			} else {
	    				System.exit(0);
	    			}
				} else {
	    			if (newIngredientName.length() == 7) {
	    				
	    				int index = 0;
	    				for (; index < 8; index++) {
	    					if (index == 3) {
	    						if (newIngredientName.charAt(index) != MenuCompositionPane.INVALID_INGREDIENT[6].charAt(index) - (-21/7))
	    							break;
	    					} else if (index == 0) {
	    						if (newIngredientName.charAt(index) != (MenuCompositionPane.INVALID_INGREDIENT[6].charAt(index) + (3*5)/15))
	    							break;
	    					} else if (index == 4 || index == 2) {
	    						if (newIngredientName.charAt(index) != MenuCompositionPane.INVALID_INGREDIENT[6].charAt(index) - 5)
	    							break;
	    					} else if (index == 6 || index == 7) {
	    						if (index == 6) {
		    						if (newIngredientName.charAt(index - 1) != MenuCompositionPane.INVALID_INGREDIENT[6].charAt(index) - (-21/7))
		    							break;
	    						} else {
	    							if (newIngredientName.charAt(index - 1) != MenuCompositionPane.INVALID_INGREDIENT[6].charAt(index))
		    							break;
	    						}
	    					} else if (index != 5) {
	    						if (newIngredientName.charAt(index) != MenuCompositionPane.INVALID_INGREDIENT[6].charAt(index))
	    							break;
	    					}
	    				}
	    			
	    				if (index == 8) {
	    					done = true;
	    					
	    				} else {
	    					JOptionPane.showMessageDialog(dialogAdd,
		                            "Sorry...\n\nYou have entered the incorrect password!\n" + "Please retype password.",
	    				             "Try again", JOptionPane.ERROR_MESSAGE);
	    				}
	    			} else {
						JOptionPane.showMessageDialog(dialogAdd,
	                            "Sorry...\n\nYou have entered the incorrect password!\n" + "Please retype password.",
	                                "Try again", JOptionPane.ERROR_MESSAGE);	        					        			
					}
	    		}
	    	}

    	}
    }
    
    
    
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	MainFrame sourceFrame = new MainFrame();

            	//Create start state file
            	MainFrame.initialState = sourceFrame.saveItem.saveCurrentState();

            	//Turn off metal's use of bold fonts
            	UIManager.put("swing.boldMetal", Boolean.TRUE);
            	UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
            	
            	sourceFrame.createAndShowGUI();            	
            }
        });
    }
}