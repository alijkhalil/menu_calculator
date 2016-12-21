import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.beans.*; //property change stuff
import java.util.HashMap;
import java.util.Set;
import java.awt.*;
import java.awt.event.*;


public class NumberInputDialog extends JDialog
			implements ActionListener, PropertyChangeListener {
	
    private JOptionPane optionPane;
    boolean isInt;
    
	private String typedText = null;
	
    private JTextField textField;
    
    private String btnString1 = "Enter";
    private String btnString2 = "Cancel";
	

	public NumberInputDialog(String title, String question, boolean isInteger) {
        super();
        this.setModal(true);
        
        setTitle(title);
        isInt = isInteger;
        
        //Create an array of the text and components to be displayed
        textField = new JTextField(12);
        question += "\n";
        Object[] array = {question, textField};

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
                textField.requestFocusInWindow();
            }
        });

        //Register an event handler that puts the text into the option pane.
        textField.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
	}	

	public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
         && (e.getSource() == optionPane)
         && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
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
                typedText = textField.getText();
                
                //Trim text
                typedText = typedText.replaceAll("^\\s+", "");
                typedText = typedText.replaceAll("\\s+$", "");
                
                try {
                	if (isInt)
                		Integer.valueOf(typedText);
                	else
                		Double.valueOf(typedText);
                	
                    clearAndHide();
                } catch (Exception except) {
                    //text was invalid
                    textField.selectAll();
                    
                    String errorMessage;
                    if (isInt)
                    	errorMessage = ("Sorry...\n\n\"" + typedText + "\", is not a valid WHOLE number.\n" 
                    					+ "Please try another input.");
                    else
                    	errorMessage = ("Sorry...\n\n\"" + typedText + "\", is not a valid number.\n" 
            					+ "Please try another input.");
                    
                    JOptionPane.showMessageDialog(NumberInputDialog.this, errorMessage, "Try again", JOptionPane.ERROR_MESSAGE);
                    typedText = null;
                    textField.requestFocusInWindow();
                }
            } else { //user closed dialog or clicked cancel
                typedText = null;
                clearAndHide();
            }
        }		
	}

    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
    }
	
    public String getValidatedText() {
        return typedText;
    }
    
    // This method clears the dialog and hides it.
    public void clearAndHide() {
        textField.setText(null);
        setVisible(false);
    }

}