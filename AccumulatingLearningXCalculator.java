//Guangsen Ji  @03/14/2020 Copyright Reserved
//This lab builds a Calcultor supports Test Mode and Accumulator Mode 
// Run it with CalculatorInterface.lass


import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class AccumulatingLearningXCalculator implements ActionListener, CalculatorInterface
{
	public static void main(String[] args) throws Exception
	{
		new AccumulatingLearningXCalculator();
	}

	// GUI objects
	private JFrame       window              = new JFrame("Accumulating-Expression-Graphing-Testing CALCULATOR.  8 Operators are + - * / ^ (exponentiation) r (root) and () (parentheses).  Operands are numbers, X, pi, or e");
	
	private JTextField   expressionTextField = new JTextField();
	private JTextField   errorTextField      = new JTextField("You can copy expressions from the log area into the Expression field.");
	private JTextField   resultTextField     = new JTextField();
	private JTextField   forXTextField       = new JTextField();
	private JTextField   byXTextField        = new JTextField();
	private JTextArea    displayTextArea     = new JTextArea("previous expressions will be listed here");
	private JScrollPane  transactionLog      = new JScrollPane(displayTextArea);		
	private JButton      clearButton         = new JButton("clear expression");
	private JButton      accumulatorButton   = new JButton("Accum");
	private JButton      expressionButton    = new JButton("Expres");
	private JButton      graphButton         = new JButton("Graph");
	private JButton      testButton          = new JButton("Test");
	private JLabel       forXlabel           = new JLabel("where X =");
	private JLabel       byXlabel            = new JLabel("use X increments of");
	private JLabel       expressionLabel     = new JLabel("Enter Value/Expression here");
	private JLabel       resultLabel         = new JLabel("Result Accumulates here");
	private JButton      modeLabel           = new JButton("Pick mode above");
	private JPanel       topPanel            = new JPanel();
	private JPanel       penultimateTopPanel = new JPanel();
	private JPanel       northPanel          = new JPanel();
	private JPanel       buttonPanel         = new JPanel(); 
	private JPanel       modeLabelPanel      = new JPanel();
	private JPanel       xPanel              = new JPanel();
	private JPanel       xLabelPanel         = new JPanel();

	//instance variables
	private String       newLine             = System.getProperty("line.separator");
	private String       previousResult;
	private double		 accumulatedTotal = 0;
	private double       previousAnswer;
	private boolean      inAccumulatorMode;
	private boolean      inExpressionMode;
	private boolean      inGraphMode;
	private boolean      inTestMode;
	private double InputSize = 0;
	private double CorrectSize = 0;
	private int percentage = 0;

	public AccumulatingLearningXCalculator() // CONSTRUCTOR
	{
		System.out.println("******************************************************");
		System.out.println("* This is the instructor-provided Lab Calculator GUI *");
		System.out.println("******************************************************");

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
		buttonPanel.setLayout(new GridLayout(1,4)); // rows, cols
		modeLabelPanel.setLayout(new GridLayout(1,2)); // rows, cols
		xPanel.setLayout(new GridLayout(1,2)); // rows, cols
		xLabelPanel.setLayout(new GridLayout(1,2)); // rows, cols

		buttonPanel.add(accumulatorButton);
		buttonPanel.add(expressionButton);
		buttonPanel.add(graphButton);
		buttonPanel.add(testButton);
		topPanel.add(buttonPanel);
		topPanel.add(expressionLabel);
		topPanel.add(resultLabel);
		xLabelPanel.add(forXlabel);
		xLabelPanel.add(byXlabel);
		topPanel.add(xLabelPanel);
		modeLabelPanel.add(modeLabel);
		modeLabelPanel.add(clearButton);
		penultimateTopPanel.add(modeLabelPanel);
		penultimateTopPanel.add(expressionTextField);
		penultimateTopPanel.add(resultTextField);
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
		displayTextArea.setFont(new Font("default",Font.BOLD,15));
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

		// Show window
		window.setSize(1100,300); // width, height
		window.setVisible(true);
		expressionTextField.requestFocus(); // set cursor in
	}	

	//===================================================================================
	public void actionPerformed(ActionEvent ae)
	{
		errorTextField.setText("");
		errorTextField.setBackground(Color.white);
		resultTextField.setText(""); // clear previous result

		if ((ae.getSource() == expressionTextField) 
				|| (ae.getSource() == forXTextField) 
				|| (ae.getSource() == byXTextField))
		{
			if (!inAccumulatorMode && !inExpressionMode && !inGraphMode && !inTestMode)
			{
				errorTextField.setText("Must select a mode to start.");
				errorTextField.setBackground(Color.pink);
				return;
			}

			if (inAccumulatorMode)
			{
				//    errorTextField.setText("Accumulator Mode is not yet implemented.");
				//   errorTextField.setBackground(Color.yellow);
				//	return;
				String AccumulatorEnterdExpression = expressionTextField.getText().trim();
				double AccumulatorEnteredDouble = 0;
				double Pre = 0;
				try
				{
					AccumulatorEnteredDouble = Double.parseDouble(AccumulatorEnterdExpression);


				}
				catch (Exception e)
				{
					errorTextField.setText(e.getMessage());
					errorTextField.setBackground(Color.pink);
				}
				if(AccumulatorEnteredDouble %1 == 0 )
				{		Pre = accumulatedTotal;
				accumulatedTotal += AccumulatorEnteredDouble;

				}
				else 
				{
					int bitPos =  AccumulatorEnterdExpression.indexOf(".");
					int numOfBits = AccumulatorEnterdExpression.length() - bitPos - 1;
					if (numOfBits != 2)
					{
						errorTextField.setBackground(Color.pink);
						errorTextField.setText(" 2 digits must be followed by decimal point.  ");
					}
					else 
					{		Pre = accumulatedTotal;
					accumulatedTotal += AccumulatorEnteredDouble;
					}

				}

				expressionTextField.setText("");
				displayTextArea.append(newLine + "Data Entered: " + AccumulatorEnteredDouble + "+" + Pre + " = " + accumulatedTotal);
				resultTextField.setText("new total = "+accumulatedTotal);


			}
			if (inExpressionMode)
			{
				errorTextField.setText("Expression Mode is only partially implemented. Expressions are limited to a single operator and must not contain x or parentheses.");
			}
			if (inGraphMode)
			{
				errorTextField.setText("Graph Mode is not yet implemented.");
				errorTextField.setBackground(Color.yellow);
				return;
			}
			if (inTestMode)
			{
				
				String input = expressionTextField.getText();
				int equaloffset = input.indexOf("=");
				if(input.trim().length()==0) return;
				if(equaloffset < 0)
				{
					errorTextField.setText("in test mode the expression must be followed by '=' then by a number");
					errorTextField.setBackground(Color.pink);
					return;
				}
				if ( input.substring(equaloffset+1).length() == 0)
				{
					errorTextField.setText("in test mode the expression after '=' must be followed by a number ");
					errorTextField.setBackground(Color.pink);
					return;
				}
				try 
				{
					String ExpressionLeft = calculate(input.substring(0,equaloffset));
					double CalculatedResult = Double.parseDouble(ExpressionLeft);
					String ExpressionRight = input.substring(equaloffset+1).trim();
					double InputResult = Double.parseDouble(ExpressionRight);
					if (CalculatedResult == InputResult)
					{
						errorTextField.setText("Congrats");
						errorTextField.setBackground(Color.blue);
						displayTextArea.append(newLine + "Data Entered: " +input );
						CorrectSize ++;
						InputSize ++;
						

					}
					else 
					{
						errorTextField.setText("oops the correct answer should be " + CalculatedResult);
						errorTextField.setBackground(Color.yellow);
						displayTextArea.append(newLine + input + "correct answer is "+  CalculatedResult);
						
						InputSize = InputSize + 1;
						
					}
					percentage = (int)( (CorrectSize*100) /InputSize);
					resultTextField.setText(CorrectSize + " of " + InputSize + " (" + percentage +"%" + ")");
					
				}
				catch(Exception e)
				{
					errorTextField.setText(e.getMessage());
					errorTextField.setBackground(Color.pink);
					System.out.println("ERROR: " + e.getMessage());
				}
				
				
				return;

			}

			String enteredExpression = expressionTextField.getText().trim();
			if (enteredExpression.length() == 0) return; // ignore blanks
			String expression = enteredExpression.toLowerCase();
			System.out.println(expression);

			try {
				String result  = calculate(expression);
				previousResult = result;
				previousAnswer = Double.parseDouble(previousResult);
				String transactionLogLine = newLine + expression + " = " + result;
				displayTextArea.append(transactionLogLine);
				displayTextArea.setCaretPosition(displayTextArea.getDocument().getLength());//scroll to bottom
				resultTextField.setText(" = " + result);
			}
			catch (Exception e)
			{
				errorTextField.setText(e.getMessage());
				errorTextField.setBackground(Color.pink);
			}
		}

		if (ae.getSource() == clearButton)
		{
			errorTextField.setText(""); 
			byXTextField.setText(""); 
			forXTextField.setText(""); 
			resultTextField.setText("");
			expressionTextField.setText(""); // also sets cursor in...
			previousAnswer = 0;
			previousResult = "";
		}

		if (ae.getSource() == accumulatorButton) 
		{
			modeLabel.setBackground(Color.white);
			accumulatorButton.setBackground(Color.yellow);
			expressionButton.setBackground(Color.white);
			graphButton.setBackground(Color.white);
			testButton.setBackground(Color.white);
			inAccumulatorMode = true;
			inExpressionMode  = false;
			inGraphMode       = false;
			inTestMode        = false;
			errorTextField.setText("Enter single number in the Value field and press ENTER (optional - sign)");
			errorTextField.setBackground(Color.yellow);
			forXTextField.setEditable(false);
			byXTextField.setEditable(false);
		}

		if (ae.getSource() == expressionButton) 
		{
			modeLabel.setBackground(Color.white);
			accumulatorButton.setBackground(Color.white);
			expressionButton.setBackground(Color.yellow);
			graphButton.setBackground(Color.white);
			testButton.setBackground(Color.white);
			inAccumulatorMode = false;
			inExpressionMode  = true;
			inGraphMode       = false;
			inTestMode        = false;
			errorTextField.setText("Enter an expression in the Expression field. (may contain x)");
			errorTextField.setBackground(Color.yellow);
			byXTextField.setEditable(false);
			forXTextField.setEditable(true);
		}

		if (ae.getSource() == graphButton) 
		{
			modeLabel.setBackground(Color.white);
			accumulatorButton.setBackground(Color.white);
			expressionButton.setBackground(Color.white);
			graphButton.setBackground(Color.yellow);
			testButton.setBackground(Color.white);
			inAccumulatorMode = false;
			inExpressionMode  = false;
			inGraphMode       = true;
			inTestMode        = false;
			errorTextField.setText("The expression must contain x. Also enter starting x value and x scale increment.");
			errorTextField.setBackground(Color.yellow);
			byXTextField.setEditable(true);
			forXTextField.setEditable(true);
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
			inAccumulatorMode = false;
			inExpressionMode  = false;
			inGraphMode       = false;
			inTestMode        = true;
			errorTextField.setText("Follow the expression with an = sign and the value of the expression.");
			errorTextField.setBackground(Color.yellow);
			byXTextField.setEditable(false);
			forXTextField.setEditable(true);
		}
	}// end of actionPerformed()

	//===================================================================================
	public String calculate(String expression, double x)
			throws IllegalArgumentException
	{
		//Substitute value of X in the expression and call calculate(expression)!

		return calculate(expression);
	}

	//********************************************************************************
	public String calculate(String expression) throws IllegalArgumentException
	{
		System.out.println("In calculate() with expression " + expression);
		if ((expression==null) || (expression.trim().length() < 3))
			throw new IllegalArgumentException("Expression is missing or is too short.");
		expression = expression.trim().toLowerCase();
		expression = expression.replace("e", String.valueOf(Math.E)); 
		expression = expression.replace("pi",String.valueOf(Math.PI)); 
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
		System.out.println("Found operator " + operator + " at offset " + i);
		// Find the operands!
		String leftOperand  = expression.substring(0,i).trim();
		String rightOperand = expression.substring(i+1).trim();
		System.out.println("Left operand is '"    + leftOperand 
				+ "' Right operand is '" + rightOperand + "'");
		// Convert the String operands to their numeric value 
		double leftNumber;
		double rightNumber;
		try { 
			leftNumber = Double.parseDouble(leftOperand);
		}
		catch(NumberFormatException nfe)
		{
			throw new IllegalArgumentException("Left operand " + leftOperand + " is not numeric.");
		}
		try {
			rightNumber = Double.parseDouble(rightOperand);
		}
		catch(NumberFormatException nfe)
		{
			throw new IllegalArgumentException("Right operand " + rightOperand + " is not numeric.");
		}
		System.out.println("Left number is " + leftNumber + " Right number is " + rightNumber);
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