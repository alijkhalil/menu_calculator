import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.beans.*; //property change stuff
import java.util.Arrays;
import java.awt.*;
import java.awt.event.*;


public class WastageCalculatorDialog extends JDialog
			implements ActionListener, PropertyChangeListener {
	
    private JOptionPane optionPane;
    boolean isInt;
    
	private String typedStartText = null;
	private String typedEndText = null;
	private String returnDbl = null;
	
	private static String title = "Waste Percentage Calculator";
	
	private static JLabel startLabel = new JLabel("Enter the weight of the item BEFORE it was processed: ");
    private JTextField startTextField = new JTextField(12);
    private JComboBox startCombo = new JComboBox(MenuCompositionPane.INGREDIENT_UNITS);
	private static String startNL = "\n";
	
	private static JLabel endLabel = new JLabel("Enter the weight of the item AFTER it was processed: ");
    private JTextField endTextField = new JTextField(12);
    private JComboBox endCombo = new JComboBox(MenuCompositionPane.INGREDIENT_INCOMPLETE);
	private static String endNL = "\n";
    
    private String btnString1 = "Enter";
    private String btnString2 = "Cancel";
	

	public WastageCalculatorDialog() {

        super();
        this.setModal(true);
        
        setTitle(title);
        
        //Create an array of the text and components to be displayed
        Object[] array = {startLabel, startTextField, startCombo, startNL,
        						endLabel, endTextField, endCombo, endNL};

        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {btnString1, btnString2};

        //Create the JOptionPane.
        optionPane = new JOptionPane(array,
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION,
                                    null,
                                    options,
                                    options[0]);

        //Make this dialog display it.
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                    optionPane.setValue(new Integer(
                                        JOptionPane.CLOSED_OPTION));
            }
        });

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                startTextField.requestFocusInWindow();
            }
        });

        //Register an event handler that puts the text into the option pane.
        startTextField.addActionListener(this);
        startCombo.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        
        		JComboBox tempCombo = null;
        		if (Arrays.asList(MenuCompositionPane.INGREDIENT_INVALID_ITEMS).contains(startCombo.getSelectedItem())) {
        			
        			tempCombo = new JComboBox(MenuCompositionPane.INGREDIENT_INCOMPLETE);
        		} else if (Arrays.asList(MenuCompositionPane.OTHER_UNITS).contains(startCombo.getSelectedItem())) {
        			
        			tempCombo = new JComboBox(MenuCompositionPane.OTHER_UNITS);
    			} else if (Arrays.asList(MenuCompositionPane.LIQUID_UNITS).contains(startCombo.getSelectedItem())) {
        			
        			tempCombo = new JComboBox(MenuCompositionPane.LIQUID_UNITS);	
        		} else {
        			
        			tempCombo = new JComboBox(MenuCompositionPane.SOLID_UNITS);
        		}
        		
    			endCombo.setModel(tempCombo.getModel());
        	}
        } );

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
	}	

	public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
        		&& (e.getSource() == optionPane)
        		&& (JOptionPane.VALUE_PROPERTY.equals(prop) 
				|| JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
                typedStartText = startTextField.getText();
                
                //Trim text
                typedStartText = typedStartText.replaceAll("^\\s+", "");
                typedStartText = typedStartText.replaceAll("\\s+$", "");
                
                typedEndText = endTextField.getText();
                
                //Trim text
                typedEndText = typedEndText.replaceAll("^\\s+", "");
                typedEndText = typedEndText.replaceAll("\\s+$", "");
                
                try {
            		Double.valueOf(typedStartText);
                    try {
                		Double.valueOf(typedEndText);
                    } catch (Exception except1) {
                        //text was invalid
                        endTextField.selectAll();
                        
                        String errorMessage;
                    	errorMessage = ("Sorry...\n\n\"" + typedEndText + "\", is not a valid number.\n" 
                    						+ "Please try another input.");
                        
                        JOptionPane.showMessageDialog(WastageCalculatorDialog.this, errorMessage, "Try again", JOptionPane.ERROR_MESSAGE);
                        typedEndText = null;
                        endTextField.requestFocusInWindow();
                    }      
                } catch (Exception except2) {
                    //text was invalid
                    startTextField.selectAll();
                    
                    String errorMessage;
                	errorMessage = ("Sorry...\n\n\"" + typedStartText + "\", is not a valid number.\n" 
                						+ "Please try another input.");
                    
                    JOptionPane.showMessageDialog(WastageCalculatorDialog.this, errorMessage, "Try again", JOptionPane.ERROR_MESSAGE);
                    typedStartText = null;
                    startTextField.requestFocusInWindow();
                }
                
                int error=-1;
                if (typedStartText != null && typedEndText != null) {
                	
                	error = 0;
                	if (Arrays.asList(MenuCompositionPane.INGREDIENT_INVALID_ITEMS).contains(startCombo.getSelectedItem())) {
                		error = 1;            		
                	
                	} else if (Arrays.asList(MenuCompositionPane.INGREDIENT_INVALID_ITEMS).contains(endCombo.getSelectedItem())) {
                		error = 2;
                	}
                	
                	if (error != 0) {
	            		String errorMessage = ("Sorry...\n\nYou have not selected valid units and cannot get a percent of waste.\n" 
	    						+ "Please try another selection.");
	            		JOptionPane.showMessageDialog(WastageCalculatorDialog.this, errorMessage, "Try again", JOptionPane.ERROR_MESSAGE);
	            		
	            		if (error == 1)
	            			startCombo.requestFocusInWindow();  
	            		else
	            			endCombo.requestFocusInWindow();
                	}
                }
                
            	//Check for both values greater than 0
                if (error == 0) {
                	if (Double.valueOf(typedStartText) <= 0) {
                		error = 1;
                	} else if (Double.valueOf(typedEndText) <= 0) {
                		error = 2;
                	}
                	
                	if (error != 0) {
	            		String errorMessage = ("Sorry...\n\nYou have entered a negative number.\n" 
	    						+ "Please enter a weight larger than zero.");
	            		JOptionPane.showMessageDialog(WastageCalculatorDialog.this, errorMessage, "Try again", JOptionPane.ERROR_MESSAGE);
	            		
	            		if (error == 1) {
	            			typedStartText = null;
	            			startTextField.requestFocusInWindow();  
	            		} else {
	            			typedEndText = null;
	            			endTextField.requestFocusInWindow();
	            		}
                	}
                }
                
                //Do calculation
                if (error == 0) {
                	String startString = (String) startCombo.getSelectedItem();
                	String endString = (String) endCombo.getSelectedItem();
                	
                	Double factor = new Double(1);
                	if (Arrays.asList(MenuCompositionPane.LIQUID_UNITS).contains(startCombo.getSelectedItem())) {
                		factor = RecipeItem.getLiquidFactor(startString, endString);
                	} else if (Arrays.asList(MenuCompositionPane.SOLID_UNITS).contains(startCombo.getSelectedItem())) {
                		factor = RecipeItem.getSolidFactor(startString, endString);
                	}
                	
                	//number2 * factor so that units are equal
                	Double startNum = Double.valueOf(typedStartText);
                	Double endNum = Double.valueOf(typedEndText) * factor;
                	
                	if (startNum < endNum) {
	            		String errorMessage = ("Sorry...\n\nYour starting weight is less than your ending weight.\n" 
	            									+ "Please enter either a larger starting weight or smaller ending weight.");
	            		JOptionPane.showMessageDialog(WastageCalculatorDialog.this, errorMessage, "Try again", JOptionPane.ERROR_MESSAGE);
	            		
	            		startTextField.requestFocusInWindow();
                	} else {
                		returnDbl = MainFrame.toStringWithXDemical(Double.valueOf(typedStartText), 3) + "|" + startString + "|" +
                						MainFrame.toStringWithXDemical(Double.valueOf(typedEndText) , 3) + "|" + endString + "|" +
                						MainFrame.toStringWithXDemical(((startNum -endNum) * 100/startNum), 2);
                		clearAndHide();
                	}
                }  
            } else { //user closed dialog or clicked cancel
                typedStartText = null;
                typedEndText = null;
                clearAndHide();
            }
        }		
	}

    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
    }
	
    public String getValidatedPercentage() {
    	return returnDbl;
    }
    
    // This method clears the dialog and hides it.
    public void clearAndHide() {
        startTextField.setText(null);
        endTextField.setText(null);
        setVisible(false);
    }

}