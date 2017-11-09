import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.beans.*; //property change stuff
import java.util.HashMap;
import java.util.Set;
import java.awt.*;
import java.awt.event.*;


public class SingleFieldDialog extends JDialog
			implements ActionListener, PropertyChangeListener {
	
    private JOptionPane optionPane;
    
    private boolean isCat;
    private boolean isPassword;    
    
    private Set<String> keys;
	private String typedText = null;
	
    private JTextField textField = new JTextField(24);
    JPasswordField jpf = new JPasswordField(24);

    
    private String btnString1 = "Enter";
    private String btnString2 = "Cancel";
    
	public SingleFieldDialog(String title, String question, Set<String> keySet, boolean isCat, final boolean isPassword) {
        super();
        this.setModal(true);
        
        setTitle(title);
        this.keys = keySet;
        
        this.isCat = isCat;
        this.isPassword = isPassword;
        
        //Create an array of the text and components to be displayed
        question += "\n";
        
    	Object[] array1 = {question, jpf};
    	Object[] array2 = {question, textField};


        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {btnString1, btnString2};

        //Create the JOptionPane.
        if (!isPassword) {
	        optionPane = new JOptionPane(array2,
	                                    JOptionPane.QUESTION_MESSAGE,
	                                    JOptionPane.YES_NO_OPTION,
	                                    null,
	                                    options,
	                                    options[0]);
		} else {
	        optionPane = new JOptionPane(array1,
	                                    JOptionPane.QUESTION_MESSAGE,
	                                    JOptionPane.YES_NO_OPTION,
	                                    null,
	                                    options,
	                                    options[0]);
        }
        
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
            	if (!isPassword)
            		textField.requestFocusInWindow();
            	else
            		jpf.requestFocusInWindow();
            }
        });

        //Register an event handler that puts the text into the option pane.
        if (!isPassword)
        	textField.addActionListener(this);
        else
        	jpf.addActionListener(this);
        
        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
	}	

	@SuppressWarnings("deprecation")
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
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
            	if (!isPassword)
            		typedText = textField.getText();
            	else
            		typedText = new String(jpf.getPassword());
            	
                //Trim
                typedText = typedText.replaceAll("^\\s+", "");
                typedText = typedText.replaceAll("\\s+$", "");

                
                String ucText = typedText.toUpperCase();
                
                boolean anyEqual = false;
                String upperText = ucText.toUpperCase().replaceAll("\\s+", "");
                                
                if (upperText.isEmpty())
                	anyEqual = true;
                
                for (String itemName : keys) {
                	if (anyEqual)
                		break;
                	
                	if ((itemName.toUpperCase().replaceAll("\\s+", "")).equals(upperText))
                		anyEqual = true;
                }
               
                if (!anyEqual) {
                    //we're done; clear and dismiss the dialog
                    clearAndHide();
                } else {
                    //text was invalid
                    if (!isPassword)
                    	textField.selectAll();
                    else
                    	jpf.selectAll();
                    
                    if (isCat)
                    	JOptionPane.showMessageDialog(SingleFieldDialog.this,
                                "Sorry...\n\nThe letters in category, \"" + typedText + "\" "
                                + ", do not make sense OR are already used for another category.\n"
                                + "Please use another name.",
                                    "Try again",
                                    JOptionPane.ERROR_MESSAGE);
                    else if (!isCat && !isPassword)
                    	JOptionPane.showMessageDialog(SingleFieldDialog.this,
                                "Sorry...\n\nThe letters in item, \"" + typedText + "\" "
                                + ", do not make sense OR are already used for either another ingredient or dish.\n"
                                + "Please use another name.",
                                    "Try again",
                                    JOptionPane.ERROR_MESSAGE);  	
                    	
                    typedText = null;
                    
                    if (!isPassword)
                    	textField.requestFocusInWindow();
                    else
                    	jpf.requestFocusInWindow();
                }
            } else { //user closed dialog or clicked cancel
                typedText = null;
                if (isPassword)
                	keys.add("...");
                
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
    	if (!isPassword)
    		textField.setText(null);
    	else
    		jpf.setText(null);
    	
        setVisible(false);
    }

}