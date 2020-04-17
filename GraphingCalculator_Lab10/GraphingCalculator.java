//Guangsen Ji @03/20/2020  Lab10 

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
// 2-28-20

public class GraphingCalculator implements ActionListener, CalculatorInterface
{
public static void main(String[] args) throws Exception
  {
  GraphingCalculator alxtc = new GraphingCalculator();
  if ((args.length == 1) && args[0].equalsIgnoreCase("DEBUG"))
	   alxtc.debugMode = true;
  }

// GUI objects
//private JFrame       window              = new JFrame("Guangsen Ji@2020 Lab10");
private JFrame       window              = new JFrame("Accumulating-Expression-Graphing-Learning-Time CALCULATOR.  8 Operators are + - * / ^ (exponentiation) r (root) and () (parentheses).  Operands are numbers, X, pi, or e");
private JTextField   expressionTextField = new JTextField();
private JTextField   errorTextField      = new JTextField("You can copy expressions from the log area into the Expression field, or to NotePad to save.");
private JTextField   resultTextField     = new JTextField();
private JTextField   logLineLabelTextField=new JTextField();
private JTextField   forXTextField       = new JTextField();
private JTextField   byXTextField        = new JTextField();
private JTextArea    displayTextArea     = new JTextArea("previous expressions will be listed here");
private JScrollPane  transactionLog      = new JScrollPane(displayTextArea);		
private JButton      clearButton         = new JButton("clear expression");
private JButton      accumulatorButton   = new JButton("Accum");
private JButton      expressionButton    = new JButton("Expres");
private JButton      graphButton         = new JButton("Graph");
private JButton      testButton          = new JButton("Learn");
private JButton      timeButton          = new JButton("Time");
private JLabel       forXlabel           = new JLabel("where X =");
private JLabel       byXlabel            = new JLabel("use X increments of");
private JLabel       expressionLabel     = new JLabel("Enter Value/Expression here");
private JLabel       resultLabel         = new JLabel("Result");
private JLabel       logLabel            = new JLabel("Log line label");
private JButton      modeLabel           = new JButton("Pick mode above");
private JPanel       topPanel            = new JPanel();
private JPanel       penultimateTopPanel = new JPanel();
private JPanel       northPanel          = new JPanel();
private JPanel       buttonPanel         = new JPanel(); 
private JPanel       modeLabelPanel      = new JPanel();
private JPanel       xPanel              = new JPanel();
private JPanel       xLabelPanel         = new JPanel();
private JPanel       resultLogPanel      = new JPanel();
private JPanel       resultLogLabelPanel = new JPanel();



//instance variables
private String       newLine             = System.getProperty("line.separator");
private String       previousResult;
private double       previousAnswer;
private int          entries;
private int          winners;
private int          totalHours;
private int          totalMinutes; 
private boolean      inAccumulatorMode;
private boolean      inExpressionMode;
private boolean      inGraphMode;
private boolean      inTestMode;
private boolean      inTimeMode;
private boolean      debugMode;

public GraphingCalculator() // CONSTRUCTOR
  {
  System.out.println("***********************************************************************");
  System.out.println("* This is the Guangsen Ji AccumulatingLearningXTimeCalculator *");
  System.out.println("***********************************************************************");

  // Build the GUI
  // How to get a dozen buttons in "North":
  // Set Grid(1,5) in the two top panels
  // Set Grid(1,4) in button panel
  // Set Grid(1,2) in label panel
  // Set Grid(2,1) in northPanel. Put top panels in northPanel.
  //
  topPanel.setLayout(new GridLayout(1,4)); // rows, cols
  penultimateTopPanel.setLayout(new GridLayout(1,5)); // rows, cols
  northPanel.setLayout(new GridLayout(2,1)); // rows, cols
  buttonPanel.setLayout(new GridLayout(1,5)); // rows, cols
  modeLabelPanel.setLayout(new GridLayout(1,2)); // rows, cols
  xPanel.setLayout(new GridLayout(1,2)); // rows, cols
  xLabelPanel.setLayout(new GridLayout(1,2)); // rows, cols
  resultLogPanel.setLayout(new GridLayout(1,2)); // rows, cols
  resultLogLabelPanel.setLayout(new GridLayout(1,2)); // rows, cols


  buttonPanel.add(accumulatorButton);
  buttonPanel.add(expressionButton);
  buttonPanel.add(graphButton);
  buttonPanel.add(testButton);
  buttonPanel.add(timeButton);
  topPanel.add(buttonPanel);
  topPanel.add(expressionLabel);
  resultLogLabelPanel.add(resultLabel);
  resultLogLabelPanel.add(logLabel);
  topPanel.add(resultLogLabelPanel);
  xLabelPanel.add(forXlabel);
  xLabelPanel.add(byXlabel);
  topPanel.add(xLabelPanel);
  modeLabelPanel.add(modeLabel);
  modeLabelPanel.add(clearButton);
  penultimateTopPanel.add(modeLabelPanel);
  penultimateTopPanel.add(expressionTextField);
  resultLogPanel.add(resultTextField);
  resultLogPanel.add(logLineLabelTextField);
  penultimateTopPanel.add(resultLogPanel);
  xPanel.add(forXTextField);
  xPanel.add(byXTextField);
  penultimateTopPanel.add(xPanel);
  northPanel.add(topPanel);
  northPanel.add(penultimateTopPanel);
  window.getContentPane().add(northPanel,    "North");
  window.getContentPane().add(transactionLog,"Center");
  window.getContentPane().add(errorTextField,"South");
  
  // Miscellaneous
  window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  displayTextArea.setEditable(false); // keep cursor out
  errorTextField.setEditable(false);
  resultTextField.setEditable(false);
  
  forXTextField.setFont(new Font("default",Font.BOLD,15));
  byXTextField.setFont(new Font("default",Font.BOLD,15));
  displayTextArea.setFont(new Font("default",Font.BOLD,15));
  expressionLabel.setFont(new Font("default",Font.BOLD,15));
  resultLabel.setFont(new Font("default",Font.BOLD,15));
  expressionTextField.setFont(new Font("default",Font.BOLD,15));
  resultTextField.setFont(new Font("default",Font.BOLD,15));
  
  resultTextField.setBackground(Color.white);
  modeLabel.setBackground(Color.yellow);
  
  // Register for event notification
  expressionTextField.addActionListener(this); // give our address to GUI objects
  forXTextField.addActionListener(this); // give our address to GUI objects
  byXTextField.addActionListener(this); // give our address to GUI objects
  clearButton.addActionListener(this);
  accumulatorButton.addActionListener(this);
  expressionButton.addActionListener(this);
  graphButton.addActionListener(this);
  testButton.addActionListener(this);
  timeButton.addActionListener(this);
  logLineLabelTextField.addActionListener(this);
  
  // Show window
  window.setSize(1500,300); // width, height
  window.setVisible(true);
  expressionTextField.requestFocus(); // set cursor in
  }	

//===================================================================================
public void actionPerformed(ActionEvent ae)
  {
  errorTextField.setText("");
  errorTextField.setBackground(Color.white);
  resultTextField.setText(""); // clear previous result
  boolean xWasEntered         = false;
  boolean expressionContainsX = false;
  boolean GraphModexValueWasEntered = false;
  boolean GraphModeIncrementsWasEntered = false;
  boolean GraphModeContainsX 	= false;
  
  
  if ((ae.getSource() == expressionTextField) 
   || (ae.getSource() == forXTextField) 
   || (ae.getSource() == byXTextField)
   || (ae.getSource() == logLineLabelTextField))
     {
	 if (!inAccumulatorMode && !inExpressionMode && !inGraphMode && !inTestMode && !inTimeMode)
	    {
		errorTextField.setText("Must select a mode to start.");
		errorTextField.setBackground(Color.pink);
	    return;
	    }
	 if (inGraphMode)
	    {
    //    errorTextField.setText("Graph Mode is not yet implemented.");
    //    errorTextField.setBackground(Color.yellow);
		 double[] xValues = new double[11];
		 double[] yValues = new double[xValues.length];
		 double doubleByXof = 0;
		 double StartingdoubleForX = 0;
		 String GraphExpression = expressionTextField.getText().toLowerCase().trim();
		 String ForX = forXTextField.getText().toLowerCase().trim();
		 String ByXof = byXTextField.getText().toLowerCase().trim(); 
		 try
		 {
			 
			 doubleByXof = Double.parseDouble(ByXof);
			 StartingdoubleForX = Double.parseDouble(ForX);
			 if (ForX.length() > 0) GraphModexValueWasEntered = true;
			 if (ByXof.length() > 0) GraphModeIncrementsWasEntered = true;
			 if (GraphExpression.contains("x")) GraphModeContainsX = true;
			 if(!GraphModexValueWasEntered && GraphModeContainsX && GraphModeIncrementsWasEntered)
			 {
				  errorTextField.setText("A value of X must be provided");
				  errorTextField.setBackground(Color.pink);
				  return;
			 }
			 if(GraphModexValueWasEntered && !GraphModeContainsX && GraphModeIncrementsWasEntered)
			 {
				  errorTextField.setText("A X must be provided ");
				  errorTextField.setBackground(Color.pink);
				  return;
			 }
			 
			 if(GraphModexValueWasEntered && !GraphModeContainsX && !GraphModeIncrementsWasEntered)
			 {
				  errorTextField.setText("A Increment value must be provided");
				  errorTextField.setBackground(Color.pink);
				  return;
			 }
			 
			 if(doubleByXof <= 0)
			 {
				 errorTextField.setText("Input step value has to be greater than 0");
				 errorTextField.setBackground(Color.pink);
				 return;
			 }
			 
			 for (int i = 0; i < xValues.length; i++)
			    {
				 String calculatedOutput;
				 xValues[i] =  StartingdoubleForX;
				 StartingdoubleForX = StartingdoubleForX+doubleByXof;
				calculatedOutput = calculate(GraphExpression, xValues[i]);
				yValues[i] = Double.parseDouble(calculatedOutput);
			    }
			 System.out.println(Arrays.toString(xValues));
			 System.out.println(Arrays.toString(yValues));
			 
			 
			 
		 }
			
		 
		 catch(Exception e)
		 
		 {
			 errorTextField.setText(e.getMessage());
			 errorTextField.setBackground(Color.pink);
			 
		 }
		 JFrame window = new JFrame(GraphExpression);
		 window.setSize(300,300); // width, height
		 window.setVisible(true);
		 window.setLocation(0,350);
		 window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		 RefreshingGraphPanel rgp = new RefreshingGraphPanel(GraphExpression, xValues, yValues, this, window );
		 window.add(rgp,"Center");
		 
		 
		 
		return;
		}
	 
	 // Read expression and x from the GUI
	 String expression = expressionTextField.getText().toLowerCase().trim();
	 String xString = forXTextField.getText().toLowerCase().trim();
	 if ((expression.length() == 0) && (xString.length() == 0)) return; // ignore blanks
	 if (xString.length() > 0) xWasEntered = true;
	 if (expression.contains("x")) expressionContainsX = true;
	 String printLine = "Expression is " + expression;
	 if (xWasEntered) printLine += " for x = " + xString;
	 if (debugMode) System.out.println(printLine);
	 // Look for inconsistencies in x
	 if (expressionContainsX && !xWasEntered)
	    {
	    errorTextField.setText("The expression contains X but a value for X was not entered.");
	    errorTextField.setBackground(Color.pink);
		return;
		}
	 if (!expressionContainsX && xWasEntered)
	    {
	    errorTextField.setText("A value for X was entered but the expression does not contain X.");
	    errorTextField.setBackground(Color.pink);
		return;
		}

	 
	 // Do processing appropriate for the mode that is set.
	 if (inAccumulatorMode) // **********************************************
	    {
		if (expression.startsWith("$")) expression = expression.substring(1); // drop $ 
		if (expression.contains(","))
           {
           errorTextField.setText("Entry cannot contain commas.");
		   errorTextField.setBackground(Color.pink);
	       return; 
	       }
    	int periodOffset = expression.indexOf(".");
	    if (!(periodOffset<0)) // a period was found!
	       {
	       String decimalPortion = expression.substring(periodOffset);
	       if (decimalPortion.length() != 3)
	          {
              errorTextField.setText("A decimal point must be followed by 2 decimal digits.");
			  errorTextField.setBackground(Color.pink);
	    	  return; 
	          }
	       }
		try { 
		    double enteredValue = Double.parseDouble(expression);
		    previousAnswer += enteredValue;
		    String logString = newLine + logLineLabelTextField.getText() + " " + previousResult + " + " + expression + " = ";
		    previousResult = String.valueOf(previousAnswer);
		    // Ensure answer shows 2 decimal digits
		   	int dotOffset = previousResult.indexOf(".");
		    if (!(dotOffset<0)) // a period was found!
		       {
		       String decimalPortion = previousResult.substring(dotOffset);
		       if (decimalPortion.length() < 3) // only one digit showing
		           previousResult += "0";
		       if (decimalPortion.length() > 3) // more than 2 digits showing
		          {
		    	  previousResult = previousResult.substring(0,dotOffset) + decimalPortion.substring(0,3); 
		          }
		       }
		    resultTextField.setText("new total = " + previousResult);
		    displayTextArea.append(logString + previousResult);
	        displayTextArea.setCaretPosition(displayTextArea.getDocument().getLength());//scroll to bottom
		    expressionTextField.setText("");//clear last entry & set cursor
	        return;
		    }
		catch(NumberFormatException nfe)
		    {
			errorTextField.setText("Entered value '" + expression + "' is not numeric.");
			errorTextField.setBackground(Color.pink);
		    }
		return;
		} // END OF ACCUMULATOR MODE ************************************
	 
    if (inExpressionMode) // **********************************************
       {
       try {
    	   if (expression.contains("="))
 	          {
    		  errorTextField.setText("Expression should not contain '=' in Expression Mode.");
    		  errorTextField.setBackground(Color.pink);
    		  return;
              }
    	   // If a value for X was provided, convert xString to double.
    	   // Allow the x value to be e or pi
    	   xString = xString.replace("e", String.valueOf(Math.E)); 
    	   xString = xString.replace("pi",String.valueOf(Math.PI));
           double xValue = 0; // will either be replaced or not used...
    	   if (xWasEntered)
    	      { 
              try {xValue = Double.parseDouble(xString);}
              catch(NumberFormatException nfe) 
                  {
   			      errorTextField.setText("Entered X value '" + xString + "' is not numeric.");
   			      errorTextField.setBackground(Color.pink);
   			      return;
			      }
    	      }
 /**/      String result;
           if (xWasEntered) result = calculate(expression, xValue);
             else           result = calculate(expression);
	       double resultValue = Double.parseDouble(result);
	       String transactionLogLine = newLine + logLineLabelTextField.getText() + " " + expression + " = " + result;
	       if (xWasEntered) transactionLogLine += " for x = " + xString;
	       displayTextArea.append(transactionLogLine);
           displayTextArea.setCaretPosition(displayTextArea.getDocument().getLength());//scroll to bottom
	       resultTextField.setText(" = " + result);
	       }
	   catch (Exception e)
	      {
	      errorTextField.setText(e.getMessage());
		  errorTextField.setBackground(Color.pink);
	      }
	   return;
       } // END OF EXPRESSION MODE *****************************************
	
    if (inTestMode) // *****************************************************
       {
   	   int equalsOffset = expression.indexOf("=");
   	   if (equalsOffset < 0) // no "=" was found
   	      {
          errorTextField.setText("In TEST mode the expression must be followed by '=' and then by the value of the expression."); 
   	      errorTextField.setBackground(Color.pink);
   	      return;
   	      }
   	   String onlyExpression = expression.substring(0,equalsOffset).trim();
   	   String expressionValueString = expression.substring(equalsOffset).trim();
   	   if (expressionValueString.length() == 1) // missing value following = ?
  	      {
          errorTextField.setText("In TEST mode the expression value must follow the = sign at the end."); 
     	  errorTextField.setBackground(Color.pink);
     	  return;
     	  }
  	   expressionValueString = expressionValueString.substring(1).trim(); // drop "=" at front	   
       double expressionValue = 0; // will be replaced or throw exception
       try {expressionValue = Double.parseDouble(expressionValueString);}
       catch(NumberFormatException nfe) 
	        {
	        errorTextField.setText("The expression value entered following the = sign at the end is not numeric."); 
	        errorTextField.setBackground(Color.pink);
	        return;
	 	    }
       entries++;
	   // If a value for X was provided, convert xString to double.
	   // Allow the x value to be e or pi
	   xString = xString.replace("e", String.valueOf(Math.E)); 
	   xString = xString.replace("pi",String.valueOf(Math.PI));
       double xValue = 0; // will either be replaced or not used...
	   if (xWasEntered)
	      { 
          try {xValue = Double.parseDouble(xString);}
          catch(NumberFormatException nfe) 
              {
			  errorTextField.setText("Entered X value '" + xString + "' is not numeric.");
			  errorTextField.setBackground(Color.pink);
			  return;
		      }
	      }
/**/   String result;
       try {
           if (xWasEntered) result = calculate(onlyExpression, xValue);
            else            result = calculate(onlyExpression);
           }
       catch(Exception e)
            {
    	    errorTextField.setText(e.getMessage());
    	    errorTextField.setBackground(Color.pink);
            return;    	   
            }
 	   double resultDouble = Double.parseDouble(result);
 	   if (expressionValue == resultDouble) // CORRECT RESULT!
 	      {
 		  errorTextField.setText("CONGRATULATIONS!");
 		  errorTextField.setBackground(Color.cyan);
	      String transactionLogLine = newLine + logLineLabelTextField.getText() + " " + expression;
	      if (xWasEntered) transactionLogLine += " for x = " + xString;
	      displayTextArea.append(transactionLogLine);
          displayTextArea.setCaretPosition(displayTextArea.getDocument().getLength());//scroll to bottom
 		  winners++;
          int percent = (int)(((double)winners/(double)entries) * 100.0);
          resultTextField.setText(winners + " of " + entries + " (" + percent + "%)");
 	      }
 	    else
 	     {
 	 	 errorTextField.setText("OOPS! Correct answer is " + result);
 	     errorTextField.setBackground(Color.orange);
 		 String transactionLogLine = newLine + logLineLabelTextField.getText() + " " + expression + " Correct answer is " + result;
	      if (xWasEntered) transactionLogLine += " for x = " + xString;
 		 displayTextArea.append(transactionLogLine);
 	     displayTextArea.setCaretPosition(displayTextArea.getDocument().getLength());//scroll to bottom
 	 	 int percent = (int)(100.0 * ((double)winners/(double)entries));
 	     resultTextField.setText(winners + " of " + entries + " (" + percent + "%)");
  	     }
	   return;
       } // END OF TEST MODE *************************************************
  
  if (inTimeMode) //**********************************************************
     {
	 int toOffset = expression.indexOf("to");
	 if (toOffset < 0) // no "to"
        {
	    errorTextField.setText("In TIME mode, start time and end time must be separated by 'to'.");
	    errorTextField.setBackground(Color.pink);
        return;
        }
	 String startTime = expression.substring(0,toOffset).trim();
	 String endTime   = expression.substring(toOffset+1).trim();
	 if ((startTime.length()==0) || (endTime.length()==1))
        {
	    errorTextField.setText("Start time or end time is missing.");
	    errorTextField.setBackground(Color.pink);
        return;
        }
     endTime = endTime.substring(1).trim(); // drop "o" of to
     if (debugMode) System.out.println("StartTime = '" + startTime + "' EndTime = '" + endTime + "'");
     if (startTime.contains(" ") || endTime.contains(" ")
      || startTime.contains("-") || endTime.contains("-"))
        {
	    errorTextField.setText("Times must not contain a blank or be negative.");
	    errorTextField.setBackground(Color.pink);
        return;
        }
     int startColonOffset = startTime.indexOf(":");
     int endColonOffset   = endTime.indexOf(":");
     if ((startColonOffset == 0) || (endColonOffset == 0))
        {
	    errorTextField.setText("Time before colon not specified.");
	    errorTextField.setBackground(Color.pink);
        return;
        }
     String startHour;
     String startMinute;
     String endHour;
     String endMinute;
     int    startHourValue;
     int    startMinuteValue;
     int    endHourValue;
     int    endMinuteValue;
     int    dailyMinutes;
     int    dailyHours;
     if (startColonOffset < 0) // no start colon
        { 
    	startHour = startTime;
    	startMinute = "00";
        }
     else // yes start colon
        { 
    	startHour = startTime.substring(0,startColonOffset);
        startMinute = startTime.substring(startColonOffset+1);
        }
     if (endColonOffset < 0) // no end colon
        { 
 	    endHour = endTime;
 	    endMinute = "00";
        }
      else // yes end colon
       { 
 	   endHour = endTime.substring(0,endColonOffset);
       endMinute = endTime.substring(endColonOffset+1);
       }
     if ((endMinute.length() != 2) || (startMinute.length() != 2))
        {
	    errorTextField.setText("Time after colon must be 2 digits.");
	    errorTextField.setBackground(Color.pink);
        return;
        }
     try {
         startHourValue   = Integer.parseInt(startHour);
         startMinuteValue = Integer.parseInt(startMinute);
         endHourValue     = Integer.parseInt(endHour);
         endMinuteValue   = Integer.parseInt(endMinute);
         }
     catch(NumberFormatException nfe)
         {
	     errorTextField.setText("Times before and after colon must be a numeric integer.");
	     errorTextField.setBackground(Color.pink);
         return;
         }
     System.out.println("startHour is " + startHourValue + " startMinute is " + startMinuteValue
    		         + " endHour is "   + endHourValue   + " endMinute is "   + endMinuteValue);
     // check ranges
     if ((startMinuteValue > 59) || (endMinuteValue > 59))
        {
        errorTextField.setText("Times after the colon must be less than 60.");
        errorTextField.setBackground(Color.pink);
        return;
        }
     if ((startHourValue > 23) || (endHourValue > 23))
        {
        errorTextField.setText("Times before the colon must be less than 24.");
        errorTextField.setBackground(Color.pink);
        return;
        }
     // compute daily totals
     if (endMinuteValue >= startMinuteValue)
         dailyMinutes = endMinuteValue - startMinuteValue;
       else 
         {  
    	 dailyMinutes = 60-startMinuteValue + endMinuteValue;
    	 endHourValue--; // subtract minutes added above
         }
     if (endHourValue >= startHourValue)
    	 dailyHours = endHourValue - startHourValue;
       else
    	 dailyHours = 24-startHourValue + endHourValue;
     System.out.println("dailyHours is " + dailyHours + " dailyMinutes is " + dailyMinutes);
     // accumulate weekly totals
     totalHours   += dailyHours;
     totalMinutes += dailyMinutes;
     if (totalMinutes > 59)
        {
    	totalMinutes -= 60;
    	totalHours ++;
        }
     String dailyMinuteString = String.valueOf(dailyMinutes);
     if (dailyMinuteString.length() == 1) 
    	 dailyMinuteString = "0" + dailyMinuteString;
     String totalMinuteString = String.valueOf(totalMinutes);
     if (totalMinuteString.length() == 1) 
    	 totalMinuteString = "0" + totalMinuteString;
     String resultLine = " = " + String.valueOf(dailyHours)
    		               + ":"   + dailyMinuteString
    		               + " cumulative " + String.valueOf(totalHours)
    		               + ":"   + totalMinuteString;
     resultTextField.setText(resultLine);
     String transactionLogLine = newLine + logLineLabelTextField.getText()
    		                     + " " + expression + resultLine;
     displayTextArea.append(transactionLogLine);
     displayTextArea.setCaretPosition(displayTextArea.getDocument().getLength());//scroll to bottom
     } // end of TIME mode processing *************************************
     
  } // end of expressionTextField input processing ************************
  
  
  if (ae.getSource() == clearButton)
     {
	 clear();  
     }
  
  if (ae.getSource() == accumulatorButton) 
     {
	 modeLabel.setBackground(Color.white);
	 accumulatorButton.setBackground(Color.yellow);
	 expressionButton.setBackground(Color.white);
	 graphButton.setBackground(Color.white);
	 testButton.setBackground(Color.white);
	 timeButton.setBackground(Color.white);
	 inAccumulatorMode = true;
	 inExpressionMode  = false;
	 inGraphMode       = false;
	 inTestMode        = false;
	 inTimeMode        = false;
     forXTextField.setEditable(false);
     byXTextField.setEditable(false);
     clear();
	 errorTextField.setText("In ACCUMULATOR mode, enter a single number in the Value field and press ENTER (optional - sign)");
	 errorTextField.setBackground(Color.yellow);
     }
  
  if (ae.getSource() == expressionButton) 
     {
	 modeLabel.setBackground(Color.white);
	 accumulatorButton.setBackground(Color.white);
	 expressionButton.setBackground(Color.yellow);
	 graphButton.setBackground(Color.white);
	 testButton.setBackground(Color.white);
	 timeButton.setBackground(Color.white);
	 inAccumulatorMode = false;
	 inExpressionMode  = true;
	 inGraphMode       = false;
	 inTestMode        = false;
	 inTimeMode        = false;
     byXTextField.setEditable(false);
     forXTextField.setEditable(true);
     clear();
	 errorTextField.setText("In EXPRESSION mode, if the expression contains X enter the value of x in the separate 'where x =' field. Expression Mode is only partially implemented: expressions are limited to a single operator.");
	 errorTextField.setBackground(Color.yellow);
     }

  if (ae.getSource() == graphButton) 
     {
	 modeLabel.setBackground(Color.white);
	 accumulatorButton.setBackground(Color.white);
	 expressionButton.setBackground(Color.white);
	 graphButton.setBackground(Color.yellow);
	 testButton.setBackground(Color.white);
	 timeButton.setBackground(Color.white);
	 inAccumulatorMode = false;
	 inExpressionMode  = false;
	 inGraphMode       = true;
	 inTestMode        = false;
	 inTimeMode        = false;
     byXTextField.setEditable(true);
     forXTextField.setEditable(true);
     clear();
	 errorTextField.setText("In GRAPH mode, the expression must contain x. Also enter a starting x value and the positive x scale increment. 11 values of x will be plotted.");
	 errorTextField.setBackground(Color.yellow);
     }
  
  if (ae.getSource() == testButton) 
     {
	 // show number of correct answers in resultTextField: 5 of 10 (50%)
	 // show incorrect answers in log area with != sign 
	 modeLabel.setBackground(Color.white);
	 accumulatorButton.setBackground(Color.white);
	 expressionButton.setBackground(Color.white);
	 graphButton.setBackground(Color.white);
	 testButton.setBackground(Color.yellow);
	 timeButton.setBackground(Color.white);
	 inAccumulatorMode = false;
	 inExpressionMode  = false;
	 inGraphMode       = false;
	 inTestMode        = true;
	 inTimeMode        = false;
     byXTextField.setEditable(false);
     forXTextField.setEditable(true);
     clear();
	 errorTextField.setText("In LEARNING mode, follow the expression with an = sign followed by what you think the value of the expression is!");
	 errorTextField.setBackground(Color.yellow);
	 }

  if (ae.getSource() == timeButton) 
     {
	 // There are 3 sub-modes:
	 // "to" separates starting from ending time & returns how long you worked.
	 // "for" separates starting time from work time & returns when you get off.
	 // "+" separates a series of times that are added & returns total time worked.  
	 modeLabel.setBackground(Color.white);
	 accumulatorButton.setBackground(Color.white);
	 expressionButton.setBackground(Color.white);
	 graphButton.setBackground(Color.white);
	 testButton.setBackground(Color.white);
	 timeButton.setBackground(Color.yellow);
	 inAccumulatorMode = false;
	 inExpressionMode  = false;
	 inGraphMode       = false;
	 inTestMode        = false;
	 inTimeMode        = true;
	 totalHours        = 0;
	 totalMinutes      = 0; 
     byXTextField.setEditable(false);
     forXTextField.setEditable(false);
     clear();
	 errorTextField.setText("In TIME mode,  use 24-hour time notation and enter 'StartTime to EndTime' (e.g. '9 to 5' would be '9:00 to 17:00') returns time worked and cumulative total. Can add hrs:min or min:sec. A single work period must be less than 24 hours.");
	 errorTextField.setBackground(Color.yellow);
	 }

  }// end of actionPerformed()

//==========================================================================
private void clear()
  {
  errorTextField.setText(""); 
  byXTextField.setText(""); 
  forXTextField.setText(""); 
  resultTextField.setText("");
  logLineLabelTextField.setText("");
  expressionTextField.setText(""); // also sets cursor in...
  previousAnswer = 0;
  previousResult = "";
  }

//===================================================================================
public String calculate(String expression, double x)
		throws IllegalArgumentException
  {
  expression = expression.toLowerCase().trim();	
  if (debugMode) System.out.println("In calculate() with expression "
                              + expression + " with x value of " + x);
  if (!expression.contains("x")) 
	  throw new IllegalArgumentException("In calculate() with x value but expression does not contain X.");
  // Find the operator!
  // Note: to substitute the value for x we must do an equals() compare
  // on left & right operators rather than do a replace("x",xValue) to
  // avoid turning an incorrect 2x operator into 21 when x = 1!
  char operator = ' ';
  int  i;
  for (i = 1; i < expression.length(); i++) 
       if((expression.charAt(i) == '+')
        ||(expression.charAt(i) == '-')
        ||(expression.charAt(i) == '*')
        ||(expression.charAt(i) == '/')
        ||(expression.charAt(i) == '^')
        ||(expression.charAt(i) == 'r'))
          {
          operator = expression.charAt(i);
          break;
          }
  if (i == expression.length()) // no find operator
	  throw new IllegalArgumentException("No operator found between operands.");
  if (i == expression.length()-1) // find operator at end
	  throw new IllegalArgumentException("Expression cannot end with an operator.");
  if (debugMode) System.out.println("Found operator " + operator 
		                          + " at offset " + i);
  // Find the operands!
  String leftOperand  = expression.substring(0,i).trim();
  String rightOperand = expression.substring(i+1).trim();
  if (debugMode) System.out.println("Left operand is '"    + leftOperand 
		                          + "' Right operand is '" + rightOperand + "'");

  //Substitute value of X in the expression and call calculate(expression)!
  String xString = String.valueOf(x);
  String positiveXString = null; // watch for a negative x creating
  if (xString.startsWith("-"))   // a "--" negative unary operator! 
      positiveXString = xString.substring(1); // drop leading "-"
  if (leftOperand.equals("x"))
	  leftOperand = xString; // whatever x is
  if (leftOperand.equals("-x") && (x<0))
	  leftOperand = positiveXString; // double negative makes a positive.
  if (leftOperand.contains("x"))
	  throw new IllegalArgumentException ("Left operand (" + leftOperand + ") uses an unvalid form of x.");
  if (rightOperand.equals("x"))
	  rightOperand = xString; // whatever x is
  if (rightOperand.equals("-x") && (x<0))
	  rightOperand = positiveXString; // double negative makes a positive.
  if (rightOperand.contains("x"))
	  throw new IllegalArgumentException ("Right operand (" + rightOperand + ")  contains an invalid form of x.");

  // reform the expression
  expression = leftOperand + " " + operator + " " + rightOperand;
  return calculate(expression);             
  }

//===================================================================================
public String calculate(String expression) throws IllegalArgumentException
  {
	if (debugMode) System.out.println("In calculate() with expression " + expression);
  if ((expression==null) || (expression.trim().length() < 3))
		  throw new IllegalArgumentException("Expression is missing or is too short.");
  expression = expression.trim().toLowerCase();
  if (expression.contains("x")) 
	  throw new IllegalArgumentException("In calculate() with no x value but expression contains X.");
  // Find the operator!
  char operator = ' ';
  int  i;
  for (i = 1; i < expression.length(); i++) 
       if((expression.charAt(i) == '+')
        ||(expression.charAt(i) == '-')
        ||(expression.charAt(i) == '*')
        ||(expression.charAt(i) == '/')
        ||(expression.charAt(i) == '^')
        ||(expression.charAt(i) == 'r'))
          {
          operator = expression.charAt(i);
          break;
          }
  if (i == expression.length()) // no find operator
	  throw new IllegalArgumentException("No operator found between operands.");
  if (i == expression.length()-1) // find operator at end
	  throw new IllegalArgumentException("Expression cannot end with an operator.");
  if (debugMode) System.out.println("Found operator " + operator 
		                          + " at offset " + i);
  // Find the operands!
  String leftOperand  = expression.substring(0,i).trim();
  String rightOperand = expression.substring(i+1).trim();
  if (debugMode) System.out.println("Left operand is '"    + leftOperand 
		                          + "' Right operand is '" + rightOperand + "'");
  // Convert the String operands to their numeric value 
  double leftNumber = 0;
  double rightNumber= 0;
  // Replace e and pi operands with their value (careful!)
  if (leftOperand.equals("e"))  leftNumber =  Math.E;
  if (leftOperand.equals("-e")) leftNumber = -Math.E;
  if (leftOperand.equals("pi")) leftNumber =  Math.PI;
  if (leftOperand.equals("-pi"))leftNumber = -Math.PI;
  if (leftNumber == 0) // left operand was not e or pi
     {
     try { 
         leftNumber = Double.parseDouble(leftOperand);
         }
     catch(NumberFormatException nfe)
        {
        throw new IllegalArgumentException("Left operand '" + leftOperand + "' is not numeric.");
        }
     }
  if (rightOperand.equals("e"))  rightNumber =  Math.E;
  if (rightOperand.equals("-e")) rightNumber = -Math.E;
  if (rightOperand.equals("pi")) rightNumber =  Math.PI;
  if (rightOperand.equals("-pi"))rightNumber = -Math.PI;
  if (rightNumber == 0) // right operand was not e or pi
     {
     try {
         rightNumber = Double.parseDouble(rightOperand);
         }
     catch(NumberFormatException nfe)
         {
         throw new IllegalArgumentException("Right operand '" + rightOperand + "' is not numeric.");
         }
     }
  if (debugMode) System.out.println("Left number is " + leftNumber
		                          + " Right number is " + rightNumber);
  // Calculate the expression value.
  double result = 0;
  switch (operator)
     {
     case '+' : result = leftNumber + rightNumber; 	        break;
     case '-' : result = leftNumber - rightNumber; 	        break;
     case '*' : result = leftNumber * rightNumber; 	        break;
     case '/' : result = leftNumber / rightNumber; 	        break;
     case '^' : result = Math.pow(leftNumber,rightNumber);  break;
     case 'r' : result = Math.pow(leftNumber,1/rightNumber);break;
     }
  // Return expression value in String form.
  return String.valueOf(result);
  }
}